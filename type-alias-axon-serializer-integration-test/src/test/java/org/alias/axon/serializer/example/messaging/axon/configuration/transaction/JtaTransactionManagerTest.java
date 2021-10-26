package org.alias.axon.serializer.example.messaging.axon.configuration.transaction;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.axonframework.common.transaction.Transaction;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;

public class JtaTransactionManagerTest {

	@Mock
	private UserTransaction userTransaction;

	@Mock
	private TransactionSynchronizationRegistry transactionRegistry;

	/**
	 * class under test
	 */
	@InjectMocks
	private JtaTransactionManager transactionManager;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void useParticipatingRegisteredTransactionIfAvailable() {
		setField("userTransaction", null);
		Transaction transaction = transactionManager.startTransaction();
		assertThat(transaction, CoreMatchers.instanceOf(ParticipatingRegisteredTransaction.class));
	}

	@Test
	public void otherwiseUseParticipatingUserTransactionIfAvailable() throws SystemException {
		setField("transactionRegistry", null);
		when(userTransaction.getStatus()).thenReturn(Status.STATUS_PREPARED);
		Transaction transaction = transactionManager.startTransaction();
		assertThat(transaction, CoreMatchers.instanceOf(ParticipatingUserTransaction.class));
	}

	@Test
	public void useLeadingUserTransactionIfNoUserTransactionIsRunningYet() throws SystemException {
		setField("transactionRegistry", null);
		when(userTransaction.getStatus()).thenReturn(Status.STATUS_NO_TRANSACTION);
		Transaction transaction = transactionManager.startTransaction();
		assertThat(transaction, CoreMatchers.instanceOf(LeadingUserTransaction.class));
	}

	@Test
	public void useLeadingUserTransactionIfUserTransactionStatusQueryFails() throws SystemException {
		setField("transactionRegistry", null);
		when(userTransaction.getStatus()).thenThrow(new SystemException("Test"));
		Transaction transaction = transactionManager.startTransaction();
		assertThat(transaction, CoreMatchers.instanceOf(LeadingUserTransaction.class));
	}

	@Test
	public void noTransactionIfThereIsNoWayToObtainOne() {
		setField("userTransaction", null);
		setField("transactionRegistry", null);
		Transaction transaction = transactionManager.startTransaction();
		assertThat(transaction, equalTo(NoTransaction.INSTANCE));
	}

	private void setField(String fieldname, Object fieldcontent) {
		try {
			Field field = transactionManager.getClass().getDeclaredField(fieldname);
			field.setAccessible(true);
			field.set(transactionManager, fieldcontent);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
}