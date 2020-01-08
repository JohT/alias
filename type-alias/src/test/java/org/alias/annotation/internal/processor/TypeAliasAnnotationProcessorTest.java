package org.alias.annotation.internal.processor;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.tools.JavaFileObject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * These tests are something in between integration and unit tests.
 * 
 * @author JohT
 */
public class TypeAliasAnnotationProcessorTest {

	private static final String DEFAULT_PROPERTYFILENAME = "TypeAlias.properties";

	/**
	 * class under test.
	 */
	private TypeAliasAnnotationProcessor processor = new TypeAliasAnnotationProcessor();

	@Rule
	public final TemporaryFolder directory = new TemporaryFolder();

	@Test
	public void propertyFileShouldContainTypeAliasName() throws IOException {
		JavaFileObjectTestFixtures typeAliasAnnotatedClass = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_TYPE_ALIAS;
		JavaFileObject source = typeAliasAnnotatedClass.createJavaFile();

		compileWithPresetProperties(source).to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("OrgExampleAliasClass=" + typeAliasAnnotatedClass.getJavaFileName(), DEFAULT_PROPERTYFILENAME);
		files.assertHasUncommentedPropertyLineCountOf(1, DEFAULT_PROPERTYFILENAME);
	}

	@Test
	public void propertyFileShouldContainAllAliasNames() throws IOException {
		JavaFileObjectTestFixtures orgExampleClass = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_TYPE_ALIAS;
		JavaFileObjectTestFixtures orgAnotherClass = JavaFileObjectTestFixtures.CLASS_ORG_ANOTHER_TYPE_ALIAS;
		JavaFileObjectTestFixtures orgClass = JavaFileObjectTestFixtures.CLASS_ORG_TYPE_ALIAS;

		compileWithPresetProperties(orgExampleClass.createJavaFile(), orgAnotherClass.createJavaFile(), orgClass.createJavaFile())
				.to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("OrgExampleAliasClass=" + orgExampleClass.getJavaFileName(), DEFAULT_PROPERTYFILENAME);
		files.assertContains("OrgAnotherAliasClass=" + orgAnotherClass.getJavaFileName(), DEFAULT_PROPERTYFILENAME);
		files.assertContains("OrgAliasClass=" + orgClass.getJavaFileName(), DEFAULT_PROPERTYFILENAME);
		files.assertHasUncommentedPropertyLineCountOf(3, DEFAULT_PROPERTYFILENAME);
	}

	@Test
	public void resourceBundleFileShouldContainAliasName() throws IOException {
		JavaFileObjectTestFixtures orgClass = JavaFileObjectTestFixtures.CLASS_ORG_TYPE_ALIAS;
		JavaFileObjectTestFixtures resourceBundleConfig = JavaFileObjectTestFixtures.PACKAGE_ORG_RESOURCE_BUNDLE;

		compileWithPresetProperties(orgClass.createJavaFile(), resourceBundleConfig.createJavaFile()).to(directory.getRoot())
				.executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("\"OrgAliasClass\", " + orgClass.getJavaFileName() + ".class", "org", "AliasResourceBundle.java");
	}

	@Test
	public void defaultResourceBundleFileShouldContainAliasName() throws IOException {
		JavaFileObjectTestFixtures orgClass = JavaFileObjectTestFixtures.CLASS_ORG_TYPE_ALIAS;
		compileSources(orgClass.createJavaFile()).to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("\"OrgAliasClass\", " + orgClass.getJavaFileName() + ".class", "TypeAlias.java");
	}

	@Test
	public void propertyFileShouldNotContainClassesWithoutTypeAlias() throws IOException {
		JavaFileObjectTestFixtures withAlias = JavaFileObjectTestFixtures.CLASS_ORG_TYPE_ALIAS;
		JavaFileObjectTestFixtures withoutAlias = JavaFileObjectTestFixtures.CLASS_ORG_NO_ALIAS;

		compileWithPresetProperties(withAlias.createJavaFile(), withoutAlias.createJavaFile()).to(directory.getRoot())
				.executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("OrgAliasClass=" + withAlias.getJavaFileName(), DEFAULT_PROPERTYFILENAME);
		files.assertHasUncommentedPropertyLineCountOf(1, DEFAULT_PROPERTYFILENAME);
	}

