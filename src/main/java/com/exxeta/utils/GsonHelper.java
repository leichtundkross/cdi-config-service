package com.exxeta.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class GsonHelper {

	private GsonHelper() {
		super();
	}

	public static boolean isJson(String json) {
		try {
			HashMap<?, ?> fromJson = new Gson().fromJson(json, HashMap.class);
			return fromJson != null || (fromJson == null && json == null) ? true : false;
		} catch (JsonParseException e) {
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Erzeugt aus einer VariablenMap einen JSON String.
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> jsonToVariables(String json) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Object nextJSON = new JSONTokener(json).nextValue();
			if (nextJSON instanceof JSONObject) {
				JSONObject object = (JSONObject) nextJSON;
				for (Iterator iter = object.keys(); iter.hasNext();) {
					String key = (String) iter.next();
					String value = object.getString(key);

					if (value == null || value.trim().equals("")) {
						map.put(key, null);
					} else if (GsonHelper.isJson(value)) {
						Map<String, Object> jsonToVariables = GsonHelper.jsonToVariables(value);
						map.put(key, jsonToVariables);
					} else {
						map.put(key, value);
					}
				}
			} else {
				map.put("status", json);
			}
		} catch (JSONException e) {
			return map;
		}

		return map;
	}
}