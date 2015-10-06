package com.github.leichtundkross.configservice;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Boolean")
public class BooleanConfigEntry extends AbstractConfigEntry<Boolean> {

	private Boolean booleanValue;

	public Boolean getValue() {
		return booleanValue;
	}

	public void setValue(Boolean val) {
		this.booleanValue = val;
	}
}