	@Test
	public void propertyFileShouldContainHeaderComments() throws IOException {
		JavaFileObjectTestFixtures typeAliasAnnotatedClass = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_TYPE_ALIAS;

		compileWithPresetProperties(typeAliasAnnotatedClass.createJavaFile()).to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("# Generated by", DEFAULT_PROPERTYFILENAME);
		files.assertContains("type-alias", DEFAULT_PROPERTYFILENAME);
	}

	@Test
	public void propertyFileShouldContainAliasNames() throws IOException {
		JavaFileObjectTestFixtures noConfigOuter = JavaFileObjectTestFixtures.CLASS_ORG_TYPE_ALIAS;
		JavaFileObjectTestFixtures configuredInner = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_TYPE_ALIAS;
		JavaFileObject innerConfig = JavaFileObjectTestFixtures.PACKAGE_ORG_EXAMPLE.createJavaFile();
		JavaFileObject anotherInnerConfig = JavaFileObjectTestFixtures.PACKAGE_ORG_ANOTHER.createJavaFile();

		compileWithPresetProperties(innerConfig, anotherInnerConfig, noConfigOuter.createJavaFile(), configuredInner.createJavaFile())
				.to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		// The default properties should only contain the types,
		// that doesn't belong to a configured (annotated package-info.java)
		// package.
		files.assertContains("OrgAliasClass=" + noConfigOuter.getJavaFileName(), DEFAULT_PROPERTYFILENAME);
		files.assertHasUncommentedPropertyLineCountOf(1, DEFAULT_PROPERTYFILENAME);

		// The customized properties should contain their contained types.
		String[] innerConfigPropertyFilePathElements = new String[] { "org", "example", "OrgExampleAlias.properties" };
		files.assertContains("OrgExampleAliasClass=" + configuredInner.getJavaFileName(), innerConfigPropertyFilePathElements);
		files.assertHasUncommentedPropertyLineCountOf(1, DEFAULT_PROPERTYFILENAME);
	}

	@Test
	public void compilationShouldFailOnAmbiguousTypeAliasNames() throws IOException {
		JavaFileObjectTestFixtures original = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_TYPE_ALIAS;
		JavaFileObjectTestFixtures duplicate = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_DUPLICATE_TYPE_ALIAS;
		compileWithPresetProperties(original.createJavaFile(), duplicate.createJavaFile()).to(directory.getRoot())
				.expectErrorFrom(processor);
	}

	@Test
	public void compilationShouldFailOnEmptyTypeAliasNames() throws IOException {
		JavaFileObjectTestFixtures empty = JavaFileObjectTestFixtures.CLASS_ORG_EMPTY_TYPE_ALIAS;
		compileWithPresetProperties(empty.createJavaFile()).to(directory.getRoot()).expectErrorFrom(processor);
	}

	@Test
	public void compilationShouldFailOnAmbiguousPackageConfigurations() throws IOException {
		JavaFileObjectTestFixtures original = JavaFileObjectTestFixtures.PACKAGE_ORG_ANNOTATED;
		JavaFileObjectTestFixtures duplicate = JavaFileObjectTestFixtures.PACKAGE_ORG_ANNOTATED_ON_A_TYPE;
		compileWithPresetProperties(original.createJavaFile(), duplicate.createJavaFile()).to(directory.getRoot())
				.expectErrorFrom(processor);
	}

	@Test
	public void compilationShouldFailOnAmbiguousTypeConfigurations() throws IOException {
		JavaFileObject external = JavaFileObjectTestFixtures.CLASS_COM_EXTERNAL.createJavaFile();
		JavaFileObject definitions = JavaFileObjectTestFixtures.CLASS_ORG_ALIASES_FOR_EXTERNAL_TYPES.createJavaFile();
		JavaFileObject duplicate = JavaFileObjectTestFixtures.CLASS_ORG_ALIASES_DUPLICATE_TYPE_DEFINITION.createJavaFile();
		compileWithPresetProperties(external, definitions, duplicate).to(directory.getRoot()).expectErrorFrom(processor);
	}

	@Test
	public void propertyFileShouldNotBeGeneratedWithoutAnyTypeAliasAnnotation() throws IOException {
		JavaFileObject noAlias = JavaFileObjectTestFixtures.CLASS_ORG_NO_ALIAS.createJavaFile();

		compileWithPresetProperties(noAlias).to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertFilesWithExtension(0, "properties");
	}

