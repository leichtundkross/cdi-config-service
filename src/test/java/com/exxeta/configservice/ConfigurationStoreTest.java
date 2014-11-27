package com.exxeta.configservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import com.exxeta.utils.DateRangeValidator;
import com.exxeta.utils.JsonPath;
import com.exxeta.utils.TimeService;
import com.google.gson.Gson;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationStoreTest {

	@InjectMocks
	private ConfigurationStore store;

	@Mock
	private TimeService timeService;

	@Spy
	private DateRangeValidator dateRangeValidator;

	@Spy
	private InMemoryConfigDatabase configDatabase = new InMemoryConfigDatabase();

	@Before
	public void before() {
		Mockito.when(timeService.currentTime()).thenReturn(new GregorianCalendar(2013, Calendar.APRIL, 15).getTime());
	}

	@Test
	public void testLookupJsonProperty() {
		final String propertyKey = "myKey";
		final ComplexObject complexValue = new ComplexObject("string", 25, new Date());
		final String value = new Gson().toJson(complexValue);

		store.writeProperty(propertyKey, ConfigurationEntryBuilder.createConfig().value(value));

		String configuredPropertyJson = store.readProperty(propertyKey, String.class);
		assertEquals(complexValue, new Gson().fromJson(configuredPropertyJson, ComplexObject.class));

		ComplexObject configuredProperty = store.readProperty(propertyKey, ComplexObject.class);
		assertEquals(complexValue, configuredProperty);
	}

	// ---------------------------------------------
	// Schreiben
	// ---------------------------------------------

	@Test
	public void testWriteProperty_SimpleString() {
		final String key = "myKey";
		final String value = "helloConfigWorld";

		assertNull(configDatabase.load(key));

		store.writeProperty(key, ConfigurationEntryBuilder.createConfig().value(value));
		assertPropertySet(key, value);
	}

	@Test
	public void testWriteProperty_ComplexObject() {
		final String key = "myKey";
		final Object value = new ComplexObject("ein String", 49, new Date());

		assertNull(configDatabase.load(key));

		store.writeProperty(key, ConfigurationEntryBuilder.createConfig().value(value));

		String doc = configDatabase.load(key);
		assertNotNull(doc);
		ComplexObject configuredValue = new JsonPath().get(doc, JsonPath.path("entries[0]", "value"), ComplexObject.class);
		assertEquals(value, configuredValue);
	}

	@Test
	public void testWriteProperty_Override() {
		final String key = "myKey";

		store.writeProperty(key, ConfigurationEntryBuilder.createConfig().value("oldValue"));
		assertPropertySet(key, "oldValue");

		store.writeProperty(key, ConfigurationEntryBuilder.createConfig().value("newValue"));
		assertPropertySet(key, "newValue");
	}

	@Test
	public void testWriteProperty_Validity() {
		final String key = "myKey";

		ConfigurationEntryBuilder ceb = ConfigurationEntryBuilder.createConfig();
		ceb.value("currentValue").validFrom(new GregorianCalendar(2013, Calendar.APRIL, 1).getTime()).validTo(new GregorianCalendar(2013, Calendar.APRIL, 20).getTime());
		ceb.value("futureValue").validFrom(new GregorianCalendar(2013, Calendar.APRIL, 21).getTime()).validTo(new GregorianCalendar(2013, Calendar.APRIL, 30).getTime());

		store.writeProperty(key, ceb);
		assertPropertySet(key, "currentValue");
	}

	@Test
	public void testWriteProperty_ValidityNotUnique() {
		final String key = "myKey";

		// im Zeitraum vom 15. bis 20.04. sind zwei Entries vorhanden.
		// Beim Schreiben ist das zulaessig, beim Lesen nicht mehr
		ConfigurationEntryBuilder ceb = ConfigurationEntryBuilder.createConfig();
		ceb.value("oldValue").validFrom(new GregorianCalendar(2013, Calendar.APRIL, 10).getTime()).validTo(new GregorianCalendar(2013, Calendar.APRIL, 20).getTime());
		ceb.value("oldValue").validFrom(new GregorianCalendar(2013, Calendar.APRIL, 15).getTime()).validTo(new GregorianCalendar(2013, Calendar.APRIL, 25).getTime());
		store.writeProperty(key, ceb);

		try {
			store.readProperty(key, String.class);
			fail();
		} catch (RuntimeException e) {
			assertEquals(ConfigurationException.class, e.getClass());
		}
	}

	// ---------------------------------------------
	// Lesen
	// ---------------------------------------------

	@Test
	public void testReadProperty_SimpleString() {
		final String key = "myKey";
		final String value = "helloConfigWorld";

		String configuredValue = store.readProperty(key, String.class);
		assertNull(configuredValue);

		writePropertyManual(key, new ConfigurationEntry(value));
		configuredValue = store.readProperty(key, String.class);
		assertEquals(value, configuredValue);
	}

	@Test
	public void testReadProperty_ComplexObject() {
		final String key = "myKey";
		final Object value = new ComplexObject();

		ComplexObject configuredValue = store.readProperty(key, ComplexObject.class);
		assertNull(configuredValue);

		writePropertyManual(key, new ConfigurationEntry(value));
		configuredValue = store.readProperty(key, ComplexObject.class);
		assertNotNull(configuredValue);
		assertEquals(value, configuredValue);
	}

	@Test
	public void testReadProperty_Validity() {
		final String key = "myKey";
		final String value = "helloConfigWorld";

		writePropertyManual(key, value, new GregorianCalendar(2013, Calendar.APRIL, 10).getTime(), null);
		assertEquals("Value ist ab 10.04. gueltig", value, store.readProperty(key, String.class));

		writePropertyManual(key, value, new GregorianCalendar(2013, Calendar.APRIL, 20).getTime(), null);
		assertNull("Value ist erst ab 20.04. gueltig", store.readProperty(key, String.class));

		writePropertyManual(key, value, new GregorianCalendar(2013, Calendar.APRIL, 1).getTime(), new GregorianCalendar(2013, Calendar.APRIL, 10).getTime());
		assertNull("Value ist nur vom 01.04. bis 10.04. gueltig", store.readProperty(key, String.class));

		writePropertyManual(key, value, new GregorianCalendar(2013, Calendar.APRIL, 15).getTime(), null);
		assertEquals("Value ist am 15.04. gueltig", value, store.readProperty(key, String.class));
		writePropertyManual(key, value, new GregorianCalendar(2013, Calendar.APRIL, 1).getTime(), new GregorianCalendar(2013, Calendar.APRIL, 15).getTime());
		assertEquals("Value ist am 15.04. gueltig", value, store.readProperty(key, String.class));
	}

	@Test(expected = ConfigurationException.class)
	public void testReadProperty_ValidityNotUnique() {
		final String key = "myKey";

		// im Zeitraum vom 05. bis 20.04. sind zwei Entries vorhanden. Das ist nicht erlaubt
		ConfigurationEntry configEntry = new ConfigurationEntry();
		configEntry.addEntry("value1", new GregorianCalendar(2013, Calendar.APRIL, 1).getTime(), new GregorianCalendar(2013, Calendar.APRIL, 20).getTime());
		configEntry.addEntry("value1", new GregorianCalendar(2013, Calendar.APRIL, 5).getTime(), null);
		writePropertyManual(key, configEntry);

		store.readProperty(key, String.class);
	}

	private void writePropertyManual(String key, String value, Date validFrom, Date validTo) {
		ConfigurationEntry configEntry = new ConfigurationEntry();
		configEntry.addEntry(value, validFrom, validTo);
		writePropertyManual(key, configEntry);
	}

	private void writePropertyManual(String key, ConfigurationEntry configEntry) {
		configDatabase.save(key, configEntry);
	}

	private void assertPropertySet(String key, String value) {
		String doc = configDatabase.load(key);
		assertNotNull(doc);
		assertEquals(value, new JsonPath().get(doc, JsonPath.path("entries[0]", "value")));
	}
}
