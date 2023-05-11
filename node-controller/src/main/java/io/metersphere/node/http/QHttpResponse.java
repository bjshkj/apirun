package io.metersphere.node.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.metersphere.node.util.LogUtil;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class QHttpResponse {

	private int statusCode;
	private String body;
	private JsonNode jsonNode;
	private Map<String, String> headers = new HashMap<String, String>();
	
	private ObjectMapper mapper = new ObjectMapper();

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
		parseJson();
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public JsonNode getJsonNode() {
		return jsonNode;
	}

	public void setJsonNode(JsonNode jsonNode) {
		this.jsonNode = jsonNode;
	}

	private void parseJson() {
		if (body != null && body.length() > 0) {
			try {
				jsonNode = mapper.readTree(body);
			} catch (Exception e) {
				LogUtil.warn("parseJson failed, body: " + body, e);
			} 
		}
	}

	public static QHttpResponse parse(CloseableHttpResponse httpResponse) {
		QHttpResponse res = new QHttpResponse();
		res.setStatusCode(httpResponse.getStatusLine().getStatusCode());
		try {
			res.setBody(EntityUtils.toString(httpResponse.getEntity()));
		} catch (Exception e) {
			LogUtil.error("parse EntityUtils.toString error.", e);
		}
		Map<String, String> headers = new HashMap<String, String>();
		for (Header header : httpResponse.getAllHeaders()) {
			headers.put(header.getName(), header.getValue());
		}
		res.setHeaders(headers);
		return res;
	}
}
