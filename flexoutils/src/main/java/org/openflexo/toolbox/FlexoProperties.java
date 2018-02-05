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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * @author gpolet
 */
public class FlexoProperties extends Properties implements Comparator<Object> {

	private boolean isStoring = false;

	/**
	 * 
	 */
	public FlexoProperties() {
		super();
	}

	public FlexoProperties(Hashtable<Object, Object> p) {
		super();// Ok, I don't know what Sun has done here but it seems that
				// Properties is the lamest class ever made
				// (not typed although it can only take String as keys and values,
				// and some kind of crappy and totally incoherent way of dealing with some default values).
		putAll(p);// This call is a lot safer than passing the hashtable in the super().
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Properties#store(java.io.OutputStream, java.lang.String)
	 */
	@Override
	public synchronized void store(OutputStream out, String header) throws IOException {
		isStoring = true;
		super.store(out, header);
		isStoring = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Properties#propertyNames()
	 */
	@Override
	public Enumeration<Object> keys() {
		if (isStoring) {
			return sortedKeys();
		}
		return super.keys();
	}

	public Enumeration<Object> unsortedKeys() {
		return super.keys();
	}

	public Enumeration<Object> sortedKeys() {
		return new PropsEnum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Object o1, Object o2) {
		return o1.toString().compareTo(o2.toString());
	}

	private class PropsEnum implements Enumeration<Object> {

		private Object[] objects;

		private int index = 0;

		public PropsEnum() {
			index = 0;// Resets the enumeration
			Enumeration<Object> en = unsortedKeys();
			objects = new Object[size()];
			int i = 0;
			while (en.hasMoreElements()) {
				objects[i++] = en.nextElement();
			}
			Arrays.sort(objects, FlexoProperties.this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Enumeration#hasMoreElements()
		 */
		@Override
		public boolean hasMoreElements() {
			return index < objects.length;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Enumeration#nextElement()
		 */
		@Override
		public Object nextElement() {
			return objects[index++];
		}

	}

	public boolean isStoring() {
		return isStoring;
	}

	public void setStoring(boolean isStoring) {
		this.isStoring = isStoring;
	}

}
