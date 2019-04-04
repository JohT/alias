package org.alias.axon.serializer.aliasable;

import java.util.Objects;
import java.util.Optional;

import org.alias.axon.serializer.resolvable.ResolvableType;

/**
 * Contains all fields that are necessary for to combine {@link ResolvableType} and {@link AliasableType}.
 * 
 * @author JohT
 */
class AliasableResolvableType implements AliasableType, ResolvableType {

	private String alias;
	private String typeName;
	private Class<?> resolvedType;

	/**
	 * Creates a new {@link AliasableResolvableType} using the given type name, <br>
	 * without providing an alias or a resolved type.
	 * 
	 * @param typeName {@link String}
	 * @return {@link AliasableResolvableType}
	 */
	public static final AliasableResolvableType ofTypeName(String typeName) {
		return builder().typeName(typeName).build();
	}

	/**
	 * Starts a {@link Builder} to create a {@link AliasableResolvableType} based on the given type.
	 * 
	 * @param typeName {@link String}
	 * @return {@link AliasableResolvableType}
	 */
	public static final Builder ofType(Class<?> type) {
		return AliasableResolvableType.builder().resolvedType(type).typeNameFromResolvedType();
	}

	public static final Builder builder() {
		return new Builder();
	}

	public static final Builder builderFrom(AliasableResolvableType template) {
		return new Builder(template);
	}

	protected AliasableResolvableType() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<String> getAlias() {
		return Optional.ofNullable(alias);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeName() {
		return typeName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Class<?>> getResolvedType() {
		return Optional.ofNullable(resolvedType);
	}

	protected void setAlias(String alias) {
		this.alias = alias;
	}

	protected void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	protected void setResolvedType(Class<?> resolvedType) {
		this.resolvedType = resolvedType;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		AliasableResolvableType castOther = (AliasableResolvableType) other;
		return Objects.equals(alias, castOther.alias) //
				&& Objects.equals(typeName, castOther.typeName) //
				&& Objects.equals(resolvedType, castOther.resolvedType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(alias, typeName, resolvedType);
	}

	@Override
	public String toString() {
		return "AliasableResolvableType [alias=" + alias + ", typeName=" + typeName + ", resolvedType=" + resolvedType + "]";
	}

	public static final class Builder {

		private AliasableResolvableType type;

		protected Builder() {
			type = new AliasableResolvableType();
		}

		protected Builder(AliasableResolvableType template) {
			this();
			alias(template.getAlias().orElse(null));
			typeName(template.getTypeName());
			resolvedType(template.getResolvedType().orElse(null));
		}

		public Builder alias(String alias) {
			type.setAlias(alias);
			return this;
		}

		public Builder typeName(String typeName) {
			type.setTypeName(typeName);
			return this;
		}

		public Builder typeNameFromResolvedType() {
			return typeName(type.getResolvedType().get().getName());
		}

		public Builder resolvedType(Class<?> resolvedType) {
			type.setResolvedType(resolvedType);
			return this;
		}

		public AliasableResolvableType build() {
			validate();
			try {
				return type;
			} finally {
				type = null;
			}
		}

		private void validate() {
			notEmpty(notNull(type.getTypeName(), "typeName may not be null"), "typeName may not be empty");
		}

		@Override
		public String toString() {
			return "Builder [type=" + type + "]";
		}

		private static String notEmpty(String value, String messageIfEmpty) {
			if (value.trim().isEmpty()) {
				throw new IllegalArgumentException(messageIfEmpty);
			}
			return value;
		}

		private static String notNull(String value, String messageIfNull) {
			if (value == null) {
				throw new IllegalArgumentException(messageIfNull);
			}
			return value;
		}
	}
}