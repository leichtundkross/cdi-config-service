package com.github.leichtundkross.configservice.sample;

import javax.enterprise.inject.Produces;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.github.leichtundkross.configservice.MongoDBConfigurationEntryDAO;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * The {@link MongoDBConfigurationEntryDAO} needs any kind of initialized {@link Datastore} that can be injected by the container.
 */
public class DatastoreProducer {

	@Produces
	Datastore connectToDB() {
		String server = "localhost";
		int port = 27017;

		MongoClientURI mongoClientURI = new MongoClientURI("mongodb://" + server + ":" + port + "/configservice-sample");
		MongoClient mongoClient = new MongoClient(mongoClientURI);
		return new Morphia().createDatastore(mongoClient, mongoClientURI.getDatabase());
	}
}
