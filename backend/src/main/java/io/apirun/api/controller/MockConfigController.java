package io.apirun.api.controller;

import io.apirun.api.dto.mockconfig.MockConfigRequest;
import io.apirun.api.dto.mockconfig.MockExpectConfigRequest;
import io.apirun.api.dto.mockconfig.response.MockConfigResponse;
import io.apirun.api.dto.mockconfig.response.MockExpectConfigResponse;
import io.apirun.api.service.ApiDefinitionService;
import io.apirun.api.service.MockConfigService;
import io.apirun.base.domain.ApiDefinitionWithBLOBs;
import io.apirun.base.domain.MockExpectConfig;
import io.apirun.base.domain.MockExpectConfigWithBLOBs;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author song.tianyang
 * @Date 2021/4/12 5:11 下午
 * @Description
 */
@RestController
@RequestMapping("/mockConfig")
public class MockConfigController {

    @Resource
    private MockConfigService mockConfigService;
    @Resource
    private ApiDefinitionService apiDefinitionService;

    @PostMapping("/genMockConfig")
    public MockConfigResponse genMockConfig(@RequestBody MockConfigRequest request) {
        return mockConfigService.genMockConfig(request);
    }

    @PostMapping("/updateMockExpectConfig")
    public MockExpectConfig updateMockExpectConfig(@RequestBody MockExpectConfigRequest request) {
        return mockConfigService.updateMockExpectConfig(request);
    }

    @GetMapping("/mockExpectConfig/{id}")
    public MockExpectConfigResponse selectMockExpectConfig(@PathVariable String id) {
        MockExpectConfigWithBLOBs config = mockConfigService.findMockExpectConfigById(id);
        MockExpectConfigResponse response = new MockExpectConfigResponse(config);
        return response;
    }

    @GetMapping("/deleteMockExpectConfig/{id}")
    public String deleteMockExpectConfig(@PathVariable String id) {
        mockConfigService.deleteMockExpectConfig(id);
        return "SUCCESS";
    }

    @GetMapping("/getApiParams/{id}")
    public List<Map<String, String>> getApiParams(@PathVariable String id) {
        ApiDefinitionWithBLOBs apiDefinitionWithBLOBs = apiDefinitionService.getBLOBs(id);
        List<Map<String, String>> apiParams = mockConfigService.getApiParamsByApiDefinitionBLOBs(apiDefinitionWithBLOBs);
        return apiParams;
    }
}
