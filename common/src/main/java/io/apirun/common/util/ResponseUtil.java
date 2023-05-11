package io.apirun.common.util;

import com.google.gson.Gson;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil
{
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ResponseUtil.class);

	private static void responseJson(Message message,
			HttpServletResponse response)
	{
		String jsonStr = JsonFormat.printToString(message);
		sendJson(jsonStr, response);
	}

	public static void sendJson(String json, HttpServletResponse response)
	{
		try
		{
//			LOGGER.info("sendJson:{}",json);
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().print(json);
		}
		catch (IOException e)
		{
			LOGGER.error("sendJson error.", e);
		}
	}

	public static void responseJson(Object object, HttpServletResponse response)
	{
		if (object instanceof Message)
		{
			LOGGER.debug("responseJson((Message) object, response);");
			responseJson((Message) object, response);
		}
		else
		{
			LOGGER.debug("String jsonStr = new Gson().toJson(object);");
			String jsonStr = new Gson().toJson(object);
			sendJson(jsonStr, response);
		}
	}
	
	/**
	 * set the response's headers.
	 * @param response
	 * @param contentType
	 * @param contentLength
	 * @param cacheControl
	 * @param bGzip
	 */
	public static void setResponseHeader(HttpServletResponse response,
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
}
