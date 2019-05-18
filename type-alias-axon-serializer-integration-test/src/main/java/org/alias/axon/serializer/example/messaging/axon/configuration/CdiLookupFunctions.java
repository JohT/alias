package org.alias.axon.serializer.example.messaging.axon.configuration;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.CDI;

class CdiLookupFunctions {

	private static final Logger LOGGER = Logger.getLogger(CdiLookupFunctions.class.getName());

	protected CdiLookupFunctions() {
		super();
	}

	public static final <P, T, U extends T> Function<P, T> lookup(Class<U> subtype, Annotation... qualifiers) {
		return p -> directLookup(subtype, qualifiers);
	}

	public static <U> U directLookup(Class<U> subtype, Annotation... qualifiers) {
		try {
			return CDI.current().select(subtype, qualifiers).get();
		} catch (RuntimeException e) {
			String message = "CDI lookup for subtype %s failed for qualifiers %s";
			LOGGER.log(Level.SEVERE, String.format(message, subtype.getName(), Arrays.toString(qualifiers)), e);
			throw e;
		}
	}
}