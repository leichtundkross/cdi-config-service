package com.github.leichtundkross.configservice;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Converters;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Version;

@Entity(value = "config", noClassnameStored = true)
@Converters(EntityConverter.class)
final class MongoDBConfigurationEntity<T> extends ConfigurationEntity<T> {

	static final String PROPERTY_NAME = "name";

	static final String PROPERTY_VALUE = "value";

	static final String PROPERTY_VALID_FROM = "validFrom";

	static final String PROPERTY_VALID_TO = "validTo";

	@Id
	private ObjectId _id;

	@Version
	private Long _version;

	@Indexed(unique = true)
	private String name;

	MongoDBConfigurationEntity() {
		// constructor for morphia serialization
		super();
	}

	MongoDBConfigurationEntity(String propertyName, ConfigurationEntity<T> entity) {
		this.name = propertyName;
		this.entries.addAll(entity.entries);
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
}