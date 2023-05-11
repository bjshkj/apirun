package io.apirun.controller;

import com.google.gson.Gson;
import io.apirun.common.util.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    protected void setResponseHeader(HttpServletResponse response,
                                     String contentType, String contentLength, String cacheControl, boolean bGzip) {
        if (response != null) {
            if (contentType != null) {
                response.setContentType(contentType);
            }
            if (contentLength != null) {
                response.setHeader("Content-Length", contentLength);
            }
            if (cacheControl != null) {
                response.setHeader("Cache-Control", cacheControl);
            }
            if (bGzip) {
                response.setHeader("Content-Encoding", "gzip");
            }
        }
    }


    /**
     * 生成web接口的返回json的格式
     * @param code
     * @param message
     * @return
     */
    protected String genMessageJson(int code, Object message) {
        Map<String, Object> messageBody = new HashMap<String, Object>();
        messageBody.put("code", code);
        messageBody.put("msg", message);
        return PojoUtil.toJson(messageBody);
    }

    /**
     * 生成web接口的返回json的格式
     * @param code
     * @param message
     * @param data
     * @return
     */
    protected String genMessageJson(int code, Object message, Object data){
        Map<String, Object> messageBody = new HashMap<String, Object>();
        messageBody.put("code", code);
        messageBody.put("msg", message);
        messageBody.put("data", data);
        return PojoUtil.toJson(messageBody);
    }


    protected void sendJson(String json, HttpServletResponse response)
    {
        try
        {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(json);
        } catch (IOException e)
        {
            LOGGER.error("sendJson error.", e);
        }
    }

    protected void responseJson(Object object, HttpServletResponse response) {
        LOGGER.debug("String jsonStr = new Gson().toJson(object);");
        String jsonStr = new Gson().toJson(object);
        sendJson(jsonStr, response);
    }
}
