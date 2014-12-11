package com.github.leichtundkross.configservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.leichtundkross.configservice.ConfigurationProducer;
import com.github.leichtundkross.configservice.ConfigurationService;
import com.github.leichtundkross.configservice.PropertyObject;
import com.github.leichtundkross.configservice.annotation.BooleanProperty;
import com.github.leichtundkross.configservice.annotation.DoubleProperty;
import com.github.leichtundkross.configservice.annotation.IntegerProperty;
import com.github.leichtundkross.configservice.annotation.ObjectProperty;
import com.github.leichtundkross.configservice.annotation.StringProperty;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationProducerTest {

	private static final String PROP_KEY = "myKey";

	@InjectMocks
	private ConfigurationProducer producer;

	@Mock
	private ConfigurationService configService;

	@Test
	public void lookupStringProperty() {
		StringProperty property = Mockito.mock(StringProperty.class);
		Mockito.when(property.key()).thenReturn(PROP_KEY);
		InjectionPoint ip = mockInjectionPoint(StringProperty.class, property, null);

		Mockito.when(configService.getOrCreateProperty(Matchers.eq(PROP_KEY), Matchers.eq(String.class), Matchers.anyString())).thenReturn("helloProperty");

		String propValue = producer.lookupStringProperty(ip);
		assertEquals("helloProperty", propValue);
	}

	@Test
	public void lookupStringProperty_NoDefault() {
		StringProperty property = Mockito.mock(StringProperty.class);
		Mockito.when(property.key()).thenReturn(PROP_KEY);
		Mockito.when(property.defaultValue()).thenReturn(null);
		InjectionPoint ip = mockInjectionPoint(StringProperty.class, property, null);

		Mockito.when(configService.getOrCreateProperty(Matchers.eq(PROP_KEY), Matchers.eq(String.class), Matchers.anyString())).thenReturn(null);

		String propValue = producer.lookupStringProperty(ip);
		assertNull(propValue);
	}

	@Test
	public void lookupIntegerProperty() {
		IntegerProperty property = Mockito.mock(IntegerProperty.class);
		Mockito.when(property.key()).thenReturn(PROP_KEY);
		InjectionPoint ip = mockInjectionPoint(IntegerProperty.class, property, null);

		Mockito.when(configService.getOrCreateProperty(Matchers.eq(PROP_KEY), Matchers.eq(Integer.class), Matchers.anyInt())).thenReturn(new Integer(30));

		Integer propValue = producer.lookupIntegerProperty(ip);
		assertEquals(new Integer(30), propValue);
	}

	@Test
	public void lookupDoubleProperty() {
		DoubleProperty property = Mockito.mock(DoubleProperty.class);
		Mockito.when(property.key()).thenReturn(PROP_KEY);
		InjectionPoint ip = mockInjectionPoint(DoubleProperty.class, property, null);

		Mockito.when(configService.getOrCreateProperty(Matchers.eq(PROP_KEY), Matchers.eq(Double.class), Matchers.anyDouble())).thenReturn(new Double(49.95));

		Double propValue = producer.lookupDoubleProperty(ip);
		assertEquals(new Double(49.95), propValue);
	}

	@Test
	public void lookupBooleanProperty() {
		BooleanProperty property = Mockito.mock(BooleanProperty.class);
		Mockito.when(property.key()).thenReturn(PROP_KEY);
		InjectionPoint ip = mockInjectionPoint(BooleanProperty.class, property, null);

		Mockito.when(configService.getOrCreateProperty(Matchers.eq(PROP_KEY), Matchers.eq(Boolean.class), Matchers.anyBoolean())).thenReturn(true);

		Boolean propValue = producer.lookupBooleanProperty(ip);
		assertEquals(true, propValue.booleanValue());
	}

	@Test
	public void lookupObjectProperty() {
		final ComplexObject value = new ComplexObject("hallo", 25, new Date());

		ObjectProperty property = Mockito.mock(ObjectProperty.class);
		Mockito.when(property.key()).thenReturn(PROP_KEY);
		InjectionPoint ip = mockInjectionPoint(ObjectProperty.class, property, ComplexObject.class);

		Mockito.when(configService.getOrCreateProperty(Matchers.eq(PROP_KEY), Matchers.eq(ComplexObject.class), Matchers.any(ComplexObject.class))).thenReturn(value);

		PropertyObject<ComplexObject> propValue = producer.lookupObjectProperty(ip);
		assertEquals(value, propValue.get());
	}

	@Test(expected = RuntimeException.class)
	@SuppressWarnings("unchecked")
	public void lookupStringProperty_doesNotCatchException() {
		StringProperty property = Mockito.mock(StringProperty.class);
		Mockito.when(property.key()).thenReturn(PROP_KEY);
		InjectionPoint ip = mockInjectionPoint(StringProperty.class, property, null);

		Mockito.when(configService.getOrCreateProperty(Matchers.anyString(), Matchers.any(Class.class), Matchers.any())).thenThrow(new RuntimeException());

		producer.lookupStringProperty(ip);
	}

	private <T extends Annotation> InjectionPoint mockInjectionPoint(Class<T> annotationClazz, T annotation, Type parameterType) {
		ParameterizedType type = Mockito.mock(ParameterizedType.class);
		Mockito.when(type.getActualTypeArguments()).thenReturn(new Type[] { parameterType });

		Annotated annotated = Mockito.mock(Annotated.class);
		Mockito.when(annotated.getAnnotation(annotationClazz)).thenReturn(annotation);

		InjectionPoint ip = Mockito.mock(InjectionPoint.class);
		Mockito.when(ip.getAnnotated()).thenReturn(annotated);
		Mockito.when(ip.getType()).thenReturn(type);
		return ip;
	}
}