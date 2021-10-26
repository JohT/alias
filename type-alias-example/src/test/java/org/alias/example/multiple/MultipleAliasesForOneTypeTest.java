package org.alias.example.multiple;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ResourceBundle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class MultipleAliasesForOneTypeTest {

	private ResourceBundle bundle;

	@BeforeEach
	public void setUp() {
		bundle = ResourceBundle.getBundle("TypeAlias");
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
