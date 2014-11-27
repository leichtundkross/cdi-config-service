package com.exxeta.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.exxeta.utils.JsonPath;
import com.google.gson.Gson;

public class JsonPathTest {

	private static class MyContainer {
		Betrag betrag = Betrag.fuer(99l, 95l);
		String string1 = "das ist ein String";

		public MyContainer() {
		}

		@Override
		public int hashCode() {
			return 1;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			return true;
		}
	}

	private JsonPath jsonPath = new JsonPath();

	@Test
	public void testGet() {
		String json = "{\"objekt1\" : {\"key1\" : \"value1\", \"key2\" : \"value2\"}, \"objekt3\" : \"value3\" }";

		String expression = "$.objekt1";
		String extracted = jsonPath.get(json, expression);
		assertNotNull(extracted);
		assertEquals("{\"key2\":\"value2\",\"key1\":\"value1\"}", extracted);

		String expression2 = "$.objekt3";
		String extracted2 = jsonPath.get(json, expression2);
		assertNotNull(extracted2);
		assertEquals("value3", extracted2);
	}

	@Test
	public void testGet_Invalid() {
		String json = "{\"objekt\" : \"value\"}";
		String extracted = jsonPath.get(json, "$.unbekanntesAttribut");
		assertNull(extracted);

		extracted = jsonPath.get(json, "invalid Expression");
		assertNull(extracted);

		extracted = jsonPath.get("das ist doch kein json!", "invalid Expression");
		assertNull(extracted);
	}

	@Test
	public void testGet_ConcreteObjekt() {
		String json = new Gson().toJson(new MyContainer());

		Betrag betrag = jsonPath.get(json, "$.betrag", Betrag.class);
		assertNotNull(betrag);
		assertEquals(new MyContainer().betrag, betrag);

		String string = jsonPath.get(json, "$.string1", String.class);
		assertNotNull(string);
		assertEquals(new MyContainer().string1, string);

		MyContainer myContainer = jsonPath.get(json, "$", MyContainer.class);
		assertNotNull(myContainer);
		assertEquals(new MyContainer(), myContainer);

		json = "{\"value1\":\"2013-01-11T16:29:39.000+0100\"}";
		Date transformedDate = jsonPath.get(json, "$.value1", Date.class);
		assertNotNull(transformedDate);
		assertEquals(new GregorianCalendar(2013, Calendar.JANUARY, 11, 16, 29, 39).getTime(),
				transformedDate);
	}

	private static String trimJson(String json) {
		return json.replaceAll("\\s", "");
	}
}