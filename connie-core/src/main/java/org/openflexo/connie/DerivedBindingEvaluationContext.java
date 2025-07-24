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

package org.openflexo.connie;

import java.util.function.Function;

import org.openflexo.connie.expr.ExpressionEvaluator;

/**
 * A {@link BindingEvaluationContext} based on a delegate {@link BindingEvaluationContext}
 * 
 * @author sylvain
 * 
 */
public class DerivedBindingEvaluationContext {

	private final BindingEvaluationContext baseBindingEvaluationContext;
	private final Function<BindingVariable, Object> localVariableResolver;

	public DerivedBindingEvaluationContext(BindingEvaluationContext baseBindingEvaluationContext,
			Function<BindingVariable, Object> localVariableResolver) {
		this.baseBindingEvaluationContext = baseBindingEvaluationContext;
		this.localVariableResolver = localVariableResolver;
	}

	/**
	 * Return the value of symbolic variable {@link BindingVariable} in current run-time context
	 * 
	 * @param variable
	 *            the binding to evaluate.
	 */
	public Object getValue(BindingVariable variable) {
		Object returned = localVariableResolver.apply(variable);
		if (returned != null) {
			return returned;
		}
		return baseBindingEvaluationContext.getValue(variable);
	}

	public ExpressionEvaluator getEvaluator() {
		return baseBindingEvaluationContext.getEvaluator();
	}
}
