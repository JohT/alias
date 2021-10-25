package org.alias.axon.serializer.example.query.model.member.account;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.Warning;

public class AccountEntityTest {

	@Test
	public void equalsAndHashcodeTechnicallyCorrect() {
		EqualsVerifierReport report = EqualsVerifier.forClass(AccountEntity.class).usingGetClass().suppress(Warning.SURROGATE_KEY).report();
		assertTrue(report.isSuccessful(), report.getMessage());
	}

}
