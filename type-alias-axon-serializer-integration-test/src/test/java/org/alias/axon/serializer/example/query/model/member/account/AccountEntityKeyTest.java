package org.alias.axon.serializer.example.query.model.member.account;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;

public class AccountEntityKeyTest {

	@Test
	public void equalsAndHashcodeTechnicallyCorrect() {
		EqualsVerifierReport report = EqualsVerifier.forClass(AccountEntityKey.class).usingGetClass().report();
		assertTrue(report.isSuccessful(), report.getMessage());
	}
}
