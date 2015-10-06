package com.github.leichtundkross.configservice;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("String")
public class StringConfigEntry extends AbstractConfigEntry<String> {

	private String stringValue;

	public String getValue() {
		return stringValue;
	}

	public void setValue(String val) {
		this.stringValue = val;
	}
}
