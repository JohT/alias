package org.alias.jsonb.typereference.configprovider;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;

public class ImmutableJsonbConfig {

	private final JsonbConfig jsonbConfig;

	public static final ImmutableJsonbConfig of(JsonbConfig config) {
		return new ImmutableJsonbConfig(config);
	}

	protected ImmutableJsonbConfig(JsonbConfig config) {
		this.jsonbConfig = JsonbConfigProvider.copyOf(config);
	}

	public JsonbConfig getJsonbConfig() {
		return JsonbConfigProvider.copyOf(jsonbConfig);
	}

	/**
	 * Returns a new {@link JsonbConfig} that combines the settings of the given {@link JsonbConfig} <br>
	 * and this {@link ImmutableJsonbConfig}.
	 * 
	 * @param configToJoin {@link JsonbConfig}
	 * @return {@link JsonbConfig}
	 */
	public JsonbConfig join(JsonbConfig configToJoin) {
		JsonbConfig joinedConfig = getJsonbConfig();
		configToJoin.getAsMap().forEach(joinPropertyInto(joinedConfig));
		return joinedConfig;
	}

	private static BiConsumer<String, Object> joinPropertyInto(JsonbConfig joinedConfig) {
		return (name, value) -> {
			joinedConfig.setProperty(name, newValue(joinedConfig, name, value));
		};
	}

	private static Object newValue(JsonbConfig joinedConfig, String name, Object value) {
		if (!isArray(value)) {
			return value;
		}
		return concat((Object[]) value, joinedConfig.getProperty(name).map(Object[].class::cast).orElse(new Object[0]));
	}

	private static boolean isArray(Object newProperty) {
		return (newProperty != null) && newProperty.getClass().isArray();
	}

	private static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public <T extends JsonbAdapter<?, ?>> JsonbConfig withoutAdapterOfType(Class<T> adapterType) {
		JsonbConfig newConfig = getJsonbConfig();
		Optional<Object> property = jsonbConfig.getProperty(JsonbConfig.ADAPTERS);
		newConfig.setProperty(JsonbConfig.ADAPTERS, withoutType(adapterType, contentsOf(property)).toArray(JsonbAdapter[]::new));
		return newConfig;
	}

	private static Stream<Object> withoutType(Class<?> adapterType, Stream<Object> propertyContents) {
		return propertyContents.filter(predicate(adapterType::isInstance).negate());
	}

	private static Stream<Object> contentsOf(Optional<Object> source) {
		return source
				.map(element -> (element instanceof Object[]) ? Arrays.stream((Object[]) element) : Stream.of(element))
				.orElseGet(Stream::empty);
	}

	private static <T> Predicate<T> predicate(Predicate<T> predicateToInvert) {
		return predicateToInvert;
	}

	@Override
	public String toString() {
		return "ImmutableJsonbConfig [jsonbConfig=" + jsonbConfig + "]";
	}
}