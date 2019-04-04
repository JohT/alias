package org.alias.annotation.internal.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.alias.annotation.internal.templates.TypeAliasName;

/**
 * Contains informations about the package and a {@link Collection} of all (relevant) elements inside of it.
 * 
 * @author JohT
 */
class TypeAliasPackage {

	private final TypeAliasPackageInfo info;
	private final Collection<TypeAliasName> types = new ArrayList<>();

	public TypeAliasPackage(TypeAliasPackageInfo info, Collection<? extends TypeAliasName> typesInPackage) {
		this.info = info;
		this.types.addAll(typesInPackage);
	}

	public TypeAliasPackageInfo getInfo() {
		return info;
	}

	public Collection<TypeAliasName> getTypesInPackage() {
		return new ArrayList<>(types);
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		TypeAliasPackage castOther = (TypeAliasPackage) other;
		return Objects.equals(info, castOther.info) && Objects.equals(types, castOther.types);
	}

	@Override
	public int hashCode() {
		return Objects.hash(info, types);
	}

	@Override
	public String toString() {
		return "TypeAliasPackage [info=" + info + ", types=" + types + "]";
	}
}