	@Test
	public void propertyFileShouldOnlyContainClassesWithinTheSameCustomizedPackage() throws IOException {
		JavaFileObject packageInfo = JavaFileObjectTestFixtures.PACKAGE_ORG_EXAMPLE.createJavaFile();
		JavaFileObjectTestFixtures outsidePackage = JavaFileObjectTestFixtures.CLASS_ORG_ANOTHER_TYPE_ALIAS;
		JavaFileObjectTestFixtures withinPackage = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_TYPE_ALIAS;

		compileWithPresetProperties(packageInfo, withinPackage.createJavaFile(), outsidePackage.createJavaFile()).to(directory.getRoot())
				.executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("OrgExampleAliasClass=" + withinPackage.getJavaFileName(), "org", "example", "OrgExampleAlias.properties");
		files.assertHasUncommentedPropertyLineCountOf(1, "org", "example", "OrgExampleAlias.properties");
	}

	@Test
	public void defaultPropertyFileShouldOnlyContainClassesOutsideCustomizedPackage() throws IOException {
		JavaFileObject packageInfo = JavaFileObjectTestFixtures.PACKAGE_ORG_EXAMPLE.createJavaFile();
		JavaFileObjectTestFixtures outsidePackage = JavaFileObjectTestFixtures.CLASS_ORG_ANOTHER_TYPE_ALIAS;
		JavaFileObjectTestFixtures withinPackage = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_TYPE_ALIAS;

		compileWithPresetProperties(packageInfo, withinPackage.createJavaFile(), outsidePackage.createJavaFile()).to(directory.getRoot())
				.executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("OrgAnotherAliasClass=" + outsidePackage.getJavaFileName(), DEFAULT_PROPERTYFILENAME);
		files.assertHasUncommentedPropertyLineCountOf(1, DEFAULT_PROPERTYFILENAME);
	}

	@Test
	public void propertyFileShouldBeGeneratedWithCustomizedOuterPackageName() throws IOException {
		JavaFileObject packageInfo = JavaFileObjectTestFixtures.PACKAGE_ORG_ANNOTATED.createJavaFile();
		JavaFileObjectTestFixtures source = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_TYPE_ALIAS;

		compileWithPresetProperties(packageInfo, source.createJavaFile()).to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("OrgExampleAliasClass=" + source.getJavaFileName(), "org", "OrgAlias.properties");
		files.assertHasUncommentedPropertyLineCountOf(1, "org", "OrgAlias.properties");
		files.assertFilesWithExtension(1, "properties");
	}

	@Test
	public void propertyFileShouldBeGeneratedWithSubPackageTree() throws IOException {
		JavaFileObject packageInfo = JavaFileObjectTestFixtures.PACKAGE_ORG_ANNOTATED.createJavaFile();
		JavaFileObject samePackage = JavaFileObjectTestFixtures.CLASS_ORG_TYPE_ALIAS.createJavaFile();
		JavaFileObject neighbourPackage = JavaFileObjectTestFixtures.CLASS_ORG_ANOTHER_TYPE_ALIAS.createJavaFile();
		JavaFileObject subPackage = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_TYPE_ALIAS.createJavaFile();

		compileWithPresetProperties(packageInfo, samePackage, subPackage, neighbourPackage).to(directory.getRoot())
				.executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertHasUncommentedPropertyLineCountOf(3, "org", "OrgAlias.properties");
		files.assertFilesWithExtension(1, "properties");
	}

	@Test
	public void propertyFileShouldBeGeneratedWithCustomizedInnerPackageName() throws IOException {
		JavaFileObject packageInfo = JavaFileObjectTestFixtures.PACKAGE_ORG_EXAMPLE.createJavaFile();
		JavaFileObject source = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_TYPE_ALIAS.createJavaFile();

		compileWithPresetProperties(packageInfo, source).to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertHasUncommentedPropertyLineCountOf(1, "org", "example", "OrgExampleAlias.properties");
		files.assertFilesWithExtension(1, "properties");
	}

	@Test
	public void propertyFileShouldBeGeneratedWithNonCustomizedDefaultPackageName() throws IOException {
		JavaFileObject packageInfo = JavaFileObjectTestFixtures.PACKAGE_ORG_EXAMPLE_NOT_ANNOTATED.createJavaFile();
		JavaFileObject source = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_TYPE_ALIAS.createJavaFile();

		compileWithPresetProperties(packageInfo, source).to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertFilesWithExtension(1, "properties");
	}

