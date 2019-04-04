package org.alias.axon.serializer.example.configuration.eventsourcing;

import java.lang.annotation.Annotation;
import java.util.function.Function;

import javax.enterprise.inject.spi.CDI;

final class CdiLookupFunctions {

	private CdiLookupFunctions() {
		super();
	}

	public static final <P, T, U extends T> Function<P, T> lookup(Class<U> subtype, Annotation... qualifiers) {
		return p -> directLookup(subtype, qualifiers);
	}

	public static <U> U directLookup(Class<U> subtype, Annotation... qualifiers) {
		return CDI.current().select(subtype, qualifiers).get();
	}
}