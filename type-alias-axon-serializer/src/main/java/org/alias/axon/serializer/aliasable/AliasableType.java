package org.alias.axon.serializer.aliasable;

import java.util.Optional;
import java.util.function.Function;

public interface AliasableType {

	String getTypeName();

	Optional<String> getAlias();

	static AliasableType noAliasFor(String typeName) {
		return new NoAliasableType(typeName);
	}

	static Function<String, AliasableType> noAlias() {
		return AliasableType::noAliasFor;
	}
}
