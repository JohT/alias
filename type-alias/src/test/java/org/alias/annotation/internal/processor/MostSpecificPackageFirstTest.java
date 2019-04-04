package org.alias.annotation.internal.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import org.junit.Test;

public class MostSpecificPackageFirstTest {

	/**
	 * class under test.
	 */
	private Comparator<String> comparator = MostSpecificPackageFirst.INSTANCE;

	@Test
	public void subPackageShouldBeLower() {
		int result = comparator.compare("org.sub", "org");
		assertTrue(String.valueOf(result), result < 0);
	}

	@Test
	public void firstLevelPackageShouldBeLowerThanDefaultPackage() {
		int result = comparator.compare("org", "");
		assertTrue(String.valueOf(result), result < 0);
	}

	@Test
	public void outerPackageShouldBeHigherThanSubPackage() {
		int result = comparator.compare("org", "org.sub");
		assertTrue(String.valueOf(result), result > 0);
	}

	@Test
	public void defaultPackageShouldBeHigherThanFirstLevelPackage() {
		int result = comparator.compare("", "org");
		assertTrue(String.valueOf(result), result > 0);
	}

	@Test
	public void samePackageDepthShouldBeHigherIfNameInAlphabeticalOrder() {
		int result = comparator.compare("org.sub.a", "org.sub.b");
		assertTrue(String.valueOf(result), result < 0);
	}

	@Test
	public void samePackageDepthShouldBeLowerIfNameInCounterAlphabeticalOrder() {
		int result = comparator.compare("org.sub.b", "org.sub.a");
		assertTrue(String.valueOf(result), result > 0);
	}

	@Test
	public void equalPackageShouldBeEqual() {
		int result = comparator.compare("org", "org");
		assertEquals(0, result);
	}
}