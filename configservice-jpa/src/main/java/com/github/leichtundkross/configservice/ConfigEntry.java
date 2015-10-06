package com.github.leichtundkross.configservice;

public interface ConfigEntry<T> {

	void setName(String name);

	String getName();

	T getValue();

	void setValue(T val);
}
