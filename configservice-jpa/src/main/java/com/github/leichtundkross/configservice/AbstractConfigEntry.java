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
public abstract class AbstractConfigEntry<T> implements ConfigEntry<T> {

	@Id
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}