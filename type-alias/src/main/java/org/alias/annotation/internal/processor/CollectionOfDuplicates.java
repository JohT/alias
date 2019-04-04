package org.alias.annotation.internal.processor;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Simple {@link Collection}, that does only collect duplicate (or multiple) entries.
 * <p>
 * A {@link HashSet} is used to detect multiple entries, <br>
 * so the elements need to implement {@link #hashCode()} and {@link #equals(Object)}.
 * 
 * @author JohT
 *
 * @param <E>
 */
class CollectionOfDuplicates<E> extends AbstractCollection<E> {

	private final Collection<E> allElements = new ArrayList<>();
	private final Set<E> duplicates = new HashSet<>();

	public static final <E> Collection<E> collectionOfDuplicates() {
		return new CollectionOfDuplicates<>();
	}

	@Override
	public Iterator<E> iterator() {
		return duplicates.iterator();
	}

	@Override
	public int size() {
		return duplicates.size();
	}

	@Override
	public boolean add(E e) {
		if (allElements.contains(e)) {
			return duplicates.add(e);
		}
		return allElements.add(e);
	}

	@Override
	public String toString() {
		return "CollectionOfDuplicates [allElements=" + allElements + ", duplicates=" + duplicates + "]";
	}
}