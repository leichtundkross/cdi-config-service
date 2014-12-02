package com.exxeta.configservice;

/**
 * Encapsulates access to a concrete data-store.
 */
public interface ConfigurationEntryDAO {

	<T> void save(String propertyName, ConfigurationEntity<T> entity);

	<T> ConfigurationEntity<T> load(String propertyName, Class<T> propertyClass);
}