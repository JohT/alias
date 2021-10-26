package org.alias.jsonb.typereference.testvalues.adress;

import java.beans.ConstructorProperties;
import java.util.Objects;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

import org.alias.annotation.TypeAlias;

@TypeAlias("Name")
public class Name {

	private final String firstname;
	private final String surname;

	@JsonbCreator
	@ConstructorProperties({ "firstname", "surname" })
	public Name(@JsonbProperty("firstname") String firstname, @JsonbProperty("surname") String surname) {
		this.firstname = firstname;
		this.surname = surname;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getSurname() {
		return surname;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		Name castOther = (Name) other;
		return Objects.equals(firstname, castOther.firstname) && Objects.equals(surname, castOther.surname);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstname, surname);
	}
	
	@Override
	public String toString() {
		return "Name [firstname=" + firstname + ", surname=" + surname + "]";
	}
}