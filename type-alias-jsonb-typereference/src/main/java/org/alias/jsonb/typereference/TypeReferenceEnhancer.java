package org.alias.jsonb.typereference;

import java.util.ServiceLoader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.adapter.JsonbAdapter;

import org.alias.jsonb.typereference.configprovider.ImmutableJsonbConfig;
import org.alias.jsonb.typereference.configprovider.JsonbConfigProvider;
import org.alias.jsonb.typereference.resolver.ResourceBundleTypeReferenceResolver;
import org.alias.jsonb.typereference.resolver.TypeReferenceResolver;

/**
 * Enhances any dynamically typed content by a type reference property <br>
 * and provides this as an {@link JsonbAdapter} to convert the content to and from JSON.
 * 
 * @author JohT
 */
public class TypeReferenceEnhancer<T> implements JsonbAdapter<T, JsonObject> {

	private static final String FIELD_TYPE_REFERENCE = "__typeref";

	private final JsonbConfigProvider customizableJsonb;
	private final TypeReferenceResolver typeReferenceResolver;

	public TypeReferenceEnhancer() {
		this(loadCustomizedJsonb(), loadTypeNameResolver());
	}

	public TypeReferenceEnhancer(JsonbConfigProvider customizableJsonb, TypeReferenceResolver typeReferenceResolver) {
		this.customizableJsonb = customizableJsonb;
		this.typeReferenceResolver = typeReferenceResolver;
	}

	private static JsonbConfigProvider loadCustomizedJsonb() {
		for (JsonbConfigProvider provided : ServiceLoader.load(JsonbConfigProvider.class)) {
			return provided;
		}
		return JsonbConfigProvider.defaultProvider();
	}

	private static TypeReferenceResolver loadTypeNameResolver() {
		for (TypeReferenceResolver provided : ServiceLoader.load(TypeReferenceResolver.class)) {
			return provided;
		}
		return ResourceBundleTypeReferenceResolver.defaultResolver();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JsonObject adaptToJson(T obj) throws Exception {
		String json = getJsonb().toJson(obj);
		JsonObject jsonObject = getJsonb().fromJson(json, JsonObject.class);
		return Json.createObjectBuilder()
				.addAll(Json.createObjectBuilder(jsonObject))
				.add(FIELD_TYPE_REFERENCE, typeReferenceResolver.getReferenceForType(obj.getClass()))
				.build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T adaptFromJson(JsonObject obj) throws Exception {
		JsonObject original = Json.createObjectBuilder(obj).remove(FIELD_TYPE_REFERENCE).build();
		Class<?> type = typeReferenceResolver.getTypeForReference(obj.getString(FIELD_TYPE_REFERENCE));
		return (T) getJsonb().fromJson(original.toString(), type);
	}

	private Jsonb getJsonb() {
		JsonbConfig jsonbConfig = customizableJsonb.getJsonbConfig();
		jsonbConfig = ImmutableJsonbConfig.of(jsonbConfig).withoutAdapterOfType(getClass());
		return JsonbBuilder.create(jsonbConfig);
	}

	@Override
	public String toString() {
		return "TypeReferenceEnhancer [customizableJsonb=" + customizableJsonb + ", typeReferenceResolver=" + typeReferenceResolver + "]";
	}
}