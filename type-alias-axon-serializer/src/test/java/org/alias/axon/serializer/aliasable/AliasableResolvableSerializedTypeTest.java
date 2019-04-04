package org.alias.axon.serializer.aliasable;

import static org.junit.Assert.assertEquals;

import java.util.Optional;
import java.util.function.Function;

import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.SimpleSerializedType;
import org.junit.Test;

public class AliasableResolvableSerializedTypeTest {

	private static final String TYPE_NAME = AliasableResolvableSerializedTypeTest.class.getName();
	private static final String REVISION = "v2";

	private static final SerializedType ORIGINAL_SERIALIZED_TYPE = new SimpleSerializedType(TYPE_NAME, REVISION);
	private Function<String, AliasableType> FALLBACK_TYPE_RESOLVER = AliasableResolvableType::ofTypeName;

	private AliasableResolvableSerializedType type;

	@Test
	public void takesRevisionFromWrappedOriginal() {
		type = new AliasableResolvableSerializedType(ORIGINAL_SERIALIZED_TYPE, FALLBACK_TYPE_RESOLVER);
		assertEquals(REVISION, type.getRevision());
	}

	@Test
	public void resolvedTypeProvidedByTypeResolver() {
		Class<AliasableResolvableSerializedTypeTest> resolvedType = AliasableResolvableSerializedTypeTest.class;
		type = new AliasableResolvableSerializedType(ORIGINAL_SERIALIZED_TYPE, fixedResolvedType(resolvedType));
		assertEquals(resolvedType, type.getResolvedType().get());
	}

	@Test
	public void nameFromWrappedOriginalAsFallback() {
		type = new AliasableResolvableSerializedType(ORIGINAL_SERIALIZED_TYPE, FALLBACK_TYPE_RESOLVER);
		assertEquals(TYPE_NAME, type.getName());
	}

	@Test
	public void nameFromAliasIfAvailable() {
		String alias = "Alias";
		type = new AliasableResolvableSerializedType(ORIGINAL_SERIALIZED_TYPE, fixedAliasClassUnresolved(alias));
		assertEquals(alias, type.getName());
	}

	@Test
	public void nameFromResolvedTypeIfAvailable() {
		Class<AliasableResolvableSerializedTypeTest> resolvedType = AliasableResolvableSerializedTypeTest.class;
		type = new AliasableResolvableSerializedType(ORIGINAL_SERIALIZED_TYPE, fixedResolvedType(resolvedType));
		assertEquals(resolvedType.getName(), type.getName());
	}

	@Test
	public void nameFromAliasIfAliasableTypeIsNotAResolvableType() {
		Class<AliasableResolvableSerializedTypeTest> resolvedType = AliasableResolvableSerializedTypeTest.class;
		type = new AliasableResolvableSerializedType(ORIGINAL_SERIALIZED_TYPE, removeResolvableTypeFrom(fixedResolvedType(resolvedType)));
		assertEquals(resolvedType.getSimpleName(), type.getName());
	}

	public static final Function<String, AliasableType> fixedResolvedType(Class<?> type) {
		return typeName -> AliasableResolvableType.builder()
				.alias(type.getSimpleName())
				.typeName(type.getName())
				.resolvedType(type)
				.build();
	}

	public static final Function<String, AliasableType> fixedAliasClassUnresolved(String alias) {
		return typeName -> AliasableResolvableType.builder()
				.alias(alias)
				.typeName(typeName)
				.build();
	}

	public static final Function<String, AliasableType> removeResolvableTypeFrom(Function<String, AliasableType> typeResolver) {
		return typeName -> removeResolvableTypeInterfaceFrom(typeResolver.apply(typeName));
	}

	private static final AliasableType removeResolvableTypeInterfaceFrom(AliasableType aliasableType) {
		return new AliasableType() {

			@Override
			public String getTypeName() {
				return aliasableType.getTypeName();
			}

			@Override
			public Optional<String> getAlias() {
				return aliasableType.getAlias();
			}
		};
	}
}