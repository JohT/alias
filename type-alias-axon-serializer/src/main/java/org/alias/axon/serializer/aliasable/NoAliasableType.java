package org.alias.axon.serializer.aliasable;

import java.util.Optional;

class NoAliasableType implements AliasableType {

	private final String typeName;

	public NoAliasableType(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	@Override
	public Optional<String> getAlias() {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return "NoAlias [typeName=" + typeName + "]";
	}

}