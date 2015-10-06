package com.github.leichtundkross.configservice;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Double")
public class DoubleConfigEntry extends AbstractConfigEntry<Double> {

	private Double doubleValue;

	public Double getValue() {
		return doubleValue;
	}

	public void setValue(Double val) {
		this.doubleValue = val;
	}
}
