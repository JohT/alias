package org.alias.axon.serializer.example.query.model.member.account;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class AccountRepository {

	@PersistenceContext(unitName = "query.member.model")
	private EntityManager entityManager;

	public AccountEntity create(AccountEntityKey key) {
		AccountEntity account = new AccountEntity(key);
		entityManager.persist(account);
		return account;
	}

	public AccountEntity read(AccountEntityKey key) {
		return readOptional(key).orElseThrow(() -> new IllegalStateException(String.format("Account %s doesn't exist.", key)));
	}

	private Optional<AccountEntity> readOptional(AccountEntityKey key) {
		AccountEntity account = entityManager.find(AccountEntity.class, key);
		return Optional.ofNullable(account);
	}
}