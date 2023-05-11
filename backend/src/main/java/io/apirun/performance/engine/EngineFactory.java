package io.apirun.performance.engine;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.apirun.Application;
import io.apirun.base.domain.FileContent;
import io.apirun.base.domain.FileMetadata;
import io.apirun.base.domain.LoadTestWithBLOBs;
import io.apirun.base.domain.TestResourcePool;
import io.apirun.commons.constants.FileType;
import io.apirun.commons.constants.ResourcePoolTypeEnum;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.LogUtil;
import io.apirun.i18n.Translator;
import io.apirun.performance.engine.docker.DockerTestEngine;
import io.apirun.performance.parse.EngineSourceParser;
import io.apirun.performance.parse.EngineSourceParserFactory;
import io.apirun.performance.service.PerformanceTestService;
import io.apirun.service.FileService;
import io.apirun.service.KubernetesTestEngine;
import io.apirun.service.TestResourcePoolService;
import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections8.Reflections;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class EngineFactory {
    private static FileService fileService;
    private static PerformanceTestService performanceTestService;
    private static TestResourcePoolService testResourcePoolService;
    private static Class<? extends KubernetesTestEngine> kubernetesTestEngineClass;

    static {
        Reflections reflections = new Reflections(Application.class.getPackage().getName());
        Set<Class<? extends KubernetesTestEngine>> implClass = reflections.getSubTypesOf(KubernetesTestEngine.class);
        for (Class<? extends KubernetesTestEngine> aClass : implClass) {
            kubernetesTestEngineClass = aClass;
            // 第一个
            break;
        }
    }

    public static Engine createEngine(LoadTestWithBLOBs loadTest) {
        String resourcePoolId = loadTest.getTestResourcePoolId();
        if (StringUtils.isBlank(resourcePoolId)) {
            MSException.throwException(Translator.get("test_resource_pool_id_is_null"));
        }

        TestResourcePool resourcePool = testResourcePoolService.getResourcePool(resourcePoolId);
        if (resourcePool == null) {
            MSException.throwException(Translator.get("test_resource_pool_id_is_null"));
        }

        final ResourcePoolTypeEnum type = ResourcePoolTypeEnum.valueOf(resourcePool.getType());

        if (type == ResourcePoolTypeEnum.NODE) {
            return new DockerTestEngine(loadTest);
        }
        if (type == ResourcePoolTypeEnum.K8S) {
            try {
                return (Engine) ConstructorUtils.invokeConstructor(kubernetesTestEngineClass, loadTest);
            } catch (Exception e) {
                LogUtil.error(e);
                return null;
            }
        }
        return null;
    }

    public static EngineContext createContext(LoadTestWithBLOBs loadTest, double[] ratios, String reportId, int resourceIndex) {
        final List<FileMetadata> fileMetadataList = performanceTestService.getFileMetadataByTestId(loadTest.getId());
        if (org.springframework.util.CollectionUtils.isEmpty(fileMetadataList)) {
            MSException.throwException(Translator.get("run_load_test_file_not_found") + loadTest.getId());
        }

        if (ratios.length == 1 && ratios[0] < 0) {
            ratios[0] = 1;
        }

        List<FileMetadata> jmxFiles = fileMetadataList.stream().filter(f -> StringUtils.equalsIgnoreCase(f.getType(), FileType.JMX.name())).collect(Collectors.toList());
        List<FileMetadata> resourceFiles = ListUtils.subtract(fileMetadataList, jmxFiles);
        // 合并上传的jmx
        byte[] jmxBytes = mergeJmx(jmxFiles);
        final EngineContext engineContext = new EngineContext();
        engineContext.setTestId(loadTest.getId());
        engineContext.setTestName(loadTest.getName());
        engineContext.setNamespace(loadTest.getProjectId());
        engineContext.setFileType(FileType.JMX.name());
        engineContext.setResourcePoolId(loadTest.getTestResourcePoolId());
        engineContext.setReportId(reportId);
        engineContext.setResourceIndex(resourceIndex);

        if (StringUtils.isNotEmpty(loadTest.getLoadConfiguration())) {
            final JSONArray jsonArray = JSONObject.parseArray(loadTest.getLoadConfiguration());

            for (int i = 0; i < jsonArray.size(); i++) {
                if (jsonArray.get(i) instanceof List) {
                    JSONArray o = jsonArray.getJSONArray(i);
                    String strategy = "auto";
                    int resourceNodeIndex = 0;
                    JSONArray tgRatios = null;
                    for (int j = 0; j < o.size(); j++) {
                        JSONObject b = o.getJSONObject(j);
                        String key = b.getString("key");
                        if ("strategy".equals(key)) {
                            strategy = b.getString("value");
                        }
                        if ("resourceNodeIndex".equals(key)) {
                            resourceNodeIndex = b.getIntValue("value");
                        }
                        if ("ratios".equals(key)) {
                            tgRatios = b.getJSONArray("value");
                        }
                    }
                    for (int j = 0; j < o.size(); j++) {
                        JSONObject b = o.getJSONObject(j);
                        String key = b.getString("key");
                        Object values = engineContext.getProperty(key);
                        if (values == null) {
                            values = new ArrayList<>();
                        }
                        if (values instanceof List) {
                            Object value = b.get("value");
                            if ("TargetLevel".equals(key)) {
                                switch (strategy) {
                                    default:
                                    case "auto":
                                        Integer targetLevel = ((Integer) b.get("value"));
                                        if (resourceIndex + 1 == ratios.length) {
                                            double beforeLast = 0; //前几个线程
                                            for (int k = 0; k < ratios.length - 1; k++) {
                                                beforeLast += Math.round(targetLevel * ratios[k]);
                                            }
                                            value = Math.round(targetLevel - beforeLast);
                                        } else {
                                            value = Math.round(targetLevel * ratios[resourceIndex]);
                                        }
                                        break;
                                    case "specify":
                                        Integer threadNum = ((Integer) b.get("value"));
                                        if (resourceNodeIndex == resourceIndex) {
                                            value = Math.round(threadNum);
                                        } else {
                                            value = Math.round(0);
                                        }
                                        break;
                                    case "custom":
                                        Integer threadNum2 = ((Integer) b.get("value"));
                                        if (CollectionUtils.isNotEmpty(tgRatios)) {
                                            if (resourceIndex + 1 == tgRatios.size()) {
                                                double beforeLast = 0; //前几个线程数
                                                for (int k = 0; k < tgRatios.size() - 1; k++) {
                                                    beforeLast += Math.round(threadNum2 * tgRatios.getDoubleValue(k));
                                                }
                                                value = Math.round(threadNum2 - beforeLast);
                                            } else {
                                                value = Math.round(threadNum2 * tgRatios.getDoubleValue(resourceIndex));
                                            }
                                        }
                                        break;
                                }
                            }
                            if ("minThreadNumber".equals(key)) {
                                switch (strategy) {
                                    default:
                                    case "auto":
                                        Integer minThreadNumber = ((Integer) b.get("value"));
                                        LogUtil.info("minThreadNumber:{}", minThreadNumber);
                                        if (resourceIndex + 1 == ratios.length) {
                                            double beforeLast = 0; //前几个线程
                                            for (int k = 0; k < ratios.length - 1; k++) {
                                                beforeLast += Math.round(minThreadNumber * ratios[k]);
                                            }
                                            value = Math.round(minThreadNumber - beforeLast);
                                        } else {
                                            value = Math.round(minThreadNumber * ratios[resourceIndex]);
                                        }
                                        break;
                                    case "specify":
                                        Integer threadNum = ((Integer) b.get("value"));
                                        if (resourceNodeIndex == resourceIndex) {
                                            value = Math.round(threadNum);
                                        } else {
                                            value = Math.round(0);
                                        }
                                        break;
                                    case "custom":
                                        Integer threadNum2 = ((Integer) b.get("value"));
                                        if (CollectionUtils.isNotEmpty(tgRatios)) {
                                            if (resourceIndex + 1 == tgRatios.size()) {
                                                double beforeLast = 0; //前几个线程数
                                                for (int k = 0; k < tgRatios.size() - 1; k++) {
                                                    beforeLast += Math.round(threadNum2 * tgRatios.getDoubleValue(k));
                                                }
                                                value = Math.round(threadNum2 - beforeLast);
                                            } else {
                                                value = Math.round(threadNum2 * tgRatios.getDoubleValue(resourceIndex));
                                            }
                                        }
                                        break;
                                }
                            }
                            ((List<Object>) values).add(value);
                            engineContext.addProperty(key, values);
                        }
                    }
                }
            }
        }
        /*
        {"timeout":10,"statusCode":["302","301"],"params":[{"name":"param1","enable":true,"value":"0","edit":false}],"domains":[{"domain":"baidu.com","enable":true,"ip":"127.0.0.1","edit":false}]}
         */
        if (StringUtils.isNotEmpty(loadTest.getAdvancedConfiguration())) {
            JSONObject advancedConfiguration = JSONObject.parseObject(loadTest.getAdvancedConfiguration());
            engineContext.addProperties(advancedConfiguration);
        }

        final EngineSourceParser engineSourceParser = EngineSourceParserFactory.createEngineSourceParser(engineContext.getFileType());

        if (engineSourceParser == null) {
            MSException.throwException("File type unknown");
        }

        try (ByteArrayInputStream source = new ByteArrayInputStream(jmxBytes)) {
            String content = engineSourceParser.parse(engineContext, source);
            engineContext.setContent(content);
        } catch (MSException e) {
            LogUtil.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LogUtil.error(e.getMessage(), e);
            MSException.throwException(e);
        }

        if (CollectionUtils.isNotEmpty(resourceFiles)) {
            Map<String, byte[]> data = new HashMap<>();
            resourceFiles.forEach(cf -> {
                FileContent csvContent = fileService.getFileContent(cf.getId());
                data.put(cf.getName(), csvContent.getFile());
            });
            engineContext.setTestResourceFiles(data);
        }

        return engineContext;
    }

    public static byte[] mergeJmx(List<FileMetadata> jmxFiles) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Element hashTree = null;
            Document rootDocument = null;
            for (FileMetadata fileMetadata : jmxFiles) {
                FileContent fileContent = fileService.getFileContent(fileMetadata.getId());
                final InputSource inputSource = new InputSource(new ByteArrayInputStream(fileContent.getFile()));
                if (hashTree == null) {
                    rootDocument = docBuilder.parse(inputSource);
                    Element jmeterTestPlan = rootDocument.getDocumentElement();
                    NodeList childNodes = jmeterTestPlan.getChildNodes();
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        Node node = childNodes.item(i);
                        if (node instanceof Element) {
                            // jmeterTestPlan的子元素肯定是<hashTree></hashTree>
                            hashTree = (Element) node;
                            break;
                        }
                    }
                } else {
                    Document document = docBuilder.parse(inputSource);
                    Element jmeterTestPlan = document.getDocumentElement();
                    NodeList childNodes = jmeterTestPlan.getChildNodes();
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        Node node = childNodes.item(i);
                        if (node instanceof Element) {
                            // jmeterTestPlan的子元素肯定是<hashTree></hashTree>
                            Element secondHashTree = (Element) node;
                            NodeList secondChildNodes = secondHashTree.getChildNodes();
                            for (int j = 0; j < secondChildNodes.getLength(); j++) {
                                Node item = secondChildNodes.item(j);
                                Node newNode = item.cloneNode(true);
                                rootDocument.adoptNode(newNode);
                                hashTree.appendChild(newNode);
                            }
                        }
                    }
                }
            }
            return documentToBytes(rootDocument);
        } catch (Exception e) {
            MSException.throwException(e);
        }
        return new byte[0];
    }

    private static byte[] documentToBytes(Document document) throws TransformerException {
        DOMSource domSource = new DOMSource(document);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(out);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return out.toByteArray();
    }

    @Resource
    private void setFileService(FileService fileService) {
        EngineFactory.fileService = fileService;
    }

    @Resource
    public void setTestResourcePoolService(TestResourcePoolService testResourcePoolService) {
        EngineFactory.testResourcePoolService = testResourcePoolService;
    }

    @Resource
    public void setPerformanceTestService(PerformanceTestService performanceTestService) {
        EngineFactory.performanceTestService = performanceTestService;
    }
}
