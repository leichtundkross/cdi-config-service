package com.exxeta.configservice;

import java.util.Date;

public class ConfigurationEntityBuilder<T> {

	private final ConfigurationEntity<T> configEntry;

	private T value;

	private Date validFrom;

	private Date validTo;

	private ConfigurationEntityBuilder() {
		configEntry = new ConfigurationEntity<T>();
	}

	public static <T> ConfigurationEntityBuilder<T> createConfig() {
		return new ConfigurationEntityBuilder<T>();
	}

	public ConfigurationEntityBuilder<T> value(T value) {
		if (this.value != null) {
			addEntry();
		}

		this.value = value;
		return this;
	}

	public ConfigurationEntityBuilder<T> validFrom(Date validFrom) {
		this.validFrom = validFrom;
		return this;
	}

	public ConfigurationEntityBuilder<T> validTo(Date validTo) {
		this.validTo = validTo;
		return this;
	}

	ConfigurationEntity<T> getConfigEntry() {
		if (this.value != null) {
			addEntry();
		}

		return configEntry;
	}

	private void addEntry() {
		configEntry.addEntry(this.value, this.validFrom, this.validTo);
		this.value = null;
		this.validFrom = null;
		this.validTo = null;
	}
}
