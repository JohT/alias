/**
 * Since the Transaction is handled by the {@link javax.transaction.Transactional}-Annotation and their CDI-Interceptor,
 * {@link at.jt.tryout.infrastructure.eventsourcing.axon.transaction.transaction.NoTransaction} is the choice in every
 * (synchronous) request.
 * 
 * The implementation is not tested enough and shouldn't be used anywhere else.
 * 
 * @author JohT
 */
package org.alias.axon.serializer.example.configuration.eventsourcing.transaction;