package io.apirun.performance.parse.xml.reader.jmx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.CommonBeanFactory;
import io.apirun.commons.utils.ScriptEngineUtils;
import io.apirun.config.KafkaProperties;
import io.apirun.i18n.Translator;
import io.apirun.performance.engine.EngineContext;
import io.apirun.performance.parse.xml.reader.DocumentParser;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;

public class JmeterDocumentParser implements DocumentParser {
    private final static String HASH_TREE_ELEMENT = "hashTree";
    private final static String TEST_PLAN = "TestPlan";
    private final static String STRING_PROP = "stringProp";
    private final static String ELEMENT_PROP = "elementProp";
    private final static String BOOL_PROP = "boolProp";
    private final static String COLLECTION_PROP = "collectionProp";
    private final static String CONCURRENCY_THREAD_GROUP = "com.blazemeter.jmeter.threads.concurrency.ConcurrencyThreadGroup";
    private final static String VARIABLE_THROUGHPUT_TIMER = "kg.apc.jmeter.timers.VariableThroughputTimer";
    private final static String THREAD_GROUP = "ThreadGroup";
    private final static String POST_THREAD_GROUP = "PostThreadGroup";
    private final static String SETUP_THREAD_GROUP = "SetupThreadGroup";
    private final static String BACKEND_LISTENER = "BackendListener";
    private final static String CONFIG_TEST_ELEMENT = "ConfigTestElement";
    private final static String DNS_CACHE_MANAGER = "DNSCacheManager";
    private final static String ARGUMENTS = "Arguments";
    private final static String RESPONSE_ASSERTION = "ResponseAssertion";
    private final static String HTTP_SAMPLER_PROXY = "HTTPSamplerProxy";
    private final static String CSV_DATA_SET = "CSVDataSet";
    private final static String THREAD_GROUP_AUTO_STOP = "io.metersphere.jmeter.reporters.ThreadGroupAutoStop";
    private final static String ULTIMATE_THREAD_GROUP = "kg.apc.jmeter.threads.UltimateThreadGroup";
    private final static String AUTO_STOP = "kg.apc.jmeter.reporters.AutoStop";

    /**定义几种压力模型*/
    private final static String SHOCK_MODEL = "shock";
    private final static String WAVE_SURGE_MODEL = "waveSurge";
    private final static String DIP = "dip";
    private EngineContext context;

    @Override
    public String parse(EngineContext context, Document document) throws Exception {
        this.context = context;

        final Element jmeterTestPlan = document.getDocumentElement();

        NodeList childNodes = jmeterTestPlan.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (node instanceof Element) {
                Element ele = (Element) node;

                // jmeterTestPlan的子元素肯定是<hashTree></hashTree>
                parseHashTree(ele);
            }
        }

