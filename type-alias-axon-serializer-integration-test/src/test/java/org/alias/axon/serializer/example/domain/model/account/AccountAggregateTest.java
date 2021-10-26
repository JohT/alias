package org.alias.axon.serializer.example.domain.model.account;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
		assertTrue(report.isSuccessful(), report.getMessage());
	}


}
