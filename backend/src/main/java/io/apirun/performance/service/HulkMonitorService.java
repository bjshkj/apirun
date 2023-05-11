package io.apirun.performance.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import io.apirun.base.domain.*;
import io.apirun.base.mapper.LoadTestMonitorDataMapper;
import io.apirun.base.mapper.LoadTestReportMapper;
import io.apirun.base.mapper.ext.ExtLoadTestReportMapper;
import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.LogUtil;
import io.apirun.dto.NodeDTO;
import io.apirun.performance.base.ReportTimeInfo;
import io.apirun.performance.config.HulkConfig;
import io.apirun.performance.controller.request.HulkRequest;
import io.apirun.performance.controller.request.MetricDataRequest;
import io.apirun.performance.dto.Monitor;
import io.apirun.performance.dto.MonitorTarget;
import io.apirun.performance.dto.MonitorTargetType;
import io.apirun.service.TestResourceService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
@Transactional(rollbackFor = Exception.class)
public class HulkMonitorService {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private LoadTestReportMapper loadTestReportMapper;
    @Resource
    private PerformanceReportService performanceReportService;
    @Resource
    private ExtLoadTestReportMapper extLoadTestReportMapper;
    @Resource
    private TestResourceService testResourceService;
    @Resource
    private LoadTestMonitorDataMapper loadTestMonitorDataMapper;
    @Resource
    private LoadTestMonitorDataService loadTestMonitorDataService;

    private static final String URL = "";

    public List<LoadTestMonitorDataWithBLOBs> queryMetric(String reportId) {
        LoadTestReportWithBLOBs report = loadTestReportMapper.selectByPrimaryKey(reportId);
        String poolId = report.getTestResourcePoolId();
        List<TestResource> resourceList = testResourceService.getTestResourceList(poolId);
        //监控列表
        List<MonitorTarget> monitorTargets = Lists.newLinkedList();
        // 默认监控资源池下的节点,施压机器
        if (CollectionUtils.isNotEmpty(resourceList)) {
            resourceList.forEach(resource -> {
                NodeDTO dto = JSON.parseObject(resource.getConfiguration(), NodeDTO.class);
                MonitorTarget target = new MonitorTarget();
                target.setId(dto.getIp());
                target.setTargetType(MonitorTargetType.PRESSURE_SERVER);
                target.setLabel(MonitorTargetType.PRESSURE_SERVER.getLabel());
                monitorTargets.add(target);
            });
        }

        String advancedConfiguration = report.getAdvancedConfiguration();
        JSONObject jsonObject = JSON.parseObject(advancedConfiguration);
        JSONArray monitorParams = jsonObject.getJSONArray("monitorParams");
        if (monitorParams == null) {
            return Lists.newLinkedList();
        }
        List<MetricDataRequest> list = new ArrayList<>();
        // 加入高级设置中的监控配置
        for (int i = 0; i < monitorParams.size(); i++) {
            Monitor monitor = monitorParams.getObject(i, Monitor.class);
            MonitorTarget target = new MonitorTarget();
            target.setId(monitor.getConfig());
            try {
                MonitorTargetType targetType = MonitorTargetType.valueOf(monitor.getTargetType());
                target.setTargetType(targetType);
                target.setLabel(targetType.getLabel());
            }catch (IllegalArgumentException e){
                MSException.throwException("参数不合法");
            }
            monitorTargets.add(target);
        }

        ReportTimeInfo reportTimeInfo = performanceReportService.getReportTimeInfo(reportId);
        HulkRequest hulkRequest = new HulkRequest();
        hulkRequest.setReportId(reportId);
        hulkRequest.setMonitorTargets(monitorTargets);
        try {
            hulkRequest.setStartTime(reportTimeInfo.getStartTime());
            hulkRequest.setEndTime(reportTimeInfo.getEndTime());
        } catch (Exception e) {
            //LogUtil.error(e.getMessage(), e);
        }
        return queryMetricData(hulkRequest);
    }

