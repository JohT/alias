package org.alias.axon.serializer.example.messaging.axon;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.alias.axon.serializer.example.messaging.boundary.query.QueryGatewayBoundaryService;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;

public class QueryGatewayBoundaryServiceAdapter implements QueryGatewayBoundaryService {

	private final QueryGateway queryGateway;

	public QueryGatewayBoundaryServiceAdapter(QueryGateway queryGateway) {
		this.queryGateway = queryGateway;
	}

	@Override
	public <R, Q> CompletableFuture<R> query(Q query, Class<R> responseType) {
		return queryGateway.query(query, responseType);
	}

	@Override
	public <Q, R> CompletableFuture<List<R>> querySubscribedList(Q query, Class<R> responseElementType, Consumer<R> resultUpdateAction) {
		SubscriptionQueryResult<List<R>, R> fetchQuery = queryGateway.subscriptionQuery(query,
				ResponseTypes.multipleInstancesOf(responseElementType),
				ResponseTypes.instanceOf(responseElementType));
		fetchQuery.updates().subscribe(resultUpdateAction);
		return fetchQuery.initialResult().toFuture();
	}

	@Override
	public <R> R waitFor(CompletableFuture<R> queryResult, String queryDetails) {
		String details = String.format("Query %s failed", queryDetails);
		return SynchonousQueryResult.of(queryResult).waitAndGet(details);
	}

	@Override
	public String toString() {
		return "QueryGatewayBoundaryServiceAdapter [queryGateway=" + queryGateway + "]";
	}
}