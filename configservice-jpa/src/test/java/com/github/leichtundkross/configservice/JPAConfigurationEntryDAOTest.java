package com.github.leichtundkross.configservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
		String value = dao.load("app.domain.property1", String.class);
		assertNull(value);

		dao.save("app.domain.property1", "propValue");

		value = dao.load("app.domain.property1", String.class);
		assertNotNull(value);
		assertEquals("propValue", value);
	}

	@Test
	public void saveAndLoad_integr() {
		Integer value = dao.load("app.domain.property1", Integer.class);
		assertNull(value);

		dao.save("app.domain.property1", 1234);

		value = dao.load("app.domain.property1", Integer.class);
		assertNotNull(value);
		assertEquals(Integer.valueOf(1234), value);
	}

	@Test
	public void saveAndLoad_double() {
		Double value = dao.load("app.domain.property1", Double.class);
		assertNull(value);

		dao.save("app.domain.property1", 1.234d);

		value = dao.load("app.domain.property1", Double.class);
		assertNotNull(value);
		assertEquals(Double.valueOf(1.234d), value);
	}

	@Test
	public void saveAndLoad_boolean() {
		Boolean value = dao.load("app.domain.property1", Boolean.class);
		assertNull(value);

		dao.save("app.domain.property1", true);

		value = dao.load("app.domain.property1", Boolean.class);
		assertNotNull(value);
		assertEquals(Boolean.TRUE, value);
	}

	@Test
	public void saveAndLoad_date() {
		final Date dateValue = new GregorianCalendar(2014, Calendar.MARCH, 27).getTime();

		Date value = dao.load("app.domain.property1", Date.class);
		assertNull(value);

		dao.save("app.domain.property1", dateValue);

		value = dao.load("app.domain.property1", Date.class);
		assertNotNull(value);
		assertEquals(dateValue, value);
	}
}
