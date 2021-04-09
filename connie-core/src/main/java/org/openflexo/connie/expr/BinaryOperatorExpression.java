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

package org.openflexo.connie.expr;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Vector;

import org.openflexo.connie.exception.TypeMismatchException;

public abstract class BinaryOperatorExpression extends Expression {

	private BinaryOperator operator;
	private Expression leftArgument;
	private Expression rightArgument;

	public BinaryOperatorExpression(BinaryOperator operator, Expression leftArgument, Expression rightArgument) {
		super();
		this.operator = operator;
		this.leftArgument = leftArgument;
		this.rightArgument = rightArgument;
	}

	@Override
	public int getDepth() {
		int left = leftArgument == null ? 0 : leftArgument.getDepth();
		int right = rightArgument == null ? 0 : rightArgument.getDepth();
		return Math.max(left, right) + 1;
	}

	public int getPriority() {
		if (operator != null) {
			return operator.getPriority();
		}
		return -1;
	}

	public Expression getLeftArgument() {
		return leftArgument;
	}

	public void setLeftArgument(Expression leftArgument) {
		this.leftArgument = leftArgument;
	}

	public BinaryOperator getOperator() {
		return operator;
	}

	public void setOperator(BinaryOperator operator) {
		this.operator = operator;
	}

	public Expression getRightArgument() {
		return rightArgument;
	}

	public void setRightArgument(Expression rightArgument) {
		this.rightArgument = rightArgument;
	}

	/*@Override
	public Expression evaluate(EvaluationContext context, Bindable bindable) throws TypeMismatchException {
		_checkSemanticallyAcceptable();
		// System.out.println("left="+leftArgument+" of "+leftArgument.getClass().getSimpleName()+" as "+leftArgument.evaluate(context)+" of "+leftArgument.evaluate(context).getClass().getSimpleName());
		// System.out.println("right="+rightArgument+" of "+rightArgument.getClass().getSimpleName()+" as "+rightArgument.evaluate(context)+" of "+rightArgument.evaluate(context).getClass().getSimpleName());
	
		Expression evaluatedLeftArgument = leftArgument.evaluate(context, bindable);
	
		// special case for AND operator, lazy evaluation
		if (operator == BooleanBinaryOperator.AND && evaluatedLeftArgument == BooleanConstant.FALSE) {
			return BooleanConstant.FALSE; // No need to analyze further
		}
	
		Expression evaluatedRightArgument = rightArgument.evaluate(context, bindable);
	
		if (evaluatedLeftArgument instanceof Constant && evaluatedRightArgument instanceof Constant) {
			Constant returned = operator.evaluate((Constant) evaluatedLeftArgument, (Constant) evaluatedRightArgument);
			return returned;
		}
		if (evaluatedLeftArgument instanceof Constant) {
			return operator.evaluate((Constant) evaluatedLeftArgument, evaluatedRightArgument);
		}
		if (evaluatedRightArgument instanceof Constant) {
			return operator.evaluate(evaluatedLeftArgument, (Constant) evaluatedRightArgument);
		}
		return new BinaryOperatorExpression(operator, evaluatedLeftArgument, evaluatedRightArgument);
	}*/

	@Override
	public void visit(ExpressionVisitor visitor) throws VisitorException {
		if (leftArgument != null) {
			leftArgument.visit(visitor);
		}
		if (rightArgument != null) {
			rightArgument.visit(visitor);
		}
		visitor.visit(this);
	}

	@Override
	public EvaluationType getEvaluationType() throws TypeMismatchException {
		return getOperator().getEvaluationType(getLeftArgument().getEvaluationType(), getRightArgument().getEvaluationType());
	}

	@Override
	protected Vector<Expression> getChilds() {
		Vector<Expression> returned = new Vector<>();
		returned.add(getLeftArgument());
		returned.add(getRightArgument());
		return returned;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BinaryOperatorExpression) {
			BinaryOperatorExpression e = (BinaryOperatorExpression) obj;
			return Objects.equals(getOperator(), e.getOperator()) && Objects.equals(getLeftArgument(), e.getLeftArgument())
					&& Objects.equals(getRightArgument(), e.getRightArgument());
		}
		return super.equals(obj);
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public Type getAccessedType() {
		try {
			return getEvaluationType().getType();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			return null;
		}
	}

}
