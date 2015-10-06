package com.github.leichtundkross.configservice;

import java.util.HashMap;
import java.util.Map;

public class InMemoryConfigurationEntryDAO implements ConfigurationEntryDAO {

	private Map<String, Object> db = new HashMap<String, Object>();

	public <T> void save(String propertyName, T entry) {
		db.put(propertyName, entry);
	}

	@SuppressWarnings("unchecked")
	public <T> T load(String propertyName, Class<T> propertyClass) {
		return (T) db.get(propertyName);
	}
}