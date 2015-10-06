package com.github.leichtundkross.configservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ConfigEntryFactoryTest {

	private ConfigEntryFactory factory = new ConfigEntryFactory();

	@Test
	public void findConfigEntryClass() {
		assertEquals(StringConfigEntry.class, factory.findConfigEntryClass(String.class));
		assertEquals(IntegerConfigEntry.class, factory.findConfigEntryClass(Integer.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findConfigEntryClass_UnknownType() {
		factory.findConfigEntryClass(Object.class);
	}

	@Test
	public void createConfigEntryInstance() {
		String propertyName = "myProp";
		Double value = 1.234;

		ConfigEntry<Double> configEntry = factory.createConfigEntry(propertyName, value);

		assertNotNull(configEntry);
		assertEquals(propertyName, configEntry.getName());
		assertEquals(value, configEntry.getValue());
	}
}
