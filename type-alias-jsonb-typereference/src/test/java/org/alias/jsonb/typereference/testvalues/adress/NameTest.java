package org.alias.jsonb.typereference.testvalues.adress;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

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
