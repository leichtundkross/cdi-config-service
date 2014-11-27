package com.exxeta.utils;

import java.nio.file.InvalidPathException;
import java.util.Date;

import net.minidev.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Eine detailierte Beschreibung und Notation zu JsonPath findet sich unter
 * http://goessner.net/articles/JsonPath/
 */
public class JsonPath {

	public static final String JSON_PATH_ROOT = "$";

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Erzeugt den Pfad zu einem Element im JSON Dokument.
	 */
	public static String path(String... nodes) {
		StringBuilder path = new StringBuilder();
		path.append(JSON_PATH_ROOT);
		for (String node : nodes) {
			path.append(".").append(node);
		}

		return path.toString();
	}

	public String get(String json, String expression) {
		Object jsonObject = eval(json, expression);
		if (jsonObject == null) {
			return null;
		} else if (jsonObject instanceof JSONObject) {
			return ((JSONObject) jsonObject).toJSONString();
		}

		return jsonObject.toString();
	}

	public <T> T get(String json, String expression, Class<T> clazz) {
		Object extracted = get(json, expression);
		if (extracted != null) {
			return fromJson(clazz, extracted);
		}
		return null;
	}

	private static Object eval(String json, String expression) {
		try {
			return com.jayway.jsonpath.JsonPath.read(json, expression);
		} catch (InvalidPathException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private static <T> T fromJson(Class<T> clazz, Object value) {
		if (clazz.equals(String.class)) {
			// Strings sind nicht zwangslaeufig JSON und werden daher nicht umgewandelt
			return clazz.cast(value);
		} else if (clazz.equals(Date.class)) {
			return GSON.fromJson("\"" + value.toString() + "\"", clazz);
		}

		return GSON.fromJson(value.toString(), clazz);
	}
}