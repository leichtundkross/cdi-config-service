package com.github.leichtundkross.configservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

@RunWith(MockitoJUnitRunner.class)
public class MongoDBConfigurationEntryDAOTest {

	@InjectMocks
	private MongoDBConfigurationEntryDAO dao;

	@Spy
	private static Datastore ds;

	private static MongodExecutable mongodExecutable;

	@BeforeClass
	public static void startMongo() {
		MongodStarter starter = MongodStarter.getDefaultInstance();
		int port = 37017;
		try {
			IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION).net(new Net(port, Network.localhostIsIPv6())).build();
			mongodExecutable = starter.prepare(mongodConfig);
			mongodExecutable.start();

			MongoClient mongo = new MongoClient("localhost", port);
			Morphia morphia = new Morphia();
			morphia.map(ConfigEntry.class);
			ds = morphia.createDatastore(mongo, "test");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterClass
	public static void shutdownMongo() {
		if (mongodExecutable != null) {
			mongodExecutable.stop();
		}
	}

	@Before
	public void cleanDB() {
		ds.delete(ds.createQuery(ConfigEntry.class));
	}

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