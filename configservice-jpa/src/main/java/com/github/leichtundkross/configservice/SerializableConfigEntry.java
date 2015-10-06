package com.github.leichtundkross.configservice;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
@DiscriminatorValue("Serializable")
public class SerializableConfigEntry extends AbstractConfigEntry<Serializable> {

	@Lob
	private Serializable serValue;

	public Serializable getValue() {
		return serValue;
	}

	public void setValue(Serializable val) {
		this.serValue = val;
	}
}
