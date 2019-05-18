package org.alias.axon.serializer.example.messaging.axon.configuration.transaction;

import org.axonframework.common.transaction.Transaction;

enum NoTransaction implements Transaction {

	INSTANCE {
		@Override
		public void commit() {
			// No action
		}

		@Override
		public void rollback() {
			// no action
		}
	},
	;
}
