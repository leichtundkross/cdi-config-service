package com.github.leichtundkross.configservice;

import java.util.Date;

class TimeService {

	public Date currentTime() {
		return new Date(currentTimeMillis());
	}

	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}
}