package org.alias.annotation.internal.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

public class MostSpecificPackageFirstTest {

	/**
	 * class under test.
	 */
	private Comparator<String> comparator = MostSpecificPackageFirst.INSTANCE;

	@Test
	public void subPackageShouldBeLower() {
		int result = comparator.compare("org.sub", "org");
		assertTrue(result < 0, String.valueOf(result));
	}

	@Test
	public void firstLevelPackageShouldBeLowerThanDefaultPackage() {
		int result = comparator.compare("org", "");
		assertTrue(result < 0, String.valueOf(result));
	}

	@Test
	public void outerPackageShouldBeHigherThanSubPackage() {
		int result = comparator.compare("org", "org.sub");
		assertTrue(result > 0, String.valueOf(result));
	}

	@Test
	public void defaultPackageShouldBeHigherThanFirstLevelPackage() {
		int result = comparator.compare("", "org");
		assertTrue(result > 0, String.valueOf(result));
	}

	@Test
	public void samePackageDepthShouldBeHigherIfNameInAlphabeticalOrder() {
		int result = comparator.compare("org.sub.a", "org.sub.b");
		assertTrue(result < 0, String.valueOf(result));
	}

	@Test
	public void samePackageDepthShouldBeLowerIfNameInCounterAlphabeticalOrder() {
		int result = comparator.compare("org.sub.b", "org.sub.a");
		assertTrue(result > 0, String.valueOf(result));
	}

	@Test
	public void equalPackageShouldBeEqual() {
		int result = comparator.compare("org", "org");
		assertEquals(0, result);
	}
}