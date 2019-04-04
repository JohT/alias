package org.alias.axon.serializer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Function;

import org.alias.axon.serializer.resolvable.ResolvableSerializedType;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.SimpleSerializedType;
import org.junit.Before;
import org.junit.Test;

public class TypeDecoratableSerializedObjectTest {

	private static final Function<SerializedType, ResolvableSerializedType> TYPE_DECORATOR = ResolvableSerializedType::systemClassloaderResolvable;

	private SerializedObject<?> delegate = mock(SerializedObject.class);

	/**
	 * class under test.
	 */
	private TypeDecoratableSerializedObject<?> decorator = new TypeDecoratableSerializedObject<>(delegate, TYPE_DECORATOR);

	@Before
	public void setUp() {
		doReturn("testdata").when(delegate).getData();
		doReturn(Object.class).when(delegate).getContentType();
	}

	@Test
	public void dataShouldBeTakenFromDelegateAndNotBeAffectedByDecorator() {
		assertEquals(delegate.getData(), decorator.getData());
	}

	@Test
	public void contentTypeShouldBeTakenFromDelegateAndNotBeAffectedByDecorator() {
		assertEquals(delegate.getContentType(), decorator.getContentType());
	}

	@Test
	public void typeFromDelegateGetsResolvedByTypeDecorator() {
		Class<String> type = String.class;
		String revisionNumber = "v1";
		SimpleSerializedType simpleSerializedType = new SimpleSerializedType(type.getName(), revisionNumber);
		when(delegate.getType()).thenReturn(simpleSerializedType);

		assertEquals(type.getName(), decorator.getType().getName());
		assertEquals(revisionNumber, decorator.getType().getRevision());
		assertEquals(type, decorator.getType().getResolvedType().get());
	}
}