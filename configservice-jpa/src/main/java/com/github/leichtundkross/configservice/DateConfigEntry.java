package com.github.leichtundkross.configservice;

import java.util.Date;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("Date")
public class DateConfigEntry extends AbstractConfigEntry<Date> {

	private Date dateValue;

	public Date getValue() {
		return dateValue;
	}

	public void setValue(Date val) {
		this.dateValue = val;
	}
}
