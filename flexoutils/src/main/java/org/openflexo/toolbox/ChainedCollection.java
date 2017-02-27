/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
 * 
 * This file is part of Flexoutils, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.toolbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ChainedCollection<T> implements Collection<T> {

	private final List<Collection<? extends T>> collections = new ArrayList<>();
	private final List<T> items = new ArrayList<>();

	public ChainedCollection(Collection<? extends T>... collections) {
		Collections.addAll(this.collections, collections);
	}

	public void add(Collection<? extends T> itemCollection) {
		this.collections.add(itemCollection);
	}

	@Override
	public boolean add(T item) {
		return this.items.add(item);
	}

	@Override
	public Iterator<T> iterator() {
		final List<Iterator<? extends T>> allIterators = new ArrayList<>();
		for (Collection<? extends T> collection : collections) {
			if (!collection.isEmpty())
				allIterators.add(collection.iterator());
		}
		if (!items.isEmpty())
			allIterators.add(items.iterator());

		if (allIterators.isEmpty())
			return Collections.emptyIterator();

		return new Iterator<T>() {

			final Iterator<Iterator<? extends T>> iteratorIterator = allIterators.iterator();
			// there is at least one iterator with one item inside
			Iterator<? extends T> currentIterator = iteratorIterator.next();

			@Override
			public boolean hasNext() {
				return currentIterator.hasNext() || iteratorIterator.hasNext();
			}

			@Override
			public T next() {
				if (!currentIterator.hasNext()) {
					currentIterator = iteratorIterator.next();
				}
				return currentIterator.next();
			}

			@Override
			public void remove() {
				currentIterator.remove();
			}
		};
	}

	@Override
	public int size() {
		int returned = 0;
		for (Collection<? extends T> collection : collections) {
			returned += collection.size();
		}
		returned += items.size();
		return returned;
	}

	@Override
	public boolean isEmpty() {
		return (size() == 0);
	}

	@Override
	public boolean contains(Object o) {
		for (Collection<? extends T> collection : collections) {
			if (collection.contains(o)) {
				return true;
			}
		}
		return items.contains(o);
	}

	@Override
	public Object[] toArray() {
		Object[] returned = new Object[size()];
		int i = 0;
		for (Collection<? extends T> collection : collections) {
			for (T item : collection) {
				returned[i++] = item;
			}
		}
		for (T item : items) {
			returned[i++] = item;
		}
		return returned;
	}

	@Override
	public <T2> T2[] toArray(T2[] a) {
		int i = 0;
		for (Collection<? extends T> collection : collections) {
			for (T item : collection) {
				a[i++] = (T2) item;
			}
		}
		for (T item : items) {
			a[i++] = (T2) item;
		}
		return a;
	}

	@Override
	public boolean remove(Object o) {
		for (Collection<? extends T> collection : collections) {
			if (collection.remove(o)) {
				return true;
			}
		}
		return items.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean returned = false;
		for (T o : c) {
			if (add(o)) {
				returned = true;
			}
		}
		return returned;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean returned = false;
		for (Object o : c) {
			if (remove(o)) {
				returned = true;
			}
		}
		return returned;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		Iterator<T> e = iterator();
		while (e.hasNext()) {
			if (!c.contains(e.next())) {
				e.remove();
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public void clear() {
		collections.clear();
		items.clear();
	}
}
