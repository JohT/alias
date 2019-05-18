package org.alias.axon.serializer.example.query.model.member.account;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;

public class AccountEntityKeyTest {

	@Test
	public void equalsAndHashcodeTechnicallyCorrect() {
		EqualsVerifierReport report = EqualsVerifier.forClass(AccountEntityKey.class).usingGetClass().report();
		assertTrue(report.getMessage(), report.isSuccessful());
	}
}
