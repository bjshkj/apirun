package io.apirun.api.dto.definition.parse;

import io.apirun.base.domain.ApiDefinitionWithBLOBs;
import io.apirun.base.domain.ApiTestCaseWithBLOBs;
import io.apirun.base.domain.EsbApiParamsWithBLOBs;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ApiDefinitionImport {
    private String projectName;
    private String protocol;
    private List<ApiDefinitionWithBLOBs> data;

    // 新版本带用例导出
    private List<ApiTestCaseWithBLOBs> cases;

    //ESB文件导入的附属数据类
    private Map<String,EsbApiParamsWithBLOBs> esbApiParamsMap;
}
