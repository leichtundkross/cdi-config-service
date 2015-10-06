package com.github.leichtundkross.configservice;

/**
 * Encapsulates access to a concrete data-store.
 */
public interface ConfigurationEntryDAO {

	<T> void save(String propertyName, T value);

	<T> T load(String propertyName, Class<T> propertyClass);
}