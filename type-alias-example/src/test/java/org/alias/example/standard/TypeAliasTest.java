package org.alias.example.standard;


import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;


public class TypeAliasTest {

	private ResourceBundle bundle;

	@Before
	public void setUp() {
		bundle = ResourceBundle.getBundle("TypeAlias");
	}

	@Test
	public void customizedResourceBundleFileTypeRegistered() {
		assertEquals(DefaultFileType.class, bundle.getObject("AliasForDefaultFile"));
	}

	@Test
	public void anotherDefaultFileTypeRegistered() {
		assertEquals(AnotherDefaultFileType.class, bundle.getObject("AliasForAnotherDefaultFile"));
	}

}
