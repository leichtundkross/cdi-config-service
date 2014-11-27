package com.exxeta.configservice;

/**
 * Helfer-Klasse zum Zugriff auf Configuration-Entries mit komplexer Struktur. Diese Indirektion
 * wird benoetigt, da wir mittels CDI keine untypisierten Objekte injecten koennen.
 */
public class PropertyObject<T> {

	private final String name;

	private T value;

	protected PropertyObject(String name, T value) {
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