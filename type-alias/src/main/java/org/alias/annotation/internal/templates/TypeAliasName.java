package org.alias.annotation.internal.templates;

import java.util.Objects;

public class TypeAliasName implements PlaceholderReplacer {
	private final String aliasname;
	private final String fullqualifiedname;
	
	/**
	 * Creates a new {@link TypeAliasName} and uses the full qualified name as alias.
	 * 
	 * @param fullqualifiedname {@link String}
	 * @return {@link TypeAliasName}
	 */
	public static final TypeAliasName alsoAsAlias(String fullqualifiedname) {
		return new TypeAliasName(fullqualifiedname, fullqualifiedname);
	}

	public TypeAliasName(String alias, String fullqualifiedname) {
		this.aliasname = alias.trim();
		this.fullqualifiedname = fullqualifiedname.trim();
	}

	public String getAliasname() {
		return aliasname;
	}

	public String getFullqualifiedname() {
		return fullqualifiedname;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String replacePlaceholderInLine(String templateLine) {
		templateLine = templateLine.replace("{(" + "aliasname" + ")}", aliasname);
		templateLine = templateLine.replace("{(" + "fullqualifiedname" + ")}", fullqualifiedname);
		return templateLine;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		TypeAliasName castOther = (TypeAliasName) other;
		return Objects.equals(aliasname, castOther.aliasname) && Objects.equals(fullqualifiedname, castOther.fullqualifiedname);
	}

	@Override
	public int hashCode() {
		return Objects.hash(aliasname, fullqualifiedname);
	}

	@Override
	public String toString() {
		return "TypeAliasName [aliasname=" + aliasname + ", fullqualifiedname=" + fullqualifiedname + "]";
	}
}