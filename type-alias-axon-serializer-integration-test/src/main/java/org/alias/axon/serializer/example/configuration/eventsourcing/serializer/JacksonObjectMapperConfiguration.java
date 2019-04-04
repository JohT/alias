package org.alias.axon.serializer.example.configuration.eventsourcing.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;

/**
 * Configuration for JSON (de)-serialization.
 * 
 * @author JohT
 */
public enum JacksonObjectMapperConfiguration {

	DEFAULT {

		@Override
		public ObjectMapper createObjectMapper() {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setVisibility(PropertyAccessor.GETTER, Visibility.PROTECTED_AND_PUBLIC);
			objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
			objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			objectMapper.setDateFormat(new StdDateFormat());
			return objectMapper;
		}
	},

	;
	private ObjectMapper objectMapper; // Lazy loaded only once per JVM.

	protected abstract ObjectMapper createObjectMapper();

	/**
	 * Provides the chosen {@link ObjectMapper}.
	 * <p>
	 * This is done only once for the JVM (lazy load).
	 * 
	 * @return {@link ObjectMapper}
	 */
	public final ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			objectMapper = createObjectMapper();
		}
		return objectMapper;
	}
}