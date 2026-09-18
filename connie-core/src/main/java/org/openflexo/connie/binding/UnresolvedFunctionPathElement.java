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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionTransformer;
import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.type.UnresolvedType;

/**
 * Modelize an unresolved function (method call) path element.
 *
 * This is the counterpart, for calls, of {@link UnresolvedSimplePathElement}. A {@link BindingFactory} chooses the KIND of path element to
 * build from the type of the parent element, and that type is not always known at construction time: a {@link FunctionPathElement} only
 * knows its return type once it has been resolved, so any call whose parent is itself a call is built while the parent's type is still
 * unknown. Returning a concrete implementation in that situation is what used to break Openflexo's FML - the factory fell back on a Java
 * method element, which can never become a behaviour element afterwards, and the call silently failed to resolve.
 *
 * Rather than guessing, the factory returns this explicitly UNDECIDED element. {@link org.openflexo.connie.expr.BindingPath} then calls
 * {@link #attemptResolvingFromParent()} once the preceding elements have been resolved, and substitutes whatever the factory now builds.
 *
 * @author sylvain
 *
 */
public class UnresolvedFunctionPathElement extends SimpleMethodPathElementImpl<Function> {

	public UnresolvedFunctionPathElement(IBindingPathElement parent, String methodName, List<DataBinding<?>> args, Bindable bindable) {
		super(parent, methodName, args, bindable);
		setType(new UnresolvedType("?"));
	}

	/**
	 * Ask the {@link BindingFactory} to build this element again, now that the parent is resolved and its type known. Returns an element of
	 * the appropriate kind, or another unresolved one if the parent is still undecidable.
	 */
	public SimpleMethodPathElement<?> attemptResolvingFromParent() {
		if (getBindable() != null && getBindable().getBindingFactory() != null) {
			return getBindable().getBindingFactory().makeSimpleMethodPathElement(getParent(), getParsed(), getArguments(), getBindable());
		}
		return null;
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
		return getParsed();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return "<html>Unresolved</html>";
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) {
		return null;
	}

	@Override
	public boolean isNotificationSafe() {
		return false;
	}

	@Override
	public boolean requiresContext() {
		return false;
	}

	@Override
	public boolean isResolved() {
		return false;
	}

	@Override
	public void resolve() {
		// Never resolvable as such: BindingPath substitutes it through attemptResolvingFromParent()
	}

	/**
	 * Even a placeholder must transform its ARGUMENTS: an expression transformer simplifies them in place (that is how
	 * function2(8+1,9,10-1) serializes as function2(9,9,9)), and that has to keep working while the element itself is
	 * still undecided.
	 */
	@Override
	public FunctionPathElement<Function> transform(ExpressionTransformer transformer) throws TransformException {

		boolean hasBeenTransformed = false;
		List<DataBinding<?>> transformedArgs = new ArrayList<>();

		for (DataBinding<?> argValue : getArguments()) {
			if (argValue != null) {
				Expression currentExpression = argValue.getExpression();
				if (currentExpression != null) {
					Expression transformedExpression = currentExpression.transform(transformer);
					if (!transformedExpression.equals(currentExpression)) {
						hasBeenTransformed = true;
						DataBinding<?> newTransformedBinding = new DataBinding<>(argValue.getOwner(), argValue.getDeclaredType(),
								argValue.getBindingDefinitionType(), false);
						newTransformedBinding.setExpression(transformedExpression);
						newTransformedBinding.isValid();
						transformedArgs.add(newTransformedBinding);
					}
					else {
						transformedArgs.add(argValue);
					}
				}
			}
		}

		if (!hasBeenTransformed) {
			return this;
		}

		return new UnresolvedFunctionPathElement(getParent(), getParsed(), transformedArgs, getBindable());
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
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		UnresolvedFunctionPathElement other = (UnresolvedFunctionPathElement) obj;
		return Objects.equals(getParsed(), other.getParsed());
	}
}
