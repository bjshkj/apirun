package io.apirun.api.dto.definition.request.processors.pre;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import io.apirun.api.dto.RunningParamKeys;
import io.apirun.api.dto.definition.request.MsTestElement;
import io.apirun.api.dto.definition.request.ParameterConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.modifiers.JSR223PreProcessor;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.collections.HashTree;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JSONType(typeName = "JSR223PreProcessor")
public class MsJSR223PreProcessor extends MsTestElement {
    private String type = "JSR223PreProcessor";

    @JSONField(ordinal = 20)
    private String script;

    @JSONField(ordinal = 21)
    private String scriptLanguage;

    @Override
    public void toHashTree(HashTree tree, List<MsTestElement> hashTree, ParameterConfig config) {
        //替换Metersphere环境变量
        script = StringUtils.replace(script,RunningParamKeys.API_ENVIRONMENT_ID,"\""+RunningParamKeys.RUNNING_PARAMS_PREFIX+this.getUseEnviroment()+".\"");

        // 非导出操作，且不是启用状态则跳过执行
        if (!config.isOperating() && !this.isEnable()) {
            return;
        }
        final HashTree jsr223PreTree = tree.add(getJSR223PreProcessor());
        if (CollectionUtils.isNotEmpty(hashTree)) {
            hashTree.forEach(el -> {
                el.toHashTree(jsr223PreTree, el.getHashTree(), config);
            });
        }
    }

    public JSR223PreProcessor getJSR223PreProcessor() {

        JSR223PreProcessor processor = new JSR223PreProcessor();
        processor.setEnabled(this.isEnable());
        if (StringUtils.isNotEmpty(this.getName())) {
            processor.setName(this.getName());
        } else {
            processor.setName("JSR223PreProcessor");
        }
        processor.setProperty(TestElement.TEST_CLASS, JSR223PreProcessor.class.getName());
        processor.setProperty(TestElement.GUI_CLASS, SaveService.aliasToClass("TestBeanGUI"));
        /*processor.setProperty("cacheKey", "true");*/
        processor.setProperty("scriptLanguage", this.getScriptLanguage());
        if (StringUtils.isNotEmpty(this.getScriptLanguage()) && this.getScriptLanguage().equals("nashornScript")) {
            processor.setProperty("scriptLanguage", "nashorn");
        }
        if (StringUtils.isNotEmpty(this.getScriptLanguage()) && this.getScriptLanguage().equals("rhinoScript")) {
            processor.setProperty("scriptLanguage", "javascript");
        }

        processor.setProperty("script", this.getScript());
        return processor;
    }

}
