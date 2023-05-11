package io.apirun.common.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FreemarkerUtil
 * @Description freemarker相关操作
 *
 * 三处要注意一下编码集的设置
 * （1）configuration.setDefaultEncoding("UTF-8");
 * （2）Template t = configuration.getTemplate("模板文件","UTF-8");
 * （3）Writer out = new BufferedWriter(new OutputStreamWriter(文件输出流 fos, "UTF-8"))
 *
 * @Author longhui
 * @Date 2019/2/1 10:24
 * @Version 1.0
 **/
public class FreemarkerUtil {

    /**
     * 绝对路径，加载模板
     * @param ftlFilePath
     * @return
     */
    private static Template getTemplate(String ftlFilePath) {
        try {

            if (ftlFilePath == null) {
                return null;
            }

            File ftlFile = new File(ftlFilePath);
            if (!ftlFile.exists()) {
                return null;
            }

            String ftlName = ftlFile.getName();
            // 通过Freemaker的Configuration读取相应的ftl
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);

            cfg.setDefaultEncoding("UTF-8");

            // 设定去哪里读取相应的ftl模板文件
            cfg.setDirectoryForTemplateLoading(ftlFile.getParentFile());
            // 在模板文件目录中找到名称为name的文件
            Template temp = cfg.getTemplate(ftlName, "utf-8");
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  从jar包中resources目录读取模板文件
     * @param classz 调用方的class对象：模板文件在该resource路径之下
     * @param templateDirPath  "填你的resource下的路径，比如/ftl"
     * @param templateName 例如：XXX.tpl
     * @return
     */
    private static Template getTemplateFromResourcePath(Class classz, String templateDirPath, String templateName) throws IOException {

        // 通过Freemaker的Configuration读取相应的ftl
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateLoader(new ClassTemplateLoader(classz, templateDirPath));

        return cfg.getTemplate(templateName, "utf-8");
    }

    /**
     * 使用freemarker生成文件
     *
     * @param ftlFilePath  freemarker模板文件路径
     * @param root  填充模板的数据
     * @param outFilePath 输出文件路径
     */
    public static void fprint(String ftlFilePath, Object root, String outFilePath) {
        BufferedWriter out = null;
        try {
            // 通过一个文件输出流，就可以写到相应的文件中，此处用的是绝对路径
            File outFile = new File(outFilePath);
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }

            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(outFile), "UTF-8"));
            Template temp = getTemplate(ftlFilePath);
            temp.process(root, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用freemarker生成文件
     *
     * @param ftlFilePath  freemarker模板文件路径
     * @param root  填充模板的数据
     * @return 返回数据
     */
    public static String fprint(String ftlFilePath, Object root) {
        CharArrayWriter out = null;
        try {

            out = new CharArrayWriter();
            Template temp = getTemplate(ftlFilePath);
            temp.process(root, out);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } finally {
            if (out != null)
                out.close();
        }
        return out.toString();
    }

    /**
     * 从jar包中resources目录读取模板文件, 使用freemarker生成文件
     * @param resFtlPath 填你的resource下的路径，比如/ftl"
     * @param tplName  模板文件，例如：XXX.tpl
     * @param data  填充模板的数据
     * @return 返回文本数据
     */
    public static String fprint(Class classz, String resFtlPath, String tplName, Object data) throws FreemarkerException {
        CharArrayWriter out = new CharArrayWriter();
        try {
            Template temp = getTemplateFromResourcePath(classz, resFtlPath, tplName);
            temp.process(data, out);
        } catch (IOException | TemplateException e) {
            throw new FreemarkerException(e);
        } finally {
            out.close();
        }
        return out.toString();
    }


    public static class FreemarkerException extends Exception {

        public FreemarkerException(String message) {
            super(message);
        }

        public FreemarkerException(String message, Exception e) {
            super(message, e);
        }

        public FreemarkerException(Exception e) {
            super(e);
        }

    }

    public static void main(String[] args) {

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("title", "XXXXX邀请您加入TA的项目");

        List<String> contents = new ArrayList<String>(2);
        contents.add("您的好友，XXXXX邀请您加入TA的项目");
        contents.add("请在内网环境下，点击下面按钮进入平台查看：");

        data.put("contents", contents);
        Map<String, Object> urlInfo = new HashMap<String, Object>();
        urlInfo.put("url", "http://10.209.1.228/usecenter/usecase");
        urlInfo.put("label", "进入平台");
        data.put("linkinfo", urlInfo);
        data.put("linkman", "longhui@360.cn");
        data.put("indexUrl", "http://10.209.1.228/usecenter/usecase");
        data.put("reciverName", "longhui");

        fprint("E:\\pmanage\\trunk\\web-app\\src\\main\\resources\\ftl\\email\\common.ftl", data, "D:\\testData\\email.html");

    }

}
