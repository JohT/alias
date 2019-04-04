package org.alias.example.customized.external;


import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

public class CustomizedExternalResourceBundleTest {

	private static final String PACKAGE = ExternalAnnotationResourceBundleFileType.class.getPackage().getName();
	private ResourceBundle bundle;

	@Before
	public void setUp() {
		bundle = ResourceBundle.getBundle(PACKAGE + ".CustomizedExternalResourceBundle");
	}

	@Test
	public void anotherCustomizedResourceBundleFileTypeRegistered() {
		assertEquals(ExternalAnnotationResourceBundleFileType.class, bundle.getObject("AliasForNotAnnotatedResourceBundleFile"));
	}

	@Test
	public void externalAnnotationResourceBundleFileTypeRegistered() {
		assertEquals(NotAnnotatedExternalResourceBundleFileType.class, bundle.getObject("AliasForExternalAlias"));
	}

}
