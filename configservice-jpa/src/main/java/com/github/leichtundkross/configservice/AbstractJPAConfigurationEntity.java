package com.github.leichtundkross.configservice;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;

@Entity(name = "ConfigurationEntity")
@Inheritance
@DiscriminatorColumn(name = "PROPERTY_TYPE")
@Table(name = "CONFIG")
public abstract class AbstractJPAConfigurationEntity {

	@Id
	private String name;

	String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}
}