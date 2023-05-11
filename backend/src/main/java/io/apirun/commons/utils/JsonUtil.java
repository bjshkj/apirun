package io.apirun.commons.utils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;


/**
 * json 工具类（用来补充PojoUtil类处理集合json与Object之间互转的缺陷）
 * 
 * @author lixin1-s@360.cn
 * @version
 * @since Ver 1.1
 * @Date 2015-6-29
 */
public class JsonUtil {
	private static ObjectMapper mapper = new ObjectMapper();

	static{
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public static ObjectMapper getMapper() {
		return mapper;
	}

	/**
	 * objectToJson(java对象转json格式) (这里描述这个方法适用条件 – 可选)
	 * 
	 * @param data
	 * @return String
	 * @exception
	 * @since 1.0.0
	 */
	public static String objectToJson(final Object data) {
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(data);
			return jsonString;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * jsonToObject(json格式转java对象) (这里描述这个方法适用条件 – 可选)
	 * 
	 * @param json
	 * @param typeReference
	 * @return Object
	 * @exception
	 * @since 1.0.0
	 */
	public static <T> T jsonToObject(final String json, TypeReference<?> typeReference) {
		try {
			return (T)mapper.readValue(json, typeReference);
		} catch (JsonParseException e) {
			throw new IllegalArgumentException(e);
		} catch (JsonMappingException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	
	/**
	 * jsonToObject(jsonNode格式转java对象) (这里描述这个方法适用条件 – 可选)
	 * 
	 * @param jsonNode
	 * @param typeReference
	 * @return Object
	 * @exception
	 * @since 1.0.0
	 */
	public static <T> T jsonToObject(final JsonNode jsonNode, TypeReference<?> typeReference) {
		try {
			return (T)mapper.readValue(jsonNode, typeReference);
		} catch (JsonParseException e) {
			throw new IllegalArgumentException(e);
		} catch (JsonMappingException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	/**
	 * 通过json字符串获取JsonNode
	 * @param json
	 * @return
	 */
	public static JsonNode getJsonNode(String json) {
		try {
			return mapper.readTree(json);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
