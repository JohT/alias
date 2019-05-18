package org.alias.axon.serializer.example.messages.query.account;

import java.util.Objects;

import org.alias.annotation.TypeAlias;

@TypeAlias("FetchNicknamesQuery")
public class FetchNicknamesQuery {

	private static final String ALL_NICKNAMES = "";

	private final String partOfNickname;

	public static final FetchNicknamesQuery allNicknames() {
		return new FetchNicknamesQuery(ALL_NICKNAMES);
	}

	public FetchNicknamesQuery(String partOfNickname) {
		this.partOfNickname = partOfNickname.replace("%", "\\%").replace("_", "\\_").trim();
	}

	public String getPartOfNickname() {
		return partOfNickname;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		return Objects.equals(partOfNickname, ((FetchNicknamesQuery) other).partOfNickname);
	}

	@Override
	public int hashCode() {
		return Objects.hash(partOfNickname);
	}

	@Override
	public String toString() {
		return "FetchNicknamesQuery [partOfNickname=" + partOfNickname + "]";
	}
}