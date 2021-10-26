package org.alias.annotation.internal.processor;

import java.net.URI;

import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;

/**
 * Enumeration of compile-able {@link JavaFileObject}s, that are used as fixtures for tests.
 * 
 * @author JohT
 */
enum JavaFileObjectTestFixtures {

	/**
	 * To configure the default/fallback file generation for the default/unnamed package,<br>
	 * a type needs to be used for that, since it doesn't seem to be possible to annotate the default package
	 * "package-info.java" file.
	 */
	PACKAGE_ORG_DEFAULT_PROPERTY_FILE("DefaultConfig",
			typeAliasGeneratedFile() + "(" + propertyFileTemplate() + ")\npublic class DefaultConfig {}"),
	PACKAGE_ORG_EXAMPLE("package-info",
			typeAliasGeneratedFile() + "(value=\"OrgExampleAlias\"," + propertyFileTemplate() + ")\npackage org.example;"),
	PACKAGE_ORG_ANOTHER("package-info",
			typeAliasGeneratedFile() + "(value=\"OrgAnotherAlias\"," + propertyFileTemplate() + ")\npackage org.another;"),
	PACKAGE_ORG_EXAMPLE_NOT_ANNOTATED("package-info", "package org.example;"),
	PACKAGE_ORG_ANNOTATED("package-info", typeAliasGeneratedFile() + "(value=\"OrgAlias\"," + propertyFileTemplate() + ")\npackage org;"),

	PACKAGE_ORG_ANNOTATED_EXTERNAL_ALIASES("package-info",
			"@org.alias.annotation.TypeAliases({\n" +
					"@org.alias.annotation.TypeAlias(value=\"ExternalAlias\",type=\"com.ExternalClass\"),\n" +
					"@org.alias.annotation.TypeAlias(value=\"AnotherExternalAlias\",type=\"com.AnotherExternalClass\")\n" +
					"})" +
					typeAliasGeneratedFile() + "(value=\"OrgExternalAlias\"," + propertyFileTemplate() + ")\npackage org;"),
	PACKAGE_ORG_RESOURCE_BUNDLE("package-info",
			typeAliasGeneratedFile() + "(" + "value=\"AliasResourceBundle\"," + resourceBundleTemplate() + ")\npackage org;"),
	PACKAGE_ORG_ANNOTATED_ON_A_TYPE("OrgConfig",
			"package org;\n" + typeAliasGeneratedFile() + "(" + propertyFileTemplate() + ")\npublic class OrgConfig {}"),
	CLASS_COM_EXTERNAL("com.ExternalClass", "" + //
			"package com;\n" +
			"public class ExternalClass {}"),
	CLASS_COM_ANOTHER_EXTERNAL("com.AnotherExternalClass", "" + //
			"package com;\n" +
			"public class AnotherExternalClass {}"),
	CLASS_ORG_ALIASES_FOR_EXTERNAL_TYPES("org.ExternalAnnotatedOrgClass", "" + //
			"package org;\n" +
			"@org.alias.annotation.TypeAliases({\n" +
			"@org.alias.annotation.TypeAlias(value=\"ExternalAlias\",type=\"com.ExternalClass\"),\n" +
			"@org.alias.annotation.TypeAlias(value=\"AnotherExternalAlias\",type=\"com.AnotherExternalClass\")\n" +
			"})" +
			"public class ExternalAnnotatedOrgClass {}"),
	CLASS_ORG_ALIASES_DUPLICATE_TYPE_DEFINITION("org.DuplicateTypeDefinitionAnnotatedOrgClass", "" + //
			"package org;\n" +
			"@org.alias.annotation.TypeAliases({\n" +
			"@org.alias.annotation.TypeAlias(value=\"Something\",type=\"com.ExternalClass\"),\n" +
			"})" +
			"public class DuplicateTypeDefinitionAnnotatedOrgClass {}"),
	CLASS_ORG_MULTIPLE_ALIASES("org.MultipleAliases", "" + //
			"package org;\n" +
			"@org.alias.annotation.TypeAliases({\n" +
			"@org.alias.annotation.TypeAlias(value=\"FirstAlias\",primary=true),\n" +
			"@org.alias.annotation.TypeAlias(value=\"SecondAlias\"),\n" +
			"})" +
			"public class MultipleAliases {}"),
	CLASS_ORG_MULTIPLE_PRIMARY_ALIASES("org.MultipleAliases", "" + //
			"package org;\n" +
			"@org.alias.annotation.TypeAliases({\n" +
			"@org.alias.annotation.TypeAlias(value=\"FirstAlias\",primary=true),\n" +
			"@org.alias.annotation.TypeAlias(value=\"SecondAlias\",primary=true),\n" +
			"})" +
			"public class MultipleAliases {}"),
	CLASS_ORG_NO_ALIAS("org.NotAnnotatedOrgClass", "" + //
			"package org;\n" +
			"public class NotAnnotatedOrgClass {}"),
	CLASS_ORG_ALIAS_FOR_NOT_ANNOTATED_TYPE("org.ForeignAnnotatedOrgClass", "" + //
			"package org;\n" +
			"@org.alias.annotation.TypeAlias(value=\"ForeignAlias\",type=\"org.NotAnnotatedOrgClass\")\n" +
			"public class ForeignAnnotatedOrgClass {}"),
	CLASS_ORG_TYPE_ALIAS("org.AnnotatedOrgClass", "" + //
			"package org;\n" +
			"@org.alias.annotation.TypeAlias(\"OrgAliasClass\")\n" +
			"public class AnnotatedOrgClass {}"),
	CLASS_ORG_TYPE_ALIASES_WITH_DEFAULT("org.AnnotatedOrgClass", "" + //
			"package org;\n" +
			"@org.alias.annotation.TypeAliases({\n" +
			"@org.alias.annotation.TypeAlias(\"OrgAliasClass\")\n" +
			"})" +
			"public class AnnotatedOrgClass {}"),
	CLASS_ORG_EMPTY_TYPE_ALIAS("org.EmptyAliasOrgClass", "" + //
			"package org;\n" +
			"@org.alias.annotation.TypeAlias(\"\")\n" +
			"public class EmptyAliasOrgClass {}"),
	CLASS_ORG_EXAMPLE_TYPE_ALIAS("org.example.AnnotatedExampleClass", "" + //
			"package org.example;\n" +
			"@org.alias.annotation.TypeAlias(\"OrgExampleAliasClass\")\n" +
			"public class AnnotatedExampleClass {}"),
	CLASS_ORG_EXAMPLE_DUPLICATE_TYPE_ALIAS("org.example.DuplicatedAnnotatedExampleClass", "" + //
			"package org.example;\n" +
			"@org.alias.annotation.TypeAlias(\"OrgExampleAliasClass\")\n" +
			"public class DuplicatedAnnotatedExampleClass {}"),
	CLASS_ORG_ANOTHER_TYPE_ALIAS("org.another.AnnotatedAnotherClass", "" + //
			"package org.another;\n" +
			"@org.alias.annotation.TypeAlias(\"OrgAnotherAliasClass\")\n" +
			"public class AnnotatedAnotherClass {}"),
			;

