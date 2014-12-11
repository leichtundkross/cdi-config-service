package com.github.leichtundkross.configservice;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("S")
public class StringJPAConfigurationEntity extends AbstractJPAConfigurationEntity {

	private String stringValue;

	String getStringValue() {
		return stringValue;
	}

	void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
}