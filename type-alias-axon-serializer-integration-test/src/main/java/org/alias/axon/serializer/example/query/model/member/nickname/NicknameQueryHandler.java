package org.alias.axon.serializer.example.query.model.member.nickname;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alias.axon.serializer.example.messages.query.account.FetchNicknamesQuery;
import org.alias.axon.serializer.example.messaging.boundary.query.model.QueryModelQueryHandler;

@ApplicationScoped
public class NicknameQueryHandler {

	@Inject
	private NicknameRepository repository;

	@QueryModelQueryHandler
	public List<String> queryNicknames(FetchNicknamesQuery query) {
		return repository.getNicknamesContaining(query.getPartOfNickname());
	}
}