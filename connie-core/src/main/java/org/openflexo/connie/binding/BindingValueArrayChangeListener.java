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

import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.logging.FlexoLogger;

/**
 * A listener that tracks evalutated value of a list DataBinding, given a run-time context given by a {@link BindingEvaluationContext}<br>
 * Modifications are detected from the evaluation of the content of the list
 * 
 * @author sylvain
 * 
 */
public abstract class BindingValueArrayChangeListener<T> extends BindingValueChangeListener<T[]> {

	private static final Logger LOGGER = FlexoLogger.getLogger(BindingValueArrayChangeListener.class.getName());

	private T[] lastKnownValues = null;

	public BindingValueArrayChangeListener(DataBinding<T[]> dataBinding, BindingEvaluationContext context) {
		super(dataBinding, context);
	}

	public void delete() {
		super.delete();
	}

	protected void fireChange(Object source) {

		T[] newValue;
		try {
			newValue = evaluateValue();
		} catch (NullReferenceException e) {
			LOGGER.warning("Could not evaluate " + getDataBinding() + " with context " + getContext()
					+ " because NullReferenceException has raised");
			newValue = null;
		}
		if (newValue != lastNotifiedValue) {
			lastNotifiedValue = newValue;
			bindingValueChanged(source, newValue);
		} else {
			// Arrays are sames, but values inside arrays, may have changed

			if (lastKnownValues == null || newValue == null) {
				// We continue
			} else if (lastKnownValues.length != newValue.length) {
				// We continue
			} else {
				boolean valuesAreSame = true;
				for (int i = 0; i < lastKnownValues.length; i++) {
					if (!lastKnownValues[i].equals(newValue[i])) {
						valuesAreSame = false;
					}
				}
				if (!valuesAreSame) {
					bindingValueChanged(source, newValue);
				}
			}
		}
	}
}
