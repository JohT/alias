package org.alias.axon.serializer.example.messaging.axon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CommandGatewayBoundaryServiceAdapterTest {

	private static final Object A_COMMAND = new Object();
	private static final Exception CHECKED_CAUSE = new Exception("CheckedCauseOfTestFailure");
	private static final IllegalArgumentException CAUSE = new IllegalArgumentException("CauseOfTestFailure");
	private static final CommandExecutionException FAILED_WITH_CAUSE = new CommandExecutionException("WithCause", CAUSE);
	private static final CommandExecutionException FAILED_CHECKED_CAUSE = new CommandExecutionException("WithCheckedCause", CHECKED_CAUSE);
	private static final CommandExecutionException FAILED_WITHOUT_CAUSE = new CommandExecutionException("NoCause", null);

	@Mock
	private CommandGateway commandGateway;

	/**
	 * class under test.
	 */
	@InjectMocks
	private CommandGatewayBoundaryServiceAdapter adapter;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void sendAndWaitIsDelegatedToCommandGateway() {
		adapter.sendAndWait(A_COMMAND);
		verify(commandGateway).sendAndWait(A_COMMAND);
	}

	@Test
	public void sendAndWaitUncheckedExceptionCauseIsReturnedDirectly() {
		when(commandGateway.sendAndWait(A_COMMAND)).thenThrow(FAILED_WITH_CAUSE);
		try {
			adapter.sendAndWait(A_COMMAND);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals(CAUSE, e);
		}
	}

	@Test
	public void sendAndWaitExceptionWithoutCauseGetsReportedAsNewUncheckedExceptionWithTheOriginalMessage() {
		when(commandGateway.sendAndWait(A_COMMAND)).thenThrow(FAILED_WITHOUT_CAUSE);
		try {
			adapter.sendAndWait(A_COMMAND);
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			assertEquals(FAILED_WITHOUT_CAUSE.getMessage(), e.getMessage());
		}
	}

	@Test
	public void sendAndWaitExceptionWithCheckedExceptionAsCauseIsWrappedIntoIllegalStateException() {
		when(commandGateway.sendAndWait(A_COMMAND)).thenThrow(FAILED_CHECKED_CAUSE);
		try {
			adapter.sendAndWait(A_COMMAND);
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			assertEquals(CHECKED_CAUSE, e.getCause());
		}
	}
}