package io.apirun.log.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.apirun.commons.utils.PageUtils;
import io.apirun.commons.utils.Pager;
import io.apirun.commons.utils.SessionUtils;
import io.apirun.log.service.OperatingLogService;
import io.apirun.log.vo.OperatingLogDTO;
import io.apirun.log.vo.OperatingLogRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/operating/log")
public class OperatingLogController {
    @Resource
    private OperatingLogService operatingLogService;

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<OperatingLogDTO>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody OperatingLogRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        request.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        return PageUtils.setPageInfo(page, operatingLogService.list(request));
    }

    @GetMapping("/get/{id}")
    public OperatingLogDTO get(@PathVariable String id) {
        return operatingLogService.get(id);
    }


    @PostMapping("/get/source")
    public List<OperatingLogDTO> findBySourceId(@RequestBody OperatingLogRequest request) {
        return operatingLogService.findBySourceId(request);
    }

}
