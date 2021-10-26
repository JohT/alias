package org.alias.example.customized.external;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CustomizedExternalResourceBundleTest {

	private static final String PACKAGE = ExternalAnnotationResourceBundleFileType.class.getPackage().getName();
	private ResourceBundle bundle;

	@BeforeEach
	public void setUp() {
		bundle = ResourceBundle.getBundle(PACKAGE + ".CustomizedExternalResourceBundle");
	}

	@Test
	public void onTopOfATypeInsideArrayOfAliasesDefinedNormalAliasIsRegistered() {
		assertEquals(ExternalAnnotationResourceBundleFileType.class, bundle.getObject("AliasForNotAnnotatedResourceBundleFile"));
	}

	@Test
	public void onTopOfATypeAnnotatedExternalAliasInsideTheSamePackageIsRegistered() {
		assertEquals(NotAnnotatedExternalResourceBundleFileType.class, bundle.getObject("AliasForExternalAlias"));
	}

	@Test
	public void onTopOfATypeAnnotatedExternalAliasOutsideThePackageIsRegistered() {
		assertEquals(Integer.class, bundle.getObject("Integer"));
	}

	@Test
	public void inPackageInfoAnnotatedExternalAliasIsRegistered() {
		assertEquals(String.class, bundle.getObject("String"));
	}

}
