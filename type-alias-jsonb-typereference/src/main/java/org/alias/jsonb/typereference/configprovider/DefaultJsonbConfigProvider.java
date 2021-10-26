package org.alias.jsonb.typereference.configprovider;

import jakarta.json.bind.JsonbConfig;

enum DefaultJsonbConfigProvider implements JsonbConfigProvider {

	DEFAULT {
		@Override
		public JsonbConfig getJsonbConfig() {
			return JsonbConfigProvider.copyOf(new JsonbConfig());
		};
	}

}
