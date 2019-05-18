package org.alias.axon.serializer.example.messaging.axon.configuration.database;

import java.sql.Connection;
import java.sql.SQLException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import javax.sql.DataSource;

import org.axonframework.common.jdbc.ConnectionProvider;

@ApplicationScoped
@Eventsourcing
@Typed(ConnectionProvider.class)
public class EventsourcingConnectionProvider implements ConnectionProvider {

	@Inject
	@Eventsourcing
	private DataSource dataSource;

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public String toString() {
		return "EventsourcingConnectionProvider [dataSource=" + dataSource + "]";
	}
}