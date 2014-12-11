package com.github.leichtundkross.configservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JPAConfigurationEntryDAOTest {

	@InjectMocks
	private JPAConfigurationEntryDAO dao;

	@Spy
	private EntityManager em = Persistence.createEntityManagerFactory("configservice").createEntityManager();

	@Test
	public void saveAndLoad() {
		ConfigurationEntity<String> entity = dao.load("app.domain.property1", String.class);
		assertNull(entity);

		dao.save("app.domain.property1", ConfigurationEntityBuilder.<String> createConfig().value("propValue").getConfigEntry());

		entity = dao.load("app.domain.property1", String.class);
		assertNotNull(entity);
		assertEquals("propValue", entity.getEntries().get(0).getValue());
	}

	@Test
	public void saveAndLoad_withValidFrom() {
		final Date validFrom = new GregorianCalendar(2014, Calendar.MARCH, 27).getTime();
		final Date validTo = new GregorianCalendar(2014, Calendar.MARCH, 29).getTime();

		ConfigurationEntity<String> entity = dao.load("app.domain.property1", String.class);
		assertNull(entity);

		dao.save("app.domain.property1", ConfigurationEntityBuilder.<String> createConfig().value("propValue").validFrom(validFrom).validTo(validTo)
				.getConfigEntry());

		entity = dao.load("app.domain.property1", String.class);
		assertNotNull(entity);
		assertEquals("propValue", entity.getEntries().get(0).getValue());
		assertEquals(validFrom, entity.getEntries().get(0).getValidFrom());
		assertEquals(validTo, entity.getEntries().get(0).getValidTo());
	}

	@Test
	public void saveAndLoad_integr() {
		ConfigurationEntity<Integer> entity = dao.load("app.domain.property1", Integer.class);
		assertNull(entity);

		dao.save("app.domain.property1", ConfigurationEntityBuilder.<Integer> createConfig().value(1234).getConfigEntry());

		entity = dao.load("app.domain.property1", Integer.class);
		assertNotNull(entity);
		assertEquals(Integer.valueOf(1234), entity.getEntries().get(0).getValue());
	}

	@Test
	public void saveAndLoad_double() {
		ConfigurationEntity<Double> entity = dao.load("app.domain.property1", Double.class);
		assertNull(entity);

		dao.save("app.domain.property1", ConfigurationEntityBuilder.<Double> createConfig().value(1.234d).getConfigEntry());

		entity = dao.load("app.domain.property1", Double.class);
		assertNotNull(entity);
		assertEquals(Double.valueOf(1.234d), entity.getEntries().get(0).getValue());
	}

	@Test
	public void saveAndLoad_boolean() {
		ConfigurationEntity<Boolean> entity = dao.load("app.domain.property1", Boolean.class);
		assertNull(entity);

		dao.save("app.domain.property1", ConfigurationEntityBuilder.<Boolean> createConfig().value(true).getConfigEntry());

		entity = dao.load("app.domain.property1", Boolean.class);
		assertNotNull(entity);
		assertTrue(entity.getEntries().get(0).getValue());
	}

	@Test
	public void saveAndLoad_date() {
		final Date dateValue = new GregorianCalendar(2014, Calendar.MARCH, 27).getTime();

		ConfigurationEntity<Date> entity = dao.load("app.domain.property1", Date.class);
		assertNull(entity);

		dao.save("app.domain.property1", ConfigurationEntityBuilder.<Date> createConfig().value(dateValue).getConfigEntry());

		entity = dao.load("app.domain.property1", Date.class);
		assertNotNull(entity);
		assertEquals(dateValue, entity.getEntries().get(0).getValue());
	}
}
