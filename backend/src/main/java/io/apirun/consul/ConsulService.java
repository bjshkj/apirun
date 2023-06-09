package io.apirun.consul;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.apirun.base.domain.TestResource;
import io.apirun.commons.constants.PerformanceTestStatus;
import io.apirun.commons.constants.ResourcePoolTypeEnum;
import io.apirun.dto.LoadTestDTO;
import io.apirun.dto.NodeDTO;
import io.apirun.dto.TestResourcePoolDTO;
import io.apirun.performance.dto.Monitor;
import io.apirun.performance.request.QueryTestPlanRequest;
import io.apirun.performance.service.PerformanceTestService;
import io.apirun.service.TestResourcePoolService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ConsulService {
    @Resource
    private TestResourcePoolService testResourcePoolService;
    @Resource
    private PerformanceTestService performanceTestService;

    public Map<String, List<String>> getActiveNodes() {
        Map<String, List<String>> result = new HashMap<>();

        List<TestResourcePoolDTO> testResourcePoolDTOS = testResourcePoolService.listValidResourcePools();
        QueryTestPlanRequest request = new QueryTestPlanRequest();
        request.setFilters(new HashMap<String, List<String>>() {{
            put("status", Arrays.asList(PerformanceTestStatus.Starting.name(), PerformanceTestStatus.Running.name()));
        }});
        List<LoadTestDTO> list = performanceTestService.list(request);
        for (LoadTestDTO loadTestDTO : list) {
            String advancedConfiguration = performanceTestService.getAdvancedConfiguration(loadTestDTO.getId());
            JSONObject adv = JSON.parseObject(advancedConfiguration);
            Object monitorParams = adv.get("monitorParams");
            if (monitorParams == null) {
                continue;
            }
            List<Monitor> monitors = JSON.parseArray(monitorParams.toString(), Monitor.class);
            for (Monitor monitor : monitors) {
                result.put(monitor.getIp() + "-" + monitor.getPort(), Collections.singletonList("apirun"));
            }
        }
        for (TestResourcePoolDTO pool : testResourcePoolDTOS) {
            if (!StringUtils.equals(pool.getType(), ResourcePoolTypeEnum.NODE.name())) {
                continue;
            }
            List<TestResource> resources = pool.getResources();
            for (TestResource resource : resources) {
                NodeDTO node = JSON.parseObject(resource.getConfiguration(), NodeDTO.class);
                // 资源池默认9100
                int port = 9100;
                if (node.getMonitorPort() != null) {
                    port = node.getMonitorPort();
                }
                result.put(node.getIp() + "-" + port, Collections.singletonList("apirun"));
            }
        }
        return result;
    }
}
