package org.alias.axon.serializer.example.messages.query.account;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;

public class FetchNicknamesQueryTest {

	@Test
	public void equalsAndHashcodeTechnicallyCorrect() {
		EqualsVerifierReport report = EqualsVerifier.forClass(FetchNicknamesQuery.class).usingGetClass().report();
		assertTrue(report.isSuccessful(), report.getMessage());
	}

}
