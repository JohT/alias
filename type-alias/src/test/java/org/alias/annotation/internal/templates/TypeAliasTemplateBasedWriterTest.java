package org.alias.annotation.internal.templates;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.alias.annotation.TypeAliasGeneratedFile.Template;
import org.junit.jupiter.api.Test;

public class TypeAliasTemplateBasedWriterTest {

	private TypeAliasTemplateBasedWriter template;

	@Test
	public void successfulTemplatePropertyFile() throws IOException {
		template = TypeAliasTemplateBasedWriter.forTemplate(Template.PROPERTY_FILE);
		TypeAliasTemplateFields data = propertiesWith(alias("TestAliasName", "org.SomeType"));

		String content = getWrittenContentAsString(template.writeContent(data));
		assertContainsText("# Generated", content);
		assertContainsText("TestAliasName=org.SomeType", content);
	}

	@Test
	public void successfulResourceBundleFile() throws IOException {
		template = TypeAliasTemplateBasedWriter.forTemplate(Template.RESOURCE_BUNDLE);
		TypeAliasTemplateFields data = resourceBunldeWith(alias("TestAliasName", "org.SomeType"));

		String content = getWrittenContentAsString(template.writeContent(data));
		assertContainsText("package " + data.getPackagename(), content);
		assertContainsText("import javax.annotation.Generated;", content);
		assertContainsText("public class TestResourceBundle ", content);
		assertContainsText("import javax.annotation.Generated;", content);
		assertContainsText("* Generated", content);
		assertContainsText("TestAliasName", content);
		assertContainsText("org.SomeType", content);
	}

	@Test
	public void lastCharacterIsNotaNewLine() throws IOException {
		template = TypeAliasTemplateBasedWriter.forTemplate(Template.PROPERTY_FILE);
		TypeAliasTemplateFields data = propertiesWith(alias("TestAliasName", "org.SomeType"));

		String content = getWrittenContentAsString(template.writeContent(data));
		assertNotEquals('\n', content.charAt(content.length() - 1));
	}

	private void assertContainsText(String expectedContent, String content) {
		String message = String.format("%s not found in\n%s", expectedContent, content);
		assertTrue(content.contains(expectedContent), message);
	}

	private String getWrittenContentAsString(Consumer<PrintWriter> contentWriter) throws IOException {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				PrintWriter writer = new PrintWriter(outputStream)) {
			contentWriter.accept(writer);
			writer.flush();
			return outputStream.toString(StandardCharsets.UTF_8.name());
		}
	}

	private static TypeAliasTemplateFields propertiesWith(TypeAliasName... aliases) {
		return new TypeAliasTemplateFields("org", "TestAlias", asList(aliases));
	}

	private static TypeAliasTemplateFields resourceBunldeWith(TypeAliasName... aliases) {
		return new TypeAliasTemplateFields("org", "TestResourceBundle", asList(aliases));
	}

	private static TypeAliasName alias(String aliasname, String type) {
		return TypeAliasName.builder().aliasName(aliasname).fullQualifiedName(type).build();
	}
}