/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Connie-core, a component of the software infrastructure 
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

package org.openflexo.connie.binding;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.logging.FlexoLogger;

/**
 * A listener that tracks evalutated value of a list DataBinding, given a run-time context given by a {@link BindingEvaluationContext}<br>
 * Modifications are detected from the evaluation of the content of the list (even if the list is the same, but if values inside the list
 * are not the same, then change is fired)
 * 
 * @author sylvain
 * 
 */
public abstract class BindingValueListChangeListener<T2, T extends Collection<T2>> extends BindingValueChangeListener<T> {

	private static final Logger LOGGER = FlexoLogger.getLogger(BindingValueListChangeListener.class.getName());

	private Collection<T2> lastKnownValues = null;

	// Boolean used to indicate that lastKnownValues has never been notified
	private boolean neverNotified = true;

	public BindingValueListChangeListener(DataBinding<T> dataBinding, BindingEvaluationContext context) {
		super(dataBinding, context);
	}

	@Override
	public void delete() {
		super.delete();
	}

	@Override
	protected void fireChange(PropertyChangeEvent evt) {

		T newValue;
		try {
			newValue = evaluateValue();
		} catch (NullReferenceException e) {
			LOGGER.warning("Could not evaluate " + getDataBinding() + " with context " + getContext()
					+ " because NullReferenceException has raised");
			newValue = null;
		} catch (InvocationTargetException e) {
			LOGGER.warning("Could not evaluate " + getDataBinding() + " with context " + getContext() + " because Exception has raised: "
					+ e.getTargetException());
			newValue = null;
		} catch (ClassCastException e) {
			LOGGER.warning("ClassCastException while evaluating " + getDataBinding() + " with context " + getContext());
			newValue = null;
		}

		/*if (getDataBinding().toString().equals("data.selectedDocumentElements")) {
			System.out.println(">>>>>>>>>> fireChange for " + getDataBinding().toString());
			System.out.println("newValue=" + newValue);
			System.out.println("lastNotifiedValue=" + lastNotifiedValue);
			System.out.println("lastKnownValues=" + lastKnownValues);
		}*/

		// Fixed CONNIE-17
		// OK, i get the problem
		// When the first notification raised for a new value set to null, both values are not different
		// and refreshObserving() is not called, thus some objects are not observed
		// A solution is to force refreshObserving() to be called the first time, even values are both null

		if (newValue != lastNotifiedValue || neverNotified) {
			lastNotifiedValue = newValue;
			/*if (getDataBinding() != null) {
				System.out.println("1-For " + getDataBinding().toString() + " notifying from " + lastNotifiedValue + " to " + newValue);
			}*/
			bindingValueChanged(evt.getSource(), newValue);
			refreshObserving(false);
		}
		else {
			// Lists are sames, but values inside lists, may have changed
			try {
				if ((lastKnownValues == null && newValue != null) || (lastKnownValues != null && !lastKnownValues.equals(newValue))) {
					/*if (getDataBinding() != null) {
						System.out.println("2-For " + getDataBinding().toString() + " notifying from " + lastKnownValues + " to " + newValue);
					}*/
					lastKnownValues = (newValue != null ? new ArrayList<>(newValue) : null);
					bindingValueChanged(evt.getSource(), newValue);
					refreshObserving(false);
				}
			} catch (ConcurrentModificationException e) {
				// List changed while equals() beeing computed: this is a good reason to fire change
				lastKnownValues = (newValue != null ? new ArrayList<>(newValue) : null);
				bindingValueChanged(evt.getSource(), newValue);
				refreshObserving(false);
			}
		}

		neverNotified = false;
	}
}
