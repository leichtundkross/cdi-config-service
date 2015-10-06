package com.github.leichtundkross.configservice;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * JPA based implementation of the {@link ConfigurationEntryDAO}. Uses discriminator-based single table inheritance. Non primitive property types are supported when implementing
 * {@link Serializable}.
 */
public class JPAConfigurationEntryDAO implements ConfigurationEntryDAO {

	@Resource
	private EntityManager em;

	@Inject
	private ConfigEntryFactory configEntryFactory;

	@Override
	public <T> void save(String propertyName, T value) {
		ConfigEntry<T> jpaEntity = configEntryFactory.createConfigEntry(propertyName, value);
		em.merge(jpaEntity);
	}

	@Override
	public <T> T load(String propertyName, Class<T> propertyClass) {
		Class<ConfigEntry<T>> entryClass = configEntryFactory.findConfigEntryClass(propertyClass);
		ConfigEntry<T> jpaEntity = em.find(entryClass, propertyName);
		if (jpaEntity == null) {
			return null;
		}

		return jpaEntity.getValue();
	}
}
