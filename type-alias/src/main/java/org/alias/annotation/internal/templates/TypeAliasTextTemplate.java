package org.alias.annotation.internal.templates;

import static org.alias.annotation.internal.templates.TypeAliasTemplateReader.forTemplateName;

/**
 * Uses the content of another template file to replace a placeholder in the main template.
 * 
 * @author JohT
 */
class TypeAliasTextTemplate implements PlaceholderReplacer {

	private final static String PLACEHOLDER_FORMAT = "{(%s)}";
	private static final TypeAliasTextTemplate HEADER = TypeAliasTextTemplate.forPlaceholder("header");

	private final String placeholder;
	private final TypeAliasTemplateReader template;

	/**
	 * Returns a {@link TypeAliasTextTemplate} for the placeholder "header".
	 * 
	 * @return {@link TypeAliasTextTemplate}
	 */
	public static final TypeAliasTextTemplate header() {
		return HEADER;
	}

	/**
	 * Returns a {@link TypeAliasTextTemplate}, assuming that the placeholder has the same name as the template (with
	 * default extension ".template").
	 * 
	 * @param placeholder {@link String}
	 * @return {@link TypeAliasTextTemplate}
	 */
	public static final TypeAliasTextTemplate forPlaceholder(String placeholder) {
		return forDifferentPlaceholderAndReader(placeholder, forTemplateName(String.format("%s.template", placeholder)));
	}

	/**
	 * Returns a {@link TypeAliasTextTemplate} where the name of the placeholder may differ from the name of the template
	 * file represented by the {@link TypeAliasTemplateReader}.
	 * 
	 * @param placeholder {@link String}
	 * @param template    {@link TypeAliasTemplateReader}
	 * @return {@link TypeAliasTextTemplate}
	 */
	public static final TypeAliasTextTemplate forDifferentPlaceholderAndReader(String placeholder, TypeAliasTemplateReader template) {
		return new TypeAliasTextTemplate(placeholder, template);
	}

	protected TypeAliasTextTemplate(String placeholder, TypeAliasTemplateReader template) {
		this.placeholder = String.format(PLACEHOLDER_FORMAT, notEmpty(placeholder, "placeholder"));
		this.template = notNull(template, "template");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String replacePlaceholderInLine(String templateLine) {
		if (!templateLine.contains(placeholder)) {
			return templateLine;
		}
		StringBuilder replacedLines = new StringBuilder();
		for (String contentLine : template.getContentLines()) {
			if (replacedLines.length() > 0) {
				replacedLines.append("\n");
			}
			replacedLines.append(templateLine.replace(placeholder, contentLine));
		}
		return replacedLines.toString();
	}

	private static <T> T notNull(T value, String fieldname) {
		if (value == null) {
			throw new IllegalArgumentException(fieldname + " may not be null.");
		}
		return value;
	}

	private static String notEmpty(String value, String fieldname) {
		if (notNull(value, fieldname).trim().isEmpty()) {
			throw new IllegalArgumentException(fieldname + " may not be empty.");
		}
		return value;
	}

	@Override
	public String toString() {
		return "TypeAliasTextTemplate [placeholder=" + placeholder + ", template=" + template + "]";
	}
}