package com.exxeta.configservice;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ConfigDatabaseProducer {

	@Produces
	public ConfigDatabase create() {
		throw new RuntimeException("implement me. DB Provider specific");
	}
}