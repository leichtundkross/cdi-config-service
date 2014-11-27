package com.exxeta.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.exxeta.utils.GsonHelper;
import com.google.gson.Gson;

public class GsonHelperTest {

	private static final Gson GSON = new Gson();

	public enum MyEnum {
		VALUE1, VALUE2;
	}

	public class POJO {
		boolean booleanValue;
		byte byteValue;
		char charValue;
		Number numberValue;
		int intValue;
		short shortValue;
		long longValue;
		double doubleValue;
		float floatValue;
		BigDecimal bigDecimalValue;
		Date dateValue;
		MyEnum myEnum;
	}

	@Test
	public void testParseJsonWithInvalidPrimitiveValues() {
		String json = "{\"booleanValue\" : \"kein boolean\"}";
		POJO pojo = GSON.fromJson(json, POJO.class);
		assertEquals(false, pojo.booleanValue);

		json = "{\"byteValue\" : \"keine Zahl\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertEquals(0, pojo.byteValue);

		json = "{\"charValue\" : \"langer Text\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertEquals('l', pojo.charValue);
		json = "{\"charValue\" : \"\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertEquals(' ', pojo.charValue);

		json = "{\"numberValue\" : \"das ist keine Zahl\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertEquals(0.0, pojo.doubleValue, 0);
		json = "{\"numberValue\" : NaN}";
		pojo = GSON.fromJson(json, POJO.class);
		assertEquals(0.0, pojo.doubleValue, 0);

		json = "{\"intValue\" : \"text im int Feld?!\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertEquals(0, pojo.intValue);

		json = "{\"shortValue\" : \"text im int Feld?!\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertEquals(0, pojo.shortValue);

		json = "{\"longValue\" : \"das ist keine Zahl\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertEquals(0l, pojo.longValue);

		json = "{\"doubleValue\" : \"das ist keine Zahl\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertEquals(0.0, pojo.doubleValue, 0);
		json = "{\"doubleValue\" : NaN}";
		pojo = GSON.fromJson(json, POJO.class);
		assertEquals(0.0, pojo.doubleValue, 0);

		json = "{\"floatValue\" : \"das ist keine Zahl\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertEquals(0.0, pojo.floatValue, 0);
		json = "{\"floatValue\" : NaN}";
		pojo = GSON.fromJson(json, POJO.class);
		assertEquals(0.0, pojo.floatValue, 0);

		json = "{\"bigDecimalValue\" : \"das ist keine Zahl\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertNull(pojo.bigDecimalValue);

		json = "{\"dateValue\" : \"komisches Datum\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertNull(pojo.dateValue);

		json = "{\"myEnum\" : \"\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertNull(pojo.myEnum);

		json = "{\"myEnum\" : \"keinEnumWert\"}";
		pojo = GSON.fromJson(json, POJO.class);
		assertNull(pojo.myEnum);
	}

	@Test
	public void testDateFormat() {
		GregorianCalendar cal = new GregorianCalendar(2012, Calendar.JULY, 30);
		final Date date = cal.getTime();

		String dateAsJson = GSON.toJson(date);
		assertEquals("\"2012-07-30T00:00:00.000+0200\"", dateAsJson);
		assertEquals(date, GSON.fromJson(dateAsJson, Date.class));
	}

	@Test
	public void testDoubleValues() {
		assertEquals(Double.valueOf(1d), GSON.fromJson("1", Double.class));
		assertEquals(Double.valueOf(2.33d), GSON.fromJson("2.33", Double.class));
		assertEquals("Default-Value 0", Double.valueOf(0d), GSON.fromJson("a", Double.class));
		assertEquals("Default-Value 0", Double.valueOf(0d), GSON.fromJson("\"a\"", Double.class));

		assertEquals(Double.valueOf(1d), GSON.fromJson("1", double.class));
		assertEquals(Double.valueOf(2.33d), GSON.fromJson("2.33", double.class));
		assertEquals("Default-Value 0", Double.valueOf(0d), GSON.fromJson("a", double.class));
		assertEquals("Default-Value 0", Double.valueOf(0d), GSON.fromJson("\"a\"", double.class));
	}

	@Test
	public void testBuildJson() {
		Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("statusCode", 404);
		jsonMap.put("fehlertext", "Nicht gefunden");

		String json = GSON.toJson(jsonMap);
		assertTrue(json.startsWith("{"));
		assertTrue(json.endsWith("}"));
		assertTrue(json.contains("\"statusCode\": 404"));
		assertTrue(json.contains("\"fehlertext\": \"Nicht gefunden\""));
	}

	@Test
	public void testIsJson() {
		String notValid = "dies ist kein JSON";
		String valid = "{\"test\":\"value\"}";

		assertTrue(GsonHelper.isJson(valid));
		assertFalse(GsonHelper.isJson(notValid));

		// Test hinzugefuegt da GSON.fromJson(...) null anstatt JsonParseException
		// liefert sofern ein String mit # beginnt
		notValid = "#dies ist kein JSON";

		assertFalse(GsonHelper.isJson(notValid));

		// Pendant zu notValid2, null ist valid
		valid = null;
		assertTrue(GsonHelper.isJson(valid));
	}

	@Test
	public void testIsJsonArrayIndexOOBException() {
		String notValid = "/";
		assertFalse(GsonHelper.isJson(notValid));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_json_aus_flachen_objekten_und_property_composites() {
		String json = "{\"attribut1\":\"valueAtt1\", \"kategorie2\":{\"aspekt2\":\"value2\"},\"kategorie1\":{\"aspekt1\":\"value1\"}}";

		Map<String, Object> jsonToVariables = GsonHelper.jsonToVariables(json);

		assertNotNull(jsonToVariables.get("attribut1"));
		assertNotNull(jsonToVariables.get("kategorie1"));
		assertNotNull(jsonToVariables.get("kategorie2"));

		assertTrue(jsonToVariables.get("attribut1") instanceof String);
		assertTrue(jsonToVariables.get("kategorie1") instanceof Map);
		assertTrue(jsonToVariables.get("kategorie2") instanceof Map);

		String attribut1 = (String) jsonToVariables.get("attribut1");
		Map<String, Object> kategorie1Map = (Map<String, Object>) jsonToVariables.get("kategorie1");
		Map<String, Object> kategorie2Map = (Map<String, Object>) jsonToVariables.get("kategorie2");

		assertNotNull(attribut1);
		assertEquals("valueAtt1", attribut1);

		assertNotNull(kategorie1Map.get("aspekt1"));
		assertEquals("value1", kategorie1Map.get("aspekt1").toString());

		assertNotNull(kategorie2Map.get("aspekt2"));
		assertEquals("value2", kategorie2Map.get("aspekt2").toString());
	}

}
