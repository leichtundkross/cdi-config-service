package com.github.leichtundkross.configservice;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents an entry in the configuration database. An entity might consist of 0..n entries, each entry valid for a certain period of time.
 */
class ConfigurationEntity<T> {

	static final class Entry<T> {

		private final T value;

		private final Date validFrom;

		private final Date validTo;

		Entry() {
			// constructor for serialization
			this.value = null;
			this.validFrom = null;
			this.validTo = null;
		}

		Entry(T value, Date validFrom, Date validTo) {
			this.value = value;
			this.validFrom = validFrom;
			this.validTo = validTo;
		}

		public Date getValidFrom() {
			return validFrom;
		}

		public Date getValidTo() {
			return validTo;
		}

		public T getValue() {
			return value;
		}
	}

	protected final List<Entry<T>> entries;

	/**
	 * constructor for serialization
	 */
	protected ConfigurationEntity() {
		this(null);
	}

	ConfigurationEntity(T value) {
		this.entries = new LinkedList<Entry<T>>();
		if (value != null) {
			addEntry(value);
		}
	}

	void addEntry(T value) {
		addEntry(value, null, null);
	}

	void addEntry(T value, Date validFrom, Date validTo) {
		entries.add(new Entry<T>(value, validFrom, validTo));
	}

	List<Entry<T>> getEntries() {
		return Collections.unmodifiableList(entries);
	}
}