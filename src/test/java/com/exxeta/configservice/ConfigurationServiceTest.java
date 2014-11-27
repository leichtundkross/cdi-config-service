package com.exxeta.configservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Iterator;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.exxeta.configservice.BooleanProperty;
import com.exxeta.configservice.Configuration;
import com.exxeta.configservice.ConfigurationEntryBuilder;
import com.exxeta.configservice.ConfigurationService;
import com.exxeta.configservice.ConfigurationStore;
import com.exxeta.configservice.DoubleProperty;
import com.exxeta.configservice.IntegerProperty;
import com.exxeta.configservice.ObjectProperty;
import com.exxeta.configservice.PropertyObject;
import com.exxeta.configservice.StringProperty;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceTest {

	@InjectMocks
	private ConfigurationService configService;

	@Mock
	private ConfigurationStore store;

    @Mock
    Instance<Configuration> configInstances;
    
	@Test
	public void testReadProperty() {
		Mockito.when(store.readProperty("myProperty", String.class)).thenReturn("helloProperty");
		assertEquals("helloProperty", configService.readProperty("myProperty", String.class));
	}
    
    @Test
	public void testWriteProperty() {
		configService.writeProperty("helloProperty", "myProperty");
		Mockito.verify(store)
				.writeProperty(Matchers.eq("helloProperty"), Matchers.argThat(configEntryBuilderMatcher(ConfigurationEntryBuilder.createConfig().value("myProperty"))));
	}

	@Test
	public void testWriteProperty_ConfigBuilder() {
		ConfigurationEntryBuilder ceb = ConfigurationEntryBuilder.createConfig().value("myProperty").validFrom(new Date());
		configService.writeProperty("helloProperty", ceb);
		Mockito.verify(store).writeProperty(Matchers.eq("helloProperty"), Matchers.argThat(configEntryBuilderMatcher(ceb)));
	}

	@Test
	public void testLookupStringProperty() {
		final String propertyKey = "myKey";

		StringProperty property = Mockito.mock(StringProperty.class);
		Mockito.when(property.key()).thenReturn(propertyKey);
		InjectionPoint ip = mockInjectionPoint(StringProperty.class, property, null);

		Mockito.when(store.readProperty(propertyKey, String.class)).thenReturn("helloProperty");

		String propValue = configService.lookupStringProperty(ip);
		assertEquals("helloProperty", propValue);

		Mockito.verify(store, Mockito.times(0)).writeProperty(Matchers.eq(propertyKey), Matchers.any(ConfigurationEntryBuilder.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testLookupStringProperty_KeinDefaultHinterlegt() {
		final String propertyKey = "myKey";

		StringProperty property = Mockito.mock(StringProperty.class);
		Mockito.when(property.key()).thenReturn(propertyKey);
		Mockito.when(property.defaultValue()).thenReturn(null);
		InjectionPoint ip = mockInjectionPoint(StringProperty.class, property, null);

		Mockito.when(store.readProperty(propertyKey, String.class)).thenReturn(null);
        Iterator<Configuration> configsIteratorMock = Mockito.mock(Iterator.class);
        Mockito.when(configsIteratorMock.hasNext()).thenReturn(false);        
        Mockito.when(configInstances.iterator()).thenReturn(configsIteratorMock);

		String propValue = configService.lookupStringProperty(ip);
		assertNull(propValue);

		Mockito.verify(store, Mockito.times(0)).writeProperty(Matchers.eq(propertyKey), Matchers.any(ConfigurationEntryBuilder.class));
	}

	@Test
	public void testLookupIntegerProperty() {
		final String propertyKey = "myKey";

		IntegerProperty property = Mockito.mock(IntegerProperty.class);
		Mockito.when(property.key()).thenReturn(propertyKey);
		InjectionPoint ip = mockInjectionPoint(IntegerProperty.class, property, null);

		Mockito.when(store.readProperty(propertyKey, Integer.class)).thenReturn(new Integer(30));

		Integer propValue = configService.lookupIntegerProperty(ip);
		assertEquals(new Integer(30), propValue);
	}

	@Test
	public void testLookupDoubleProperty() {
		final String propertyKey = "myKey";

		DoubleProperty property = Mockito.mock(DoubleProperty.class);
		Mockito.when(property.key()).thenReturn(propertyKey);
		InjectionPoint ip = mockInjectionPoint(DoubleProperty.class, property, null);

		Mockito.when(store.readProperty(propertyKey, Double.class)).thenReturn(new Double(49.95));

		Double propValue = configService.lookupDoubleProperty(ip);
		assertEquals(new Double(49.95), propValue);
	}

	@Test
	public void testLookupBooleanProperty() {
		final String propertyKey = "myKey";

		BooleanProperty property = Mockito.mock(BooleanProperty.class);
		Mockito.when(property.key()).thenReturn(propertyKey);
		InjectionPoint ip = mockInjectionPoint(BooleanProperty.class, property, null);

		Mockito.when(store.readProperty(propertyKey, Boolean.class)).thenReturn(true);

		Boolean propValue = configService.lookupBooleanProperty(ip);
		assertEquals(true, propValue.booleanValue());
	}

	@Test
	public void testLookupObjectProperty() {
		final String propertyKey = "myKey";
		final ComplexObject value = new ComplexObject("hallo", 25, new Date());

		ObjectProperty property = Mockito.mock(ObjectProperty.class);
		Mockito.when(property.key()).thenReturn(propertyKey);
		InjectionPoint ip = mockInjectionPoint(ObjectProperty.class, property, ComplexObject.class);

		Mockito.when(store.readProperty(propertyKey, ComplexObject.class)).thenReturn(value);

		PropertyObject<ComplexObject> propValue = configService.lookupObjectProperty(ip);
		assertEquals(value, propValue.get());
	}

	@Test(expected = RuntimeException.class)
	public void testLookupStringProperty_doesNotCatchException() {
		final String propertyKey = "myKey";

		StringProperty property = Mockito.mock(StringProperty.class);
		Mockito.when(property.key()).thenReturn(propertyKey);
		InjectionPoint ip = mockInjectionPoint(StringProperty.class, property, null);

		Mockito.when(store.readProperty(propertyKey, String.class)).thenThrow(new RuntimeException());

		configService.lookupStringProperty(ip);
	}

	@Test
	public void testGetOrCreateProperty_PropertyVorhanden() {
		final String propertyKey = "myKey";
		Mockito.when(store.readProperty(propertyKey, Integer.class)).thenReturn(new Integer(30));

		Integer propValue = configService.getOrCreateProperty(propertyKey, Integer.class, new Integer(20));
		assertEquals(new Integer(30), propValue);

		Mockito.verify(store, Mockito.times(0)).writeProperty(Matchers.eq(propertyKey), Matchers.any(ConfigurationEntryBuilder.class));
	}

	@Test
	public void testGetOrCreateProperty_PropertyNichtVorhanden() {
		final String propertyKey = "myKey";
		final Integer defaultValue = new Integer(20);
		Mockito.when(store.readProperty(propertyKey, Integer.class)).thenReturn(null);

		Integer propValue = configService.getOrCreateProperty(propertyKey, Integer.class, defaultValue);
		assertEquals(defaultValue, propValue);

		Mockito.verify(store, Mockito.times(1)).writeProperty(Matchers.eq(propertyKey),
				Matchers.argThat(configEntryBuilderMatcher(ConfigurationEntryBuilder.createConfig().value(defaultValue))));
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

	private static Matcher<ConfigurationEntryBuilder> configEntryBuilderMatcher(final ConfigurationEntryBuilder expectedCeb) {
		return new BaseMatcher<ConfigurationEntryBuilder>() {

			public boolean matches(Object item) {
				ConfigurationEntryBuilder ceb = (ConfigurationEntryBuilder) item;

				assertEquals(expectedCeb.getConfigEntry().getEntries().size(), ceb.getConfigEntry().getEntries().size());
				for (int i = 0; i < expectedCeb.getConfigEntry().getEntries().size(); i++) {
					assertEquals(expectedCeb.getConfigEntry().getEntries().get(i).value, ceb.getConfigEntry().getEntries().get(i).value);
					assertEquals(expectedCeb.getConfigEntry().getEntries().get(i).validFrom, ceb.getConfigEntry().getEntries().get(i).validFrom);
					assertEquals(expectedCeb.getConfigEntry().getEntries().get(i).validTo, ceb.getConfigEntry().getEntries().get(i).validTo);
				}

				return true;
			}

			public void describeTo(Description arg0) {
				// do nothing
			}
		};
	}
}