package org.alias.axon.serializer.example;


import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import org.alias.axon.serializer.example.domain.model.account.AccountAggregate;
import org.junit.Before;
import org.junit.Test;

public class DomainModelTypeAliasBundleTest {

	private static final String BUNDLE_NAME = "TypeAlias";

	private ResourceBundle bundle;

	@Before
	public void setUp() {
		bundle = ResourceBundle.getBundle(BUNDLE_NAME);
	}

	@Test
	public void accountAggregateRegistered() {
		assertEquals(AccountAggregate.class, bundle.getObject("AccountAggregate"));
	}
}
