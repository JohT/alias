package org.alias.example.customized.resourcebundle;


import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

public class CustomizedSimpleResourceBundleTest {

	private static final String PACKAGE = CustomizedResourceBundleFileType.class.getPackage().getName();
	private ResourceBundle bundle;

	@Before
	public void setUp() {
		bundle = ResourceBundle.getBundle(PACKAGE + ".CustomizedSimpleResourceBundle");
	}

	@Test
	public void customizedResourceBundleFileTypeRegistered() {
		assertEquals(CustomizedResourceBundleFileType.class, bundle.getObject("AliasForCustomizedResourceBundleFile"));
	}

	@Test
	public void anotherCustomizedResourceBundleFileTypeRegistered() {
		assertEquals(AnotherCustomizedResourceBundleFileType.class, bundle.getObject("AliasForAnotherCustomizedResourceBundleFile"));
	}
}
