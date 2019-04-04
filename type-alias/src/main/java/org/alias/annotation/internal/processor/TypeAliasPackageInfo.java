package org.alias.annotation.internal.processor;

import java.util.Collection;
import java.util.Objects;

import org.alias.annotation.TypeAliasGeneratedFile;
import org.alias.annotation.TypeAliasGeneratedFile.Template;
import org.alias.annotation.internal.templates.TypeAliasName;
import org.alias.annotation.internal.templates.PlaceholderReplacer;
import org.alias.annotation.internal.templates.TypeAliasTemplateFields;

/**
 * Describes the package with informations <br>
 * taken from (amongst others) the {@link TypeAliasGeneratedFile} annotation.
 * 
 * @author JohT
 */
final class TypeAliasPackageInfo {

	private static final String JAVA_SOURCE_EXTENSION = "java";

	private static final TypeAliasPackageInfo DEFAULT = new TypeAliasPackageInfo();

	private String packageName = "";
	private String fileName = TypeAliasGeneratedFile.DEFAULT_FILE_NAME;
	private String fileExtension = Template.RESOURCE_BUNDLE.getFileExtension();
	private String templateName = Template.RESOURCE_BUNDLE.getTemplatename();

	public static final TypeAliasPackageInfo defaultPackageInfo() {
		return DEFAULT;
	}

	public static final Builder create() {
		return new Builder();
	}

	public static final Builder createFrom(TypeAliasPackageInfo template) {
		return new Builder(template);
	}

	private TypeAliasPackageInfo() {
		super();
	}

	public String getPackageName() {
		return packageName;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public String getTemplateName() {
		return templateName;
	}

	public boolean isJavaSource() {
		return JAVA_SOURCE_EXTENSION.equals(getFileExtension());
	}

	/**
	 * Return the full qualified class name.
	 * <p>
	 * Only meant to be called if {@link #isJavaSource()} is <code>true</code>.
	 * 
	 * @return {@link String}
	 */
	public String getFullQualifiedJavaSourceType() {
		return isUnnamedPackage() ? getFileName() : getPackageName() + "." + getFileName();
	}

	private boolean isUnnamedPackage() {
		return getPackageName().isEmpty();
	}

	/**
	 * Returns the filename followed by its extension.
	 * 
	 * @return {@link String}
	 */
	public String getFileNameWithExtension() {
		return getFileName() + "." + getFileExtension();
	}

	public PlaceholderReplacer asTemplateContentWith(Collection<? extends TypeAliasName> aliases) {
		return new TypeAliasTemplateFields(getPackageName(), getFileName(), aliases);
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		TypeAliasPackageInfo castOther = (TypeAliasPackageInfo) other;
		return Objects.equals(packageName, castOther.packageName) //
				&& Objects.equals(fileName, castOther.fileName)//
				&& Objects.equals(fileExtension, castOther.fileExtension) //
				&& Objects.equals(templateName, castOther.templateName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(packageName);
	}

	@Override
	public String toString() {
		return "TypeAliasPackageInfo [packageName=" + packageName + ", fileName=" + fileName + ", fileExtension=" + fileExtension
				+ ", templateName=" + templateName + "]";
	}

	public static class Builder {

		private TypeAliasPackageInfo info;

		public Builder() {
			info = new TypeAliasPackageInfo();
		}

		public Builder(TypeAliasPackageInfo template) {
			this();
			packageName(template.getPackageName());
			fileName(template.getFileName());
			fileExtension(template.getFileExtension());
			templateName(template.getTemplateName());
		}

		public Builder packageName(String name) {
			info.packageName = name;
			return this;
		}

		public Builder fileName(String name) {
			info.fileName = name;
			return this;
		}

		public Builder fileExtension(String extension) {
			info.fileExtension = extension;
			return this;
		}

		public Builder templateName(String name) {
			info.templateName = name;
			return this;
		}

		public Builder template(Template template) {
			return templateName(template.getTemplatename()).fileExtension(template.getFileExtension());
		}

		public TypeAliasPackageInfo build() {
			try {
				return info;
			} finally {
				info = null;
			}
		}

		@Override
		public String toString() {
			return "Builder [info=" + info + "]";
		}
	}
}