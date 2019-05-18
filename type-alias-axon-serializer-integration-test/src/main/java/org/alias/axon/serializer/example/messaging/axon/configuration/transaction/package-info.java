/**
 * Since the Transaction is handled by the {@link javax.transaction.Transactional}-Annotation and their CDI-Interceptor,
 * {@link org.alias.axon.serializer.example.messaging.axon.configuration.transaction.NoTransaction} is the choice in
 * every (synchronous) request.
 * 
 * The implementation is not tested enough and shouldn't be used anywhere else.
 * 
 * @author JohT
 */
package org.alias.axon.serializer.example.messaging.axon.configuration.transaction;