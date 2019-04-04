package org.alias.axon.serializer.example.configuration.eventsourcing.database;

import static javax.transaction.Transactional.TxType.REQUIRED;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.sql.DataSource;
import javax.transaction.Transactional;

@Typed()
public class EventsourcingDatasourceProducer {

	@Resource(name = "jdbc/eventsourcing")
	private DataSource eventsourcingDataSource;

	@Produces
	@Transactional(REQUIRED)
	// @RequestScoped
	@Eventsourcing
	public DataSource produceEventsourcingDataSource() {
		return eventsourcingDataSource;
	}
}
