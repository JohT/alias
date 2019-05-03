package org.alias.jsonb.typereference.resolver;

import java.util.ListResourceBundle;

import org.junit.Ignore;

@Ignore
class AliasTypeFixtureResourceBundle extends ListResourceBundle {

	static final String ALIAS = "Alias";

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
				{ ALIAS, AliasTypeFixtureResourceBundle.class },
				{ AliasTypeFixtureResourceBundle.class.getName(), ALIAS },
		};
	}

	@Override
	public String getBaseBundleName() {
		return getClass().getName();
	}
}