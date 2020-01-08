package org.alias.annotation.internal.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class TypeAliasTemplateFieldsTest {

	private TypeAliasTemplateFields fields;

	private static final String PACKAGE_NAME = "org";
	private static final String FILE_NAME = "TestAlias";
	private static final TypeAliasName ALIAS = alias("Alias", "org.AliasValueObject");

	private final Collection<TypeAliasName> aliases = new ArrayList<>(Arrays.asList(ALIAS));

	@Test
	public void containsPackagename() {
		fields = new TypeAliasTemplateFields(PACKAGE_NAME, FILE_NAME, aliases);
		assertEquals(PACKAGE_NAME, fields.getPackagename());
	}

	@Test
	public void containsFilename() {
		fields = new TypeAliasTemplateFields(PACKAGE_NAME, FILE_NAME, aliases);
		assertEquals(FILE_NAME, fields.getFilename());
	}

	@Test
	public void containsAliases() {
		fields = new TypeAliasTemplateFields(PACKAGE_NAME, FILE_NAME, aliases);
		assertEquals(aliases, fields.getAliases());
		assertNotSame(aliases, fields.getAliases());
	}

	@Test
	public void replacePlaceholderForPackagename() {
		fields = new TypeAliasTemplateFields(PACKAGE_NAME, FILE_NAME, aliases);
		assertEquals(PACKAGE_NAME, fields.replacePlaceholderInLine("{(packagename)}"));
	}

	@Test
	public void replacePlaceholderForFilename() {
		fields = new TypeAliasTemplateFields(PACKAGE_NAME, FILE_NAME, aliases);
		assertEquals(FILE_NAME, fields.replacePlaceholderInLine("{(filename)}"));
	}

	@Test
	public void replacePlaceholderForAliasName() {
		fields = new TypeAliasTemplateFields(PACKAGE_NAME, FILE_NAME, aliases);
		assertEquals(ALIAS.getAliasname(), fields.replacePlaceholderInLine("{(aliases.aliasname)}"));
	}

	@Test
	public void replacePlaceholderForAliasFullqualifiedname() {
		fields = new TypeAliasTemplateFields(PACKAGE_NAME, FILE_NAME, aliases);
		assertEquals(ALIAS.getFullqualifiedname(), fields.replacePlaceholderInLine("{(aliases.fullqualifiedname)}"));
	}

	@Test
	public void noLineIfTheLineIsOnlyMeantForPrimaryAliases() {
		fields = new TypeAliasTemplateFields(PACKAGE_NAME, FILE_NAME, aliases);
		assertEquals("", fields.replacePlaceholderInLine("{(aliases.aliasname)}{(aliases.only.primary)}"));
	}

	@Test
	public void skippedLineIfTheLineIsOnlyMeantForPrimaryAliases() {
		aliases.clear();
		aliases.add(primary(alias("PrimaryAlias", "org.AliasValueObject")));
		aliases.add(alias("SeconaryAlias", "org.AliasValueObject"));
		aliases.add(primary(alias("AnotherAlias", "org.AnotherAliasValueObject")));
		fields = new TypeAliasTemplateFields(PACKAGE_NAME, FILE_NAME, aliases);
		List<String> replacedLines = linesOf(fields.replacePlaceholderInLine("{(aliases.aliasname)}{(aliases.only.primary)}"));
		assertEquals("PrimaryAlias", replacedLines.get(0));
		assertEquals("AnotherAlias", replacedLines.get(1));
	}

	@Test
	public void replacePlaceholderForMultipleAliases() {
		TypeAliasName another = alias("AnotherAlias", "org.AnotherAliasValueObject");
		aliases.add(another);
		fields = new TypeAliasTemplateFields(PACKAGE_NAME, FILE_NAME, aliases);

		String replacedContents = fields.replacePlaceholderInLine("{(aliases.aliasname)},{(aliases.fullqualifiedname)}");

		assertEquals(ALIAS.getAliasname() + "," + ALIAS.getFullqualifiedname(), linesOf(replacedContents).get(0));
		assertEquals(another.getAliasname() + "," + another.getFullqualifiedname(), linesOf(replacedContents).get(1));
	}

	private List<String> linesOf(String replacedContents) {
		return Arrays.asList(replacedContents.split("\n"));
	}

	private static TypeAliasName alias(String aliasname, String type) {
		return TypeAliasName.builder().aliasName(aliasname).fullQualifiedName(type).build();
	}
	
	private static TypeAliasName primary(TypeAliasName original) {
		return TypeAliasName.builder().basedOn(original).primary(true).build();
	}
}