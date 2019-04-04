package org.alias.axon.serializer.example.configuration.eventsourcing.transaction;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;

import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;

/**
 * Provides the {@link TransactionManager} for Axon.
 * 
 * @author Johannes Troppacher
 */
@RequestScoped
public class JtaTransactionManager implements TransactionManager {

	private static final Logger LOGGER = Logger.getLogger(JtaTransactionManager.class.getName());

	@Inject
	private UserTransaction userTransaction;

	@Resource(lookup = "java:comp/TransactionSynchronizationRegistry")
	private TransactionSynchronizationRegistry transactionRegistry;

	@Override
	public Transaction startTransaction() {
		if (isTransactionynchronizationRegistryAvailable()) {
			return ParticipatingRegisteredTransaction.usingRegistry(transactionRegistry);
		}
		if (isUserTransactionAvailable()) {
			if (isInTransaction()) {
				return ParticipatingUserTransaction.usingUserTransaction(userTransaction);
			}
			return LeadingUserTransaction.usingUserTransaction(userTransaction);
		}
		return NoTransaction.INSTANCE;
	}

	private boolean isUserTransactionAvailable() {
		return (userTransaction != null);
	}

	private boolean isInTransaction() {
		try {
			return userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION;
		} catch (SystemException | IllegalStateException | UnsupportedOperationException e) {
			LOGGER.info("UserTransaction not availalbe: " + e.getMessage());
			return false;
		}
	}

	private boolean isTransactionynchronizationRegistryAvailable() {
		return getTransactionRegistry() != null;
	}

	private TransactionSynchronizationRegistry getTransactionRegistry() {
		return transactionRegistry;
	}

	@Override
	public String toString() {
		return "JtaTransactionManager [userTransaction=" + userTransaction + ", transactionRegistry="
				+ transactionRegistry + "]";
	}
}