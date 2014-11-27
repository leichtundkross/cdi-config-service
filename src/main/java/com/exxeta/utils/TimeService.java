package com.exxeta.utils;

import java.util.Date;

/**
 * Der {@link TimeService} ist ein Helfer, fuer alle Probleme, bei denen die aktuelle Uhrzeit
 * benoetigt wird. Durch die Verwendung des Services, kann die aktuelle Uhrzeit fuer Tests einfach
 * gemockt werden.
 */
public class TimeService {

	public Date currentTime() {
		return new Date(currentTimeMillis());
	}

	public long currentTimeMillis() {
		return System.currentTimeMillis();
	}
}