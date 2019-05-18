package org.alias.axon.serializer.example.query.model.member.nickname;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "NICKNAME")
@NamedQueries({
		@NamedQuery(name = NicknameEntity.QUERY_NICKNAMES, query = "SELECT nickname FROM NicknameEntity nickname WHERE nickname.key.nickname LIKE :partOfNickname"),
})
public class NicknameEntity {

	public static final String QUERY_NICKNAMES = "NicknameEntityQueryNicknames";

	@EmbeddedId
	private NicknameEntityKey key;

	@Column(name = "COUNT")
	private long count = 1;

	/**
	 * @deprecated Internal constructor for frameworks. Not meant to be called directly.
	 */
	@Deprecated
	protected NicknameEntity() {
		super();
	}

	public NicknameEntity(NicknameEntityKey key) {
		this.key = key;
	}

	public NicknameEntityKey getKey() {
		return key;
	}

	public String getNickname() {
		return key.getNickname();
	}

	public long getCount() {
		return count;
	}

	public boolean isDistinct() {
		return count <= 1;
	}

	public NicknameEntity incrementCount() {
		count++;
		return this;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		NicknameEntity castOther = (NicknameEntity) other;
		return Objects.equals(key, castOther.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public String toString() {
		return "NicknameEntity [key=" + key + ", count=" + count + "]";
	}
}