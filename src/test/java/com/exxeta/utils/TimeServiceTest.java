package com.exxeta.utils;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.exxeta.utils.TimeService;

public class TimeServiceTest {

	private TimeService ts = new TimeService();

	@Test
	public void testCcurrentTime() {
		long now = System.currentTimeMillis();
		assertTrue(ts.currentTimeMillis() >= now);
	}

	@Test
	public void testCurrentTimeMillis() {
		long nowInMs = System.currentTimeMillis();
		Date now = new Date(nowInMs);
		assertTrue(ts.currentTime().equals(now) || ts.currentTime().after(now));
	}
}