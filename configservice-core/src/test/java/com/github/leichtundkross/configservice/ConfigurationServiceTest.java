package com.github.leichtundkross.configservice;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceTest {

	private static final String PROP_KEY = "myKey";

	@InjectMocks
	private ConfigurationService configService;

	@Mock
	private ConfigurationEntryDAO dao;

	@Test
	public void readProperty() {
		Mockito.when(dao.load(PROP_KEY, String.class)).thenReturn("helloProperty");

		assertEquals("helloProperty", configService.readProperty(PROP_KEY, String.class));

		Mockito.verify(dao, Mockito.never()).save(Matchers.anyString(), Matchers.any(String.class));
	}

	@Test
	public void readProperty_SetDefault() {
		Mockito.when(dao.load(PROP_KEY, String.class)).thenReturn(null);

		assertEquals("defaultValue", configService.readProperty(PROP_KEY, String.class, "defaultValue"));

		Mockito.verify(dao).save(PROP_KEY, "defaultValue");
	}

	@Test
	public void writeProperty() {
		configService.writeProperty(PROP_KEY, "myProperty");

		Mockito.verify(dao).save(PROP_KEY, "myProperty");
	}
}
