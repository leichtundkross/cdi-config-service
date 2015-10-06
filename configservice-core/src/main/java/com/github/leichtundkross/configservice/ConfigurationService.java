package com.github.leichtundkross.configservice;

import javax.inject.Inject;

/**
 * Accessing properties should by achieved by simply injecting the desired property/field. However, there are some use-cases where you should access ConfigurationService directly:
 * <ul>
 * <li>you need to ensure to always retrieve the latest version of a property (remember: e.g. if you are running in ApplicationScope properties will not be re-injected)</li>
 * <li>you need to write/update a property</li>
 * </ul>
 */
public class ConfigurationService {

	@Inject
	private ConfigurationEntryDAO dao;

	/**
	 * Retrieves the property from the data-store. If the property is not found, a null value will be stored in the data-stored and returned.
	 */
	public <V> V readProperty(String propertyName, Class<V> propertyClass) {
		return getOrCreateProperty(propertyName, propertyClass, null);
	}

	/**
	 * Retrieves the property from the data-store. If the property is not found, the defaultValue will be stored in the data-stored and returned.
	 */
	public <V> V readProperty(String propertyName, Class<V> propertyClass, V defaultValue) {
		return getOrCreateProperty(propertyName, propertyClass, defaultValue);
	}

	/**
	 * Writes the configuration property to the data-store. Existing values will be overwritten.
	 */
	public <T> void writeProperty(String propertyName, T value) {
		dao.save(propertyName, value);
	}

	<T> T getOrCreateProperty(String propertyName, Class<T> propertyClass, T defaultValue) {
		T value = read(propertyName, propertyClass);
		if (isNull(value) && !isNull(defaultValue)) {
			writeProperty(propertyName, defaultValue);
		}

		return !isNull(value) ? value : (!isNull(defaultValue) ? defaultValue : null);
	}

	private <V> V read(String propertyName, Class<V> propertyClass) {
		V entity = dao.load(propertyName, propertyClass);
		return entity;
	}

	private static boolean isNull(Object value) {
		return (value instanceof String) ? String.valueOf(value).trim().equals("") : (value == null);
	}
}