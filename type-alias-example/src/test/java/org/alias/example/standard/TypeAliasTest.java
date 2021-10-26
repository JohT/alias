package org.alias.example.standard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ResourceBundle;

import org.alias.example.multiple.MultipleAliasedFileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeAliasTest {

	private ResourceBundle bundle;

	@BeforeEach
	public void setUp() {
		bundle = ResourceBundle.getBundle("TypeAlias");
	}

	@Test
	public void standardDefaultFileTypeRegistered() {
		assertEquals(DefaultFileType.class, bundle.getObject("AliasForDefaultFile"));
	}

	@Test
	public void anotherDefaultFileTypeRegistered() {
		assertEquals(AnotherDefaultFileType.class, bundle.getObject("AliasForAnotherDefaultFile"));
	}

	@Test
	public void multipleAliases() {
		assertEquals(MultipleAliasedFileType.class, bundle.getObject("PrimaryAlias"));
		assertEquals(MultipleAliasedFileType.class, bundle.getObject("SecondaryAlias"));
	}
	
	@Test
	public void primaryAliasForMultipleAliasedType() {
		assertEquals("PrimaryAlias", bundle.getObject(MultipleAliasedFileType.class.getName()));
	}
}
