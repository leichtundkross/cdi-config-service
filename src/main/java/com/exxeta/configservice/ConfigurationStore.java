package com.exxeta.configservice;

import java.util.Date;
import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.inject.Inject;

import com.exxeta.utils.DateRangeValidator;
import com.exxeta.utils.JsonPath;
import com.exxeta.utils.TimeService;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ConfigurationStore {

	private static final JsonPath JSON_PATH = new JsonPath();

	@Inject
	private ConfigDatabase configDatabase;

	@Inject
	private TimeService timeService;

	@Inject
	private DateRangeValidator dateRangeValidator;

	/**
	 * Liefert den Value zu einer Property. Falls die Property nicht hinterlegt oder kein Value fuer den aktuellen Abfragezeitpunkt
	 * definiert ist, wird null zurueckgegeben.
	 * 
	 * @throws ConfigurationException
	 *             - falls es fuer die aktuelle Uhrzeit mehrere Values hinterlegt sind
	 */
    public <V> V readProperty(String propertyName, Class<V> propertyClass) {
		String json = readProperty(propertyName);
		if (json == null) {
			return null;
		}

		V configValue = null;
		List<?> entries = JSON_PATH.get(json, JsonPath.path("entries"), List.class);
		for (int i = 0; i < entries.size(); i++) {
			Date validFrom = JSON_PATH.get(json, JsonPath.path("entries[" + i + "]", "validFrom"), Date.class);
			Date validTo = JSON_PATH.get(json, JsonPath.path("entries[" + i + "]", "validTo"), Date.class);
			if (dateRangeValidator.inRange(timeService.currentTime(), validFrom, validTo)) {
				if (configValue != null) {
					throw new ConfigurationException("Die Konfiguration fuer Property '" + propertyName + "' ist nicht eindeutig!");
				}

				configValue = JSON_PATH.get(json, JsonPath.path("entries[" + i + "]", "value"), propertyClass);
			}
		}

		return configValue;
	}

	/**
	 * Setzt eine Property. Etwaig vorhandene Properties werden komplett ueberschrieben. Ggf. gesetzte Gueltigkeits-Intervalle werden NICHT
	 * validiert.
	 */
    public void writeProperty(String propertyName, ConfigurationEntryBuilder configEntryBuilder) {
	    final ConfigurationEntry configEntry = configEntryBuilder.getConfigEntry();
	    configDatabase.save(propertyName, configEntry);
	}

	protected String readProperty(String propertyName) {
	    final String configValue = configDatabase.load(propertyName);
	    return configValue;
	}
}