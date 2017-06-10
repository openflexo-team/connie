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

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class PropertyChangeListenerRegistrationManager {

	private List<PropertyChangeListenerRegistration> registrations;

	public PropertyChangeListenerRegistrationManager() {
		registrations = new Vector<>();
	}

	public boolean hasListener(String propertyName, PropertyChangeListener listener, HasPropertyChangeSupport hasPropertyChangeSupport) {
		for (PropertyChangeListenerRegistration registration : registrations) {
			if (registration.hasPropertyChangeSupport == hasPropertyChangeSupport && registration.listener == listener
					&& registration.propertyName == null && propertyName == null
					|| registration.propertyName != null && registration.propertyName.equals(propertyName)) {
				return true;
			}
		}
		return false;
	}

	public void delete() {
		for (PropertyChangeListenerRegistration registration : new ArrayList<>(registrations)) {
			registration.removeListener();
		}
		// Just to be sure
		registrations.clear();
	}

	public class PropertyChangeListenerRegistration {
		private final String propertyName;
		private final PropertyChangeListener listener;
		private final HasPropertyChangeSupport hasPropertyChangeSupport;

		public PropertyChangeListenerRegistration(PropertyChangeListener listener, HasPropertyChangeSupport hasPropertyChangeSupport) {
			this(null, listener, hasPropertyChangeSupport);
		}

		public PropertyChangeListenerRegistration(String propertyName, PropertyChangeListener listener,
				HasPropertyChangeSupport hasPropertyChangeSupport) {
			this.propertyName = propertyName;
			this.listener = listener;
			this.hasPropertyChangeSupport = hasPropertyChangeSupport;
			if (propertyName != null) {
				hasPropertyChangeSupport.getPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);
			}
			else {
				hasPropertyChangeSupport.getPropertyChangeSupport().addPropertyChangeListener(listener);
			}
			registrations.add(this);
		}

		public void removeListener() {
			if (propertyName != null) {
				hasPropertyChangeSupport.getPropertyChangeSupport().removePropertyChangeListener(propertyName, listener);
			}
			else {
				hasPropertyChangeSupport.getPropertyChangeSupport().removePropertyChangeListener(listener);
			}
			registrations.remove(this);
		}
	}

	public void addListener(PropertyChangeListener listener, HasPropertyChangeSupport hasPropertyChangeSupport) {
		addListener(null, listener, hasPropertyChangeSupport);
	}

	public void addListener(String propertyName, PropertyChangeListener listener, HasPropertyChangeSupport hasPropertyChangeSupport) {
		new PropertyChangeListenerRegistration(propertyName, listener, hasPropertyChangeSupport);
	}

	public void removeListener(PropertyChangeListener listener, HasPropertyChangeSupport hasPropertyChangeSupport) {
		removeListener(null, listener, hasPropertyChangeSupport);
	}

	public void removeListener(String propertyName, PropertyChangeListener listener, HasPropertyChangeSupport hasPropertyChangeSupport) {
		Iterator<PropertyChangeListenerRegistration> i = registrations.iterator();
		while (i.hasNext()) {
			PropertyChangeListenerRegistration r = i.next();
			if (r.hasPropertyChangeSupport == hasPropertyChangeSupport
					&& (r.propertyName == null && propertyName == null || propertyName != null && propertyName.equals(r.propertyName))
					&& r.listener == listener) {
				i.remove();
			}
		}
	}

}
