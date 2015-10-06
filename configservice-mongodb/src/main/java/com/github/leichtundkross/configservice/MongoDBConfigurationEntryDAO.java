package com.github.leichtundkross.configservice;

import javax.inject.Inject;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import com.mongodb.MongoException;

public class MongoDBConfigurationEntryDAO implements ConfigurationEntryDAO {

	@Inject
	private Datastore ds;

	@Override
	public <T> void save(String propertyName, T value) {
		final ConfigEntry<T> mongoEntity = new ConfigEntry<>(propertyName, value);
		ConfigEntry<T> existingEntity = findExistingEntry(propertyName);
		insertOrUpdate(mongoEntity, existingEntity);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T load(String propertyName, Class<T> propertyClass) {
		ConfigEntry<T> entity = queryForConfigEntry(propertyName).get();
		if (entity == null) {
			return null;
		}

		return entity.getValue();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> ConfigEntry<T> findExistingEntry(String propertyName) {
		Query<ConfigEntry> query = queryForConfigEntry(propertyName);
		query.retrievedFields(true, "_id", "_version", ConfigEntry.PROPERTY_NAME);
		return query.get();
	}

	@SuppressWarnings("rawtypes")
	private Query<ConfigEntry> queryForConfigEntry(String propertyName) {
		return ds.createQuery(ConfigEntry.class).field(ConfigEntry.PROPERTY_NAME).equal(propertyName);
	}

	private <T> void insertOrUpdate(final ConfigEntry<T> mongoEntity, ConfigEntry<T> existingEntity) {
		if (existingEntity == null) {
			ds.save(mongoEntity);
		} else {
			merge(mongoEntity, existingEntity);
		}
	}

	/**
	 * Merges the new entity into the existing one which means that the existing DB entry will be totally replaced beside the object id.
	 */
	private <T> void merge(ConfigEntry<T> updatedEntity, ConfigEntry<T> existingEntity) {
		Long version = updatedEntity.getVersion();
		if (version == null) {
			version = existingEntity.getVersion();
		}

		if (version == null) {
			throw new MongoException("Missing version for entity " + existingEntity.getId());
		}

		updatedEntity.setId(existingEntity.getId());
		updatedEntity.setVersion(version);
		ds.merge(updatedEntity);
	}
}
