package org.alias.jsonb.typereference.resolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.ListResourceBundle;

import org.junit.Ignore;

@Ignore
class AliasTypeFixtureInvalidResourceBundle extends ListResourceBundle {

	static final String KEY_TO_UNEXPECTED_VALUE_TYPE = "KeyToUnexpectedValueType";
	static final Object UNEXPECTED_VALUE_TYPE = new Object();

	static final String KEY_TO_VALUE_THAT_DOES_NOT_EXIST_AS_KEY = "KeyToValueThatDoesNotExistAsKey";
	static final String VALUE_THAT_DOES_NOT_EXIST_AS_KEY = "ValueThatDoesNotExistAsKey";

	static final String EXPECTED_ALIAS_AS_KEY = "SomeAlias";
	static final String NOT_MATCHING_ALIAS_AS_VALUE = "DoesNotMatchSomeAlias";

	static final Collection<String> WRONG_ENTRIES = Arrays.asList(//
			KEY_TO_UNEXPECTED_VALUE_TYPE,
			UNEXPECTED_VALUE_TYPE.toString(),
			KEY_TO_VALUE_THAT_DOES_NOT_EXIST_AS_KEY,
			VALUE_THAT_DOES_NOT_EXIST_AS_KEY, 
			EXPECTED_ALIAS_AS_KEY,
			NOT_MATCHING_ALIAS_AS_VALUE);

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
				{ KEY_TO_UNEXPECTED_VALUE_TYPE, UNEXPECTED_VALUE_TYPE },
				{ KEY_TO_VALUE_THAT_DOES_NOT_EXIST_AS_KEY, VALUE_THAT_DOES_NOT_EXIST_AS_KEY },
				{ EXPECTED_ALIAS_AS_KEY, AliasTypeFixtureInvalidResourceBundle.class },
				{ AliasTypeFixtureInvalidResourceBundle.class.getName(), NOT_MATCHING_ALIAS_AS_VALUE },
				{ "", "ValueOfEmptyKey" },
		};
	}

	@Override
	public String getBaseBundleName() {
		return getClass().getName();
	}
}