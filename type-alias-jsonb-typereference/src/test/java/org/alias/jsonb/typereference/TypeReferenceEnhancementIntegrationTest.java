package org.alias.jsonb.typereference;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.alias.jsonb.typereference.testvalues.adress.Name;
import org.alias.jsonb.typereference.testvalues.adress.Names;
import org.alias.jsonb.typereference.testvalues.animals.Animal;
import org.alias.jsonb.typereference.testvalues.animals.Horse;
import org.alias.jsonb.typereference.testvalues.animals.Monkey;
import org.alias.jsonb.typereference.testvalues.animals.Zoo;
import org.junit.Before;
import org.junit.Test;

public class TypeReferenceEnhancementIntegrationTest {

	/**
	 * class under test.
	 */
	private TypeReferenceEnhancer<Animal> typeReferenceEnhancer = new TypeReferenceEnhancer<Animal>() {
	};

	@Before
	public void setUp() {
		new TestJsonbConfigProvider().joinConfig(new JsonbConfig().withAdapters(typeReferenceEnhancer));
	}

	@Test
	public void adapterDoesNotAffectOtherObject() {
		Name name = new Name("John", "Doe");
		Name deserialized = serializeAndDeserialize(name);
		assertEquals(name, deserialized);
	}

	@Test
	public void adapterDoesNotAffectSubObject() {
		Monkey monkey = new Monkey("Charley");
		Monkey deserialized = serializeAndDeserialize(monkey);
		assertEquals(monkey, deserialized);
	}

	@Test
	public void includedNullValuesDeserializable() {
		new TestJsonbConfigProvider().joinConfig(new JsonbConfig().withNullValues(Boolean.TRUE));
		Animal nullContent = new Monkey(null);
		assertEquals(nullContent, serializeAndDeserialize(nullContent));
	}

	@Test
	public void staticTypedListDeserializable() {
		Names names = new Names(Arrays.asList(new Name("John", "Doe"), new Name("Richard", "Roe")));
		Names deserialized = serializeAndDeserialize(names);
		assertEquals(names.toString(), deserialized.toString());
		assertEquals(names, deserialized);
	}

	@Test
	public void dynamicTypedListElementsDeserializable() {
		Zoo zoo = new Zoo(Arrays.asList(new Monkey("Nilsson"), new Horse("Uncle")));
		Zoo deserialized = serializeAndDeserialize(zoo);
		assertEquals(zoo.toString(), deserialized.toString());
		assertEquals(zoo, deserialized);
	}

	@SuppressWarnings("unchecked")
	private <T> T serializeAndDeserialize(T object) {
		JsonbConfig config = new TestJsonbConfigProvider().getJsonbConfig();
		Jsonb jsonb = JsonbBuilder.create(config);
		String json = printed(jsonb.toJson(object));
		return (T) jsonb.fromJson(json, object.getClass());
	}

	private static <T> T printed(T json) {
		System.out.println(json);
		return json;
	}
}