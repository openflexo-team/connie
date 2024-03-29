/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

import java.lang.reflect.Type;
import java.util.Comparator;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.Typed;

/**
 * General API for an element of a formal binding path, whichever type it is.
 * 
 * @author sylvain
 * 
 */
public interface IBindingPathElement extends Typed {

	public final String BINDING_PATH_CHANGED = "BindingPathChanged";

	Comparator<IBindingPathElement> COMPARATOR = new Comparator<IBindingPathElement>() {
		@Override
		public int compare(IBindingPathElement o1, IBindingPathElement o2) {
			if (o1.getLabel() == null) {
				if (o2.getLabel() == null) {
					return 0;
				}
				return -1;
			}
			else if (o2.getLabel() == null) {
				return 1;
			}
			return o1.getLabel().compareTo(o2.getLabel());
		}
	};

	/**
	 * Return boolean indicating if this path element is resolved
	 * 
	 * @return
	 */
	public boolean isResolved();

	/**
	 * Try to resolve this binding path element assuming this element is not resolved yet
	 * 
	 */
	public void resolve();

	/**
	 * Return accessed type for this {@link IBindingPathElement}
	 * 
	 * @return
	 */
	@Override
	Type getType();

	/**
	 * Return serialized representation for this {@link IBindingPathElement}
	 * 
	 * @return
	 */
	String getSerializationRepresentation();

	/**
	 * Returns label of this {@link IBindingPathElement}
	 * 
	 * @return
	 */
	String getLabel();

	/**
	 * Returns toolip text of this {@link IBindingPathElement}
	 * 
	 * @return
	 */
	String getTooltipText(Type resultingType);

	/**
	 * Return a flag indicating if this path element is settable or not (settable indicates that a new value can be set)
	 */
	boolean isSettable();

	/**
	 * Evaluate and return value for related path element, given a binding evaluation context
	 * 
	 * @param target
	 *            : address object as target of parent path: the object on which setting will be performed
	 * @param context
	 *            : binding evaluation context
	 * @return accessed value
	 * @throws NullReferenceException
	 * @throws TypeMismatchException
	 */
	Object getBindingValue(Object target, BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException, InvocationTargetTransformException;

	/**
	 * When set to true, means that this path element is beeing changing, and that available accessible path elements following this path
	 * element are to be recomputed<br>
	 * This method is generally called from {@link BindingFactory}
	 * 
	 * @return
	 */
	public boolean isNotifyingBindingPathChanged();
}
