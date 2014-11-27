package com.exxeta.configservice;

import java.util.Date;

import com.exxeta.utils.GsonHelper;

public class ConfigurationEntryBuilder {

	private final ConfigurationEntry configEntry;

	private Object value;

	private Date validFrom;

	private Date validTo;

	private ConfigurationEntryBuilder() {
		configEntry = new ConfigurationEntry();
	}

	public static ConfigurationEntryBuilder createConfig() {
		return new ConfigurationEntryBuilder();
	}

	public ConfigurationEntryBuilder value(Object value) {
		if (this.value != null) {
			addEntry();
		}

		this.value = transformJson(value);
		return this;
	}

	public ConfigurationEntryBuilder validFrom(Date validFrom) {
		this.validFrom = validFrom;
		return this;
	}

	public ConfigurationEntryBuilder validTo(Date validTo) {
		this.validTo = validTo;
		return this;
	}

	ConfigurationEntry getConfigEntry() {
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

	/**
	 * Handelt es sich bei dem uebergeben Wert um einen JSON Ausdruck, so wird dieser in eine Map konvertiert. Andernfalls wuerde bei der
	 * Umwandlung des {@link ConfigurationEntry}s nach JSON der Value als nicht als JSON-Objektstruktur erkannt und als String escaped.
	 */
	private static Object transformJson(Object value) {
		if (value instanceof String && GsonHelper.isJson((String) value)) {
			return GsonHelper.jsonToVariables((String) value);
		}

		return value;
	}
}
