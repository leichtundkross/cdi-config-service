package com.github.leichtundkross.configservice;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.leichtundkross.configservice.ConfigurationEntity;
import com.github.leichtundkross.configservice.ConfigurationEntityBuilder;
import com.github.leichtundkross.configservice.ConfigurationEntityValidator;
import com.github.leichtundkross.configservice.ConfigurationException;
import com.github.leichtundkross.configservice.TimeService;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationEntityValidatorTest {

	@InjectMocks
	private ConfigurationEntityValidator validator;

	@Mock
	private TimeService timeService;

	@Before
	public void mockTimeService() {
		Mockito.when(timeService.currentTime()).thenReturn(date(2014, 5, 20));
	}

	@Test
	public void validate() {
		String value = validator.validate(ConfigurationEntityBuilder.<String> createConfig().value("property").getConfigEntry(), "key");

		assertEquals("property", value);
	}

	@Test
	public void validate_severalEntries() {
		ConfigurationEntity<String> entity = ConfigurationEntityBuilder.<String> createConfig() //
				.value("propertyOld").validFrom(date(2014, 0, 1)).validTo(date(2014, 1, 1)) //
				.value("propertyNew").validFrom(date(2014, 1, 2)) //
				.getConfigEntry();

		String value = validator.validate(entity, "key");

		assertEquals("propertyNew", value);
	}

	@Test
	public void validate_noValidTo() {
		String value = validator.validate(ConfigurationEntityBuilder.<String> createConfig().value("property").validFrom(date(2014, 0, 1)).getConfigEntry(), "key");

		assertEquals("property", value);
	}

	@Test(expected = ConfigurationException.class)
	public void validate_inFuture() {
		validator.validate(ConfigurationEntityBuilder.createConfig().value("property").validFrom(date(2014, 6, 20)).getConfigEntry(), "key");
	}

	@Test(expected = ConfigurationException.class)
	public void validate_inPast() {
		validator.validate(ConfigurationEntityBuilder.createConfig().value("property").validFrom(date(2010, 6, 20)).validTo(date(2012, 6, 20)).getConfigEntry(), "key");
	}

	@Test(expected = ConfigurationException.class)
	public void validate_overlap() {
		ConfigurationEntity<Object> entity = ConfigurationEntityBuilder.createConfig() //
				.value("propertyOld").validFrom(date(2014, 0, 1)).validTo(date(2014, 5, 25)) //
				.value("propertyNew").validFrom(date(2014, 5, 15)) //
				.getConfigEntry();
		validator.validate(entity, "key");
	}

	private static Date date(int year, int month, int dayOfMonth) {
		return new GregorianCalendar(year, month, dayOfMonth).getTime();
	}
}
