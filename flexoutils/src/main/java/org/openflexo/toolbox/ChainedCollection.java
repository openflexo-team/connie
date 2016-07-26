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

import org.apache.commons.collections4.iterators.IteratorChain;

public class ChainedCollection<T> implements Collection<T> {

	private List<Collection<? extends T>> collections;
	private List<T> items;

	public ChainedCollection() {
		collections = new ArrayList<>();
		items = new ArrayList<>();
	}

	public ChainedCollection(T... items) {
		this();
		Collections.addAll(this.items, items);
	}

	public ChainedCollection(Collection<? extends T>... collections) {
		this();
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
		List<Iterator<? extends T>> allIterators = new ArrayList<>();
		for (Collection<? extends T> collection : collections) {
			if (collection.size() > 0) {
				allIterators.add(collection.iterator());
			}
		}
		if (items.size() > 0) {
			allIterators.add(items.iterator());
		}
		return new IteratorChain<>(allIterators);
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

	// TODO: make JUnit tests
	public static void main(String[] args) {
		List<String> l1 = new ArrayList<>();
		l1.add("String1");
		l1.add("String2");
		l1.add("String3");
		List<String> l2 = new ArrayList<>();
		l2.add("String4");
		l2.add("String5");
		System.out.println("ChainedCollection1=");
		ChainedCollection<String> cc1 = new ChainedCollection<>(l1, l2);
		for (String s : cc1) {
			System.out.println("> " + s);
		}
		ChainedCollection<String> cc2 = new ChainedCollection<>(cc1);
		cc2.add("String6");
		cc2.add(l1);
		System.out.println("ChainedCollection2=");
		for (String s : cc2) {
			System.out.println("> " + s);
		}
	}

}
