package org.alias.axon.serializer.example.query.model.member;

import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import org.alias.axon.serializer.example.query.model.member.account.AccountProjection;
import org.alias.axon.serializer.example.query.model.member.account.AccountQueryHandler;
import org.junit.Before;
import org.junit.Test;

public class QueryMemberTypeAliasBundleTest {

	private static final String BUNDLE_NAME = "org.alias.axon.serializer.example.query.model.member.QueryMemberTypeAliasBundle";

	private ResourceBundle bundle;

	@Before
	public void setUp() {
		bundle = ResourceBundle.getBundle(BUNDLE_NAME);
	}

	@Test
	public void accountProjectionRegistered() {
		assertEquals(AccountProjection.class, bundle.getObject("AccountProjection"));
	}

	@Test
	public void accountQueryHandlerRegistered() {
		assertEquals(AccountQueryHandler.class, bundle.getObject("AccountQuery"));
	}

}
