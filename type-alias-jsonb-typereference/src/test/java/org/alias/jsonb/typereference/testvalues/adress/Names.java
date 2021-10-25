package org.alias.jsonb.typereference.testvalues.adress;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.alias.annotation.TypeAlias;

@TypeAlias("Names")
public class Names {

	private Collection<Name> names = new ArrayList<Name>();

	// ConstructorProperties need yasson commit 06422e4 to be released
	@ConstructorProperties({ "names" })
	public Names(Collection<Name> names) {
		this();
		this.names.addAll(names);
	}

	/**
	 * @deprecated needed until yasson fix 6ae0add released
	 */
	@Deprecated
	protected Names() {
		super();
	}

	public Collection<Name> getNames() {
		return Collections.unmodifiableCollection(names);
	}

	/**
	 * @deprecated needed until yasson fix 6ae0add released
	 */
	@Deprecated
	public synchronized void setNames(Collection<? extends Name> names) {
		this.names.clear();
		this.names.addAll(names);
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		Names castOther = (Names) other;
		return Objects.equals(names, castOther.names);
	}

	@Override
	public int hashCode() {
		return Objects.hash(names);
	}

	@Override
	public String toString() {
		return "Names [names=" + names + "]";
	}
}