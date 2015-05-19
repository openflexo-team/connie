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

package org.openflexo.connie.expr;

import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.expr.Constant.BooleanConstant;
import org.openflexo.connie.expr.Constant.FloatConstant;
import org.openflexo.connie.expr.Constant.FloatSymbolicConstant;

/**
 * This ExpressionTransformer is used to evaluate expressions
 * 
 * @author sylvain
 * 
 */
public class ExpressionEvaluator implements ExpressionTransformer {

	/**
	 * Performs the transformation of a resulting expression e, asserting that all contained expressions have already been transformed (this
	 * method is not recursive, to do so, use Expression.transform(ExpressionTransformer) API)
	 */
	@Override
	public Expression performTransformation(Expression e) throws TransformException {
		// e._checkSemanticallyAcceptable();
		if (e instanceof BinaryOperatorExpression) {
			return transformBinaryOperatorExpression((BinaryOperatorExpression) e);
		} else if (e instanceof UnaryOperatorExpression) {
			return transformUnaryOperatorExpression((UnaryOperatorExpression) e);
		} else if (e instanceof ConditionalExpression) {
			return transformConditionalExpression((ConditionalExpression) e);
		} else if (e instanceof FloatSymbolicConstant) {
			return transformFloatSymbolicConstant((FloatSymbolicConstant) e);
		}
		return e;
	}

	private Expression transformBinaryOperatorExpression(BinaryOperatorExpression e) throws TransformException {
		// If both arguments are constants, we try to evaluate them
		if (e.getLeftArgument() instanceof Constant && e.getRightArgument() instanceof Constant
		/*&& e.getLeftArgument().getEvaluationType() == e.getRightArgument().getEvaluationType()*/
		/*&& (e.getLeftArgument() != ObjectSymbolicConstant.NULL) && (e.getRightArgument() != ObjectSymbolicConstant.NULL)*/) {
			Constant returned = e.getOperator().evaluate((Constant) e.getLeftArgument(), (Constant) e.getRightArgument());
			return returned;
		}
		if (e.getLeftArgument() instanceof Constant /*&& (e.getLeftArgument() != ObjectSymbolicConstant.NULL)*/) {
			return e.getOperator().evaluate((Constant) e.getLeftArgument(), e.getRightArgument());
		}
		if (e.getRightArgument() instanceof Constant /*&& (e.getRightArgument() != ObjectSymbolicConstant.NULL)*/) {
			return e.getOperator().evaluate(e.getLeftArgument(), (Constant) e.getRightArgument());
		}
		return e;
	}

	private Expression transformUnaryOperatorExpression(UnaryOperatorExpression e) throws TransformException {
		if (e.getArgument() instanceof Constant /*&& (e.getArgument() != ObjectSymbolicConstant.NULL)*/) {
			Constant returned = e.getOperator().evaluate((Constant) e.getArgument());
			return returned;
		}
		return e;
	}

	private Expression transformConditionalExpression(ConditionalExpression e) throws TransformException {
		if (e.getCondition() == BooleanConstant.TRUE) {
			return e.getThenExpression();
		} else if (e.getCondition() == BooleanConstant.FALSE) {
			return e.getElseExpression();
		}
		return e;
	}

	private FloatConstant transformFloatSymbolicConstant(FloatSymbolicConstant e) {
		return new FloatConstant(e.getValue());
	}

}
