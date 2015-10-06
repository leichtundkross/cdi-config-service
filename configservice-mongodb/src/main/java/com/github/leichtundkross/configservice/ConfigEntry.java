package com.github.leichtundkross.configservice;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.PostLoad;
import org.mongodb.morphia.annotations.PrePersist;
import org.mongodb.morphia.annotations.Transient;
import org.mongodb.morphia.annotations.Version;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

@Entity(value = "config", noClassnameStored = true)
final class ConfigEntry<T> {

	static final String PROPERTY_NAME = "name";

	static final String PROPERTY_VALUE = "value";

	private static final String DUMMY_KEY = "1";

	@Id
	private ObjectId _id;

	@Version
	private Long _version;

	@Indexed(unique = true)
	private String name;

	@Transient
	private T value;

	private DBObject val;

	ConfigEntry() {
		// constructor for morphia serialization
		super();
	}

	ConfigEntry(String propertyName, T value) {
		this.name = propertyName;
		this.value = value;
	}

	/**
	 * Since Morphia cannot handle generic types we store the value in a map using MongoDB's object translation.
	 */
	@PrePersist
	void pre() {
		val = BasicDBObjectBuilder.start(DUMMY_KEY, value).get();
	}

	@PostLoad
	@SuppressWarnings("unchecked")
	void post() {
		value = (T) val.get(DUMMY_KEY);
	}

	ObjectId getId() {
		return _id;
	}

	void setId(ObjectId _id) {
		this._id = _id;
	}

	Long getVersion() {
		return _version;
	}

	void setVersion(Long _version) {
		this._version = _version;
	}

	String getName() {
		return name;
	}

	T getValue() {
		return value;
	}
}