	public static final String TEMPLATE_PROPERTY_FILE = "template=org.alias.annotation.TypeAliasGeneratedFile.Template.PROPERTY_FILE";

	private final String javaFileName;
	private final String javaFileContent;

	private JavaFileObjectTestFixtures(String javaFileName, String javaFileContent) {
		this.javaFileName = javaFileName;
		this.javaFileContent = javaFileContent;
	}

	public String getJavaFileName() {
		return javaFileName;
	}

	public JavaFileObject createJavaFile() {
		return new JavaSourceFromString(javaFileName, javaFileContent);
	}

	private static final String propertyFileTemplate() {
		return "template=org.alias.annotation.TypeAliasGeneratedFile.Template.PROPERTY_FILE";
	}

	private static final String resourceBundleTemplate() {
		return "template=org.alias.annotation.TypeAliasGeneratedFile.Template.RESOURCE_BUNDLE";
	}

	private static final String typeAliasGeneratedFile() {
		return "@org.alias.annotation.TypeAliasGeneratedFile";
	}

	private static class JavaSourceFromString extends SimpleJavaFileObject {

		private final String code;

		public JavaSourceFromString(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}

		public JavaSourceFromString(URI uri, Kind kind, String code) {
			super(uri, kind);
			this.code = code;
		}

		@Override
		public String toString() {
			return "JavaSourceFromString [code=" + code + ", uri=" + uri + ", kind=" + kind + "]";
		}
	}
}