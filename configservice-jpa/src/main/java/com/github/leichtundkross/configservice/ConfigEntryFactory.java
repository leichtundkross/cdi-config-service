package com.github.leichtundkross.configservice;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class ConfigEntryFactory {

	private static final Map<Class<?>, Class<? extends ConfigEntry<?>>> CONFIG_ENTRY_BY_TYPE = new HashMap<>();

	{
		registerTypeMapping(Boolean.class, BooleanConfigEntry.class);
		registerTypeMapping(Date.class, DateConfigEntry.class);
		registerTypeMapping(Double.class, DoubleConfigEntry.class);
		registerTypeMapping(Integer.class, IntegerConfigEntry.class);
		registerTypeMapping(Serializable.class, SerializableConfigEntry.class);
		registerTypeMapping(String.class, StringConfigEntry.class);
	}

	@SuppressWarnings("unchecked")
	<T> Class<ConfigEntry<T>> findConfigEntryClass(Class<T> propertyClass) {
		Class<?> supportedType = detectSupportedPropertyType(propertyClass);
		Class<? extends ConfigEntry<?>> entityClass = CONFIG_ENTRY_BY_TYPE.get(supportedType);
		return (Class<ConfigEntry<T>>) entityClass;
	}

	<T> ConfigEntry<T> createConfigEntry(String propertyName, T value) {
		Class<? extends ConfigEntry<?>> entityClass = findConfigEntryClass(value.getClass());
		ConfigEntry<T> entry = createConfigEntryInstance(entityClass, propertyName, value);
		return entry;
	}

	private Class<?> detectSupportedPropertyType(Class<?> propertyClass) {
		Class<?> supportedType = propertyClass;
		if (!CONFIG_ENTRY_BY_TYPE.containsKey(supportedType)) {
			if (isSerializable(supportedType)) {
				supportedType = Serializable.class;
			} else {
				throw new IllegalArgumentException("Property Type '" + propertyClass + "' is not supported!");
			}
		}

		return supportedType;
	}

	private boolean isSerializable(Class<?> supportedType) {
		return Serializable.class.isAssignableFrom(supportedType);
	}

	@SuppressWarnings("unchecked")
	private <T> ConfigEntry<T> createConfigEntryInstance(Class<? extends ConfigEntry<?>> entityClass, String propertyName, T value) {
		try {
			ConfigEntry<T> entry = (ConfigEntry<T>) entityClass.newInstance();
			entry.setName(propertyName);
			entry.setValue(value);
			return entry;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	static <T> void registerTypeMapping(Class<T> type, Class<? extends ConfigEntry<T>> configEntryType) {
		CONFIG_ENTRY_BY_TYPE.put(type, configEntryType);
	}
}
