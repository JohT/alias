package org.alias.annotation.internal.processor;

import java.util.ListResourceBundle;

public class ExampleResourceBundle extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
				{ "key", "value" },
		};
	}

}
