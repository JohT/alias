package org.alias.example.customized.resourcebundle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CustomizedSimpleResourceBundleTest {

	private static final String PACKAGE = CustomizedResourceBundleFileType.class.getPackage().getName();
	private ResourceBundle bundle;

	@BeforeEach
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
