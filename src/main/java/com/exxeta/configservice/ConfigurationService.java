package com.exxeta.configservice;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

public class ConfigurationService {

	@Inject
	private ConfigurationStore store;
	
	@Inject
	private Instance<Configuration> configInstances;

	/**
	 * Liefert den Wert zu der gesuchten Property. Ist die Property noch nicht in der DB hinterlegt, wird diese dort mit ihrem Default-Wert
	 * neu angelegt und dieser Wert zurueckgegeben.
	 */
	public <V> V readProperty(String propertyName, Class<V> propertyClass, V defaultValue) {
		return getOrCreateProperty(propertyName, propertyClass, defaultValue);
	}

	/**
	 * Liefert den Wert zu der gesuchten Property. Ist die Property noch nicht in der DB hinterlegt, wird diese dort leer angelegt und null
	 * zurueckgegeben.
	 */
	public <V> V readProperty(String propertyName, Class<V> propertyClass) {
		return getOrCreateProperty(propertyName, propertyClass, null);
	}
    
    /**
	 * Schreibt die definierte Property mit Wert in die DB. Ist die Property bereits vorhanden, wird diese vollstaendig ersetzt.
	 */
	public void writeProperty(String propertyName, Object value) {
		writeProperty(propertyName, ConfigurationEntryBuilder.createConfig().value(value));
	}

	/**
	 * Schreibt die definierte Property mit Wert in die DB. Ist die Property bereits vorhanden, wird diese vollstaendig ersetzt.
	 */
	public void writeProperty(String propertyName, ConfigurationEntryBuilder configBuilder) {
		store.writeProperty(propertyName, configBuilder);
	}

	// ---------------------------------------------
	// CDI Producer
	// ---------------------------------------------

	@Produces
	@Property
	protected Integer lookupIntegerProperty(InjectionPoint ip) {
		IntegerProperty property = ip.getAnnotated().getAnnotation(IntegerProperty.class);
		return getOrCreateProperty(property.key(), Integer.class, property.defaultValue());
	}

	@Produces
	@Property
	protected Double lookupDoubleProperty(InjectionPoint ip) {
		DoubleProperty property = ip.getAnnotated().getAnnotation(DoubleProperty.class);
		return getOrCreateProperty(property.key(), Double.class, property.defaultValue());
	}

	@Produces
	@Property
	protected String lookupStringProperty(InjectionPoint ip) {
		StringProperty property = ip.getAnnotated().getAnnotation(StringProperty.class);
		return getOrCreateProperty(property.key(), String.class, property.defaultValue());
	}

	@Produces
	@Property
	protected Boolean lookupBooleanProperty(InjectionPoint ip) {
		BooleanProperty property = ip.getAnnotated().getAnnotation(BooleanProperty.class);
		return getOrCreateProperty(property.key(), Boolean.class, property.defaultValue());
	}

	@Produces
	@Property
	@SuppressWarnings("unchecked")
	protected <T> PropertyObject<T> lookupObjectProperty(InjectionPoint ip) {
		ObjectProperty property = ip.getAnnotated().getAnnotation(ObjectProperty.class);
		Type beanType = ((ParameterizedType) ip.getType()).getActualTypeArguments()[0];

		T value = getOrCreateProperty(property.key(), (Class<T>) beanType, null);
		return new PropertyObject<T>(property.key(), value);
	}

	/**
	 * Laedt die Property aus der DB. Falls die Property noch nicht hinterlegt ist, wird jetzt der defaultValue dort gespeichert.
	 */
	protected <T> T getOrCreateProperty(String propertyName, Class<T> propertyClass, T defaultValue) {
			T value = store.readProperty(propertyName, propertyClass);			
			if (isNull(value)) {
				if (!isNull(defaultValue)) {
					store.writeProperty(propertyName, ConfigurationEntryBuilder.createConfig().value(defaultValue));
				}
				else {
					// Komplexe Properties haben keine Default-Values,
					// daher suchen wir in den configInstances nach einem passenden Key
					for (Configuration config : configInstances) {
						if (propertyName.equals(config.key())) {
							store.writeProperty(config.key(), config.initialConfig());
							value = store.readProperty(propertyName, propertyClass);
							break;
						}
					}
				}
			}
			return !isNull(value) ? value : (!isNull(defaultValue) ? defaultValue : null);
	}
    
	private static boolean isNull(Object value) {
		return (value instanceof String) ? String.valueOf(value).trim().equals("") : (value == null);
	}
}