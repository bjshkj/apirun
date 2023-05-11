package io.apirun.api.controller;

import io.apirun.api.dto.scenario.DatabaseConfig;
import io.apirun.api.service.APIDatabaseService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping(value = "/api/database")
public class ApiDatabaseController {

    @Resource
    APIDatabaseService apiDatabaseService;

    @PostMapping("/validate")
    public void validate(@RequestBody DatabaseConfig databaseConfig) {
        apiDatabaseService.validate(databaseConfig);
    }

}
