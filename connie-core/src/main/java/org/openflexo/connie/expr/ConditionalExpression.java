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

import java.lang.reflect.Type;
import java.util.Vector;

import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;

public abstract class ConditionalExpression extends Expression {

	private Expression condition;
	private Expression thenExpression;
	private Expression elseExpression;

	public ConditionalExpression(Expression condition, Expression thenExpression, Expression elseExpression) {
		super();
		this.condition = condition;
		this.thenExpression = thenExpression;
		this.elseExpression = elseExpression;
	}

	@Override
	public int getDepth() {
		return Math.max(Math.max(thenExpression.getDepth(), elseExpression.getDepth()), condition.getDepth()) + 1;
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public void visit(ExpressionVisitor visitor) throws VisitorException {
		if (condition != null) {
			condition.visit(visitor);
		}
		if (thenExpression != null) {
			thenExpression.visit(visitor);
		}
		if (elseExpression != null) {
			elseExpression.visit(visitor);
		}
		visitor.visit(this);
	}

	@Override
	public Type getAccessedType() {
		Type thenType = thenExpression.getAccessedType();
		Type elseType = elseExpression.getAccessedType();

		if (TypeUtils.isTypeAssignableFrom(thenType, elseType)) {
			return thenType;
		}
		else if (TypeUtils.isTypeAssignableFrom(elseType, thenType)) {
			return elseType;
		}
		return Object.class;
	}

	@Override
	public EvaluationType getEvaluationType() throws TypeMismatchException {
		EvaluationType thenEvaluationType = thenExpression.getEvaluationType();
		EvaluationType elseEvaluationType = elseExpression.getEvaluationType();
		if (thenEvaluationType != elseEvaluationType) {
			throw TypeMismatchException.buildIncompatibleEvaluationTypeException(thenEvaluationType, elseEvaluationType);
		}
		return thenEvaluationType;
	}

	@Override
	protected Vector<Expression> getChilds() {
		Vector<Expression> returned = new Vector<>();
		returned.add(getCondition());
		returned.add(getThenExpression());
		returned.add(getElseExpression());
		return returned;
	}

	public Expression getCondition() {
		return condition;
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public Expression getThenExpression() {
		return thenExpression;
	}

	public void setThenExpression(Expression thenExpression) {
		this.thenExpression = thenExpression;
	}

	public Expression getElseExpression() {
		return elseExpression;
	}

	public void setElseExpression(Expression elseExpression) {
		this.elseExpression = elseExpression;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConditionalExpression) {
			ConditionalExpression e = (ConditionalExpression) obj;
			return getCondition().equals(e.getCondition()) && getThenExpression().equals(e.getThenExpression())
					&& getElseExpression().equals(e.getElseExpression());
		}
		return super.equals(obj);
	}

}
