package org.alias.axon.serializer.example.messaging.axon.configuration.transaction;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.UserTransaction;

import org.axonframework.common.transaction.Transaction;
import org.axonframework.eventhandling.TransactionMethodExecutionException;

/**
 * This transaction wrapper is used for the leading/driving transaction and is
 * responsible to commit or rollback the transaction.
 * 
 * @author Johannes Troppacher
 */
final class LeadingUserTransaction implements Transaction {

	private static final Logger LOGGER = Logger.getLogger(LeadingUserTransaction.class.getName());

	private final UserTransaction userTransaction;

	public static Transaction usingUserTransaction(UserTransaction userTransaction) {
		return new LeadingUserTransaction(userTransaction);
	}

	private LeadingUserTransaction(UserTransaction userTransaction) {
		this.userTransaction = userTransaction;
		exceptionHandled(UserTransaction::begin, "transaction begin");
	}

	@Override
	public void commit() {
		exceptionHandled(UserTransaction::commit, "transaction commit");
	}

	@Override
	public void rollback() {
		exceptionHandled(UserTransaction::rollback, "transaction rollback");
	}

	private void exceptionHandled(ExceptionConsumer<UserTransaction> action, String actionDescription) {
		try {
			action.accept(userTransaction);
			LOGGER.fine(actionDescription + " successful.");
		} catch (Exception exception) {
			throw logged(transactionFailure(exception), actionDescription + " failed.");
		}
	}

	private static <T extends Throwable> T logged(T exception, String message) {
		LOGGER.log(Level.SEVERE, message, exception);
		return exception;
	}

	private static TransactionMethodExecutionException transactionFailure(Exception exception) {
		return new TransactionMethodExecutionException(exception.getMessage(), exception);
	}
}