    public List<LoadTestMonitorDataWithBLOBs> queryMetricData(HulkRequest hulkRequest) {
        HulkConfig hulkConfig = new HulkConfig();
        hulkConfig.setSecretKey("737b804ad57ec101db7f5fa16e70847a");
        hulkConfig.setMasterProjectId("2082");
        hulkConfig.setChildProjectId("2066");
        hulkRequest.setHulkConfig(hulkConfig);
        try{
            long endTime = hulkRequest.getEndTime();
            long startTime = hulkRequest.getStartTime();
            long reliableEndTime = Math.min(endTime, System.currentTimeMillis());
            hulkRequest.setStartTime(startTime);
            hulkRequest.setEndTime(reliableEndTime);
            //LOGGER.info("monitor report_id:{}, begin:{}, end:{}", hulkRequest.getReportId(), startTime, endTime);
        }catch (Exception e){
            //LogUtil.error(e.getMessage(), e);
        }
        //获取监控数据存到数据库中
        hulkRequest.getMonitorTargets().forEach(item->{
            MonitorTargetType targetType = item.getTargetType();
            List<Map> list = handleParam(item.getId(), targetType, hulkRequest);
            list.forEach(param -> {
                JSONObject response = restTemplate.getForObject(URL + "?" + createLinkStringByGet(param), JSONObject.class);
                Map<String,Object> map = handleResult(response);
                LoadTestMonitorDataWithBLOBs testMonitorDataWithBLOBs = new LoadTestMonitorDataWithBLOBs();
                testMonitorDataWithBLOBs.setReportId(hulkRequest.getReportId());
                testMonitorDataWithBLOBs.setTarget(item.getId());
                testMonitorDataWithBLOBs.setTargetType(targetType);
                testMonitorDataWithBLOBs.setTargetLabel(targetType.getLabel());
                String pointerType = "";
                if (param.get("metric") == null){
                    pointerType = String.valueOf(param.get("func"));
                }else {
                    pointerType = String.valueOf(param.get("metric"));
                }
                testMonitorDataWithBLOBs.setPointerType(pointerType);
                testMonitorDataWithBLOBs.setNumberIcalUnit(String.valueOf(map.get("unit")));
                testMonitorDataWithBLOBs.setMonitorName(String.valueOf(map.get("name")));
                testMonitorDataWithBLOBs.setMonitorData(String.valueOf(map.get("data")));
                testMonitorDataWithBLOBs.setTimeStamp(String.valueOf(map.get("categories")));
                testMonitorDataWithBLOBs.setStartTime(hulkRequest.getStartTime());
                testMonitorDataWithBLOBs.setEndTime(hulkRequest.getEndTime());
                // 查询此记录存不存在，
                List<LoadTestMonitorDataWithBLOBs> monitorData = loadTestMonitorDataService.getOnlyOneMonitorData(hulkRequest.getReportId(),item.getId(),targetType.name(),pointerType);
                if (!monitorData.isEmpty()){
                    monitorData.forEach(m -> {
                        testMonitorDataWithBLOBs.setId(m.getId());
                        loadTestMonitorDataMapper.updateByPrimaryKeyWithBLOBs(testMonitorDataWithBLOBs);
                    });
                }else {
                    loadTestMonitorDataMapper.insert(testMonitorDataWithBLOBs);
                }
            });
        });
        List<LoadTestMonitorDataWithBLOBs> monitorData = loadTestMonitorDataService.queryByReport(hulkRequest.getReportId());
        return monitorData;
    }

    public List<LoadTestMonitorDataWithBLOBs> getMonitorData(String reportId){
        LoadTestReport report = loadTestReportMapper.selectByPrimaryKey(reportId);
        try {
            if (System.currentTimeMillis() <= report.getTestEndTime()){
                return queryMetric(reportId);
            }
        }catch (Exception e){
        }
        LoadTestMonitorDataExample example = new LoadTestMonitorDataExample();
        example.createCriteria().andReportIdEqualTo(reportId);
        List<LoadTestMonitorDataWithBLOBs> monitorData = loadTestMonitorDataMapper.selectByExampleWithBLOBs(example);
        return monitorData;
    }

    public List<String> queryReportResource(String reportId) {
        List<String> result = new ArrayList<>();
        List<String> resourceIdAndIndexes = extLoadTestReportMapper.selectResourceId(reportId);
        resourceIdAndIndexes.forEach(resourceIdAndIndex -> {
            String[] split = org.apache.commons.lang3.StringUtils.split(resourceIdAndIndex, "_");
            String resourceId = split[0];
            TestResource testResource = testResourceService.getTestResource(resourceId);
            if (testResource == null) {
                return;
            }
            String configuration = testResource.getConfiguration();
            if (org.apache.commons.lang3.StringUtils.isBlank(configuration)) {
                return;
            }
            NodeDTO dto = JSON.parseObject(configuration, NodeDTO.class);
            if (com.alibaba.nacos.client.utils.StringUtils.isNotBlank(dto.getIp())) {
                /*Integer monitorPort = dto.getMonitorPort();
                int port = monitorPort == null ? 8082 : monitorPort;*/
                result.add(MonitorTargetType.PRESSURE_SERVER.getLabel() + ":" + dto.getIp());
            }
        });

        LoadTestReportWithBLOBs report = loadTestReportMapper.selectByPrimaryKey(reportId);
        String advancedConfiguration = report.getAdvancedConfiguration();
        JSONObject jsonObject = JSON.parseObject(advancedConfiguration);
        JSONArray monitorParams = jsonObject.getJSONArray("monitorParams");
        if (monitorParams == null) {
            return result;
        }

        for (int i = 0; i < monitorParams.size(); i++) {
            Monitor monitor = monitorParams.getObject(i, Monitor.class);
            MonitorTargetType targetType = MonitorTargetType.valueOf(monitor.getTargetType());
            String instance = targetType.getLabel() + ":" + monitor.getConfig();
            if (!result.contains(instance)) {
                result.add(instance);
            }
        }

        return result;
    }

