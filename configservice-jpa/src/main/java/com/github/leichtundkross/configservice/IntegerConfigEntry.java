package com.github.leichtundkross.configservice;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Integer")
public class IntegerConfigEntry extends AbstractConfigEntry<Integer> {

	private Integer integerValue;

	public Integer getValue() {
		return integerValue;
	}

	public void setValue(Integer val) {
		this.integerValue = val;
	}
}
