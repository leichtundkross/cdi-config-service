package com.exxeta.configservice;

public interface Configuration {

	String key();

	Class<?> type();

	ConfigurationEntryBuilder initialConfig();
}
