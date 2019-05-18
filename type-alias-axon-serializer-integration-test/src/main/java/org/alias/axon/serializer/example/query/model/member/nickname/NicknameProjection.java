package org.alias.axon.serializer.example.query.model.member.nickname;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.alias.axon.serializer.example.messages.event.account.NicknameChangedEvent;
import org.alias.axon.serializer.example.messages.query.account.FetchNicknamesQuery;
import org.alias.axon.serializer.example.messaging.boundary.query.QueryUpdateEmitterBoundaryService;
import org.alias.axon.serializer.example.messaging.boundary.query.model.QueryModelEventHandler;
import org.alias.axon.serializer.example.messaging.boundary.query.model.QueryModelProjection;

//Note: Without @Dependent, injection is not done and the account will be null leading to a NullPointerException.
//Note: Without @ProcessingGroup, the name of the package is used as fallback which also leads to a NullPointerException.
//For details see: https://github.com/AxonFramework/AxonFramework/issues/940
@Dependent
@QueryModelProjection(processingGroup = NicknameProjection.PROCESSING_GROUP_FOR_NICKNAMES)
public class NicknameProjection {

	public static final String PROCESSING_GROUP_FOR_NICKNAMES = "query.model.member.account.nickname";

	@Inject
	private NicknameRepository repository;

	@Inject
	private QueryUpdateEmitterBoundaryService queryUpdateEmitter;

	@QueryModelEventHandler
	private void onNicknameChanged(NicknameChangedEvent event) {
		repository.setNickname(event.getNickname());
		// Informs the "FetchNicknamesQuery", that there is a new nickname.
		// This is only for those queries relevant, that look for nicknames similar
		// to the new one. The notification is used for the "subscription query",
		// that gets the current results and keeps a reactive client up to date on changes.
		queryUpdateEmitter.emit(FetchNicknamesQuery.class,
				query -> event.getNickname().contains(query.getPartOfNickname()),
				event.getNickname());
	}

}