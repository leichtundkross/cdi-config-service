package com.exxeta.configservice;

import javax.inject.Inject;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoException;

public class MongoDBConfigurationEntryDAO implements ConfigurationEntryDAO {

	@Inject
	private Datastore ds;

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> void save(String propertyName, ConfigurationEntity<T> entity) {
		final MongoDBConfigurationEntity<T> mongoEntity = new MongoDBConfigurationEntity<>(propertyName, entity);
		// search for existing entry
		Query<MongoDBConfigurationEntity> query = ds.createQuery(MongoDBConfigurationEntity.class).field(MongoDBConfigurationEntity.PROPERTY_NAME)
				.equal(propertyName);
		query.retrievedFields(true, "_id", "_version", MongoDBConfigurationEntity.PROPERTY_NAME);
		MongoDBConfigurationEntity<T> existingEntity = query.get();
		if (existingEntity == null) {
			ds.save(mongoEntity);
		} else {
			merge(mongoEntity, existingEntity);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> ConfigurationEntity<T> load(String propertyName, Class<T> propertyClass) {
		MongoDBConfigurationEntity<T> entity = ds.createQuery(MongoDBConfigurationEntity.class).field(MongoDBConfigurationEntity.PROPERTY_NAME)
				.equal(propertyName).get();
		return (ConfigurationEntity<T>) entity;
	}

	/**
	 * Merges the new entity into the existing one which means that the existing DB entry will be totally replaced beside the object id.
	 */
	private <T> void merge(MongoDBConfigurationEntity<T> entity, MongoDBConfigurationEntity<T> existingEntity) {
		Long version = entity.getVersion();
		if (version == null) {
			version = existingEntity.getVersion();
		}

		if (version == null) {
			throw new MongoException("Missing version for entity " + existingEntity.getId());
		}

		entity.setId(existingEntity.getId());
		entity.setVersion(version);
		ds.merge(entity);
	}

}