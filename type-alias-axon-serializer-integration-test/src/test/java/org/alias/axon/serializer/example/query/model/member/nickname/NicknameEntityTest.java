package org.alias.axon.serializer.example.query.model.member.nickname;

import static nl.jqno.equalsverifier.Warning.SURROGATE_KEY;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.EqualsVerifierReport;

public class NicknameEntityTest {

	@Test
	public void equalsAndHashcodeTechnicallyCorrect() {
		EqualsVerifierReport report = EqualsVerifier.forClass(NicknameEntity.class).usingGetClass().suppress(SURROGATE_KEY).report();
		assertTrue(report.getMessage(), report.isSuccessful());
	}

}
