package org.alias.jsonb.typereference.configprovider;

import java.util.ServiceLoader;

import javax.json.bind.JsonbConfig;

/**
 * Enables a way to provide customized {@link JsonbConfig} instances for injection (e.g. via {@link ServiceLoader}).
 * <p>
 * This makes it possible to use one configuration in many places (e.g. inside a adapter).
 * 
 * @author JohT
 */
public interface JsonbConfigProvider {

	/**
	 * Gets the customized {@link JsonbConfig} to be used globally with dependency injection (e.g. via
	 * {@link ServiceLoader}).
	 * <p>
	 * The implementation shouldn't return the original configuration.<br>
	 * Please use {@link #copyOf(JsonbConfig)} and do not return your original {@link JsonbConfig}. <br>
	 * Since {@link JsonbConfig} isn't immutable, it would otherwise be possible, that the global configuration may get
	 * changed outside.
	 * 
	 * @return {@link JsonbConfig}
	 */
	JsonbConfig getJsonbConfig();

	/**
	 * Creates a defensive copy of {@link JsonbConfig}.
	 * 
	 * @param config {@link JsonbConfig}
	 * @return {@link JsonbConfig}
	 */
	static JsonbConfig copyOf(JsonbConfig config) {
		if (config == null) {
			return null;
		}
		JsonbConfig copy = new JsonbConfig();
		config.getAsMap().forEach(copy::setProperty);
		return copy;
	}

	/**
	 * Returns the default {@link JsonbConfigProvider}
	 * 
	 * @return {@link JsonbConfigProvider}
	 */
	static JsonbConfigProvider defaultProvider() {
		return DefaultJsonbConfigProvider.DEFAULT;
	}
}