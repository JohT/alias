package org.alias.annotation.internal.processor;

import java.util.Comparator;

/**
 * Gets the most specific package name first, and more general ones later.<br>
 * 
 * @author JohT
 */
enum MostSpecificPackageFirst implements Comparator<String> {

	INSTANCE;

	@Override
	public int compare(String packageName1, String packageName2) {
		long package2Depth = getPackagesDepth(packageName2);
		long package1Depth = getPackagesDepth(packageName1);
		if (package2Depth == package1Depth) {
			// On same hierarchy level, packages are sorted ascending by their name.
			return packageName1.compareTo(packageName2);
		}
		// On different hierarchy levels packages are sorted by their depth descending.
		return Long.compare(package2Depth, package1Depth);
	}

	private static long getPackagesDepth(String packagename) {
		return packagename.isEmpty() ? -1 : packagename.chars().filter(c -> c == '.').count();
	}
}