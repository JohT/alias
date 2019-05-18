package org.alias.axon.serializer.example.query.model.member.nickname;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

@ApplicationScoped
@Transactional(TxType.REQUIRED)
public class NicknameRepository {

	@PersistenceContext(unitName = "query.member.model")
	private EntityManager entityManager;

	public void setNickname(String nickname) {
		NicknameEntityKey key = new NicknameEntityKey(nickname);
		NicknameEntity entity = readOptional(key).map(NicknameEntity::incrementCount).orElseGet(() -> new NicknameEntity(key));
		if (entity.isDistinct()) {
			entityManager.persist(entity);
		}
		entityManager.flush();
	}

	public List<String> getNicknamesContaining(String partOfNickname) {
		TypedQuery<NicknameEntity> queryNicknames = entityManager.createNamedQuery(NicknameEntity.QUERY_NICKNAMES, NicknameEntity.class);
		queryNicknames.setParameter("partOfNickname", "%" + partOfNickname + "%");
		return queryNicknames.getResultList().stream().map(NicknameEntity::getNickname).collect(Collectors.toList());
	}

	private Optional<NicknameEntity> readOptional(NicknameEntityKey key) {
		return Optional.ofNullable(entityManager.find(NicknameEntity.class, key));
	}
}