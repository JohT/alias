package org.alias.axon.serializer.example.messaging.axon.configuration.injection;

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.util.Iterator;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.TypeLiteral;

import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.junit.Test;

import jdk.nashorn.internal.ir.annotations.Ignore;

public class CdiParameterResolverFactoryTest {

	/**
	 * class under test
	 */
	private CdiParameterResolverFactory resolverFactory = new CdiParameterResolverFactory(CdiDummy::new);

	@Test
	public void assureThatRegisteredClassnameMatchesRealClassName() {
		String registeredClassname = "org.alias.axon.serializer.example.messaging.axon.configuration.injection.CdiParameterResolverFactory";
		assertEquals(registeredClassname, resolverFactory.getClass().getName());
	}

	@Test
	public void assureThatRegisteredServiceLoaderMatchesRealInterface() {
		String registeredServiceLoader = "org.axonframework.messaging.annotation.ParameterResolverFactory";
		assertEquals(registeredServiceLoader, ParameterResolverFactory.class.getName());
	}

	@Ignore
	public static final class CdiDummy<T> extends CDI<Object> {

		@Override
		public Instance<Object> select(Annotation... qualifiers) {
			return null;
		}

		@Override
		public <U> Instance<U> select(Class<U> subtype, Annotation... qualifiers) {
			return null;
		}

		@Override
		public <U> Instance<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
			return null;
		}

		@Override
		public boolean isUnsatisfied() {
			return false;
		}

		@Override
		public boolean isAmbiguous() {
			return false;
		}

		@Override
		public void destroy(Object instance) {
		}

		@Override
		public Iterator<Object> iterator() {
			return null;
		}

		@Override
		public Object get() {
			return null;
		}

		@Override
		public BeanManager getBeanManager() {
			return null;
		}
	}
}
