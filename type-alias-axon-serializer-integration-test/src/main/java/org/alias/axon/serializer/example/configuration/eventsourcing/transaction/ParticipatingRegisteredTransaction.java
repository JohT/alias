package org.alias.axon.serializer.example.configuration.eventsourcing.transaction;

import java.util.logging.Logger;

import javax.transaction.TransactionSynchronizationRegistry;

import org.axonframework.common.transaction.Transaction;

/**
 * This transaction uses or contributes to an leading/driving transaction and
 * does not perform commit or rollback on its own.
 * 
 * @author Johannes Troppacher
 */
final class ParticipatingRegisteredTransaction implements Transaction {

	private static final Logger LOGGER = Logger.getLogger(ParticipatingRegisteredTransaction.class.getName());

	private final TransactionSynchronizationRegistry transactionRegistry;

	public static final Transaction usingRegistry(TransactionSynchronizationRegistry transactionRegistry) {
		return new ParticipatingRegisteredTransaction(transactionRegistry);
	}

	private ParticipatingRegisteredTransaction(TransactionSynchronizationRegistry transactionRegistry) {
		this.transactionRegistry = transactionRegistry;
	}

	@Override
	public void commit() {
		LOGGER.fine("participation transaction - commit ommited");
	}

	@Override
	public void rollback() {
		try {
			transactionRegistry.setRollbackOnly();
			LOGGER.fine("TransactionSynchronizationRegistry successfully marked for rollback");
		} catch (IllegalStateException | UnsupportedOperationException e) {
			LOGGER.info("TransactionSynchronizationRegistry could not be marked for rollback: " + e.getMessage());
		}
	}

	@Override
	public String toString() {
		return "ParticipatingRegisteredTransaction [transactionRegistry=" + transactionRegistry + "]";
	}
}