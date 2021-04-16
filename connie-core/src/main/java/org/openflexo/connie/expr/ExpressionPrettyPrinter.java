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

import java.io.PrintStream;
import java.lang.reflect.Type;

public abstract class ExpressionPrettyPrinter {

	private ExpressionGrammar grammar;

	public ExpressionPrettyPrinter(ExpressionGrammar grammar) {
		super();
		this.grammar = grammar;
	}

	public void print(Expression expression, PrintStream out) {
		out.print(getStringRepresentation(expression));
	}

	public BinaryOperator[] getAllSupportedBinaryOperators() {
		return grammar.getAllSupportedBinaryOperators();
	}

	public UnaryOperator[] getAllSupportedUnaryOperators() {
		return grammar.getAllSupportedUnaryOperators();
	}

	public String getAlternativeSymbol(Operator operator) throws OperatorNotSupportedException {
		return grammar.getAlternativeSymbol(operator);
	}

	public String getSymbol(Operator operator) throws OperatorNotSupportedException {
		return grammar.getSymbol(operator);
	}

	public String getStringRepresentation(Expression expression) {
		if (expression == null) {
			return "null";
		}
		if (expression instanceof BindingValue) {
			return makeStringRepresentation((BindingValue) expression);
		}
		if (expression instanceof Constant) {
			return makeStringRepresentation((Constant<?>) expression);
		}
		if (expression instanceof UnaryOperatorExpression) {
			return makeStringRepresentation((UnaryOperatorExpression) expression);
		}
		if (expression instanceof BinaryOperatorExpression) {
			return makeStringRepresentation((BinaryOperatorExpression) expression);
		}
		if (expression instanceof ConditionalExpression) {
			return makeStringRepresentation((ConditionalExpression) expression);
		}
		if (expression instanceof CastExpression) {
			return makeStringRepresentation((CastExpression) expression);
		}

		if (expression instanceof UnresolvedExpression) {
			return "<UnresolvedExpression>";
		}
		// return "<unknown " + expression.getClass().getSimpleName() + ">";
		return expression.toString();
	}

	protected abstract String makeStringRepresentation(Constant<?> constant);

	protected abstract String makeStringRepresentation(Type type);

	protected abstract String makeStringRepresentation(BindingValue bv);

	protected abstract String makeStringRepresentation(UnaryOperatorExpression expression);

	protected abstract String makeStringRepresentation(BinaryOperatorExpression expression);

	protected abstract String makeStringRepresentation(ConditionalExpression expression);

	protected abstract String makeStringRepresentation(CastExpression expression);

}
