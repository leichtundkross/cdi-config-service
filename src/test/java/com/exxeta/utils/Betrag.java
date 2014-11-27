package com.exxeta.utils;

import java.text.DecimalFormat;

@Deprecated
public final class Betrag implements Comparable<Betrag> {

	private static final int CENT_IN_EURO = 100;

	private final long euro;

	private final long cent;

	/**
	 * Default-Constructor for serializing to mongodb.
	 */
	private Betrag() {
		this.euro = 0;
		this.cent = 0;
	}

	private Betrag(long euro, long cent) {
		this.euro = euro;
		this.cent = cent;
	}

	public static Betrag fuer(long euro, long cent) {
		if (cent < 0) {
			throw new IllegalArgumentException("Cent Betraege koennen nicht negativ sein!");
		}

		return new Betrag(euro, cent);
	}

	public static Betrag valueOf(String betrag) {
		String[] strings;
		if (betrag.contains(",")) {
			strings = betrag.split("\\,");
		} else {
			strings = betrag.split("\\.");
		}

		if (strings.length == 0) {
			return Betrag.fuer(Integer.valueOf(betrag), 0);
		}
		Integer euro = Integer.valueOf(strings[0]);
		Integer cent = strings.length == 2 ? parseWertfromString(strings[1]) : 0;
		if (cent >= CENT_IN_EURO) {
			euro = euro + 1;
			cent = cent - CENT_IN_EURO;
		}
		return Betrag.fuer(euro, cent);
	}

	private static int parseWertfromString(String wert) {
		if (wert.length() > 2) {
			Integer value = Integer.valueOf(wert.substring(0, 2));
			// aufrunden
			if (wert.substring(2, 3).compareTo("5") > 0) {
				value = value + 1;
			}
			return value;
		}

		return Integer.valueOf(wert);
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("#########0.00 €");
		return df.format(inCent() / (double) CENT_IN_EURO);
	}

	// TODO hack fuer aspekt getter
	public String getToString() {
		return toString();
	}

	public static Betrag summe(Betrag betrag1, Betrag betrag2) {
		long summe = betrag1.inCent() + betrag2.inCent();
		return Betrag.fuer(summe / CENT_IN_EURO, Math.abs(summe) % CENT_IN_EURO);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (cent ^ (cent >>> 32));
		result = prime * result + (int) (euro ^ (euro >>> 32));
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
		Betrag other = (Betrag) obj;
		if (cent != other.cent) {
			return false;
		}
		if (euro != other.euro) {
			return false;
		}
		return true;
	}

	public static Betrag produkt(int multiplikator, Betrag multiplikand) {
		long produktInCent = multiplikator * (CENT_IN_EURO * multiplikand.euro + multiplikand.cent);
		return Betrag.fuer(produktInCent / CENT_IN_EURO, produktInCent % CENT_IN_EURO);
	}

	public String getNumericCell() {
		return "<div class='numericCell'>" + toString() + "</div>";
	}

	public boolean isMoreThan(Betrag betrag) {
		return this.compareTo(betrag) == 1;
	}

	public int compareTo(Betrag betrag) {
		if (this.equals(betrag)) {
			return 0;
		} else if (this.inCent() > betrag.inCent()) {
			return 1;
		} else {
			return -1;
		}
	}

	public String toNumericString() {
		return euro + "." + String.valueOf(CENT_IN_EURO + cent).substring(1);
	}

	public double toDoubleValue() {
		return euro + (cent / (double) CENT_IN_EURO);
	}

	private long inCent() {
		long value = (Math.abs(euro) * CENT_IN_EURO) + cent;
		if (euro < 0) {
			value *= -1;
		}

		return value;
	}

	public String toFormattedString() {
		DecimalFormat df = new DecimalFormat("#," + "###,###,##0.00 €");
		return df.format(inCent() / (double) CENT_IN_EURO);
	}
}