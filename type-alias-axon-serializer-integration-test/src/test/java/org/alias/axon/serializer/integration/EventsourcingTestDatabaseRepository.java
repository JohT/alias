package org.alias.axon.serializer.integration;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Ignore;

/**
 * Provides accesses to the eventsourcing database for integration testing.
 * 
 * @author JohT
 */
@Ignore
class EventsourcingTestDatabaseRepository {

	private static final String EVENT_QUERY = "SELECT * FROM DOMAINEVENTENTRY";
	private static final String SNAPSHOT_QUERY = "SELECT * FROM SNAPSHOTEVENTENTRY";
	private static final String EVENT_PAYLOAD_TYPE_PREFIX_UPDATE = "UPDATE DOMAINEVENTENTRY SET PAYLOADTYPE = CONCAT('%s.',PAYLOADTYPE,'Event')";

	private final DataSource dataSource;

	public EventsourcingTestDatabaseRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public static void printQueryResults(List<Map<String, Object>> queryResults, PrintStream destination) {
		destination.println(queryResults.toString().replace(", {", ",\n{"));
	}

	public List<Map<String, Object>> queryEvents() {
		return query(EVENT_QUERY);
	}

	public List<Map<String, Object>> querySnapshots() {
		return query(SNAPSHOT_QUERY);
	}
	
	public void prefixEventPayloadTypesBy(String prefix) {
		update(String.format(EVENT_PAYLOAD_TYPE_PREFIX_UPDATE, prefix));
	}

	private List<Map<String, Object>> query(String sql) {
		try (	Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			List<Map<String, Object>> resultList = new ArrayList<>();

			try (ResultSet result = statement.executeQuery()) {
				while (result.next()) {
					Map<String, Object> columns = new HashMap<>();
					ResultSetMetaData metaData = result.getMetaData();
					for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
						columns.put(metaData.getColumnName(columnIndex), result.getObject(columnIndex));
					}
					resultList.add(columns);
				}
				return resultList;
			}
		} catch (SQLException e) {
			throw databaseError(e, "Database query %s failed.", sql);
		}
	}

	private void update(String sql) {
		try (	Connection connection = dataSource.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			throw databaseError(e, "Database update %s failed.", sql);
		}
	}

	private static IllegalStateException databaseError(SQLException e, String message, Object... messageParameter) {
		return new IllegalStateException(String.format(message, messageParameter), e);
	}

	@Override
	public String toString() {
		return "EventsourcingTestDatabaseRepository [dataSource=" + dataSource + "]";
	}
}