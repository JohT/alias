package org.alias.annotation.internal.templates;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.function.Consumer;

import org.alias.annotation.TypeAliasGeneratedFile.Template;

/**
 * Coordinates file creation based on templates.
 * <ul>
 * <li>Reads the contents of the template using {@link TypeAliasTemplateReader}.
 * <li>Calls the {@link PlaceholderReplacer} for every line.
 * <li>Provides a {@link PrintWriter}-{@link Consumer} to write the replaced contents into a new file.
 * </ul>
 * 
 * @author JohT
 */
public class TypeAliasTemplateBasedWriter {

	private final TypeAliasTemplateReader templateReader;

	public static final TypeAliasTemplateBasedWriter forTemplate(Template template) {
		return new TypeAliasTemplateBasedWriter(TypeAliasTemplateReader.forTemplateName(template.getTemplatename()));
	}

	public static final TypeAliasTemplateBasedWriter forTemplateName(String templatename) {
		return new TypeAliasTemplateBasedWriter(TypeAliasTemplateReader.forTemplateName(templatename));
	}

	protected TypeAliasTemplateBasedWriter(TypeAliasTemplateReader templateReader) {
		this.templateReader = templateReader;
	}

	/**
	 * Takes a {@link PrintWriter} to write a new file based on the template using the given {@link PlaceholderReplacer}.
	 * 
	 * @param placeholder - {@link PlaceholderReplacer}
	 * @return {@link Consumer} of a {@link PrintWriter}
	 */
	public Consumer<PrintWriter> writeContent(PlaceholderReplacer placeholder) {
		return writer -> {
			for (Iterator<String> templateLines = templateReader.getContentLines().iterator(); templateLines.hasNext();) {
				String replacedLine = placeholder.replacePlaceholderInLine(templateLines.next());
				if (templateLines.hasNext()) {
					// As long as there is a next line, a new line character(s) needs to be appended.
					replacedLine += "\n";
				}
				writer.print(replacedLine);
			}
		};
	}

	@Override
	public String toString() {
		return "TypeAliasTemplateWriter [templateReader=" + templateReader + "]";
	}
}