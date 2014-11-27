package com.exxeta.utils;

import java.util.Date;

public class DateRangeValidator {

	public boolean inRange(Date value, Date from, Date to) {
		if (value == null) {
			return false;
		}

		boolean nichtKleinerAlsFrom = (from == null || !value.before(from));
		boolean nichtGroesserAlsTo = (to == null || !value.after(to));
		return nichtKleinerAlsFrom && nichtGroesserAlsTo;
	}
}