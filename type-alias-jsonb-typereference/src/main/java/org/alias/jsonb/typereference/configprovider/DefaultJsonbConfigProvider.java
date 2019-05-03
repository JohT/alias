package org.alias.jsonb.typereference.configprovider;

import javax.json.bind.JsonbConfig;

enum DefaultJsonbConfigProvider implements JsonbConfigProvider {

	DEFAULT {
		@Override
		public JsonbConfig getJsonbConfig() {
			return JsonbConfigProvider.copyOf(new JsonbConfig());
		};
	}

}
