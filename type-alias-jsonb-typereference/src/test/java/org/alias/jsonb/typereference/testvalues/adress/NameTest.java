package org.alias.jsonb.typereference.testvalues.adress;

import static org.junit.Assert.assertEquals;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.Test;

public class NameTest {

	private static final String FIRSTNAME = "John";
	private static final String SURNAME = "Doe";
	/**
	 * class under test.
	 */
	private Name name;

	@Test
	public void jsonbSerializable() {
		name = new Name(FIRSTNAME, SURNAME);
		assertEquals(name, serializeAndDeserialize(name, JsonbBuilder.create()));
	}

	@SuppressWarnings("unchecked")
	private static <T> T serializeAndDeserialize(T object, Jsonb jsonb) {
		return (T) jsonb.fromJson(jsonb.toJson(object), object.getClass());
	}

}
