package org.alias.axon.serializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.alias.axon.serializer.resolvable.ResolvableSerializedType;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.SimpleSerializedType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class TypeDecoratableSerializerTest {

	private Serializer wrappedSerializer = mock(Serializer.class);
	private SerializedType originalSerializedType = mock(SerializedType.class);
	private SerializedObject<?> originalSerializedObject = mock(SerializedObject.class);

	/**
	 * class under test.
	 */
	private TypeDecoratableSerializer serializer = new TypeDecoratableSerializer(wrappedSerializer,
			ResolvableSerializedType::systemClassloaderResolvable);

	@Test
	public void alwaysDelegateEmptyDeserializedTypeToWrappedSerializer() {
		serializer = new TypeDecoratableSerializer(wrappedSerializer, ResolvableSerializedType::systemClassloaderResolvable);
		SerializedType emptyType = SimpleSerializedType.emptyType();
		Class<?> expectedTypeFromWrappedSerializer = Void.class;
		doReturn(expectedTypeFromWrappedSerializer).when(wrappedSerializer).classForType(emptyType);
		
		assertEquals(expectedTypeFromWrappedSerializer, serializer.classForType(emptyType));
		verify(wrappedSerializer).classForType(emptyType);
	}

	@Test
	public void alwaysDelegateNotResolvableDeserializedTypeToWrappedSerializer() {
		serializer = new TypeDecoratableSerializer(wrappedSerializer, ResolvableSerializedType::notResolvable);
		Class<?> expectedTypeFromWrappedSerializer = String.class;
		doReturn(expectedTypeFromWrappedSerializer).when(wrappedSerializer).classForType(originalSerializedType);

		assertEquals(expectedTypeFromWrappedSerializer, serializer.classForType(originalSerializedType));
		verify(wrappedSerializer).classForType(originalSerializedType);
	}

	@Test
	public void classForNameReturnsClassOfResolvedTypeIfPresent() {
		serializer = new TypeDecoratableSerializer(wrappedSerializer, ResolvableSerializedType::systemClassloaderResolvable);
		Class<?> expectedTypeFromResolver = TypeDecoratableSerializerTest.class;
		when(originalSerializedType.getName()).thenReturn(expectedTypeFromResolver.getName());
		assertEquals(expectedTypeFromResolver, serializer.classForType(originalSerializedType));
		verifyNoMoreInteractions(wrappedSerializer);
	}

	@Test
	public void alwaysDelegateNotResolvableDeserializationToWrappedSerializer() {
		serializer = new TypeDecoratableSerializer(wrappedSerializer, ResolvableSerializedType::notResolvable);
		String expectedOriginalDeserializedObject = "OriginalDeserializedContent";
		when(wrappedSerializer.deserialize(originalSerializedObject)).thenReturn(expectedOriginalDeserializedObject);

		assertEquals(expectedOriginalDeserializedObject, serializer.deserialize(originalSerializedObject));
		verify(wrappedSerializer).deserialize(originalSerializedObject);
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void useResolvableDeserializationObjectIfPresent() {
		serializer = new TypeDecoratableSerializer(wrappedSerializer, ResolvableSerializedType::systemClassloaderResolvable);
		String expectedDeserializedObject = "DeserializedContent";
		ArgumentCaptor<SerializedObject> serializedObjectForDeserialization = ArgumentCaptor.forClass(SerializedObject.class);
		when(wrappedSerializer.deserialize(serializedObjectForDeserialization.capture())).thenReturn(expectedDeserializedObject);
		when(originalSerializedObject.getType()).thenReturn(originalSerializedType);
		when(originalSerializedType.getName()).thenReturn(expectedDeserializedObject.getClass().getName());

		assertEquals(expectedDeserializedObject, serializer.deserialize(originalSerializedObject));
	}

}