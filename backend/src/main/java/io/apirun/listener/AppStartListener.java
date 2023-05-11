package io.apirun.listener;

import io.apirun.api.jmeter.JMeterService;
import io.apirun.api.jmeter.NewDriverManager;
import io.apirun.base.domain.JarConfig;
import io.apirun.commons.utils.LogUtil;
import io.apirun.service.JarConfigService;
import io.apirun.service.ScheduleService;
import org.python.core.Options;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class AppStartListener implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private ScheduleService scheduleService;
    @Resource
    private JMeterService jMeterService;
    @Resource
    private JarConfigService jarConfigService;
    @Value("${jmeter.home}")
    private String jmeterHome;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        System.out.println("================= 应用启动 =================");

        System.setProperty("jmeter.home", jmeterHome);

        loadJars();

        initPythonEnv();

        try {
            Thread.sleep(3 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        scheduleService.startEnableSchedules();
    }

    /**
     * 解决接口测试-无法导入内置python包
     */
    private void initPythonEnv() {
        //解决无法加载 PyScriptEngineFactory
        Options.importSite = false;
        try {
            PythonInterpreter interp = new PythonInterpreter();
            String path = jMeterService.getJmeterHome();
            System.out.println("sys.path: " + path);
            path += "/lib/ext/jython-standalone.jar/Lib";
            interp.exec("import sys");
            interp.exec("sys.path.append(\"" + path + "\")");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error(e.getMessage(), e);
        }
    }

    /**
     * 加载jar包
     */
    private void loadJars() {
        List<JarConfig> jars = jarConfigService.list();

        jars.forEach(jarConfig -> {
            try {
                NewDriverManager.loadJar(jarConfig.getPath());
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.error(e.getMessage(), e);
            }
        });

    }
}
