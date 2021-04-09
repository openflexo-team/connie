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

package org.openflexo.connie.del;

import org.openflexo.connie.del.expr.DELArithmeticBinaryOperator;
import org.openflexo.connie.del.expr.DELArithmeticUnaryOperator;
import org.openflexo.connie.del.expr.DELBooleanBinaryOperator;
import org.openflexo.connie.del.expr.DELBooleanUnaryOperator;
import org.openflexo.connie.expr.BinaryOperator;
import org.openflexo.connie.expr.ExpressionGrammar;
import org.openflexo.connie.expr.Operator;
import org.openflexo.connie.expr.OperatorNotSupportedException;
import org.openflexo.connie.expr.UnaryOperator;

public class DELGrammar implements ExpressionGrammar {

	private static final BinaryOperator[] allSupportedBinaryOperators = { DELBooleanBinaryOperator.AND, DELBooleanBinaryOperator.OR,
			DELBooleanBinaryOperator.EQUALS, DELBooleanBinaryOperator.NOT_EQUALS, DELBooleanBinaryOperator.LESS_THAN,
			DELBooleanBinaryOperator.LESS_THAN_OR_EQUALS, DELBooleanBinaryOperator.GREATER_THAN, DELBooleanBinaryOperator.GREATER_THAN_OR_EQUALS,
			DELArithmeticBinaryOperator.ADDITION, DELArithmeticBinaryOperator.SUBSTRACTION, DELArithmeticBinaryOperator.MULTIPLICATION,
			DELArithmeticBinaryOperator.DIVISION, DELArithmeticBinaryOperator.POWER, };

	private static final UnaryOperator[] allSupportedUnaryOperators = { DELBooleanUnaryOperator.NOT, DELArithmeticUnaryOperator.UNARY_MINUS,
			DELArithmeticUnaryOperator.SIN, DELArithmeticUnaryOperator.ASIN, DELArithmeticUnaryOperator.COS, DELArithmeticUnaryOperator.ACOS,
			DELArithmeticUnaryOperator.TAN, DELArithmeticUnaryOperator.ATAN, DELArithmeticUnaryOperator.EXP, DELArithmeticUnaryOperator.LOG,
			DELArithmeticUnaryOperator.SQRT };

	@Override
	public BinaryOperator[] getAllSupportedBinaryOperators() {
		return allSupportedBinaryOperators;
	}

	@Override
	public UnaryOperator[] getAllSupportedUnaryOperators() {
		return allSupportedUnaryOperators;
	}

	public String getSymbol(UnaryOperator operator) throws OperatorNotSupportedException {
		if (operator == DELBooleanUnaryOperator.NOT) {
			return "!";
		}
		if (operator == DELArithmeticUnaryOperator.UNARY_MINUS) {
			return "-";
		}
		if (operator == DELArithmeticUnaryOperator.SIN) {
			return "sin";
		}
		if (operator == DELArithmeticUnaryOperator.ASIN) {
			return "asin";
		}
		if (operator == DELArithmeticUnaryOperator.COS) {
			return "cos";
		}
		if (operator == DELArithmeticUnaryOperator.ACOS) {
			return "acos";
		}
		if (operator == DELArithmeticUnaryOperator.TAN) {
			return "tan";
		}
		if (operator == DELArithmeticUnaryOperator.ATAN) {
			return "atan";
		}
		if (operator == DELArithmeticUnaryOperator.EXP) {
			return "exp";
		}
		if (operator == DELArithmeticUnaryOperator.LOG) {
			return "log";
		}
		if (operator == DELArithmeticUnaryOperator.SQRT) {
			return "sqrt";
		}
		throw new OperatorNotSupportedException();
	}

	public String getAlternativeSymbol(UnaryOperator operator) {
		return null;
	}

	public String getSymbol(BinaryOperator operator) throws OperatorNotSupportedException {
		if (operator == DELBooleanBinaryOperator.AND) {
			return "&";
		}
		if (operator == DELBooleanBinaryOperator.OR) {
			return "|";
		}
		if (operator == DELBooleanBinaryOperator.EQUALS) {
			return "=";
		}
		if (operator == DELBooleanBinaryOperator.NOT_EQUALS) {
			return "!=";
		}
		if (operator == DELBooleanBinaryOperator.LESS_THAN) {
			return "<";
		}
		if (operator == DELBooleanBinaryOperator.LESS_THAN_OR_EQUALS) {
			return "<=";
		}
		if (operator == DELBooleanBinaryOperator.GREATER_THAN) {
			return ">";
		}
		if (operator == DELBooleanBinaryOperator.GREATER_THAN_OR_EQUALS) {
			return ">=";
		}
		if (operator == DELArithmeticBinaryOperator.ADDITION) {
			return "+";
		}
		if (operator == DELArithmeticBinaryOperator.SUBSTRACTION) {
			return "-";
		}
		if (operator == DELArithmeticBinaryOperator.MULTIPLICATION) {
			return "*";
		}
		if (operator == DELArithmeticBinaryOperator.DIVISION) {
			return "/";
		}
		if (operator == DELArithmeticBinaryOperator.POWER) {
			return "^";
		}
		throw new OperatorNotSupportedException();
	}

	public String getAlternativeSymbol(BinaryOperator operator) {
		if (operator == DELBooleanBinaryOperator.AND) {
			return "&&";
		}
		if (operator == DELBooleanBinaryOperator.OR) {
			return "||";
		}
		if (operator == DELBooleanBinaryOperator.EQUALS) {
			return "==";
		}
		if (operator == DELArithmeticBinaryOperator.DIVISION) {
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
