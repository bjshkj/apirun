package io.apirun.commons.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PojoUtil {

	private static Gson gson = new Gson();
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PojoUtil.class);

	/**
	 * 把pojo类转换成json
	 * 
	 * @param obj
	 * @return
	 */
	public static <T> String toJson(T obj) {

		return gson.toJson(obj);
	}

	/**
	 * 批量把pojo类转换成json
	 * 
	 * @param objs
	 * @return
	 */
	public static <T> List<String> toJsons(List<T> objs) {

		List<String> result = new ArrayList<String>();
		for (T obj : objs) {
			result.add(gson.toJson(obj));
		}
		return result;
	}

	/**
	 * 把json转换成pojo类
	 * 
	 * @param json
	 * @param classOfT
	 * @return
	 */
	public static <T> T fromJson(String json, Class<T> classOfT) {
		T res = null;
		if(json != null){
			try {
				res = gson.fromJson(json, classOfT);
			} catch (JsonSyntaxException e) {
				LOGGER.error("tansfer json to pojo class error, {}\n{}", json, e);
				throw e;
			}
		}
		return res;
	}

	/**
	 * 批量把json转换成pojo类
	 * 
	 * @param jsons
	 * @param classOfT
	 * @return
	 */
	public static <T> List<T> fromJsons(List<String> jsons, Class<T> classOfT) {

		List<T> result = new ArrayList<T>();
		for (String json : jsons) {
			try {
				result.add(gson.fromJson(json, classOfT));
			} catch (JsonSyntaxException e) {
				LOGGER.error("tansfer jsons to pojo classes error, {}\n{}", json, e);
				throw e;
			}
		}
		return result;
	}


}
