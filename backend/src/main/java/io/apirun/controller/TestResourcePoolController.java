package io.apirun.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.commons.constants.OperLogConstants;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.controller.request.resourcepool.QueryResourcePoolRequest;
import io.apirun.dto.TestResourcePoolDTO;
import io.apirun.dto.UpdatePoolDTO;
import io.apirun.log.annotation.MsAuditLog;
import io.apirun.service.TestResourcePoolService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("testresourcepool")
@RestController

public class TestResourcePoolController {

    @Resource
    private TestResourcePoolService testResourcePoolService;

    @PostMapping("/add")
    @MsAuditLog(module = "system_test_resource", type = OperLogConstants.CREATE, content = "#msClass.getLogDetails(#testResourcePoolDTO.id)", msClass = TestResourcePoolService.class)
    public TestResourcePoolDTO addTestResourcePool(@RequestBody TestResourcePoolDTO testResourcePoolDTO) {
        return testResourcePoolService.addTestResourcePool(testResourcePoolDTO);
    }

    @GetMapping("/delete/{testResourcePoolId}")
    @MsAuditLog(module = "system_test_resource", type = OperLogConstants.DELETE, beforeEvent = "#msClass.getLogDetails(#testResourcePoolId)",  msClass = TestResourcePoolService.class)
    public void deleteTestResourcePool(@PathVariable(value = "testResourcePoolId") String testResourcePoolId) {
        testResourcePoolService.deleteTestResourcePool(testResourcePoolId);
    }

    @PostMapping("/update")
    @MsAuditLog(module = "system_test_resource", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#testResourcePoolDTO.id)", content = "#msClass.getLogDetails(#testResourcePoolDTO.id)", msClass = TestResourcePoolService.class)
    public void updateTestResourcePool(@RequestBody TestResourcePoolDTO testResourcePoolDTO) {
        testResourcePoolService.updateTestResourcePool(testResourcePoolDTO);
    }

    @GetMapping("/update/{poolId}/{status}")
    @MsAuditLog(module = "system_test_resource", type = OperLogConstants.UPDATE, beforeEvent = "#msClass.getLogDetails(#poolId)", content = "#msClass.getLogDetails(#poolId)", msClass = TestResourcePoolService.class)
    public void updateTestResourcePoolStatus(@PathVariable String poolId, @PathVariable String status) {
        testResourcePoolService.updateTestResourcePoolStatus(poolId, status);
    }

    @GetMapping("/check/use/{poolId}")
    public UpdatePoolDTO checkHaveTestUsePool(@PathVariable String poolId) {
        return testResourcePoolService.checkHaveTestUsePool(poolId);
    }

    @PostMapping("list/{goPage}/{pageSize}")
    public Pager<List<TestResourcePoolDTO>> listResourcePools(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryResourcePoolRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        return PageUtils.setPageInfo(page, testResourcePoolService.listResourcePools(request));
    }

    @GetMapping("list/all/valid")
    public List<TestResourcePoolDTO> listValidResourcePools() {
        return testResourcePoolService.listValidResourcePools();
    }

    @GetMapping("list/quota/valid")
    public List<TestResourcePoolDTO> listValidQuotaResourcePools() {
        return testResourcePoolService.listValidQuotaResourcePools();
    }


}
