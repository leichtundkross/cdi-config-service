package com.github.leichtundkross.configservice.sample;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import com.github.leichtundkross.configservice.ConfigurationService;
import com.github.leichtundkross.configservice.annotation.IntegerProperty;
import com.github.leichtundkross.configservice.annotation.Property;

/**
 * Runs a timer that prints out some random config properties every 5 seconds.
 */
@Startup
@Singleton
public class Demo {

	@Inject
	private ConfigurationService configService;

	@Inject
	@Property
	@IntegerProperty(key = "com.github.leichtundkross.demo.property0", defaultValue = 2015)
	private Integer property0;

	@Schedule(second = "*/5", minute = "*", hour = "*", persistent = false)
	public void accessConfigService() {
		System.out.println("Injected property >>>> " + property0);

		String property1 = configService.readProperty("com.github.leichtundkross.demo.property1", String.class, "my value");
		System.out.println("Manally read property >>>> " + property1);
	}
}
