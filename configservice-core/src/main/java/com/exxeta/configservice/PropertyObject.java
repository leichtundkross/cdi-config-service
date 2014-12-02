package com.exxeta.configservice;

/**
 * This is a helper for injecting properties with complex object structures (since CDI does not support injection of generic types).
 */
public class PropertyObject<T> {

	private final String name;

	private T value;

	PropertyObject(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public T get() {
		return value;
	}
}