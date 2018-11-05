/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class provide an implementation of a list containing some other list or elements The goal of this implementation is to rely on
 * embedded lists instead of copying data structures into an other one (performance reasons): you can manage a List without having to
 * duplicate concatened lists
 * 
 * @author sylvain
 * 
 * @param <E>
 */
public class ConcatenedList<E> extends AbstractList<E> {

	private ArrayList<Object> embedded = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public ConcatenedList(Object... elements) {
		for (Object element : elements) {
			if (element instanceof List) {
				addElementList((List<E>) element);
			}
			else {
				addElement((E) element);
			}
		}
	}

	@Override
	public void add(int index, E element) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException();
		}
		int current = 0;
		for (int i = 0; i < embedded.size(); i++) {
			Object o = embedded.get(i);
			if (current == index) {
				embedded.add(i, element);
				return;
			}
			if (o instanceof List) {
				List<?> list = (List<?>) o;
				if (index < current + list.size()) {
					throw new UnsupportedOperationException();
				}
				current += list.size();
			}
			else {
				// Fabien: I think the following code is useless as if current == index we have already returned in line 86
				// if (current == index) {
				// embedded.add(i, element);
				// return;
				// }
				current++;
			}
		}
	}

	public void addElement(E element) {
		embedded.add(element);
	}

	public void addElementList(List<? extends E> elementList) {
		embedded.add(elementList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public E get(int index) {
		int current = 0;
		for (Object o : embedded) {
			if (o instanceof List) {
				List<E> list = (List<E>) o;
				if (index < current + list.size()) {
					return list.get(index - current);
				}
				current += list.size();
			}
			else {
				if (index == current) {
					return (E) o;
				}
				current++;
			}
		}
		throw new NoSuchElementException();
	}

	@Override
	public int size() {
		int returned = 0;
		for (Object o : embedded) {
			if (o instanceof List) {
				returned += ((List<?>) o).size();
			}
			else {
				returned++;
			}
		}
		return returned;
	}

}
