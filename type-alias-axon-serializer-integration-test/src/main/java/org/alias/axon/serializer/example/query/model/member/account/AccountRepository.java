package org.alias.axon.serializer.example.query.model.member.account;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

@ApplicationScoped
@Transactional(TxType.REQUIRED)
public class AccountRepository {

	@PersistenceContext(unitName = "query.member.model")
	private EntityManager entityManager;

	public AccountEntity create(AccountEntityKey key) {
		AccountEntity account = new AccountEntity(key);
		entityManager.persist(account);
		return account;
	}

	public void setNickname(AccountEntityKey key, String nickname) {
		AccountEntity account = read(key);
		account.setNickname(nickname);
		entityManager.flush();
	}

	public AccountEntity read(AccountEntityKey key) {
		return readOptional(key).orElseThrow(() -> new IllegalStateException(String.format("Account %s doesn't exist.", key)));
	}

	private Optional<AccountEntity> readOptional(AccountEntityKey key) {
		AccountEntity account = entityManager.find(AccountEntity.class, key);
		return Optional.ofNullable(account);
	}
}