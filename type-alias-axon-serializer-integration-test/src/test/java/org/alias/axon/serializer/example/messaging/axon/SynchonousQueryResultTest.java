package org.alias.axon.serializer.example.messaging.axon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

public class SynchonousQueryResultTest {

	private static final String MESSAGE_DETAILS = "TestMessageDetails";
	private static final String QUERY_RESULT = "TestQueryResult";

	@Mock
	private CompletableFuture<String> future;

	@Captor
	private ArgumentCaptor<Long> usedTimeout;

	@Captor
	private ArgumentCaptor<TimeUnit> usedTimeoutUnit;

	/**
	 * class under test.
	 */
	@InjectMocks
	private SynchonousQueryResult<String> query;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		whenResultIsRead().thenReturn(QUERY_RESULT);
	}

	@Test
	public void successfulQueryReturnsResult() {
		String queryResult = query.waitAndGet(MESSAGE_DETAILS);
		assertEquals(QUERY_RESULT, queryResult);
	}

	@Test
	public void interruptedExceptionIsWrappedIntoIllegalArgumentException() throws Exception {
		InterruptedException cause = new InterruptedException("TestQueryInterruption");
		whenResultIsRead().thenThrow(cause);
		try {
			query.waitAndGet(MESSAGE_DETAILS);
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			assertEquals(cause, e.getCause());
		}
	}

	@Test
	public void timeoutExceptionIsWrappedIntoIllegalArgumentException() throws Exception {
		TimeoutException cause = new TimeoutException("TestQueryInterruption");
		whenResultIsRead().thenThrow(cause);
		try {
			query.waitAndGet(MESSAGE_DETAILS);
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			assertEquals(cause, e.getCause());
		}
	}

	@Test
	public void executionExceptionIsWrappedIntoIllegalArgumentException() throws Exception {
		IllegalStateException cause = new IllegalStateException("TestQueryInterruption");
		ExecutionException executionException = new ExecutionException("TestQueryInterruption", cause);
		whenResultIsRead().thenThrow(executionException);
		try {
			query.waitAndGet(MESSAGE_DETAILS);
			fail("Expected exception");
		} catch (IllegalStateException e) {
			assertEquals(cause, e);
		}
	}

	private OngoingStubbing<String> whenResultIsRead() throws Exception {
		return when(future.get(usedTimeout.capture(), usedTimeoutUnit.capture()));
	}

}