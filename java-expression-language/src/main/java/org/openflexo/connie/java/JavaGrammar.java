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

package org.openflexo.connie.java;

import org.openflexo.connie.expr.BinaryOperator;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionGrammar;
import org.openflexo.connie.expr.Operator;
import org.openflexo.connie.expr.OperatorNotSupportedException;
import org.openflexo.connie.expr.UnaryOperator;
import org.openflexo.connie.java.expr.JavaArithmeticBinaryOperator;
import org.openflexo.connie.java.expr.JavaArithmeticUnaryOperator;
import org.openflexo.connie.java.expr.JavaBinaryOperatorExpression;
import org.openflexo.connie.java.expr.JavaBooleanBinaryOperator;
import org.openflexo.connie.java.expr.JavaBooleanUnaryOperator;
import org.openflexo.connie.java.expr.JavaConditionalExpression;
import org.openflexo.connie.java.expr.JavaConstant;
import org.openflexo.connie.java.expr.JavaUnaryOperatorExpression;

/**
 * Represents Java expression language grammar
 * 
 * @author sylvain
 *
 */
public class JavaGrammar implements ExpressionGrammar {

	private static final BinaryOperator[] ALL_SUPPORTED_BINARY_OPERATORS = { JavaBooleanBinaryOperator.AND, JavaBooleanBinaryOperator.OR,
			JavaBooleanBinaryOperator.EQUALS, JavaBooleanBinaryOperator.NOT_EQUALS, JavaBooleanBinaryOperator.LESS_THAN,
			JavaBooleanBinaryOperator.LESS_THAN_OR_EQUALS, JavaBooleanBinaryOperator.GREATER_THAN,
			JavaBooleanBinaryOperator.GREATER_THAN_OR_EQUALS, JavaArithmeticBinaryOperator.ADDITION,
			JavaArithmeticBinaryOperator.SUBSTRACTION, JavaArithmeticBinaryOperator.MULTIPLICATION, JavaArithmeticBinaryOperator.DIVISION,
			JavaArithmeticBinaryOperator.MOD, JavaArithmeticBinaryOperator.SHIFT_LEFT, JavaArithmeticBinaryOperator.SHIFT_RIGHT,
			JavaArithmeticBinaryOperator.SHIFT_RIGHT_2, JavaArithmeticBinaryOperator.BITWISE_AND, JavaArithmeticBinaryOperator.BITWISE_OR,
			JavaArithmeticBinaryOperator.BITWISE_XOR };

	private static final UnaryOperator[] ALL_SUPPORTED_UNARY_OPERATORS = { JavaBooleanUnaryOperator.NOT,
			JavaArithmeticUnaryOperator.UNARY_PLUS, JavaArithmeticUnaryOperator.UNARY_MINUS, JavaArithmeticUnaryOperator.PRE_INCREMENT,
			JavaArithmeticUnaryOperator.PRE_DECREMENT, JavaArithmeticUnaryOperator.POST_INCREMENT,
			JavaArithmeticUnaryOperator.POST_DECREMENT, JavaArithmeticUnaryOperator.BITWISE_COMPLEMENT };

	private static final Operator[] logicalOperators = { JavaBooleanBinaryOperator.AND, JavaBooleanBinaryOperator.OR,
			JavaBooleanUnaryOperator.NOT };
	private static final Operator[] comparisonOperators = { JavaBooleanBinaryOperator.EQUALS, JavaBooleanBinaryOperator.NOT_EQUALS,
			JavaBooleanBinaryOperator.LESS_THAN, JavaBooleanBinaryOperator.LESS_THAN_OR_EQUALS, JavaBooleanBinaryOperator.GREATER_THAN,
			JavaBooleanBinaryOperator.GREATER_THAN_OR_EQUALS };
	private static final Operator[] arithmeticOperators = { JavaArithmeticBinaryOperator.ADDITION,
			JavaArithmeticBinaryOperator.SUBSTRACTION, JavaArithmeticBinaryOperator.MULTIPLICATION, JavaArithmeticBinaryOperator.DIVISION,
			JavaArithmeticUnaryOperator.UNARY_MINUS };

	@Override
	public BinaryOperator[] getAllSupportedBinaryOperators() {
		return ALL_SUPPORTED_BINARY_OPERATORS;
	}

	@Override
	public UnaryOperator[] getAllSupportedUnaryOperators() {
		return ALL_SUPPORTED_UNARY_OPERATORS;
	}

