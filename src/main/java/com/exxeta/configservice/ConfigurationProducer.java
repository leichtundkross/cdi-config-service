package com.exxeta.configservice;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import com.exxeta.configservice.annotation.BooleanProperty;
import com.exxeta.configservice.annotation.DoubleProperty;
import com.exxeta.configservice.annotation.IntegerProperty;
import com.exxeta.configservice.annotation.ObjectProperty;
import com.exxeta.configservice.annotation.Property;
import com.exxeta.configservice.annotation.StringProperty;

class ConfigurationProducer {

	@Inject
	private ConfigurationService configService;

	@Produces
	@Property
	Integer lookupIntegerProperty(InjectionPoint ip) {
		IntegerProperty property = ip.getAnnotated().getAnnotation(IntegerProperty.class);
		return configService.getOrCreateProperty(property.key(), Integer.class, property.defaultValue());
	}

	@Produces
	@Property
	Double lookupDoubleProperty(InjectionPoint ip) {
		DoubleProperty property = ip.getAnnotated().getAnnotation(DoubleProperty.class);
		return configService.getOrCreateProperty(property.key(), Double.class, property.defaultValue());
	}

	@Produces
	@Property
	String lookupStringProperty(InjectionPoint ip) {
		StringProperty property = ip.getAnnotated().getAnnotation(StringProperty.class);
		return configService.getOrCreateProperty(property.key(), String.class, property.defaultValue());
	}

	@Produces
	@Property
	Boolean lookupBooleanProperty(InjectionPoint ip) {
		BooleanProperty property = ip.getAnnotated().getAnnotation(BooleanProperty.class);
		return configService.getOrCreateProperty(property.key(), Boolean.class, property.defaultValue());
	}

	@Produces
	@Property
	@SuppressWarnings("unchecked")
	<T> PropertyObject<T> lookupObjectProperty(InjectionPoint ip) {
		ObjectProperty property = ip.getAnnotated().getAnnotation(ObjectProperty.class);
		Type beanType = ((ParameterizedType) ip.getType()).getActualTypeArguments()[0];

		T value = configService.getOrCreateProperty(property.key(), (Class<T>) beanType, null);
		return new PropertyObject<T>(property.key(), value);
	}
}