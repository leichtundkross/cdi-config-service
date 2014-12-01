package com.exxeta.configservice;

import java.util.Date;

import javax.inject.Inject;

import com.exxeta.configservice.ConfigurationEntity.Entry;

class ConfigurationEntityValidator {

	@Inject
	private TimeService timeService;

	/**
	 * Validates the {@link ConfigurationEntity} - an entity is valid if there is exactly one matching entry for the current point in time.
	 * 
	 * @throws ConfigurationException
	 *             - if the entity is not valid
	 */
	<T> T validate(ConfigurationEntity<T> entity, String propertyName) throws ConfigurationException {
		final Date currentTime = timeService.currentTime();
		T configValue = null;

		for (Entry<T> v : entity.getEntries()) {
			if (isInRange(currentTime, v.getValidFrom(), v.getValidTo())) {
				if (configValue == null) {
					configValue = v.getValue();
					continue;
				}

				throw new ConfigurationException("Config for property '" + propertyName + "' is ambiguous!");
			}
		}

		if (configValue == null) {
			throw new ConfigurationException("Config for property '" + propertyName + "' has no valid entry!");
		}

		return configValue;
	}

	private static boolean isInRange(Date value, Date from, Date to) {
		if (value == null) {
			return false;
		}

		return (from == null || !value.before(from)) && (to == null || !value.after(to));
	}
}