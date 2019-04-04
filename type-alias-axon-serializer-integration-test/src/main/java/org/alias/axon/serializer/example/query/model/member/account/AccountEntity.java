package org.alias.axon.serializer.example.query.model.member.account;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ACCOUNT")
public class AccountEntity {

	@EmbeddedId
	private AccountEntityKey key;

	@Column(name = "NICKNAME", nullable = false)
	private String nickname;

	/**
	 * @deprecated Internal constructor for frameworks. Not meant to be called directly.
	 */
	@Deprecated
	protected AccountEntity() {
		super();
	}

	public AccountEntity(AccountEntityKey key) {
		this.key = key;
	}

	public AccountEntityKey getKey() {
		return key;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		AccountEntity castOther = (AccountEntity) other;
		return Objects.equals(key, castOther.key);
	}

	@Override
	public int hashCode() {
		return Objects.hash(key);
	}

	@Override
	public String toString() {
		return "AccountEntity [key=" + key + ", nickname=" + nickname + "]";
	}
}