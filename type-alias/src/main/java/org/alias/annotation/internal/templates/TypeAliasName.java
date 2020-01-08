package org.alias.annotation.internal.templates;

import java.util.Objects;

import org.alias.annotation.TypeAlias;

/**
 * Contains all informations about a type name, its alias and the package it is assigned to.
 * 
 * @author JohT
 */
public class TypeAliasName implements PlaceholderReplacer {
	private static final String CONDITIONAL_MARKER_FOR_PRIMARY_ALIASES = "{(only.primary)}";

	private String aliasname;
	private String fullqualifiedname;
	private String assignedtypename;
	private boolean primary = false;
	
	/**
	 * Creates a new {@link TypeAliasName} and uses the full qualified name as alias.
	 * 
	 * @param fullqualifiedname {@link String}
	 * @return {@link TypeAliasName}
	 */
	public static final TypeAliasName alsoAsAlias(String fullqualifiedname) {
		return builder().aliasName(fullqualifiedname).fullQualifiedName(fullqualifiedname).build();
	}

	public static final Builder builder() {
		return new Builder();
	}

	public static final Builder builderBasedOn(TypeAliasName template) {
		return builder().basedOn(template);
	}

	public static final Builder ofAnnotation(TypeAlias annotation) {
		return builder().ofAnnotation(annotation);
	}
	
	/**
	 * @deprecated Please use the {@link #builder()}.
	 */
	@Deprecated
	public TypeAliasName(String alias, String fullqualifiedname) {
		this.aliasname = alias.trim();
		this.fullqualifiedname = fullqualifiedname.trim();
		this.assignedtypename = this.fullqualifiedname;
	}
	
	/**
	 * Internal Constructor.
	 */
	protected TypeAliasName() {
		super();
	}

	/**
	 * Distinct alias name of the typeAliasName.
	 * 
	 * @return {@link String}
	 */
	public String getAliasname() {
		return aliasname;
	}

	/**
	 * Full qualified name of the typeAliasName the alias belongs to.
	 * 
	 * @return {@link String}
	 */
	public String getFullqualifiedname() {
		return fullqualifiedname;
	}

	/**
	 * Full qualified name of the typeAliasName, where the alias had been defined. <br>
	 * The assigned typeAliasName name determines to which package the alias will be assigned.
	 * 
	 * @return {@link String}
	 */
	public String getAssignedtypename() {
		return assignedtypename;
	}

	/**
	 * Matches, if the typeAliasName is inside the given package.
	 * 
	 * @param packagename - {@link String}
	 * @return <code>true</code> if it matches.
	 */
	public boolean isAssignedToPackage(String packagename) {
		return assignedtypename.startsWith(packagename);
	}

	/**
	 * Matches, if this is the primary alias for a type with multiple alias names.
	 * 
	 * @return <code>true</code> if it matches.
	 */
	public boolean isPrimary() {
		return primary;
	}

