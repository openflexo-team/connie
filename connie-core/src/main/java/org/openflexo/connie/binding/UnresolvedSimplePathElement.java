/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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
import java.util.Objects;
import java.util.logging.Logger;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingPathElement.BindingPathCheck;
import org.openflexo.connie.type.UnresolvedType;

/**
 * Modelize an unresolved simple path element
 * 
 * @author sylvain
 * 
 */
public class UnresolvedSimplePathElement extends SimplePathElementImpl {

	private static final Logger logger = Logger.getLogger(DataBinding.class.getPackage().getName());

	// public Exception creation;

	public UnresolvedSimplePathElement(IBindingPathElement parent, String propertyName, Bindable bindable) {
		super(parent, propertyName, new UnresolvedType("?"), bindable);
		// creation = new Exception();
	}

	@Override
	public BindingPathCheck checkBindingPathIsValid(IBindingPathElement parentElement, Type parentType) {

		BindingPathCheck check = super.checkBindingPathIsValid(parentElement, parentType);

		check.invalidBindingReason = "Unresolved path element (by nature): " + getParsed();
		check.valid = false;

		return check;
	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return "<html>Unresolved</html>";
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) {
		return null;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) {
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public void resolve() {
		// Never resolvable
	}

	public SimplePathElement<?> attemptResolvingFromParent() {
		return getBindable().getBindingFactory().makeSimplePathElement(getParent(), getParsed(), getBindable());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(getParsed());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UnresolvedSimplePathElement other = (UnresolvedSimplePathElement) obj;
		if (!Objects.equals(getParsed(), other.getParsed())) {
		}
		return Objects.equals(getParsed(), other.getParsed());
	}

}
