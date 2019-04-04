package org.alias.annotation.internal.templates;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

/**
 * Reads the template and provides its contents as {@link Collection} of {@link String}s.
 * 
 * @author JohT
 */
class TypeAliasTemplateReader {

	private final String templatename;
	private final Collection<String> contentLines = new ArrayList<String>();

	public static final TypeAliasTemplateReader forTemplateName(String templatename) {
		return new TypeAliasTemplateReader(templatename);
	}

	TypeAliasTemplateReader(String templatename) {
		this.templatename = templatename;
	}

	/**
	 * Returns the template file content lines. The file will be read only once.
	 * 
	 * @return {@link Collection} of content lines as {@link String}s
	 */
	public Collection<String> getContentLines() {
		if (contentLines.isEmpty()) {
			this.contentLines.addAll(readContentLines());
		}
		return this.contentLines;
	}

	private Collection<String> readContentLines() {
		try (	InputStream inputStream = templateAsInputStream();
				Scanner scanner = new Scanner(inputStream)) {
			return asList(scanner.useDelimiter("\\A").next().split("\\n"));
		} catch (IOException e) {
			throw error("Error while reading template file '%s'", templatename);
		}
	}

	private InputStream templateAsInputStream() {
		InputStream resource = getClass().getResourceAsStream(templatename);
		if (resource == null) {
			throw error("Template file '%s' not found", templatename);
		}
		return resource;
	}

	private final static RuntimeException error(String message, Object... messageParameter) {
		return new IllegalStateException(String.format(message, messageParameter));
	}

	@Override
	public String toString() {
		return "TypeAliasTemplateReader [templatename=" + templatename + ", contentLines=" + contentLines + "]";
	}
}