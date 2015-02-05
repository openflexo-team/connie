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

import org.openflexo.connie.expr.ArithmeticBinaryOperator;
import org.openflexo.connie.expr.ArithmeticUnaryOperator;
import org.openflexo.connie.expr.BinaryOperator;
import org.openflexo.connie.expr.BooleanBinaryOperator;
import org.openflexo.connie.expr.BooleanUnaryOperator;
import org.openflexo.connie.expr.ExpressionGrammar;
import org.openflexo.connie.expr.Operator;
import org.openflexo.connie.expr.OperatorNotSupportedException;
import org.openflexo.connie.expr.UnaryOperator;

public class JavaGrammar implements ExpressionGrammar {

	private static final BinaryOperator[] ALL_SUPPORTED_BINARY_OPERATORS = { BooleanBinaryOperator.AND, BooleanBinaryOperator.OR,
			BooleanBinaryOperator.EQUALS, BooleanBinaryOperator.NOT_EQUALS, BooleanBinaryOperator.LESS_THAN,
			BooleanBinaryOperator.LESS_THAN_OR_EQUALS, BooleanBinaryOperator.GREATER_THAN, BooleanBinaryOperator.GREATER_THAN_OR_EQUALS,
			ArithmeticBinaryOperator.ADDITION, ArithmeticBinaryOperator.SUBSTRACTION, ArithmeticBinaryOperator.MULTIPLICATION,
			ArithmeticBinaryOperator.DIVISION, ArithmeticBinaryOperator.POWER, };

	private static final UnaryOperator[] ALL_SUPPORTED_UNARY_OPERATORS = { BooleanUnaryOperator.NOT, ArithmeticUnaryOperator.UNARY_MINUS,
			ArithmeticUnaryOperator.SIN, ArithmeticUnaryOperator.ASIN, ArithmeticUnaryOperator.COS, ArithmeticUnaryOperator.ACOS,
			ArithmeticUnaryOperator.TAN, ArithmeticUnaryOperator.ATAN, ArithmeticUnaryOperator.EXP, ArithmeticUnaryOperator.LOG,
			ArithmeticUnaryOperator.SQRT };

	@Override
	public BinaryOperator[] getAllSupportedBinaryOperators() {
		return ALL_SUPPORTED_BINARY_OPERATORS;
	}

	@Override
	public UnaryOperator[] getAllSupportedUnaryOperators() {
		return ALL_SUPPORTED_UNARY_OPERATORS;
	}

	public String getSymbol(UnaryOperator operator) throws OperatorNotSupportedException {
		if (operator == BooleanUnaryOperator.NOT) {
			return "!";
		}
		if (operator == ArithmeticUnaryOperator.UNARY_MINUS) {
			return "-";
		}
		if (operator == ArithmeticUnaryOperator.SIN) {
			return "Math.sin";
		}
		if (operator == ArithmeticUnaryOperator.ASIN) {
			return "Math.asin";
		}
		if (operator == ArithmeticUnaryOperator.COS) {
			return "Math.cos";
		}
		if (operator == ArithmeticUnaryOperator.ACOS) {
			return "Math.acos";
		}
		if (operator == ArithmeticUnaryOperator.TAN) {
			return "Math.tan";
		}
		if (operator == ArithmeticUnaryOperator.ATAN) {
			return "Math.atan";
		}
		if (operator == ArithmeticUnaryOperator.EXP) {
			return "Math.exp";
		}
		if (operator == ArithmeticUnaryOperator.LOG) {
			return "Math.log";
		}
		if (operator == ArithmeticUnaryOperator.SQRT) {
			return "Math.sqrt";
		}
		throw new OperatorNotSupportedException();
	}

	public String getAlternativeSymbol(UnaryOperator operator) throws OperatorNotSupportedException {
		return null;
	}

	public String getSymbol(BinaryOperator operator) throws OperatorNotSupportedException {
		if (operator == BooleanBinaryOperator.AND) {
			return "&&";
		}
		if (operator == BooleanBinaryOperator.OR) {
			return "||";
		}
		if (operator == BooleanBinaryOperator.EQUALS) {
			return "==";
		}
		if (operator == BooleanBinaryOperator.NOT_EQUALS) {
			return "!=";
		}
		if (operator == BooleanBinaryOperator.LESS_THAN) {
			return "<";
		}
		if (operator == BooleanBinaryOperator.LESS_THAN_OR_EQUALS) {
			return "<=";
		}
		if (operator == BooleanBinaryOperator.GREATER_THAN) {
			return ">";
		}
		if (operator == BooleanBinaryOperator.GREATER_THAN_OR_EQUALS) {
			return ">=";
		}
		if (operator == ArithmeticBinaryOperator.ADDITION) {
			return "+";
		}
		if (operator == ArithmeticBinaryOperator.SUBSTRACTION) {
			return "-";
		}
		if (operator == ArithmeticBinaryOperator.MULTIPLICATION) {
			return "*";
		}
		if (operator == ArithmeticBinaryOperator.DIVISION) {
			return "/";
		}
		if (operator == ArithmeticBinaryOperator.POWER) {
			return "^";
		}
		throw new OperatorNotSupportedException();
	}

	public String getAlternativeSymbol(BinaryOperator operator) throws OperatorNotSupportedException {
		if (operator == BooleanBinaryOperator.AND) {
			return "&";
		}
		if (operator == BooleanBinaryOperator.OR) {
			return "|";
		}
		if (operator == BooleanBinaryOperator.EQUALS) {
			return "=";
		}
		if (operator == ArithmeticBinaryOperator.DIVISION) {
			return ":";
		}
		return null;
	}

	@Override
	public String getAlternativeSymbol(Operator operator) throws OperatorNotSupportedException {
		if (operator instanceof UnaryOperator) {
			return getAlternativeSymbol((UnaryOperator) operator);
		}
		if (operator instanceof BinaryOperator) {
			return getAlternativeSymbol((BinaryOperator) operator);
		}
		throw new OperatorNotSupportedException();
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

}
