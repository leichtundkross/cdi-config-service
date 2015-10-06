package com.github.leichtundkross.configservice;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

/**
 * TODO TDD - just works for first test :-)
 */
public class JPAConfigurationEntryDAO implements ConfigurationEntryDAO {

	@Resource
	private EntityManager em;

	@Override
	public <T> void save(String propertyName, T value) {
		StringJPAConfigurationEntity jpaEntity = new StringJPAConfigurationEntity();
		jpaEntity.setName(propertyName);
		jpaEntity.setStringValue(value.toString());
		em.merge(jpaEntity);
	}

	@Override
	public <T> T load(String propertyName, Class<T> propertyClass) {
		StringJPAConfigurationEntity jpaEntity = em.find(StringJPAConfigurationEntity.class, propertyName);
		if (jpaEntity == null) {
			return null;
		}
		
		return (T) jpaEntity.getStringValue();
	}
}