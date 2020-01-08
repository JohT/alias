package org.alias.annotation.internal.templates;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Contains all fields, that can be used as placeholders inside the template.<br>
 * As long as there are no further requirements, replacing the placeholders is done here line by line.
 * 
 * @author JohT
 */
public class TypeAliasTemplateFields implements PlaceholderReplacer {

	private final String packagename;
	private final String filename;
	private final TypeAliasTextTemplate header = TypeAliasTextTemplate.header();
	private final Collection<TypeAliasName> aliases = new ArrayList<>();
	
	public TypeAliasTemplateFields(String packagename, String filename, Collection<? extends TypeAliasName> aliases) {
		this.packagename = packagename;
		this.filename = filename;
		this.aliases.addAll(aliases);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String replacePlaceholderInLine(String rawTemplateLine) {
		String templateLine = rawTemplateLine;
		if (templateLine.contains("{(packagename)}") && packagename.isEmpty()) {
			templateLine = "";
		}
		templateLine = templateLine.replace("{(packagename)}", packagename);
		templateLine = templateLine.replace("{(filename)}", filename);
		if (templateLine.contains("{(aliases.")) {
			templateLine = templateLine.replace("{(aliases.", "{(");
			String templateLines = "";
			for (TypeAliasName alias : aliases) {
				String replacedLine = alias.replacePlaceholderInLine(templateLine);
				templateLines += (templateLines.isEmpty() || replacedLine.isEmpty()) ? "" : "\n";
				templateLines += replacedLine;
			}
			return templateLines;
		}
		templateLine = header.replacePlaceholderInLine(templateLine);
		return templateLine;
	}

	public String getPackagename() {
		return packagename;
	}

	public String getFilename() {
		return filename;
	}

	public Collection<TypeAliasName> getAliases() {
		return new ArrayList<>(aliases);
	}

	@Override
	public String toString() {
		return "TypeAliasTemplateFields [packagename=" + packagename + ", filename=" + filename + ", header=" + header + ", aliases="
				+ aliases + "]";
	}
}