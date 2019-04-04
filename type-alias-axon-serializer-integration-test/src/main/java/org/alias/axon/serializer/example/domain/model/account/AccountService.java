package org.alias.axon.serializer.example.domain.model.account;

import static javax.transaction.Transactional.TxType.REQUIRED;

import java.util.concurrent.ExecutionException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alias.axon.serializer.example.domain.model.id.IdGeneratorService;
import org.alias.axon.serializer.example.messages.command.account.ChangeNicknameCommand;
import org.alias.axon.serializer.example.messages.command.account.CreateAccountCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;

@ApplicationScoped
public class AccountService {

	private CommandGateway commands;
	private QueryGateway queries;
	private IdGeneratorService idGenerator;

	/**
	 * @deprecated internal constructor. not mean't to be called directly.
	 */
	@Deprecated
	protected AccountService() {
		super();
	}

	@Inject
	public AccountService(CommandGateway commandGateway, QueryGateway queryGateway, IdGeneratorService idGeneratorService) {
		this.commands = commandGateway;
		this.queries = queryGateway;
		this.idGenerator = idGeneratorService;
	}

	@Transactional(REQUIRED)
	public String createAccount() {
		String newAccountId = idGenerator.generateId();
		commands.sendAndWait(new CreateAccountCommand(newAccountId));
		return newAccountId;
	}

	@Transactional(REQUIRED)
	public void changeNickname(String accountId, String newNickName) {
		commands.sendAndWait(new ChangeNicknameCommand(accountId, newNickName));
	}

	public String queryNickname(String accountId) {
		try {
			return queries.query("memberqueryAccountNickname", accountId, String.class).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalArgumentException(String.format("Could not read account with id %s", accountId));
		}
	}
}