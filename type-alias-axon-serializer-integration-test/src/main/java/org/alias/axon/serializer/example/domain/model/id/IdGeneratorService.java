package org.alias.axon.serializer.example.domain.model.id;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IdGeneratorService {

	public String generateId() {
		return UUID.randomUUID().toString();
	}

}
