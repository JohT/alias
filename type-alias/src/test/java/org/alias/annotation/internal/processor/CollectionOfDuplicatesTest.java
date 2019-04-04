package org.alias.annotation.internal.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

public class CollectionOfDuplicatesTest {

	/**
	 * class under test.
	 */
	private Collection<String> collection = CollectionOfDuplicates.collectionOfDuplicates();

	@Test
	public void collectionShouldBeEmptyIfThereIsNoDuplicateElement() {
		collection.add("TestElement");
		collection.add("AnotherNonDuplicateTestElement");

		assertFalse(collection.iterator().hasNext());
		assertTrue(collection.isEmpty());
		assertFalse(collection.contains("TestElement"));
		assertFalse(collection.contains("AnotherNonDuplicateTestElement"));
	}

	@Test
	public void collectionShouldBeEmptyIfTheDuplicateElementHadBeenRemoved() {
		collection.add("TestElement");
		collection.add("TestElement");
		Iterator<String> elements = collection.iterator();
		elements.next();
		elements.remove();

		assertFalse(collection.iterator().hasNext());
		assertTrue(collection.isEmpty());
		assertFalse(collection.contains("TestElement"));
	}

	@Test
	public void collectionShouldBeEmptyAfterClear() {
		collection.add("TestElement");
		collection.add("TestElement");
		collection.clear();

		assertFalse(collection.iterator().hasNext());
		assertTrue(collection.isEmpty());
		assertFalse(collection.contains("TestElement"));
	}

	@Test
	public void collectionShouldContainTheDuplicateElement() {
		String expectedElement = "TestElement";
		collection.add(expectedElement);
		collection.add(expectedElement);
		collection.add("AnotherNonDuplicateTestElement");

		Iterator<String> elements = collection.iterator();
		assertEquals(expectedElement, elements.next());
		assertFalse(elements.hasNext());
		assertFalse(collection.isEmpty());
		assertTrue(collection.contains(expectedElement));
	}
}