        return documentToString(document);
    }

    private String documentToString(Document document) throws TransformerException {
        DOMSource domSource = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

    private void parseHashTree(Element hashTree) {
        if (invalid(hashTree)) {
            return;
        }

        if (hashTree.getChildNodes().getLength() > 0) {
            final NodeList childNodes = hashTree.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element) {
                    Element ele = (Element) node;

                    if (nodeNameEquals(ele, HASH_TREE_ELEMENT)) {
                        parseHashTree(ele);
                    } else if (nodeNameEquals(ele, TEST_PLAN)) {
                        processCheckoutConfigTestElement(ele);
                        processCheckoutDnsCacheManager(ele);
                        processCheckoutArguments(ele);
                        processCheckoutResponseAssertion(ele);
                        processCheckoutSerializeThreadgroups(ele);
                        processCheckoutBackendListener(ele);
                        processCheckoutAutoStopListener(ele);
                    } else if (nodeNameEquals(ele, CONCURRENCY_THREAD_GROUP)) {
                        processThreadType(ele);
                        processThreadGroupName(ele);
                        processCheckoutTimer(ele);
                    } else if (nodeNameEquals(ele, VARIABLE_THROUGHPUT_TIMER)) {
                        processVariableThroughputTimer(ele);
                    } else if (nodeNameEquals(ele, THREAD_GROUP) ||
                            nodeNameEquals(ele, SETUP_THREAD_GROUP) ||
                            nodeNameEquals(ele, POST_THREAD_GROUP) ||
                            nodeNameEquals(ele, ULTIMATE_THREAD_GROUP)) {
                        processThreadType(ele);
                        processThreadGroupName(ele);
                        processCheckoutTimer(ele);
                    } else if (nodeNameEquals(ele, BACKEND_LISTENER)) {
                        processBackendListener(ele);
                    } else if (nodeNameEquals(ele, CONFIG_TEST_ELEMENT)) {
                        processConfigTestElement(ele);
                    } else if (nodeNameEquals(ele, DNS_CACHE_MANAGER)) {
                        // todo dns cache manager bug:  https://bz.apache.org/bugzilla/show_bug.cgi?id=63858
                        // processDnsCacheManager(ele);
                    } else if (nodeNameEquals(ele, ARGUMENTS)) {
                        processArguments(ele);
                    } else if (nodeNameEquals(ele, RESPONSE_ASSERTION)) {
                        processResponseAssertion(ele);
                    } else if (nodeNameEquals(ele, CSV_DATA_SET)) {
                        processCsvDataSet(ele);
                    } else if (nodeNameEquals(ele, THREAD_GROUP_AUTO_STOP)) {
                        processAutoStopListener(ele);
                    } else if (nodeNameEquals(ele, HTTP_SAMPLER_PROXY)) {
                        // 处理http上传的附件
                        processArgumentFiles(ele);
                    }
                }
            }
        }
    }

    private void processThreadType(Element ele) {
        Object threadType = context.getProperty("threadType");
        if (threadType instanceof List) {
            Object o = ((List<?>) threadType).get(0);
            ((List<?>) threadType).remove(0);
            if ("DURATION".equals(o)) {
                processThreadGroup(ele);
            }
            if ("ITERATION".equals(o)) {
                processIterationThreadGroup(ele);
            }
        } else {
            processThreadGroup(ele);
        }
    }

    private void processAutoStopListener(Element autoStopListener) {
        Object autoStopDelays = context.getProperty("autoStopDelay");
        String autoStopDelay = "30";
        if (autoStopDelays instanceof List) {
            Object o = ((List<?>) autoStopDelays).get(0);
            autoStopDelay = o.toString();
        }
        Document document = autoStopListener.getOwnerDocument();
        // 清空child
        removeChildren(autoStopListener);
        autoStopListener.appendChild(createStringProp(document, "delay_seconds", autoStopDelay));
    }

    private void processCheckoutAutoStopListener(Element element) {
        Object autoStops = context.getProperty("autoStop");
        String autoStop = "false";
        if (autoStops instanceof List) {
            Object o = ((List<?>) autoStops).get(0);
            autoStop = o.toString();
        }
        if (!BooleanUtils.toBoolean(autoStop)) {
            return;
        }

        Document document = element.getOwnerDocument();
        Node listenerParent = element.getNextSibling();
        while (!(listenerParent instanceof Element)) {
            listenerParent = listenerParent.getNextSibling();
        }

        // add class name
        Element autoStopListener = document.createElement(THREAD_GROUP_AUTO_STOP);
        autoStopListener.setAttribute("guiclass", "io.metersphere.jmeter.reporters.ThreadGroupAutoStopGui");
        autoStopListener.setAttribute("testclass", "io.metersphere.jmeter.reporters.ThreadGroupAutoStop");
        autoStopListener.setAttribute("testname", "MeterSphere - AutoStop Listener");
        autoStopListener.setAttribute("enabled", "true");
        listenerParent.appendChild(autoStopListener);
        listenerParent.appendChild(document.createElement(HASH_TREE_ELEMENT));
    }

    private void processCheckoutSerializeThreadgroups(Element element) {
        Object serializeThreadGroups = context.getProperty("serializeThreadGroups");
        String serializeThreadGroup = "false";
        if (serializeThreadGroups instanceof List) {
            Object o = ((List<?>) serializeThreadGroups).get(0);
            serializeThreadGroup = o.toString();
        }
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (nodeNameEquals(item, BOOL_PROP)) {
                String serializeName = ((Element) item).getAttribute("name");
                if (StringUtils.equals(serializeName, "TestPlan.serialize_threadgroups")) {
                    item.setTextContent(serializeThreadGroup);
                    break;
                }
            }
        }
    }

    private void processArgumentFiles(Element element) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item instanceof Element && isHTTPFileArgs((Element) item)) {
                Node collectionProp = item.getFirstChild();

                while (!(collectionProp instanceof Element)) {
                    collectionProp = collectionProp.getNextSibling();
                }
                NodeList elementProps = collectionProp.getChildNodes();

                for (int j = 0; j < elementProps.getLength(); j++) {
                    Node eleProp = elementProps.item(j);
                    NodeList strProps = eleProp.getChildNodes();
                    for (int k = 0; k < strProps.getLength(); k++) {
                        Node strPop = strProps.item(k);
                        if (strPop instanceof Element) {
                            if (StringUtils.equals(((Element) strPop).getAttribute("name"), "File.path")) {
                                // 截取文件名
                                handleFilename(strPop);
                                break;
                            }
                        }
                    }
                }


            }
        }
    }

    private void handleFilename(Node item) {
        String separator = "/";
        String filename = item.getTextContent();
        if (!StringUtils.contains(filename, "/")) {
            separator = "\\";
        }
        filename = filename.substring(filename.lastIndexOf(separator) + 1);
        item.setTextContent(filename);
    }

    private boolean isHTTPFileArgs(Element ele) {
        return "HTTPFileArgs".equals(ele.getAttribute("elementType"));
    }

    private void processCsvDataSet(Element element) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item instanceof Element && nodeNameEquals(item, STRING_PROP)) {
                String filenameTag = ((Element) item).getAttribute("name");
                if (StringUtils.equals(filenameTag, "filename")) {
                    // 截取文件名
                    handleFilename(item);
                    break;
                }
            }
        }
    }

    private void processResponseAssertion(Element element) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item instanceof Element && nodeNameEquals(item, "collectionProp")) {
                Document document = item.getOwnerDocument();
                Object params = context.getProperty("statusCode");
                if (params instanceof List) {
                    HashSet set = new HashSet((List) params);
                    for (Object p : set) {
                        item.appendChild(createStringProp(document, p.toString(), p.toString()));
                    }
                }
            }
        }
    }

    private void processCheckoutResponseAssertion(Element element) {
        if (context.getProperty("statusCode") == null || JSON.parseArray(context.getProperty("statusCode").toString()).size() == 0) {
            return;
        }
        Document document = element.getOwnerDocument();

        Node hashTree = element.getNextSibling();
        while (!(hashTree instanceof Element)) {
            hashTree = hashTree.getNextSibling();
        }

        NodeList childNodes = hashTree.getChildNodes();
        for (int i = 0, l = childNodes.getLength(); i < l; i++) {
            Node item = childNodes.item(i);
            if (nodeNameEquals(item, RESPONSE_ASSERTION)) {
                // 如果已经存在，不再添加
                removeChildren(item);
                Element collectionProp = document.createElement(COLLECTION_PROP);
                collectionProp.setAttribute("name", "Asserion.test_strings");
                //
                item.appendChild(collectionProp);
                item.appendChild(createStringProp(document, "Assertion.custom_message", ""));
                item.appendChild(createStringProp(document, "Assertion.test_field", "Assertion.response_code"));
                item.appendChild(createBoolProp(document, "Assertion.assume_success", true));
                item.appendChild(createIntProp(document, "Assertion.test_type", 40));
                return;
            }
        }
        /*
        <ResponseAssertion guiclass="AssertionGui" testclass="ResponseAssertion" testname="Response Assertion" enabled="true">
          <collectionProp name="Asserion.test_strings">
            <stringProp name="50548">301</stringProp>
            <stringProp name="49586">200</stringProp>
          </collectionProp>
          <stringProp name="Assertion.custom_message"></stringProp>
          <stringProp name="Assertion.test_field">Assertion.response_code</stringProp>
          <boolProp name="Assertion.assume_success">false</boolProp>
          <intProp name="Assertion.test_type">33</intProp>
        </ResponseAssertion>
         */

        // add class name
        Element responseAssertion = document.createElement(RESPONSE_ASSERTION);
        responseAssertion.setAttribute("guiclass", "AssertionGui");
        responseAssertion.setAttribute("testclass", "ResponseAssertion");
        responseAssertion.setAttribute("testname", "Response Assertion");
        responseAssertion.setAttribute("enabled", "true");
        Element collectionProp = document.createElement(COLLECTION_PROP);
        collectionProp.setAttribute("name", "Asserion.test_strings");
        //
        responseAssertion.appendChild(collectionProp);
        responseAssertion.appendChild(createStringProp(document, "Assertion.custom_message", ""));
        responseAssertion.appendChild(createStringProp(document, "Assertion.test_field", "Assertion.response_code"));
        responseAssertion.appendChild(createBoolProp(document, "Assertion.assume_success", true));
        responseAssertion.appendChild(createIntProp(document, "Assertion.test_type", 40));
        hashTree.appendChild(responseAssertion);
        hashTree.appendChild(document.createElement(HASH_TREE_ELEMENT));
    }

    private void processCheckoutArguments(Element ele) {
        if (context.getProperty("params") == null || JSON.parseArray(context.getProperty("params").toString()).size() == 0) {
            return;
        }
        Node hashTree = ele.getNextSibling();
        while (!(hashTree instanceof Element)) {
            hashTree = hashTree.getNextSibling();
        }

        NodeList childNodes = hashTree.getChildNodes();
        for (int i = 0, size = childNodes.getLength(); i < size; i++) {
            Node item = childNodes.item(i);
            if (nodeNameEquals(item, ARGUMENTS)) {
                // 已经存在不再添加
                return;
            }
        }
        /*
        <Arguments guiclass="ArgumentsPanel" testclass="Arguments" testname="User Defined Variables" enabled="true">
        <collectionProp name="Arguments.arguments">
          <elementProp name="BASE_URL_1" elementType="Argument">
            <stringProp name="Argument.name">BASE_URL_1</stringProp>
            <stringProp name="Argument.value">rddev2.fit2cloud.com</stringProp>
            <stringProp name="Argument.metadata">=</stringProp>
          </elementProp>
        </collectionProp>
      </Arguments>
         */

        Document document = ele.getOwnerDocument();
        Element element = document.createElement(ARGUMENTS);
        element.setAttribute("guiclass", "ArgumentsPanel");
        element.setAttribute("testclass", "Arguments");
        element.setAttribute("testname", "User Defined Variables");
        element.setAttribute("enabled", "true");
        Element collectionProp = document.createElement(COLLECTION_PROP);
        collectionProp.setAttribute("name", "Arguments.arguments");
        element.appendChild(collectionProp);
        hashTree.appendChild(element);
        // 空的 hashTree
        hashTree.appendChild(document.createElement(HASH_TREE_ELEMENT));
    }

    private void processCheckoutDnsCacheManager(Element ele) {
        if (context.getProperty("domains") == null || JSON.parseArray(context.getProperty("domains").toString()).size() == 0) {
            return;
        }
        Node hashTree = ele.getNextSibling();
        while (!(hashTree instanceof Element)) {
            hashTree = hashTree.getNextSibling();
        }

        NodeList childNodes = hashTree.getChildNodes();
        for (int i = 0, size = childNodes.getLength(); i < size; i++) {
            Node item = childNodes.item(i);
            if (nodeNameEquals(item, DNS_CACHE_MANAGER)) {
                // 已经存在不再添加
                return;
            }
        }
         /*
        <DNSCacheManager guiclass="DNSCachePanel" testclass="DNSCacheManager" testname="DNS Cache Manager" enabled="true">
        <collectionProp name="DNSCacheManager.servers"/>
        <collectionProp name="DNSCacheManager.hosts">
          <elementProp name="baiud.com" elementType="StaticHost">
            <stringProp name="StaticHost.Name">baiud.com</stringProp>
            <stringProp name="StaticHost.Address">172.16.10.187</stringProp>
          </elementProp>
        </collectionProp>
        <boolProp name="DNSCacheManager.clearEachIteration">true</boolProp>
        <boolProp name="DNSCacheManager.isCustomResolver">true</boolProp>
      </DNSCacheManager>
         */

        Document document = ele.getOwnerDocument();
        Element element = document.createElement(DNS_CACHE_MANAGER);
        element.setAttribute("guiclass", "DNSCachePanel");
        element.setAttribute("testclass", "DNSCacheManager");
        element.setAttribute("testname", "DNS Cache Manager");
        element.setAttribute("enabled", "true");
        Element collectionProp = document.createElement(COLLECTION_PROP);
        collectionProp.setAttribute("name", "DNSCacheManager.servers");
        element.appendChild(collectionProp);

        Element collectionProp2 = document.createElement(COLLECTION_PROP);
        collectionProp2.setAttribute("name", "DNSCacheManager.hosts");
        element.appendChild(collectionProp2);

        element.appendChild(createBoolProp(document, "DNSCacheManager.clearEachIteration", true));
        element.appendChild(createBoolProp(document, "DNSCacheManager.isCustomResolver", true));

        hashTree.appendChild(element);
        // 空的 hashTree
        hashTree.appendChild(document.createElement(HASH_TREE_ELEMENT));
    }

    private void processCheckoutConfigTestElement(Element ele) {
        if (context.getProperty("timeout") == null || StringUtils.isBlank(context.getProperty("timeout").toString())) {
            return;
        }
        Node hashTree = ele.getNextSibling();
        while (!(hashTree instanceof Element)) {
            hashTree = hashTree.getNextSibling();
        }

        NodeList childNodes = hashTree.getChildNodes();
        for (int i = 0, size = childNodes.getLength(); i < size; i++) {
            Node item = childNodes.item(i);
            if (nodeNameEquals(item, CONFIG_TEST_ELEMENT)) {
                // 已经存在不再添加
                return;
            }
        }
/*
        <ConfigTestElement guiclass="HttpDefaultsGui" testclass="ConfigTestElement" testname="HTTP Request Defaults" enabled="true">
        <elementProp name="HTTPsampler.Arguments" elementType="Arguments" guiclass="HTTPArgumentsPanel" testclass="Arguments" enabled="true">
          <collectionProp name="Arguments.arguments"/>
        </elementProp>
        <stringProp name="HTTPSampler.domain"></stringProp>
        <stringProp name="HTTPSampler.port"></stringProp>
        <stringProp name="HTTPSampler.protocol"></stringProp>
        <stringProp name="HTTPSampler.contentEncoding"></stringProp>
        <stringProp name="HTTPSampler.path"></stringProp>
        <boolProp name="HTTPSampler.image_parser">true</boolProp>
        <boolProp name="HTTPSampler.concurrentDwn">true</boolProp>
        <stringProp name="HTTPSampler.concurrentPool">6</stringProp>
        <stringProp name="HTTPSampler.connect_timeout">30000</stringProp>
        <stringProp name="HTTPSampler.response_timeout"></stringProp>
        </ConfigTestElement>
         */
        Document document = ele.getOwnerDocument();
        Element element = document.createElement(CONFIG_TEST_ELEMENT);
        element.setAttribute("guiclass", "HttpDefaultsGui");
        element.setAttribute("testclass", "ConfigTestElement");
        element.setAttribute("testname", "HTTP Request Defaults");
        element.setAttribute("enabled", "true");
        Element elementProp = document.createElement("elementProp");
        elementProp.setAttribute("name", "HTTPsampler.Arguments");
        elementProp.setAttribute("elementType", "Arguments");
        elementProp.setAttribute("guiclass", "HTTPArgumentsPanel");
        elementProp.setAttribute("testclass", "Arguments");
        elementProp.setAttribute("enabled", "true");
        Element collectionProp = document.createElement(COLLECTION_PROP);
        collectionProp.setAttribute("name", "Arguments.arguments");
        elementProp.appendChild(collectionProp);
        element.appendChild(elementProp);
        element.appendChild(createStringProp(document, "HTTPSampler.domain", ""));
        element.appendChild(createStringProp(document, "HTTPSampler.port", ""));
        element.appendChild(createStringProp(document, "HTTPSampler.protocol", ""));
        element.appendChild(createStringProp(document, "HTTPSampler.contentEncoding", ""));
        element.appendChild(createStringProp(document, "HTTPSampler.path", ""));
        element.appendChild(createStringProp(document, "HTTPSampler.concurrentPool", "6"));
        element.appendChild(createStringProp(document, "HTTPSampler.connect_timeout", "60000"));
        element.appendChild(createStringProp(document, "HTTPSampler.response_timeout", ""));
        hashTree.appendChild(element);
        // 空的 hashTree
        hashTree.appendChild(document.createElement(HASH_TREE_ELEMENT));
    }

    private void processArguments(Element ele) {
        NodeList childNodes = ele.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item instanceof Element && nodeNameEquals(item, "collectionProp")) {
                //
                Document document = item.getOwnerDocument();
                Object params = context.getProperty("params");
                if (params instanceof List) {
                    for (Object p : (List) params) {
                        JSONObject jsonObject = JSON.parseObject(p.toString());
                        if (!jsonObject.getBooleanValue("enable")) {
                            continue;
                        }
                        Element elementProp = document.createElement("elementProp");
                        elementProp.setAttribute("name", jsonObject.getString("name"));
                        elementProp.setAttribute("elementType", "Argument");
                        elementProp.appendChild(createStringProp(document, "Argument.name", jsonObject.getString("name")));
                        // 处理 mock data
                        String value = jsonObject.getString("value");
                        elementProp.appendChild(createStringProp(document, "Argument.value", ScriptEngineUtils.calculate(value)));
                        elementProp.appendChild(createStringProp(document, "Argument.metadata", "="));
                        item.appendChild(elementProp);
                    }
                }
            }
        }

    }

    private void processDnsCacheManager(Element ele) {

        Object domains = context.getProperty("domains");
        if (!(domains instanceof List)) {
            return;
        }
        if (((List) domains).size() == 0) {
            return;
        }
        NodeList childNodes = ele.getChildNodes();
        for (int i = 0, size = childNodes.getLength(); i < size; i++) {
            Node item = childNodes.item(i);
            if (item instanceof Element && nodeNameEquals(item, "collectionProp")
                    && org.apache.commons.lang3.StringUtils.equals(((Element) item).getAttribute("name"), "DNSCacheManager.hosts")) {

                Document document = item.getOwnerDocument();
                for (Object d : (List) domains) {
                    JSONObject jsonObject = JSON.parseObject(d.toString());
                    if (!jsonObject.getBooleanValue("enable")) {
                        continue;
                    }
                    Element elementProp = document.createElement("elementProp");
                    elementProp.setAttribute("name", jsonObject.getString("domain"));
                    elementProp.setAttribute("elementType", "StaticHost");
                    elementProp.appendChild(createStringProp(document, "StaticHost.Name", jsonObject.getString("domain")));
                    elementProp.appendChild(createStringProp(document, "StaticHost.Address", jsonObject.getString("ip")));
                    item.appendChild(elementProp);
                }
            }
            if (item instanceof Element && nodeNameEquals(item, BOOL_PROP)
                    && org.apache.commons.lang3.StringUtils.equals(((Element) item).getAttribute("name"), "DNSCacheManager.isCustomResolver")) {
                item.getFirstChild().setNodeValue("true");
            }
        }

    }

    private void processConfigTestElement(Element ele) {

        NodeList childNodes = ele.getChildNodes();
        for (int i = 0, size = childNodes.getLength(); i < size; i++) {
            Node item = childNodes.item(i);
            if (item instanceof Element && nodeNameEquals(item, STRING_PROP)
                    && StringUtils.equals(((Element) item).getAttribute("name"), "HTTPSampler.connect_timeout")) {
                if (context.getProperty("timeout") != null) {
                    removeChildren(item);
                    item.appendChild(ele.getOwnerDocument().createTextNode(context.getProperty("timeout").toString()));
                }
            }
            // 增加一个response_timeout，避免目标网站不反回结果导致测试不能结束
            if (item instanceof Element && nodeNameEquals(item, STRING_PROP)
                    && StringUtils.equals(((Element) item).getAttribute("name"), "HTTPSampler.response_timeout")) {
                if (context.getProperty("responseTimeout") != null) {
                    removeChildren(item);
                    item.appendChild(ele.getOwnerDocument().createTextNode(context.getProperty("responseTimeout").toString()));
                }
            }
        }
    }

    private Element createBoolProp(Document document, String name, boolean value) {
        Element boolProp = document.createElement(BOOL_PROP);
        boolProp.setAttribute("name", name);
        boolProp.appendChild(document.createTextNode(String.valueOf(value)));
        return boolProp;
    }

    private Element createIntProp(Document document, String name, int value) {
        Element intProp = document.createElement("intProp");
        intProp.setAttribute("name", name);
        intProp.appendChild(document.createTextNode(String.valueOf(value)));
        return intProp;
    }

    private void processBackendListener(Element backendListener) {
        KafkaProperties kafkaProperties = CommonBeanFactory.getBean(KafkaProperties.class);
        Document document = backendListener.getOwnerDocument();
        // 清空child
        removeChildren(backendListener);
        backendListener.appendChild(createStringProp(document, "classname", "io.github.rahulsinghai.jmeter.backendlistener.kafka.KafkaBackendClient"));
        backendListener.appendChild(createStringProp(document, "QUEUE_SIZE", kafkaProperties.getQueueSize()));
        // elementProp
        Element elementProp = document.createElement("elementProp");
        elementProp.setAttribute("name", "arguments");
        elementProp.setAttribute("elementType", "Arguments");
        elementProp.setAttribute("guiclass", "ArgumentsPanel");
        elementProp.setAttribute("testclass", "Arguments");
        elementProp.setAttribute("enabled", "true");
        Element collectionProp = document.createElement("collectionProp");
        collectionProp.setAttribute("name", "Arguments.arguments");
        collectionProp.appendChild(createKafkaProp(document, "kafka.acks", kafkaProperties.getAcks()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.bootstrap.servers", kafkaProperties.getBootstrapServers()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.topic", kafkaProperties.getTopic()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.sample.filter", kafkaProperties.getSampleFilter()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.fields", kafkaProperties.getFields()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.test.mode", kafkaProperties.getTestMode()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.parse.all.req.headers", kafkaProperties.getParseAllReqHeaders()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.parse.all.res.headers", kafkaProperties.getParseAllResHeaders()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.timestamp", kafkaProperties.getTimestamp()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.compression.type", kafkaProperties.getCompressionType()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.ssl.enabled", kafkaProperties.getSsl().getEnabled()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.ssl.key.password", kafkaProperties.getSsl().getKeyPassword()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.ssl.keystore.location", kafkaProperties.getSsl().getKeystoreLocation()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.ssl.keystore.password", kafkaProperties.getSsl().getKeystorePassword()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.ssl.truststore.location", kafkaProperties.getSsl().getTruststoreLocation()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.ssl.truststore.password", kafkaProperties.getSsl().getTruststorePassword()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.ssl.enabled.protocols", kafkaProperties.getSsl().getEnabledProtocols()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.ssl.keystore.type", kafkaProperties.getSsl().getKeystoreType()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.ssl.protocol", kafkaProperties.getSsl().getProtocol()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.ssl.provider", kafkaProperties.getSsl().getProvider()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.ssl.truststore.type", kafkaProperties.getSsl().getTruststoreType()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.batch.size", kafkaProperties.getBatchSize()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.client.id", kafkaProperties.getClientId()));
        collectionProp.appendChild(createKafkaProp(document, "kafka.connections.max.idle.ms", kafkaProperties.getConnectionsMaxIdleMs()));
        // 添加关联关系 test.id test.name test.startTime test.reportId
        collectionProp.appendChild(createKafkaProp(document, "test.id", context.getTestId()));
        collectionProp.appendChild(createKafkaProp(document, "test.name", context.getTestName()));
        collectionProp.appendChild(createKafkaProp(document, "test.reportId", context.getReportId()));

        elementProp.appendChild(collectionProp);
        // set elementProp
        backendListener.appendChild(elementProp);
    }

    private Element createKafkaProp(Document document, String name, String value) {
        Element eleProp = document.createElement("elementProp");
        eleProp.setAttribute("name", name);
        eleProp.setAttribute("elementType", "Argument");
        eleProp.appendChild(createStringProp(document, "Argument.name", name));
        eleProp.appendChild(createStringProp(document, "Argument.value", value));
        eleProp.appendChild(createStringProp(document, "Argument.metadata", "="));
        return eleProp;
    }

    private void processCheckoutBackendListener(Element element) {
        Document document = element.getOwnerDocument();

        Node listenerParent = element.getNextSibling();
        while (!(listenerParent instanceof Element)) {
            listenerParent = listenerParent.getNextSibling();
        }

        // add class name
        Element backendListener = document.createElement(BACKEND_LISTENER);
        backendListener.setAttribute("guiclass", "BackendListenerGui");
        backendListener.setAttribute("testclass", "BackendListener");
        backendListener.setAttribute("testname", "Backend Listener");
        backendListener.setAttribute("enabled", "true");
        listenerParent.appendChild(backendListener);
        listenerParent.appendChild(document.createElement(HASH_TREE_ELEMENT));
    }

    private void processThreadGroup(Element threadGroup) {
        // 检查 threadgroup 后面的hashtree是否为空
        Node hashTree = threadGroup.getNextSibling();
        while (!(hashTree instanceof Element)) {
            hashTree = hashTree.getNextSibling();
        }
        if (!hashTree.hasChildNodes()) {
            MSException.throwException(Translator.get("jmx_content_valid"));
        }
        Object tgTypes = context.getProperty("tgType");
        String tgType = "ThreadGroup";
        if (tgTypes instanceof List) {
            Object o = ((List<?>) tgTypes).get(0);
            ((List<?>) tgTypes).remove(0);
            tgType = o.toString();
        }
        if (StringUtils.equals(tgType, THREAD_GROUP)) {
            processBaseThreadGroup(threadGroup, THREAD_GROUP);
        }
        if (StringUtils.equals(tgType, SETUP_THREAD_GROUP)) {
            processBaseThreadGroup(threadGroup, SETUP_THREAD_GROUP);
        }
        if (StringUtils.equals(tgType, POST_THREAD_GROUP)) {
            processBaseThreadGroup(threadGroup, POST_THREAD_GROUP);
        }
        if (StringUtils.equals(tgType, CONCURRENCY_THREAD_GROUP)) {
            processConcurrencyThreadGroup(threadGroup);
        }
        if (StringUtils.equals(tgType, ULTIMATE_THREAD_GROUP)) {
            processUltimateThreadGroup(threadGroup);
        }

    }

    private void processBaseThreadGroup(Element threadGroup, String tgType) {
        Document document = threadGroup.getOwnerDocument();
        document.renameNode(threadGroup, threadGroup.getNamespaceURI(), tgType);
        threadGroup.setAttribute("guiclass", tgType + "Gui");
        threadGroup.setAttribute("testclass", tgType);
        /*
        <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup" testname="登录" enabled="true">
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Loop Controller" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">1</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">1</stringProp>
        <stringProp name="ThreadGroup.ramp_time">1</stringProp>
        <boolProp name="ThreadGroup.scheduler">false</boolProp>
        <stringProp name="ThreadGroup.duration"></stringProp>
        <stringProp name="ThreadGroup.delay"></stringProp>
        <boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp>
      </ThreadGroup>
         */
        removeChildren(threadGroup);
        // 避免出现配置错位
        Object iterateNum = context.getProperty("iterateNum");
        if (iterateNum instanceof List) {
            ((List<?>) iterateNum).remove(0);
        }
        Object iterateRampUpTimes = context.getProperty("iterateRampUpTime");
        if (iterateRampUpTimes instanceof List) {
            ((List<?>) iterateRampUpTimes).remove(0);
        }
        Object steps = context.getProperty("Steps");
        if (steps instanceof List) {
            ((List<?>) steps).remove(0);
        }
        Object holds = context.getProperty("Hold");
        if (holds instanceof List) {
            ((List<?>) holds).remove(0);
        }
        Object targetLevels = context.getProperty("TargetLevel");
        String threads = "10";
        if (targetLevels instanceof List) {
            Object o = ((List<?>) targetLevels).get(0);
            ((List<?>) targetLevels).remove(0);
            threads = o.toString();
        }
        Object rampUps = context.getProperty("RampUp");
        String rampUp = "1";
        if (rampUps instanceof List) {
            Object o = ((List<?>) rampUps).get(0);
            ((List<?>) rampUps).remove(0);
            rampUp = o.toString();
        }
        Object durations = context.getProperty("duration");
        String duration = "2";
        if (durations instanceof List) {
            Object o = ((List<?>) durations).get(0);
            ((List<?>) durations).remove(0);
            duration = o.toString();
        }
        Object units = context.getProperty("unit");
        if (units instanceof List) {
            Object o = ((List<?>) units).get(0);
            ((List<?>) units).remove(0);
        }
        Object deleteds = context.getProperty("deleted");
        String deleted = "false";
        if (deleteds instanceof List) {
            Object o = ((List<?>) deleteds).get(0);
            ((List<?>) deleteds).remove(0);
            deleted = o.toString();
        }
        Object enableds = context.getProperty("enabled");
        String enabled = "true";
        if (enableds instanceof List) {
            Object o = ((List<?>) enableds).get(0);
            ((List<?>) enableds).remove(0);
            enabled = o.toString();
        }

        threadGroup.setAttribute("enabled", enabled);
        if (BooleanUtils.toBoolean(deleted)) {
            threadGroup.setAttribute("enabled", "false");
        }
        Element elementProp = document.createElement("elementProp");
        elementProp.setAttribute("name", "ThreadGroup.main_controller");
        elementProp.setAttribute("elementType", "LoopController");
        elementProp.setAttribute("guiclass", "LoopControlPanel");
        elementProp.setAttribute("testclass", "LoopController");
        elementProp.setAttribute("testname", "Loop Controller");
        elementProp.appendChild(createBoolProp(document, "LoopController.continue_forever", false));
        elementProp.appendChild(createStringProp(document, "LoopController.loops", "-1"));
        threadGroup.appendChild(elementProp);
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.on_sample_error", "continue"));
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.num_threads", threads));
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.ramp_time", rampUp));
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.duration", duration));
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.delay", "0"));
        threadGroup.appendChild(createBoolProp(document, "ThreadGroup.scheduler", true));
        threadGroup.appendChild(createBoolProp(document, "ThreadGroup.same_user_on_next_iteration", true));
    }

    private void processConcurrencyThreadGroup(Element threadGroup) {
        // 重命名 tagName
        Document document = threadGroup.getOwnerDocument();
        document.renameNode(threadGroup, threadGroup.getNamespaceURI(), CONCURRENCY_THREAD_GROUP);
        threadGroup.setAttribute("guiclass", CONCURRENCY_THREAD_GROUP + "Gui");
        threadGroup.setAttribute("testclass", CONCURRENCY_THREAD_GROUP);
        /*
        <elementProp name="ThreadGroup.main_controller" elementType="com.blazemeter.jmeter.control.VirtualUserController"/>
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <stringProp name="TargetLevel">2</stringProp>
        <stringProp name="RampUp">12</stringProp>
        <stringProp name="Steps">2</stringProp>
        <stringProp name="Hold">3</stringProp>
        <stringProp name="LogFilename"></stringProp>
        <stringProp name="Iterations">1</stringProp>
        <stringProp name="Unit">S</stringProp>
         */
        removeChildren(threadGroup);
        // 避免出现配置错位
        Object iterateNum = context.getProperty("iterateNum");
        if (iterateNum instanceof List) {
            ((List<?>) iterateNum).remove(0);
        }
        Object iterateRampUpTimes = context.getProperty("iterateRampUpTime");
        if (iterateRampUpTimes instanceof List) {
            ((List<?>) iterateRampUpTimes).remove(0);
        }
        Object durations = context.getProperty("duration");
        if (durations instanceof List) {
            ((List<?>) durations).remove(0);
        }
        // elementProp
        Object targetLevels = context.getProperty("TargetLevel");
        String threads = "10";
        if (targetLevels instanceof List) {
            Object o = ((List<?>) targetLevels).get(0);
            ((List<?>) targetLevels).remove(0);
            threads = o.toString();
        }
        Object rampUps = context.getProperty("RampUp");
        String rampUp = "1";
        if (rampUps instanceof List) {
            Object o = ((List<?>) rampUps).get(0);
            ((List<?>) rampUps).remove(0);
            rampUp = o.toString();
        }
        Object steps = context.getProperty("Steps");
        String step = "2";
        if (steps instanceof List) {
            Object o = ((List<?>) steps).get(0);
            ((List<?>) steps).remove(0);
            step = o.toString();
        }
        Object holds = context.getProperty("Hold");
        String hold = "2";
        if (holds instanceof List) {
            Object o = ((List<?>) holds).get(0);
            ((List<?>) holds).remove(0);
            hold = o.toString();
        }
        Object units = context.getProperty("unit");
        if (units instanceof List) {
            Object o = ((List<?>) units).get(0);
            ((List<?>) units).remove(0);
        }
        Object deleteds = context.getProperty("deleted");
        String deleted = "false";
        if (deleteds instanceof List) {
            Object o = ((List<?>) deleteds).get(0);
            ((List<?>) deleteds).remove(0);
            deleted = o.toString();
        }
        Object enableds = context.getProperty("enabled");
        String enabled = "true";
        if (enableds instanceof List) {
            Object o = ((List<?>) enableds).get(0);
            ((List<?>) enableds).remove(0);
            enabled = o.toString();
        }

        threadGroup.setAttribute("enabled", enabled);
        if (BooleanUtils.toBoolean(deleted)) {
            threadGroup.setAttribute("enabled", "false");
        }
        Element elementProp = document.createElement("elementProp");
        elementProp.setAttribute("name", "ThreadGroup.main_controller");
        elementProp.setAttribute("elementType", "com.blazemeter.jmeter.control.VirtualUserController");
        threadGroup.appendChild(elementProp);
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.on_sample_error", "continue"));
        threadGroup.appendChild(createStringProp(document, "TargetLevel", threads));
        threadGroup.appendChild(createStringProp(document, "RampUp", rampUp));
        threadGroup.appendChild(createStringProp(document, "Steps", step));
        threadGroup.appendChild(createStringProp(document, "Hold", hold));
        threadGroup.appendChild(createStringProp(document, "LogFilename", ""));
        // bzm - Concurrency Thread Group "Thread Iterations Limit:" 设置为空
//        threadGroup.appendChild(createStringProp(document, "Iterations", "1"));
        threadGroup.appendChild(createStringProp(document, "Unit", "S"));
    }

    /**
     * 自定义线程组模型
     * @param threadGroup
     */
    private void processUltimateThreadGroup(Element threadGroup) {
        Object models = context.getProperty("model");
        String model = "shock";
        if (models instanceof List) {
            Object o = ((List<?>) models).get(0);
            ((List<?>) models).remove(0);
            model = o.toString();
        }
        if (StringUtils.equals(model, SHOCK_MODEL)) {
            processShockModelThreadGroup(threadGroup);
        } else if (StringUtils.equals(model, WAVE_SURGE_MODEL)) {
            processWaveSurgeModelThreadGroup(threadGroup);
        } else if (StringUtils.equals(model, DIP)) {
            processDipModelThreadGroup(threadGroup);
        }
    }

    /**
     * 震荡模型
     * @param threadGroup
     */
    private void processShockModelThreadGroup(Element threadGroup) {
        // 重命名 tagName
        Document document = threadGroup.getOwnerDocument();
        document.renameNode(threadGroup, threadGroup.getNamespaceURI(), ULTIMATE_THREAD_GROUP);
        threadGroup.setAttribute("guiclass", ULTIMATE_THREAD_GROUP + "Gui");
        threadGroup.setAttribute("testclass", ULTIMATE_THREAD_GROUP);
        removeChildren(threadGroup);
        /**
         * <kg.apc.jmeter.threads.UltimateThreadGroup guiclass="kg.apc.jmeter.threads.UltimateThreadGroupGui" testclass="kg.apc.jmeter.threads.UltimateThreadGroup" testname="jp@gc - Ultimate Thread Group" enabled="true">
         *       <collectionProp name="ultimatethreadgroupdata">
         *         <collectionProp name="13241234">
         *           <stringProp name="1111">10</stringProp>
         *           <stringProp name="0">0</stringProp>
         *           <stringProp name="1111">10</stringProp>
         *           <stringProp name="1629">30</stringProp>
         *           <stringProp name="10">10</stringProp>
         *         </collectionProp>
         *       </collectionProp>
         */
        //持续时间
        Object durations = context.getProperty("duration");
        Integer duration = 10;
        if (durations instanceof List) {
            Object o = ((List<?>) durations).get(0);
            ((List<?>) durations).remove(0);
            duration = Integer.parseInt(o.toString());
        }
        // 并发用户数
        Object targetLevels = context.getProperty("TargetLevel");
        String threads = "10";
        if (targetLevels instanceof List) {
            Object o = ((List<?>) targetLevels).get(0);
            ((List<?>) targetLevels).remove(0);
            threads = o.toString();
        }
        // 震荡时间
        Object shockCounts = context.getProperty("shockCount");
        Integer shockCount = 1;
        if (targetLevels instanceof List) {
            Object o = ((List<?>) shockCounts).get(0);
            ((List<?>) shockCounts).remove(0);
            shockCount = Integer.parseInt(o.toString());
        }

        Integer startTime = duration/shockCount/2;
        Integer endTime = startTime;
        Integer delayTime = 0;

        Element elementProp = document.createElement("collectionProp");
        elementProp.setAttribute("name", "ultimatethreadgroupdata");
        for (int i = 0; i < shockCount; i++) {
            Element element = document.createElement("collectionProp");
            element.setAttribute("name",randomNum(9));
            element.appendChild(createStringProp(document, randomNum(4),threads));
            element.appendChild(createStringProp(document,randomNum(4),String.valueOf(delayTime)));
            element.appendChild(createStringProp(document,randomNum(4), String.valueOf(startTime)));
            element.appendChild(createStringProp(document,randomNum(4),"0"));
            element.appendChild(createStringProp(document,randomNum(4), String.valueOf(endTime)));
            elementProp.appendChild(element);
            delayTime += duration/shockCount;
        }
        threadGroup.appendChild(elementProp);
        Element element = document.createElement("elementProp");
        element.setAttribute("name", "ThreadGroup.main_controller");
        element.setAttribute("elementType", "LoopController");
        element.setAttribute("guiclass", "LoopControlPanel");
        element.setAttribute("testclass", "LoopController");
        element.setAttribute("testname", "Loop Controller");
        element.appendChild(createBoolProp(document, "LoopController.continue_forever", false));
        element.appendChild(createStringProp(document, "LoopController.loops", "-1"));
        threadGroup.appendChild(element);
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.on_sample_error", "continue"));
    }

    /**
     * 浪涌模型
     * @param threadGroup
     */
    private void processWaveSurgeModelThreadGroup(Element threadGroup) {
        // 重命名 tagName
        Document document = threadGroup.getOwnerDocument();
        document.renameNode(threadGroup, threadGroup.getNamespaceURI(), ULTIMATE_THREAD_GROUP);
        threadGroup.setAttribute("guiclass", ULTIMATE_THREAD_GROUP + "Gui");
        threadGroup.setAttribute("testclass", ULTIMATE_THREAD_GROUP);
        removeChildren(threadGroup);
        //持续时间
        Object durations = context.getProperty("duration");
        Integer duration = 10;
        if (durations instanceof List) {
            Object o = ((List<?>) durations).get(0);
            ((List<?>) durations).remove(0);
            duration = Integer.parseInt(o.toString());
        }
        // 最大并发用户数
        Object targetLevels = context.getProperty("TargetLevel");
        String maxThreads = "10";
        if (targetLevels instanceof List) {
            Object o = ((List<?>) targetLevels).get(0);
            ((List<?>) targetLevels).remove(0);
            maxThreads = o.toString();
        }
        Object minThreads = context.getProperty("minThreadNumber");
        // 最小并发用户数
        String minThread = "1";
        if (minThreads instanceof List) {
            Object o = ((List<?>) minThreads).get(0);
            ((List<?>) minThreads).remove(0);
            minThread = o.toString();
        }
        // 波涌次数
        Object shockCounts = context.getProperty("waveCount");
        Integer shockCount = 1;
        if (targetLevels instanceof List) {
            Object o = ((List<?>) shockCounts).get(0);
            ((List<?>) shockCounts).remove(0);
            shockCount = Integer.parseInt(o.toString());
        }
        // 峰值持续时间(s)
        Object peakDurations = context.getProperty("peakDuration");
        Integer peakDuration = 10;
        if (peakDurations instanceof List) {
            Object o = ((List<?>) peakDurations).get(0);
            ((List<?>) peakDurations).remove(0);
            peakDuration = Integer.parseInt(o.toString());
        }
        /**最小并发数的持续时间*/
        Integer minDuration = (duration - peakDuration * shockCount) / shockCount;
        /**延迟时间*/
        Integer delayTime = 0;
        Element elementProp = document.createElement("collectionProp");
        elementProp.setAttribute("name", "ultimatethreadgroupdata");
        //todo 明天来了开始测试一下这个算法数据的正确性
        for (int i = 1; i <= shockCount ; i++) {
            Element minElement = document.createElement("collectionProp");
            minElement.setAttribute("name",randomNum(9));
            minElement.appendChild(createStringProp(document, randomNum(4), minThread));
            minElement.appendChild(createStringProp(document, randomNum(4), String.valueOf(delayTime)));
            minElement.appendChild(createStringProp(document, randomNum(4),"0"));
            minElement.appendChild(createStringProp(document, randomNum(4),String.valueOf(minDuration)));
            minElement.appendChild(createStringProp(document, randomNum(4),"0"));
            delayTime += minDuration;
            elementProp.appendChild(minElement);

            Element maxElement = document.createElement("collectionProp");
            maxElement.setAttribute("name",randomNum(9));
            maxElement.appendChild(createStringProp(document, randomNum(4), maxThreads));
            maxElement.appendChild(createStringProp(document, randomNum(4), String.valueOf(delayTime)));
            maxElement.appendChild(createStringProp(document, randomNum(4),"0"));
            maxElement.appendChild(createStringProp(document, randomNum(4),String.valueOf(peakDuration)));
            maxElement.appendChild(createStringProp(document, randomNum(4),"0"));
            delayTime += peakDuration;
            elementProp.appendChild(maxElement);
        }
        threadGroup.appendChild(elementProp);
        Element element = document.createElement("elementProp");
        element.setAttribute("name", "ThreadGroup.main_controller");
        element.setAttribute("elementType", "LoopController");
        element.setAttribute("guiclass", "LoopControlPanel");
        element.setAttribute("testclass", "LoopController");
        element.setAttribute("testname", "Loop Controller");
        element.appendChild(createBoolProp(document, "LoopController.continue_forever", false));
        element.appendChild(createStringProp(document, "LoopController.loops", "-1"));
        threadGroup.appendChild(element);
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.on_sample_error", "continue"));
    }

    /**
     * 探底模型
     * @param threadGroup
     */
    private void processDipModelThreadGroup(Element threadGroup) {
        /** 探底模型为线性增压方式，所以jmeter线程组选择 ThreadGroup*/
        Document document = threadGroup.getOwnerDocument();
        document.renameNode(threadGroup, threadGroup.getNamespaceURI(), THREAD_GROUP);
        threadGroup.setAttribute("guiclass", THREAD_GROUP + "Gui");
        threadGroup.setAttribute("testclass", THREAD_GROUP);
        threadGroup.setAttribute("enabled", "true");

        removeChildren(threadGroup);
        Object iterateRampUpTimes = context.getProperty("iterateRampUpTime");
        if (iterateRampUpTimes instanceof List) {
            ((List<?>) iterateRampUpTimes).remove(0);
        }
        Object targetLevels = context.getProperty("TargetLevel");
        String threads = "10";
        if (targetLevels instanceof List) {
            Object o = ((List<?>) targetLevels).get(0);
            ((List<?>) targetLevels).remove(0);
            threads = o.toString();
        }
        Object rampUps = context.getProperty("RampUp");
        String rampUp = "1";
        if (rampUps instanceof List) {
            Object o = ((List<?>) rampUps).get(0);
            ((List<?>) rampUps).remove(0);
            rampUp = o.toString();
        }
        Object durations = context.getProperty("duration");
        String duration = "2";
        if (durations instanceof List) {
            Object o = ((List<?>) durations).get(0);
            ((List<?>) durations).remove(0);
            duration = o.toString();
        }

        Object responseTimes = context.getProperty("responseTime");
        String responseTime = "1000";
        if (responseTimes instanceof List) {
            Object o = ((List<?>) responseTimes).get(0);
            ((List<?>) responseTimes).remove(0);
            responseTime = o.toString();
        }

        Object errorRates = context.getProperty("errorRate");
        String errorRate = "1";
        if (errorRates instanceof List) {
            Object o = ((List<?>) errorRates).get(0);
            ((List<?>) errorRates).remove(0);
            errorRate = o.toString();
        }

        Element elementProp = document.createElement("elementProp");
        elementProp.setAttribute("name", "ThreadGroup.main_controller");
        elementProp.setAttribute("elementType", "LoopController");
        elementProp.setAttribute("guiclass", "LoopControlPanel");
        elementProp.setAttribute("testclass", "LoopController");
        elementProp.setAttribute("testname", "Loop Controller");
        elementProp.appendChild(createBoolProp(document, "LoopController.continue_forever", false));
        elementProp.appendChild(createStringProp(document, "LoopController.loops", "-1"));
        threadGroup.appendChild(elementProp);
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.on_sample_error", "continue"));
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.num_threads", threads));
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.ramp_time", rampUp));
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.duration", duration));
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.delay", "0"));
        threadGroup.appendChild(createBoolProp(document, "ThreadGroup.scheduler", true));
        threadGroup.appendChild(createBoolProp(document, "ThreadGroup.same_user_on_next_iteration", true));

        Node listenerParent = threadGroup.getNextSibling();
        while (!(listenerParent instanceof Element)) {
            listenerParent = listenerParent.getNextSibling();
        }

        // add class name
        Element autoStopListener = document.createElement(AUTO_STOP);
        autoStopListener.setAttribute("guiclass", AUTO_STOP + "Gui");
        autoStopListener.setAttribute("testclass", AUTO_STOP);
        autoStopListener.setAttribute("testname", "jp@gc - AutoStop Listener");
        autoStopListener.setAttribute("enabled", "true");
        /**
         * 响应时间连续6s大于期望值，期望被测系统的请求响应时间小于等于输入值，请求成功率大于等于输入值，摸高分析规则为任意一个指标首次出现连续6秒值不符合预期
         */
        autoStopListener.appendChild(createStringProp(document, "avg_response_time",responseTime));
        autoStopListener.appendChild(createStringProp(document, "avg_response_time_length","6"));
        autoStopListener.appendChild(createStringProp(document, "error_rate",errorRate));
        autoStopListener.appendChild(createStringProp(document, "error_rate_length", "6"));
        autoStopListener.appendChild(createStringProp(document, "avg_response_latency","0"));
        autoStopListener.appendChild(createStringProp(document, "avg_response_latency_length","0"));
        listenerParent.appendChild(autoStopListener);
        listenerParent.appendChild(document.createElement(HASH_TREE_ELEMENT));
    }

    private String randomNum(int factor) {
       return String.valueOf(new Double((Math.random() + 1) * Math.pow(10, factor -1)).longValue());
    }

    private void processIterationThreadGroup(Element threadGroup) {
        Document document = threadGroup.getOwnerDocument();
        // 检查 threadgroup 后面的hashtree是否为空
        Node hashTree = threadGroup.getNextSibling();
        while (!(hashTree instanceof Element)) {
            hashTree = hashTree.getNextSibling();
        }
        if (!hashTree.hasChildNodes()) {
            MSException.throwException(Translator.get("jmx_content_valid"));
        }
        Object tgTypes = context.getProperty("tgType");
        String tgType = "ThreadGroup";
        if (tgTypes instanceof List) {
            Object o = ((List<?>) tgTypes).get(0);
            ((List<?>) tgTypes).remove(0);
            tgType = o.toString();
        }
        if (StringUtils.equals(tgType, THREAD_GROUP)) {
            document.renameNode(threadGroup, threadGroup.getNamespaceURI(), THREAD_GROUP);
            threadGroup.setAttribute("guiclass", THREAD_GROUP + "Gui");
            threadGroup.setAttribute("testclass", THREAD_GROUP);
        }
        if (StringUtils.equals(tgType, SETUP_THREAD_GROUP)) {
            document.renameNode(threadGroup, threadGroup.getNamespaceURI(), SETUP_THREAD_GROUP);
            threadGroup.setAttribute("guiclass", SETUP_THREAD_GROUP + "Gui");
            threadGroup.setAttribute("testclass", SETUP_THREAD_GROUP);
        }
        if (StringUtils.equals(tgType, POST_THREAD_GROUP)) {
            document.renameNode(threadGroup, threadGroup.getNamespaceURI(), POST_THREAD_GROUP);
            threadGroup.setAttribute("guiclass", POST_THREAD_GROUP + "Gui");
            threadGroup.setAttribute("testclass", POST_THREAD_GROUP);
        }
        removeChildren(threadGroup);

        // 选择按照迭代次数处理线程组
        /*
        <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
        <elementProp name="ThreadGroup.main_controller" elementType="LoopController" guiclass="LoopControlPanel" testclass="LoopController" testname="Loop Controller" enabled="true">
          <boolProp name="LoopController.continue_forever">false</boolProp>
          <stringProp name="LoopController.loops">1</stringProp>
        </elementProp>
        <stringProp name="ThreadGroup.num_threads">100</stringProp>
        <stringProp name="ThreadGroup.ramp_time">5</stringProp>
        <boolProp name="ThreadGroup.scheduler">true</boolProp>
        <stringProp name="ThreadGroup.duration">10</stringProp>
        <stringProp name="ThreadGroup.delay"></stringProp>
        <boolProp name="ThreadGroup.same_user_on_next_iteration">true</boolProp>
         */
        // elementProp
        Object durations = context.getProperty("duration");
        if (durations instanceof List) {
            ((List<?>) durations).remove(0);
        }
        Object units = context.getProperty("unit");
        if (units instanceof List) {
            ((List<?>) units).remove(0);
        }
        Object holds = context.getProperty("Hold");
        if (holds instanceof List) {
            ((List<?>) holds).remove(0);
        }
        Object steps = context.getProperty("Steps");
        if (steps instanceof List) {
            ((List<?>) steps).remove(0);
        }
        Object arampUps = context.getProperty("RampUp");
        if (arampUps instanceof List) {
            ((List<?>) arampUps).remove(0);
        }
        Object targetLevels = context.getProperty("TargetLevel");
        String threads = "10";
        if (targetLevels instanceof List) {
            Object o = ((List<?>) targetLevels).get(0);
            ((List<?>) targetLevels).remove(0);
            threads = o.toString();
        }
        Object iterateNum = context.getProperty("iterateNum");
        String loops = "1";
        if (iterateNum instanceof List) {
            Object o = ((List<?>) iterateNum).get(0);
            ((List<?>) iterateNum).remove(0);
            loops = o.toString();
        }
        Object rampUps = context.getProperty("iterateRampUpTime");
        String rampUp = "10";
        if (rampUps instanceof List) {
            Object o = ((List<?>) rampUps).get(0);
            ((List<?>) rampUps).remove(0);
            rampUp = o.toString();
        }
        Object deleteds = context.getProperty("deleted");
        String deleted = "false";
        if (deleteds instanceof List) {
            Object o = ((List<?>) deleteds).get(0);
            ((List<?>) deleteds).remove(0);
            deleted = o.toString();
        }
        Object enableds = context.getProperty("enabled");
        String enabled = "true";
        if (enableds instanceof List) {
            Object o = ((List<?>) enableds).get(0);
            ((List<?>) enableds).remove(0);
            enabled = o.toString();
        }
        threadGroup.setAttribute("enabled", enabled);
        if (BooleanUtils.toBoolean(deleted)) {
            threadGroup.setAttribute("enabled", "false");
        }
        Element elementProp = document.createElement("elementProp");
        elementProp.setAttribute("name", "ThreadGroup.main_controller");
        elementProp.setAttribute("elementType", "LoopController");
        elementProp.setAttribute("guiclass", "LoopControlPanel");
        elementProp.setAttribute("testclass", "LoopController");
        elementProp.setAttribute("testname", "Loop Controller");
        elementProp.setAttribute("enabled", "true");
        elementProp.appendChild(createBoolProp(document, "LoopController.continue_forever", false));
        elementProp.appendChild(createStringProp(document, "LoopController.loops", loops));
        threadGroup.appendChild(elementProp);

        threadGroup.appendChild(createStringProp(document, "ThreadGroup.on_sample_error", "continue"));
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.num_threads", threads));
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.ramp_time", rampUp));
        threadGroup.appendChild(createBoolProp(document, "ThreadGroup.scheduler", false)); // 不指定执行时间
        threadGroup.appendChild(createStringProp(document, "Hold", "1"));
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.duration", "10"));
        threadGroup.appendChild(createStringProp(document, "ThreadGroup.delay", ""));
        threadGroup.appendChild(createBoolProp(document, "ThreadGroup.same_user_on_next_iteration", true));
    }

    private void processCheckoutTimer(Element element) {
        /*
        <kg.apc.jmeter.timers.VariableThroughputTimer guiclass="kg.apc.jmeter.timers.VariableThroughputTimerGui" testclass="kg.apc.jmeter.timers.VariableThroughputTimer" testname="jp@gc - Throughput Shaping Timer" enabled="true">
          <collectionProp name="load_profile">
            <collectionProp name="140409499">
              <stringProp name="49">1</stringProp>
              <stringProp name="49">1</stringProp>
              <stringProp name="1570">13</stringProp>
            </collectionProp>
          </collectionProp>
        </kg.apc.jmeter.timers.VariableThroughputTimer>
         */
        if (context.getProperty("rpsLimitEnable") == null) {
            return;
        }
        Object rpsLimitEnables = context.getProperty("rpsLimitEnable");
        if (rpsLimitEnables instanceof List) {
            Object o = ((List<?>) rpsLimitEnables).get(0);
            ((List<?>) rpsLimitEnables).remove(0);
            if (o == null || "false".equals(o.toString())) {
                return;
            }
        }

        Document document = element.getOwnerDocument();


        Node timerParent = element.getNextSibling();
        while (!(timerParent instanceof Element)) {
            timerParent = timerParent.getNextSibling();
        }

        NodeList childNodes = timerParent.getChildNodes();
        for (int i = 0, l = childNodes.getLength(); i < l; i++) {
            Node item = childNodes.item(i);
            if (nodeNameEquals(item, VARIABLE_THROUGHPUT_TIMER)) {
                // 如果已经存在，不再添加
                return;
            }
        }

        Element timer = document.createElement(VARIABLE_THROUGHPUT_TIMER);
        timer.setAttribute("guiclass", VARIABLE_THROUGHPUT_TIMER + "Gui");
        timer.setAttribute("testclass", VARIABLE_THROUGHPUT_TIMER);
        timer.setAttribute("testname", "jp@gc - Throughput Shaping Timer");
        timer.setAttribute("enabled", "true");

        Element collectionProp = document.createElement("collectionProp");
        collectionProp.setAttribute("name", "load_profile");
        Element childCollectionProp = document.createElement("collectionProp");
        childCollectionProp.setAttribute("name", "140409499");
        childCollectionProp.appendChild(createStringProp(document, "49", "1"));
        childCollectionProp.appendChild(createStringProp(document, "49", "1"));
        childCollectionProp.appendChild(createStringProp(document, "1570", "10"));
        collectionProp.appendChild(childCollectionProp);
        timer.appendChild(collectionProp);
        timerParent.appendChild(timer);
        // 添加一个空的hashTree
        timerParent.appendChild(document.createElement(HASH_TREE_ELEMENT));
    }

    private Element createStringProp(Document document, String name, String value) {
        Element element = document.createElement(STRING_PROP);
        element.setAttribute("name", name);
        element.appendChild(document.createTextNode(value));
        return element;
    }

    private void processThreadGroupName(Element threadGroup) {
        String testname = threadGroup.getAttribute("testname");
        threadGroup.setAttribute("testname", testname + "-" + context.getResourceIndex());
    }

    private void processVariableThroughputTimer(Element variableThroughputTimer) {
        // 设置rps时长
        Integer duration = Integer.MAX_VALUE;
        Object rpsLimits = context.getProperty("rpsLimit");
        String rpsLimit;
        if (rpsLimits instanceof List) {
            Object o = ((List<?>) rpsLimits).get(0);
            ((List<?>) rpsLimits).remove(0);
            rpsLimit = o.toString();
        } else {
            rpsLimit = rpsLimits.toString();
        }
        if (variableThroughputTimer.getChildNodes().getLength() > 0) {
            final NodeList childNodes = variableThroughputTimer.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element) {
                    Element ele = (Element) node;
                    if (invalid(ele)) {
                        continue;
                    }

                    // TODO kg.apc.jmeter.timers.VariableThroughputTimer的stringProp的name属性是动态的
                    if (nodeNameEquals(ele, COLLECTION_PROP)) {
                        NodeList eleChildNodes = ele.getChildNodes();
                        for (int j = 0; j < eleChildNodes.getLength(); j++) {
                            Node item = eleChildNodes.item(j);
                            if (nodeNameEquals(item, COLLECTION_PROP)) {
                                int stringPropCount = 0;
                                NodeList itemChildNodes = item.getChildNodes();
                                for (int k = 0; k < itemChildNodes.getLength(); k++) {
                                    Node prop = itemChildNodes.item(k);
                                    if (nodeNameEquals(prop, STRING_PROP)) {
                                        if (stringPropCount < 2) {
                                            stringPropCount++;
                                        } else {
                                            stringPropCount = 0;
                                            prop.getFirstChild().setNodeValue(String.valueOf(duration));
                                            continue;
                                        }
                                        prop.getFirstChild().setNodeValue(rpsLimit);
                                    }
                                }
                            }

                        }
                    }

                }
            }
        }
    }

    private void parseStringProp(Element stringProp) {
        Object threadParams = context.getProperty(stringProp.getAttribute("name"));
        if (stringProp.getChildNodes().getLength() > 0 && threadParams != null) {
            if (threadParams instanceof List) {
                Object o = ((List<?>) threadParams).get(0);
                ((List<?>) threadParams).remove(0);
                stringProp.getFirstChild().setNodeValue(o.toString());
            } else {
                stringProp.getFirstChild().setNodeValue(threadParams.toString());
            }
        }
    }

    private boolean nodeNameEquals(Node node, String desiredName) {
        return desiredName.equals(node.getNodeName()) || desiredName.equals(node.getLocalName());
    }

    private boolean invalid(Element ele) {
        return !StringUtils.isBlank(ele.getAttribute("enabled")) && !Boolean.parseBoolean(ele.getAttribute("enabled"));
    }

    private void removeChildren(Node node) {
        while (node.hasChildNodes()) {
            node.removeChild(node.getFirstChild());
        }
    }
}