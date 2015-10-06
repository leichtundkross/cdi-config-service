package com.github.leichtundkross;

import java.io.Serializable;

public class MySerializable implements Serializable {

	private static final long serialVersionUID = 1L;

	private String anotherString;

	public MySerializable(String anotherString) {
		this.anotherString = anotherString;
	}

	public String getAnotherString() {
		return anotherString;
	}
}