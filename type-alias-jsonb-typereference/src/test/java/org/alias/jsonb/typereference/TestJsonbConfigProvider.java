package org.alias.jsonb.typereference;

import org.alias.jsonb.typereference.configprovider.ImmutableJsonbConfig;
import org.alias.jsonb.typereference.configprovider.JsonbConfigProvider;

import jakarta.json.bind.JsonbConfig;

/**
 * Demonstrates how to provide an customized {@link JsonbConfig} e.g. for the {@link TypeReferenceEnhancer}.
 * 
 * @author JohT
 */
public class TestJsonbConfigProvider implements JsonbConfigProvider {

	// The configuration is held here only for testing purposes. Avoid ThreadLocals when it can be done easier.
	private static final ThreadLocal<JsonbConfig> config = ThreadLocal.withInitial(TestJsonbConfigProvider::initialConfig);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonbConfig getJsonbConfig() {
		return JsonbConfigProvider.copyOf(config.get());
	}

	/**
	 * Only for testing purposes. Resets the global configuration.
	 */
	public void reset() {
		config.set(initialConfig());
	}

	/**
	 * Only for testing purposes. Sets the global configuration.
	 * 
	 * @param newConfig {@link JsonbConfig}
	 */
	public void setConfig(JsonbConfig newConfig) {
		config.set(JsonbConfigProvider.copyOf(newConfig));
	}

	/**
	 * Only for testing purposes. Sets the global configuration.
	 * 
	 * @param configToJoin {@link JsonbConfig}
	 * @return {@link JsonbConfig}
	 */
	public JsonbConfig joinConfig(JsonbConfig configToJoin) {
		setConfig(ImmutableJsonbConfig.of(config.get()).join(configToJoin));
		return getJsonbConfig();
	}

	private static JsonbConfig initialConfig() {
		return new JsonbConfig().withFormatting(Boolean.TRUE);
	}

	@Override
	public String toString() {
		return "TestJsonbConfigProvider [getJsonbConfig()=" + getJsonbConfig() + "]";
	}
}