	/**
	 * Creates a new primary {@link TypeAliasName}.
	 * 
	 * @return {@link TypeAliasName}
	 */
	public TypeAliasName asPrimary() {
		return builder().basedOn(this).primary(true).build();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String replacePlaceholderInLine(String templateLine) {
		if (templateLine.contains(CONDITIONAL_MARKER_FOR_PRIMARY_ALIASES)) {
			if (!isPrimary()) {
				return ""; // Since this line is only mean't for primary aliases, it gets cleared.
			}
			// The conditional marker is removed, if it is a primary alias.
			templateLine = templateLine.replace(CONDITIONAL_MARKER_FOR_PRIMARY_ALIASES, "");
		}
		templateLine = templateLine.replace("{(" + "aliasname" + ")}", aliasname);
		templateLine = templateLine.replace("{(" + "fullqualifiedname" + ")}", fullqualifiedname);
		templateLine = templateLine.replace("{(" + "assignedtypename" + ")}", assignedtypename);
		return templateLine;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		TypeAliasName castOther = (TypeAliasName) other;
		return Objects.equals(aliasname, castOther.aliasname) //
				&& Objects.equals(fullqualifiedname, castOther.fullqualifiedname)//
				&& Objects.equals(assignedtypename, castOther.assignedtypename) //
				&& (primary == castOther.primary);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(aliasname, fullqualifiedname);
	}

	@Override
	public String toString() {
		return "TypeAliasName [aliasname=" + aliasname + ", fullqualifiedname=" + fullqualifiedname
				+ ", assignedtypename=" + assignedtypename + ", primary=" + primary + "]";
	}

	/**
	 * Builds the {@link TypeAliasName}.
	 * <p>
	 * The {@link #aliasName(String)} may not be empty. <br>
	 * <p>
	 * The {@link #fullQualifiedName(String)} may not be empty, <br>
	 * it defaults to {@link #assignedTypeName(String)}.
	 * <p>
	 * The {@link #assignedTypeName(String)} may not be empty, <br>
	 * it defaults to {@link #fullQualifiedName(String)}.<br>
	 * 
	 * @author JohT
	 */
	public static final class Builder {
		private TypeAliasName typeAliasName = new TypeAliasName();

		/**
		 * Takes all values of the given template {@link TypeAliasName}.
		 * 
		 * @param template {@link TypeAliasName}
		 * @return {@link Builder}
		 */
		public Builder basedOn(TypeAliasName template) {
			aliasName(template.getAliasname());
			fullQualifiedName(template.getFullqualifiedname());
			assignedTypeName(template.getAssignedtypename());
			primary(template.isPrimary());
			return this;
		}
		
		/**
		 * Takes the values of the given annotation.
		 * 
		 * @param typeAliasAnnotation {@link TypeAlias}
		 * @return {@link Builder}
		 */
		public Builder ofAnnotation(TypeAlias typeAliasAnnotation) {
			aliasName(typeAliasAnnotation.value());
			fullQualifiedName(typeAliasAnnotation.type());
			primary(typeAliasAnnotation.primary());
			return this;
		}
		
		/**
		 * Stable and distinct alias name of the type.
		 * <p>
		 * It is not meant to change. It unambiguously identifies the type. <br>
		 * If no alias is defined, the {@link #fullQualifiedName(String)} is used instead.
		 * 
		 * @param name {@link String}
		 * @return {@link Builder}
		 */
		public Builder aliasName(String name) {
			typeAliasName.aliasname = name;
			return this;
		}
		
		/**
		 * The full qualified type name (e.g. class name), <br>
		 * that gets assigned to the alias name.
		 * <p>
		 * If no full qualified name is defined, the {@link #assignedTypeName(String)} is used instead.
		 * 
		 * @param name {@link String}
		 * @return {@link Builder}
		 */
		public Builder fullQualifiedName(String name) {
			typeAliasName.fullqualifiedname = name;
			return this;
		}

		/**
		 * The full qualified type name (e.g. class name), <br>
		 * that locates the package, where the alias had been declared. It determines, to which package the alias will be
		 * assigned.
		 * <p>
		 * If no assigned type name is defined, the {@link #fullQualifiedName(String)} is used instead.
		 * 
		 * @param name {@link String}
		 * @return {@link Builder}
		 */
		public Builder assignedTypeName(String name) {
			typeAliasName.assignedtypename = name;
			return this;
		}

		/**
		 * Marks the primary alias for a type, that has multiple aliases.
		 * <p>
		 * Defaults to false.
		 * 
		 * @param value boolean
		 * @return {@link Builder}
		 */
		public Builder primary(boolean value) {
			typeAliasName.primary = value;
			return this;
		}

		public TypeAliasName build() {
			preset();
			validate();
			try {
				return this.typeAliasName;
			} finally {
				this.typeAliasName = null;
			}
		}

		private void preset() {
			aliasName(trimmed(typeAliasName.getAliasname()));
			fullQualifiedName(trimmed(typeAliasName.getFullqualifiedname()));
			assignedTypeName(trimmed(typeAliasName.getAssignedtypename()));

			if (typeAliasName.getFullqualifiedname().isEmpty()) {
				fullQualifiedName(this.typeAliasName.getAssignedtypename());
			}
			if (typeAliasName.getAssignedtypename().isEmpty()) {
				assignedTypeName(this.typeAliasName.getFullqualifiedname());
			}
		}

		private void validate() {
			notEmpty(typeAliasName.fullqualifiedname, "fullqualifiedname");
			notEmpty(typeAliasName.assignedtypename, "assignedtypename");
		}

		private static String trimmed(String name) {
			return (name != null) ? name.trim() : "";
		}

		private static String notEmpty(String value, String fieldname) {
			if (value.isEmpty()) {
				throw new IllegalArgumentException(fieldname + " may not be empty");
			}
			return value;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return "Builder [typeAliasName=" + typeAliasName + "]";
		}
	}
}