package org.alias.axon.serializer.example.domain.model.account;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;
import nl.jqno.equalsverifier.Warning;

public class AccountAggregateTest {

	@Test
	public void equalsAndHashcodeTechnicallyCorrect() {
		EqualsVerifierReport report = EqualsVerifier.forClass(AccountAggregate.class)
				.suppress(Warning.NONFINAL_FIELDS)
				.withOnlyTheseFields("accountId")
				.usingGetClass()
				.report();
		assertTrue(report.getMessage(), report.isSuccessful());
	}


}
