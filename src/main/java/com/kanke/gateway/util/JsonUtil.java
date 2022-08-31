package com.kanke.gateway.util;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JsonUtil {

	public static String ObjectToString(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	
	@SuppressWarnings("unchecked")
	public static <T> List<T> StringToList(String json,Class<T> cls) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			try {
				JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, cls);
				return (List<T>)mapper.readValue(json, javaType);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
	}
	
	public static <T> T StringToObject(String json,Class<T> cls) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			try {
				return mapper.readValue(json, cls);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
	}

	public static JsonObject stringToJsonObject(String json){
		JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
		return jsonObject;
	}


	
}
