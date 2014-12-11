package com.github.leichtundkross.configservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

import com.github.leichtundkross.configservice.MongoDBConfigurationEntity;
import com.github.leichtundkross.configservice.MongoDBConfigurationEntryDAO;
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
			morphia.map(MongoDBConfigurationEntity.class);
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
		ds.delete(ds.createQuery(MongoDBConfigurationEntity.class));
	}

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