	private static String getSymbol(UnaryOperator operator) throws OperatorNotSupportedException {
		if (operator == JavaBooleanUnaryOperator.NOT) {
			return "!";
		}
		if (operator == JavaArithmeticUnaryOperator.UNARY_MINUS) {
			return "-";
		}
		if (operator == JavaArithmeticUnaryOperator.PRE_INCREMENT) {
			return "++";
		}
		if (operator == JavaArithmeticUnaryOperator.PRE_DECREMENT) {
			return "--";
		}
		if (operator == JavaArithmeticUnaryOperator.POST_INCREMENT) {
			return "++";
		}
		if (operator == JavaArithmeticUnaryOperator.POST_DECREMENT) {
			return "--";
		}
		if (operator == JavaArithmeticUnaryOperator.BITWISE_COMPLEMENT) {
			return "~";
		}
		throw new OperatorNotSupportedException();
	}

	private static String getSymbol(BinaryOperator operator) throws OperatorNotSupportedException {
		if (operator == JavaBooleanBinaryOperator.AND) {
			return "&&";
		}
		if (operator == JavaBooleanBinaryOperator.OR) {
			return "||";
		}
		if (operator == JavaBooleanBinaryOperator.EQUALS) {
			return "==";
		}
		if (operator == JavaBooleanBinaryOperator.NOT_EQUALS) {
			return "!=";
		}
		if (operator == JavaBooleanBinaryOperator.LESS_THAN) {
			return "<";
		}
		if (operator == JavaBooleanBinaryOperator.LESS_THAN_OR_EQUALS) {
			return "<=";
		}
		if (operator == JavaBooleanBinaryOperator.GREATER_THAN) {
			return ">";
		}
		if (operator == JavaBooleanBinaryOperator.GREATER_THAN_OR_EQUALS) {
			return ">=";
		}
		if (operator == JavaArithmeticBinaryOperator.ADDITION) {
			return "+";
		}
		if (operator == JavaArithmeticBinaryOperator.SUBSTRACTION) {
			return "-";
		}
		if (operator == JavaArithmeticBinaryOperator.MULTIPLICATION) {
			return "*";
		}
		if (operator == JavaArithmeticBinaryOperator.DIVISION) {
			return "/";
		}
		if (operator == JavaArithmeticBinaryOperator.MOD) {
			return "%";
		}
		if (operator == JavaArithmeticBinaryOperator.SHIFT_LEFT) {
			return "<<";
		}
		if (operator == JavaArithmeticBinaryOperator.SHIFT_RIGHT) {
			return ">>";
		}
		if (operator == JavaArithmeticBinaryOperator.SHIFT_RIGHT_2) {
			return ">>>";
		}
		if (operator == JavaArithmeticBinaryOperator.BITWISE_AND) {
			return "&";
		}
		if (operator == JavaArithmeticBinaryOperator.BITWISE_OR) {
			return "|";
		}
		if (operator == JavaArithmeticBinaryOperator.BITWISE_XOR) {
			return "^";
		}
		throw new OperatorNotSupportedException();
	}

	@Override
	public String getAlternativeSymbol(Operator operator) throws OperatorNotSupportedException {
		return null;
	}

	@Override
	public String getSymbol(Operator operator) throws OperatorNotSupportedException {
		if (operator instanceof UnaryOperator) {
			return getSymbol((UnaryOperator) operator);
		}
		if (operator instanceof BinaryOperator) {
			return getSymbol((BinaryOperator) operator);
		}
		throw new OperatorNotSupportedException();
	}

	@Override
	public Operator[] getLogicalOperators() {
		return logicalOperators;
	}

	@Override
	public Operator[] getComparisonOperators() {
		return comparisonOperators;
	}

	@Override
	public Operator[] getArithmeticOperators() {
		return arithmeticOperators;
	}

	@Override
	public Operator[] getScientificOperators() {
		return null;
	}

	@Override
	public Operator[] getTrigonometricOperators() {
		return null;
	}

	@Override
	public JavaBinaryOperatorExpression makeBinaryOperatorExpression(BinaryOperator operator, Expression leftArgument,
			Expression rightArgument) {
		return new JavaBinaryOperatorExpression(operator, leftArgument, rightArgument);
	}

	@Override
	public JavaUnaryOperatorExpression makeUnaryOperatorExpression(UnaryOperator operator, Expression argument) {
		return new JavaUnaryOperatorExpression(operator, argument);
	}

	@Override
	public JavaConditionalExpression makeConditionalExpression(Expression condition, Expression thenExpression, Expression elseExpression) {
		return new JavaConditionalExpression(condition, thenExpression, elseExpression);
	}

	@Override
	public <O> Constant<O> getConstant(O value) {
		return JavaConstant.makeConstant(value);
	}

}
