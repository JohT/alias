package org.alias.axon.serializer.example.messaging.axon;

import java.util.function.Predicate;

import org.alias.axon.serializer.example.messaging.boundary.query.QueryUpdateEmitterBoundaryService;
import org.axonframework.queryhandling.QueryUpdateEmitter;

public class QueryUpdateEmitterBoundaryServiceAdapter implements QueryUpdateEmitterBoundaryService {

	private final QueryUpdateEmitter queryUpdateEmitter;

	public QueryUpdateEmitterBoundaryServiceAdapter(QueryUpdateEmitter queryUpdateEmitter) {
		this.queryUpdateEmitter = queryUpdateEmitter;
	}

	@Override
	public <Q, U> void emit(Class<Q> queryType, Predicate<? super Q> filter, U update) {
		queryUpdateEmitter.emit(queryType, filter, update);
	}

	@Override
	public String toString() {
		return "QueryUpdateEmitterBoundaryServiceAdapter [queryUpdateEmitter=" + queryUpdateEmitter + "]";
	}
}
