package com.github.leichtundkross.configservice;

import java.util.Date;

class ComplexObject {

	private String s;
	private int i;
	private Date d;

	ComplexObject() {
		super();
	}

	ComplexObject(String s, int i, Date d) {
		this.s = s;
		this.i = i;
		this.d = d;
	}

	@Override
	public String toString() {
		return "ComplexObject [s=" + s + ", i=" + i + ", d=" + d + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((d == null) ? 0 : d.hashCode());
		result = prime * result + i;
		result = prime * result + ((s == null) ? 0 : s.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ComplexObject other = (ComplexObject) obj;
		if (d == null) {
			if (other.d != null) {
				return false;
			}
		} else if (!d.equals(other.d)) {
			return false;
		}
		if (i != other.i) {
			return false;
		}
		if (s == null) {
			if (other.s != null) {
				return false;
			}
		} else if (!s.equals(other.s)) {
			return false;
		}
		return true;
	}
}