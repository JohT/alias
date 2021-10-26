package org.alias.jsonb.typereference.testvalues.animals;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.alias.annotation.TypeAlias;

import jakarta.json.bind.annotation.JsonbProperty;

@TypeAlias("Zoo")
public class Zoo {

	private Collection<Animal> animals = new ArrayList<Animal>();

	// ConstructorProperties need yasson commit 06422e4 to be released
	@ConstructorProperties({ "animals" })
	public Zoo(@JsonbProperty("animals") Collection<? extends Animal> animals) {
		this.animals.addAll(animals);
	}

	/**
	 * @deprecated needed until yasson fix 6ae0add released
	 */
	@Deprecated
	protected Zoo() {
		super();
	}

	public Collection<Animal> getAnimals() {
		return Collections.unmodifiableCollection(animals);
	}
	
	/**
	 * @deprecated needed until yasson fix 6ae0add released
	 */
	@Deprecated
	public void setAnimals(Collection<? extends Animal> animals) {
		this.animals.clear();
		this.animals.addAll(animals);
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		Zoo castOther = (Zoo) other;
		return Objects.equals(animals, castOther.animals);
	}

	@Override
	public int hashCode() {
		return Objects.hash(animals);
	}

	@Override
	public String toString() {
		return "Zoo [animals=" + animals + "]";
	}
}