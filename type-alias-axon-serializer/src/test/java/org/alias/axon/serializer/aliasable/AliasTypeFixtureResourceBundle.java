package org.alias.axon.serializer.aliasable;

import java.util.ListResourceBundle;

import org.junit.Ignore;

@Ignore
class AliasTypeFixtureResourceBundle extends ListResourceBundle {

	static final String ALIAS = "Alias";
	static final Class<?> TYPE = AliasableTypeResourceBundleRegisterTest.class;

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
				{ ALIAS, org.alias.axon.serializer.aliasable.AliasableTypeResourceBundleRegisterTest.class },
				{ "org.alias.axon.serializer.aliasable.AliasableTypeResourceBundleRegisterTest", ALIAS },
		};
	}

	@Override
	public String getBaseBundleName() {
		return getClass().getName();
	}
}