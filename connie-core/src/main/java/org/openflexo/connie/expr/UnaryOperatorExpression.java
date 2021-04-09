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
import java.util.Vector;

import org.openflexo.connie.exception.TypeMismatchException;

public abstract class UnaryOperatorExpression extends Expression {

	private UnaryOperator operator;
	private Expression argument;

	public UnaryOperatorExpression(UnaryOperator operator, Expression argument) {
		super();
		this.operator = operator;
		this.argument = argument;
	}

	@Override
	public int getDepth() {
		return argument.getDepth() + 1;
	}

	public int getPriority() {
		if (operator != null) {
			return operator.getPriority();
		}
		return -1;
	}

	public Expression getArgument() {
		return argument;
	}

	public void setArgument(Expression argument) {
		this.argument = argument;
	}

	public UnaryOperator getOperator() {
		return operator;
	}

	public void setOperator(UnaryOperator operator) {
		this.operator = operator;
	}

	/*@Override
	public Expression evaluate(EvaluationContext context, Bindable bindable) throws TypeMismatchException {
		_checkSemanticallyAcceptable();
		Expression evaluatedArgument = argument.evaluate(context, bindable);
		if (evaluatedArgument instanceof Constant) {
			Constant returned = operator.evaluate((Constant) evaluatedArgument);
			// if (context != null) return context.getConstantFactory().makeConstant(returned.getParsingValue());
			return returned;
		}
		return new UnaryOperatorExpression(operator, evaluatedArgument);
	}*/

	@Override
	public void visit(ExpressionVisitor visitor) throws VisitorException {
		argument.visit(visitor);
		visitor.visit(this);
	}

	@Override
	public EvaluationType getEvaluationType() throws TypeMismatchException {
		return getOperator().getEvaluationType(getArgument().getEvaluationType());
	}

	@Override
	protected Vector<Expression> getChilds() {
		Vector<Expression> returned = new Vector<>();
		returned.add(getArgument());
		return returned;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UnaryOperatorExpression) {
			UnaryOperatorExpression e = (UnaryOperatorExpression) obj;
			return getOperator().equals(e.getOperator()) && getArgument().equals(e.getArgument());
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
