package org.alias.axon.serializer;

import java.util.ListResourceBundle;

public class TypeAliasResourceBundleFixture extends ListResourceBundle {

	private final String key;
	private final Object value;

	public static final TypeAliasResourceBundleFixture containing(String alias, Class<?> type) {
		return new TypeAliasResourceBundleFixture(alias, type);
	}

	public static final TypeAliasResourceBundleFixture withWrongValueTypeFor(String alias) {
		return new TypeAliasResourceBundleFixture(alias, new Object());
	}

	protected TypeAliasResourceBundleFixture(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	@Override
	protected Object[][] getContents() {
		return new Object[][] { { key, value }, { nameOfValue(), key } };
	}

	private String nameOfValue() {
		return (value instanceof Class<?>) ? ((Class<?>) value).getName() : value.toString();
	}

	@Override
	public String getBaseBundleName() {
		return getClass().getName();
	}

	@Override
	public String toString() {
		return "ResourceBundleFixture [alias=" + key + ", type=" + value + "]";
	}
}