package org.alias.annotation.internal.templates;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.alias.annotation.TypeAliasGeneratedFile.Template;
import org.junit.jupiter.api.Test;

public class TypeAliasTemplateReaderTest {

	private TypeAliasTemplateReader template;

	@Test
	public void templatePropertyFileContentsShouldBeReadable() throws IOException {
		template = TypeAliasTemplateReader.forTemplateName(Template.PROPERTY_FILE.getTemplatename());

		String content = template.getContentLines().toString();
		assertContainsText("{(header)}", content);
		assertContainsText("{(aliases.aliasname)}={(aliases.fullqualifiedname)}", content);
	}

	@Test
	public void templateResourceBundleFileContentsShouldBeReadable() throws IOException {
		template = TypeAliasTemplateReader.forTemplateName(Template.RESOURCE_BUNDLE.getTemplatename());

		String content = template.getContentLines().toString();
		assertContainsText("package {(packagename)};", content);
		assertContainsText("public class {(filename)}", content);
		assertContainsText("@Generated", content);
		assertContainsText("\"{(aliases.aliasname)}\", {(aliases.fullqualifiedname)}.class", content);
		assertContainsText("\"{(aliases.fullqualifiedname)}\", \"{(aliases.aliasname)}\"", content);
	}

	@Test
	public void templateSimpleResourceBundleFileContentsShouldBeReadable() throws IOException {
		template = TypeAliasTemplateReader.forTemplateName(Template.RESOURCE_BUNDLE_SIMPLE.getTemplatename());

		String content = template.getContentLines().toString();
		assertContainsText("package {(packagename)};", content);
		assertContainsText("public class {(filename)}", content);
		assertContainsText("@Generated", content);
		assertContainsText("\"{(aliases.aliasname)}\", {(aliases.fullqualifiedname)}.class", content);
	}

	@Test
	public void failOnNonExistingTemplate() {
		String templatename = "notExistingTemplateName";
		template = new TypeAliasTemplateReader(templatename);
		try {
			template.getContentLines();
			fail("Expected IllegalStateException.");
		} catch (IllegalStateException e) {
			assertTrue(e.getMessage().contains(templatename), e.getMessage());
		}
	}

	private void assertContainsText(String expectedContent, String content) {
		String message = String.format("%s not found in\n%s", expectedContent, content);
		assertTrue(content.contains(expectedContent), message);
	}
}