	@Test
	public void propertyFileShouldNotBeGeneratedWhenThereAreNoAliases() throws IOException {
		JavaFileObject packageInfo = JavaFileObjectTestFixtures.PACKAGE_ORG_ANNOTATED.createJavaFile();
		JavaFileObject noAlias = JavaFileObjectTestFixtures.CLASS_ORG_NO_ALIAS.createJavaFile();

		compileWithPresetProperties(packageInfo, noAlias).to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());
		files.assertFilesWithExtension(0, "properties");
	}

	@Test
	public void propertyFileShouldBeLoadableIntoProperties() throws IOException {
		JavaFileObject packageInfo = JavaFileObjectTestFixtures.PACKAGE_ORG_ANNOTATED.createJavaFile();
		JavaFileObjectTestFixtures source = JavaFileObjectTestFixtures.CLASS_ORG_EXAMPLE_TYPE_ALIAS;

		compileWithPresetProperties(packageInfo, source.createJavaFile()).to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		Properties typeAlias = new Properties();
		try (InputStream inputStream = files.inputStreamOf("org", "OrgAlias.properties")) {
			typeAlias.load(inputStream);
		}
		assertEquals(source.getJavaFileName(), typeAlias.getProperty("OrgExampleAliasClass"));
	}

	@Test
	public void aliasShouldBeDefinableForAnotherNotAnnotatedType() throws IOException {
		JavaFileObjectTestFixtures orgNoAlias = JavaFileObjectTestFixtures.CLASS_ORG_NO_ALIAS;
		JavaFileObjectTestFixtures orgAliasForNotAnnotatedType = JavaFileObjectTestFixtures.CLASS_ORG_ALIAS_FOR_NOT_ANNOTATED_TYPE;

		compileWithPresetProperties(orgNoAlias.createJavaFile(), orgAliasForNotAnnotatedType.createJavaFile()).to(directory.getRoot())
				.executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("ForeignAlias=" + orgNoAlias.getJavaFileName(), DEFAULT_PROPERTYFILENAME);
		files.assertHasUncommentedPropertyLineCountOf(1, DEFAULT_PROPERTYFILENAME);
	}

	@Test
	public void aliasShouldBeDefinableForMultipleExternalClassesWithoutPackageConfiguration() throws IOException {
		JavaFileObjectTestFixtures external = JavaFileObjectTestFixtures.CLASS_COM_EXTERNAL;
		JavaFileObjectTestFixtures anotherExternal = JavaFileObjectTestFixtures.CLASS_COM_ANOTHER_EXTERNAL;
		JavaFileObject aliases = JavaFileObjectTestFixtures.CLASS_ORG_ALIASES_FOR_EXTERNAL_TYPES.createJavaFile();

		compileWithPresetProperties(external.createJavaFile(), anotherExternal.createJavaFile(), aliases).to(directory.getRoot())
				.executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("ExternalAlias=" + external.getJavaFileName(), DEFAULT_PROPERTYFILENAME);
		files.assertContains("AnotherExternalAlias=" + anotherExternal.getJavaFileName(), DEFAULT_PROPERTYFILENAME);
		files.assertHasUncommentedPropertyLineCountOf(2, DEFAULT_PROPERTYFILENAME);
	}

	@Test
	public void aliasShouldBeDefinableForMultipleExternalClassesInsidePackage() throws IOException {
		JavaFileObjectTestFixtures external = JavaFileObjectTestFixtures.CLASS_COM_EXTERNAL;
		JavaFileObjectTestFixtures anotherExternal = JavaFileObjectTestFixtures.CLASS_COM_ANOTHER_EXTERNAL;
		JavaFileObject aliases = JavaFileObjectTestFixtures.CLASS_ORG_ALIASES_FOR_EXTERNAL_TYPES.createJavaFile();
		JavaFileObject packageInfo = JavaFileObjectTestFixtures.PACKAGE_ORG_ANNOTATED.createJavaFile();

		compileWithPresetProperties(external.createJavaFile(), anotherExternal.createJavaFile(), aliases, packageInfo)
				.to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("ExternalAlias=" + external.getJavaFileName(), "org", "OrgAlias.properties");
		files.assertContains("AnotherExternalAlias=" + anotherExternal.getJavaFileName(), "org", "OrgAlias.properties");
		files.assertHasUncommentedPropertyLineCountOf(2, "org", "OrgAlias.properties");
	}

	@Test
	public void multipleAliasesWithOnePrimaryAliasForOneTypeShouldBeSupported() throws IOException {
		JavaFileObjectTestFixtures typeWithMultipleAliases = JavaFileObjectTestFixtures.CLASS_ORG_MULTIPLE_ALIASES;
		JavaFileObject objectWithMultipleAliases = typeWithMultipleAliases.createJavaFile();
		JavaFileObject packageInfo = JavaFileObjectTestFixtures.PACKAGE_ORG_ANNOTATED.createJavaFile();

		compileWithPresetProperties(objectWithMultipleAliases, packageInfo).to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("FirstAlias=" + typeWithMultipleAliases.getJavaFileName(), "org", "OrgAlias.properties");
		files.assertContains("SecondAlias=" + typeWithMultipleAliases.getJavaFileName(), "org", "OrgAlias.properties");
		files.assertHasUncommentedPropertyLineCountOf(2, "org", "OrgAlias.properties");
	}
	
	@Test
	public void failOnMultipleAliasesWithDuplicatePrimaryAliasesForOneType() throws IOException {
		JavaFileObject duplicate = JavaFileObjectTestFixtures.CLASS_ORG_MULTIPLE_PRIMARY_ALIASES.createJavaFile();
		compileWithPresetProperties(duplicate).to(directory.getRoot()).expectErrorFrom(processor);
	}

	@Test
	public void multipleAliasesWithOnePrimaryAliasForOneTypeShouldBeSupportedForResourceBundles() throws IOException {
		JavaFileObjectTestFixtures multipleAliases = JavaFileObjectTestFixtures.CLASS_ORG_MULTIPLE_ALIASES;
		JavaFileObjectTestFixtures resourceBundleConfig = JavaFileObjectTestFixtures.PACKAGE_ORG_RESOURCE_BUNDLE;

		JavaFileObject objectWithMultipleAliases = multipleAliases.createJavaFile();
		JavaFileObject packageInfo = resourceBundleConfig.createJavaFile();

		compileWithPresetProperties(objectWithMultipleAliases, packageInfo).to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("\"FirstAlias\", " + multipleAliases.getJavaFileName() + ".class", "org", "AliasResourceBundle.java");
		files.assertContains("\"SecondAlias\", " + multipleAliases.getJavaFileName() + ".class", "org", "AliasResourceBundle.java");
		files.assertContains("\"" + multipleAliases.getJavaFileName() + "\", \"FirstAlias\"", "org", "AliasResourceBundle.java");
	}

	@Test
	public void aliasShouldBeDefinableForMultipleExternalClassesInsidePackageInfo() throws IOException {
		JavaFileObjectTestFixtures external = JavaFileObjectTestFixtures.CLASS_COM_EXTERNAL;
		JavaFileObjectTestFixtures anotherExternal = JavaFileObjectTestFixtures.CLASS_COM_ANOTHER_EXTERNAL;
		JavaFileObject packageInfo = JavaFileObjectTestFixtures.PACKAGE_ORG_ANNOTATED_EXTERNAL_ALIASES.createJavaFile();

		compileWithPresetProperties(external.createJavaFile(), anotherExternal.createJavaFile(), packageInfo).to(directory.getRoot())
				.executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("ExternalAlias=" + external.getJavaFileName(), "org", "OrgExternalAlias.properties");
		files.assertContains("AnotherExternalAlias=" + anotherExternal.getJavaFileName(), "org", "OrgExternalAlias.properties");
		files.assertHasUncommentedPropertyLineCountOf(2, "org", "OrgExternalAlias.properties");
	}

	@Test
	public void multipleTypeAliasesWithOneDefault() throws IOException {
		JavaFileObjectTestFixtures withDefault = JavaFileObjectTestFixtures.CLASS_ORG_TYPE_ALIASES_WITH_DEFAULT;

		compileWithPresetProperties(withDefault.createJavaFile()).to(directory.getRoot()).executeProcessor(processor);
		FileCollector files = FileCollector.collectFilesFromRoot(directory.getRoot());

		files.assertContains("OrgAliasClass=" + withDefault.getJavaFileName(), DEFAULT_PROPERTYFILENAME);
		files.assertHasUncommentedPropertyLineCountOf(1, DEFAULT_PROPERTYFILENAME);
	}

	private static final AnnotationProcessorExecutor compileWithPresetProperties(JavaFileObject... sources) {
		JavaFileObject defaultPackageInfoForPropertyFiles = JavaFileObjectTestFixtures.PACKAGE_ORG_DEFAULT_PROPERTY_FILE.createJavaFile();
		return compileSources(sources).addSourceFile(defaultPackageInfoForPropertyFiles);
	}

	private static final AnnotationProcessorExecutor compileSources(JavaFileObject... sources) {
		return AnnotationProcessorExecutor.compileSourceFiles(sources);
	}
}