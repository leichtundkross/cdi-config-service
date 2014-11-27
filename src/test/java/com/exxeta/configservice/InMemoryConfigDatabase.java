package com.exxeta.configservice;

import java.util.HashMap;
import java.util.Map;

public class InMemoryConfigDatabase implements ConfigDatabase {

	private Map<String, String> db = new HashMap<String, String>();

	public void save(String propertyName, ConfigurationEntry entry) {
		db.put(propertyName, ConfigurationEntry.toJsonDocument(entry));
	}

	public String load(String propertyName) {
		return db.get(propertyName);
	}
}