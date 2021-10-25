package org.alias.annotation.internal.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.alias.annotation.TypeAliasGeneratedFile.Template;
import org.alias.annotation.internal.processor.TypeAliasPackageInfo.Builder;
import org.junit.jupiter.api.Test;

public class TypeAliasPackageInfoTest {

	private static final String PACKAGE = "org.example";
	private static final String FILENAME = "TestFile";
	private static final String FILE_EXTENSION = Template.PROPERTY_FILE.getFileExtension();
	private static final String TEMPLATE_NAME = Template.PROPERTY_FILE.getTemplatename();

	/**
	 * class under test.
	 */
	private TypeAliasPackageInfo packageInfo = withAllFields(TypeAliasPackageInfo.create()).build();

	@Test
	public void containsPackageName() {
		assertEquals(PACKAGE, packageInfo.getPackageName());
	}

	@Test
	public void containsPropertyFileName() {
		assertEquals(FILENAME, packageInfo.getFileName());
	}

	@Test
	public void containsPropertyFileExtension() {
		assertEquals(FILE_EXTENSION, packageInfo.getFileExtension());
	}

	@Test
	public void containsTemplateName() {
		assertEquals(TEMPLATE_NAME, packageInfo.getTemplateName());
	}

	@Test
	public void javaSource() {
		packageInfo = withAllFields(TypeAliasPackageInfo.create()).fileExtension("java").build();
		assertTrue(packageInfo.isJavaSource());
	}

	@Test
	public void noJavaSource() {
		packageInfo = withAllFields(TypeAliasPackageInfo.create()).fileExtension("properties").build();
		assertFalse(packageInfo.isJavaSource());
	}

	@Test
	public void providesFullQualifiedJavaSourceTypeName() {
		packageInfo = withAllFields(TypeAliasPackageInfo.create()).fileExtension("java").build();
		assertEquals(PACKAGE + "." + FILENAME, packageInfo.getFullQualifiedJavaSourceType());
	}

	@Test
	public void fullQualifiedNameInsideDefaultPackageIsTheSimpleClassName() {
		packageInfo = withAllFields(TypeAliasPackageInfo.create()).packageName("").fileExtension("java").build();
		assertEquals(FILENAME, packageInfo.getFullQualifiedJavaSourceType());
	}

	@Test
	public void providesFileNameWithExtension() {
		assertEquals(FILENAME + "." + FILE_EXTENSION, packageInfo.getFileNameWithExtension());
	}

	@Test
	public void takesExtensionAndTemplateNameFromTemplate() {
		Template template = Template.RESOURCE_BUNDLE;
		packageInfo = withAllFields(TypeAliasPackageInfo.create()).template(template).build();
		assertEquals(template.getTemplatename(), packageInfo.getTemplateName());
		assertEquals(template.getFileExtension(), packageInfo.getFileExtension());
	}

	@Test
	public void containsAllFieldsFromTemplate() {
		assertEquals(packageInfo, TypeAliasPackageInfo.createFrom(packageInfo).build());
	}

	@Test
	public void equalOnEqualContent() {
		TypeAliasPackageInfo equalPackageInfo = TypeAliasPackageInfo.createFrom(packageInfo).build();
		assertTrue(packageInfo.equals(equalPackageInfo), String.valueOf(equalPackageInfo));
	}

	@Test
	public void notEqualOnDifferentPackageName() {
		TypeAliasPackageInfo different = TypeAliasPackageInfo.createFrom(packageInfo).packageName(PACKAGE + ".sub").build();
		assertFalse(packageInfo.equals(different), String.valueOf(packageInfo));
	}

	@Test
	public void notEqualOnDifferentFileName() {
		TypeAliasPackageInfo different = TypeAliasPackageInfo.createFrom(packageInfo).fileName("Different" + FILENAME).build();
		assertFalse(packageInfo.equals(different), String.valueOf(packageInfo));
	}

	@Test
	public void notEqualOnDifferentFileExtension() {
		TypeAliasPackageInfo different = TypeAliasPackageInfo.createFrom(packageInfo).fileExtension("txt").build();
		assertFalse(packageInfo.equals(different), String.valueOf(packageInfo));
	}

	@Test
	public void notEqualOnDifferentTemplateName() {
		TypeAliasPackageInfo different = TypeAliasPackageInfo.createFrom(packageInfo).templateName("resourcebundle").build();
		assertFalse(packageInfo.equals(different), String.valueOf(packageInfo));
	}

	@Test
	public void notEqualComparedToNull() {
		assertFalse(packageInfo.equals(null), String.valueOf(packageInfo));
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void notEqualComparedToAnotherType() {
		assertFalse(packageInfo.equals("Other Type"), String.valueOf(packageInfo));
	}

	private static Builder withAllFields(Builder builder) {
		return withAllOptionalFields(builder);
	}

	private static Builder withAllOptionalFields(Builder builder) {
		return builder.packageName(PACKAGE).fileName(FILENAME).fileExtension(FILE_EXTENSION).templateName(TEMPLATE_NAME);
	}
}