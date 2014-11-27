package com.exxeta.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.exxeta.utils.DateRangeValidator;

public class DateRangeValidatorTest {

	private DateRangeValidator validator = new DateRangeValidator();

	@Test
	public void testInRangeGeschlossenesIntervall() {
		assertTrue(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), new GregorianCalendar(2013,
				Calendar.MAY, 2).getTime()));
		assertTrue(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), new GregorianCalendar(2013, Calendar.MAY, 1).getTime(), new GregorianCalendar(2013,
				Calendar.MAY, 5).getTime()));
		assertFalse(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 1).getTime(), new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), new GregorianCalendar(2013,
				Calendar.MAY, 5).getTime()));
		assertFalse(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 31).getTime(), new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), new GregorianCalendar(2013,
				Calendar.MAY, 5).getTime()));
	}

	@Test
	public void testInRangeNachObenOffenessIntervall() {
		assertTrue(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), null));
		assertTrue(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), new GregorianCalendar(2013, Calendar.MAY, 1).getTime(), null));
		assertFalse(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 1).getTime(), new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), null));
		assertTrue(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 31).getTime(), new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), null));
	}

	@Test
	public void testInRangeNachUntenOffenesIntervall() {
		assertTrue(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), null, new GregorianCalendar(2013, Calendar.MAY, 2).getTime()));
		assertTrue(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), null, new GregorianCalendar(2013, Calendar.MAY, 5).getTime()));
		assertTrue(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 1).getTime(), null, new GregorianCalendar(2013, Calendar.MAY, 5).getTime()));
		assertFalse(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 31).getTime(), null, new GregorianCalendar(2013, Calendar.MAY, 5).getTime()));
	}

	@Test
	public void testInRangeUnbegrenztesIntervall() {
		assertTrue(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), null, null));
		assertTrue(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 2).getTime(), null, null));
		assertTrue(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 1).getTime(), null, null));
		assertTrue(validator.inRange(new GregorianCalendar(2013, Calendar.MAY, 31).getTime(), null, null));
	}
}