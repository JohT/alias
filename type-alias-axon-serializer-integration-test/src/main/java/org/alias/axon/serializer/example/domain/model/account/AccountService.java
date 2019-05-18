package org.alias.axon.serializer.example.domain.model.account;

import static javax.transaction.Transactional.TxType.REQUIRED;

import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alias.axon.serializer.example.domain.model.id.IdGeneratorService;
import org.alias.axon.serializer.example.messages.command.account.ChangeNicknameCommand;
import org.alias.axon.serializer.example.messages.command.account.CreateAccountCommand;
import org.alias.axon.serializer.example.messages.query.account.AccountNicknameQuery;
import org.alias.axon.serializer.example.messages.query.account.FetchNicknamesQuery;
import org.alias.axon.serializer.example.messaging.boundary.command.CommandGatewayBoundaryService;
import org.alias.axon.serializer.example.messaging.boundary.query.QueryGatewayBoundaryService;

@ApplicationScoped
public class AccountService {

	@Inject
	private CommandGatewayBoundaryService commandGateway;

	@Inject
	private QueryGatewayBoundaryService queryGateway;

	@Inject
	private IdGeneratorService idGenerator;

	@Transactional(REQUIRED)
	public String createAccount() {
		String newAccountId = idGenerator.generateId();
		commandGateway.sendAndWait(new CreateAccountCommand(newAccountId));
		return newAccountId;
	}

	@Transactional(REQUIRED)
	public void changeNickname(String accountId, String newNickName) {
		commandGateway.sendAndWait(new ChangeNicknameCommand(accountId, newNickName));
	}

	public String queryNickname(String accountId) {
		AccountNicknameQuery query = new AccountNicknameQuery(accountId);
		String details = String.format("Could not read account with id %s", accountId);
		return queryGateway.waitFor(queryGateway.query(query, String.class), details);
	}

	public List<String> queryNicknamesContaining(String partOfNickname, Consumer<String> updateConsumer) {
		FetchNicknamesQuery query = new FetchNicknamesQuery(partOfNickname);
		String details = String.format("Could not read nicknames containing %s", partOfNickname);
		return queryGateway.waitFor(queryGateway.querySubscribedList(query, String.class, updateConsumer), details);
	}
}