    /**
     * 创建链接字符串
     * @param params
     * @return
     */
    private String createLinkStringByGet(Map<String, Object> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = params.get(key);
            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    /**
     * 处理返回结果
     * @param response
     * @return
     */
    private Map<String, Object> handleResult(JSONObject response){
        Map<String, Object> map = new HashMap<>();
        List<String> dataList = Lists.newArrayList();
        List<String> nameList = Lists.newArrayList();
        if (response != null && StringUtils.equals(response.getString("errno"), "0")){
            JSONArray result = response.getJSONArray("data");
            result.forEach(rObject -> {
                JSONObject resultObject = new JSONObject((Map) rObject);
                JSONArray series = resultObject.getJSONArray("series");
                series.forEach(sObject -> {
                    JSONObject seriesObject = new JSONObject((Map) sObject);
                    dataList.add(seriesObject.getString("data"));
                    nameList.add(seriesObject.getString("name"));
                });
                map.put("data", dataList);
                map.put("name", nameList);
                JSONObject xAxis = resultObject.getJSONObject("xAxis");
                map.put("title", resultObject.getString("title"));
                map.put("unit", resultObject.getString("unit"));
                map.put("categories",xAxis.getString("categories"));
            });
        }else {
            MSException.throwException("获取返回结果失败");
        }
        return map;
    }

    /**
     * 处理请求参数
     * @param name
     * @param targetType
     * @param hulkRequest
     * @return
     */
    private List<Map> handleParam(String name, MonitorTargetType targetType, HulkRequest hulkRequest){
        List<String> metricList = new ArrayList<>();
        List<Map> list = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String startTime = sdf.format(hulkRequest.getStartTime());
            String endTime = sdf.format(hulkRequest.getEndTime());
            String begin = URLEncoder.encode(startTime);
            String end = URLEncoder.encode(endTime);
            switch (targetType){
                case SERVER:
                case PRESSURE_SERVER:
                    metricList.add("cpu");
                    metricList.add("mem");
                    metricList.add("net_in");
                    metricList.add("net_out");
                    break;
                case MONGO:
                    metricList.add("stat");
                    metricList.add("conn");
                    metricList.add("mem");
                    metricList.add("queue");
                    break;
                case REDIS:
                    metricList.add("stat");
                    metricList.add("conn");
                    metricList.add("mem");
                    metricList.add("key");
                    metricList.add("hit");
                    metricList.add("frag");
                    break;
                case MYSQL:
                    metricList.add("stat");
                    metricList.add("conn");
                    metricList.add("flow");
                    metricList.add("sdelay");
                    metricList.add("innodb_time");
                    metricList.add("innodb_stat");
                    break;
                default:
                    break;
            }

            for (String metric : metricList) {
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("t", System.currentTimeMillis() / 1000);
                map.put("begin_time", begin);
                map.put("end_time", end);
                map.put("sid", hulkRequest.getHulkConfig().getChildProjectId());
                String router = "/web/host/api/host/get-statistics";
                switch (targetType) {
                    case SERVER:
                    case PRESSURE_SERVER:
                        map.put("hostname", name);
                        map.put("metric", metric);
                        break;
                    case REDIS:
                        map.put("port", name);
                        map.put("func", metric);
                        router = "/db/redis/api/ins/monitor";
                        break;
                    case MYSQL:
                        map.put("port", name);
                        map.put("func", metric);
                        router = "/db/mysql/api/instance/monitor";
                        break;
                    case MONGO:
                        map.put("port", name);
                        map.put("func", metric);
                        router = "/db/mongo/api/instance/monitor";
                        break;
                }
                String sign = DigestUtils.md5DigestAsHex((DigestUtils.md5DigestAsHex(createLinkStringByGet(map).getBytes()) + hulkRequest.getHulkConfig().getSecretKey()).getBytes());
                map.put("sign", sign);
                map.put("mid", hulkRequest.getHulkConfig().getMasterProjectId());
                map.put("begin_time", startTime);
                map.put("end_time", endTime);
                map.put("router", router);
                list.add(map);
            }
        }catch (Exception e){

        }
        return list;
    }

}
