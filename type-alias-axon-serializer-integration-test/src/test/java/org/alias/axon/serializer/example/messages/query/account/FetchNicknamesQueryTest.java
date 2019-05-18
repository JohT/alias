package org.alias.axon.serializer.example.messages.query.account;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;

public class FetchNicknamesQueryTest {

	@Test
	public void equalsAndHashcodeTechnicallyCorrect() {
		EqualsVerifierReport report = EqualsVerifier.forClass(FetchNicknamesQuery.class).usingGetClass().report();
		assertTrue(report.getMessage(), report.isSuccessful());
	}


}
