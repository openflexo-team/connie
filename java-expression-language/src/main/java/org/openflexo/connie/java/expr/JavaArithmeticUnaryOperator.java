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

package org.openflexo.connie.java.expr;

import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.EvaluationType;
import org.openflexo.connie.java.expr.JavaConstant.ArithmeticConstant;
import org.openflexo.connie.java.expr.JavaConstant.ByteConstant;
import org.openflexo.connie.java.expr.JavaConstant.DoubleConstant;
import org.openflexo.connie.java.expr.JavaConstant.FloatConstant;
import org.openflexo.connie.java.expr.JavaConstant.IntegerConstant;
import org.openflexo.connie.java.expr.JavaConstant.LongConstant;
import org.openflexo.connie.java.expr.JavaConstant.ShortConstant;

public abstract class JavaArithmeticUnaryOperator extends JavaUnaryOperator {

	public static final JavaArithmeticUnaryOperator UNARY_PLUS = new JavaArithmeticUnaryOperator() {
		@Override
		public int getPriority() {
			return 3;
		}

		@Override
		public Constant<?> evaluate(Constant<?> arg) throws TypeMismatchException {
			if (arg instanceof ArithmeticConstant) {
				return arg;
			}
			throw new TypeMismatchException(this, arg.getEvaluationType(), EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER);
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType operandType) throws TypeMismatchException {
			if (operandType.isLiteral()) {
				return EvaluationType.LITERAL;
			}
			if (operandType.isArithmeticInteger()) {
				return EvaluationType.ARITHMETIC_INTEGER;
			}
			if (operandType.isArithmeticFloat()) {
				return EvaluationType.ARITHMETIC_FLOAT;
			}
			if (operandType.isDuration()) {
				return EvaluationType.DURATION;
			}
			throw new TypeMismatchException(this, operandType, EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER,
					EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "unary_plus";
		}
	};

	public static final JavaArithmeticUnaryOperator UNARY_MINUS = new JavaArithmeticUnaryOperator() {
		@Override
		public int getPriority() {
			return 3;
		}

		@Override
		public Constant<?> evaluate(Constant<?> arg) throws TypeMismatchException {
			if (arg instanceof DoubleConstant) {
				return new DoubleConstant(-((DoubleConstant) arg).getValue());
			}
			else if (arg instanceof FloatConstant) {
				return new FloatConstant(-((FloatConstant) arg).getValue());
			}
			else if (arg instanceof IntegerConstant) {
				return new IntegerConstant(-((IntegerConstant) arg).getValue());
			}
			else if (arg instanceof LongConstant) {
				return new LongConstant(-((LongConstant) arg).getValue());
			}
			else if (arg instanceof ArithmeticConstant) {
				if (((ArithmeticConstant) arg).isFloatingPointType()) {
					return JavaConstant.makeConstant(-((ArithmeticConstant<?>) arg).getDoubleValue());
				}
				else {
					return JavaConstant.makeConstant(-((ArithmeticConstant<?>) arg).getLongValue());
				}
			}
			throw new TypeMismatchException(this, arg.getEvaluationType(), EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER);
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType operandType) throws TypeMismatchException {
			if (operandType.isLiteral()) {
				return EvaluationType.LITERAL;
			}
			if (operandType.isArithmeticInteger()) {
				return EvaluationType.ARITHMETIC_INTEGER;
			}
			if (operandType.isArithmeticFloat()) {
				return EvaluationType.ARITHMETIC_FLOAT;
			}
			if (operandType.isDuration()) {
				return EvaluationType.DURATION;
			}
			throw new TypeMismatchException(this, operandType, EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER,
					EvaluationType.DURATION, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "unary_minus";
		}
	};

	public static final JavaArithmeticUnaryOperator BITWISE_COMPLEMENT = new JavaArithmeticUnaryOperator() {
		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Constant<?> evaluate(Constant<?> arg) throws TypeMismatchException {
			if (arg instanceof ByteConstant) {
				return JavaConstant.makeConstant(~((ByteConstant) arg).getValue());
			}
			if (arg instanceof ShortConstant) {
				return JavaConstant.makeConstant(~((ShortConstant) arg).getValue());
			}
			if (arg instanceof IntegerConstant) {
				return JavaConstant.makeConstant(~((IntegerConstant) arg).getValue());
			}
			if (arg instanceof LongConstant) {
				return JavaConstant.makeConstant(~((LongConstant) arg).getValue());
			}
			throw new TypeMismatchException(this, arg.getEvaluationType(), EvaluationType.ARITHMETIC_INTEGER);
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType operandType) throws TypeMismatchException {
			if (operandType.isLiteral()) {
				return EvaluationType.LITERAL;
			}
			if (operandType.isArithmeticInteger()) {
				return EvaluationType.ARITHMETIC_INTEGER;
			}
			throw new TypeMismatchException(this, operandType, EvaluationType.ARITHMETIC_INTEGER, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "bitwise_complement";
		}
	};

	public static abstract class PreOrPostIncrementOrDecrementOperator extends JavaArithmeticUnaryOperator {

		@Override
		public Constant<?> evaluate(Constant<?> arg) throws TypeMismatchException {
			return arg;
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType operandType) throws TypeMismatchException {
			if (operandType.isLiteral()) {
				return EvaluationType.LITERAL;
			}
			if (operandType.isArithmeticInteger()) {
				return EvaluationType.ARITHMETIC_INTEGER;
			}
			throw new TypeMismatchException(this, operandType, EvaluationType.ARITHMETIC_INTEGER, EvaluationType.LITERAL);
		}

	}

	public static abstract class PreIncrementOrDecrementOperator extends PreOrPostIncrementOrDecrementOperator
			implements PreSettableUnaryOperator {

		@Override
		public int getPriority() {
			return 3;
		}

	}

	public static abstract class PostIncrementOrDecrementOperator extends PreOrPostIncrementOrDecrementOperator
			implements PostSettableUnaryOperator {

		@Override
		public int getPriority() {
			return 1;
		}

	}

	public static final JavaArithmeticUnaryOperator PRE_INCREMENT = new PreIncrementOrDecrementOperator() {
		@Override
		public String getName() {
			return "pre_increment";
		}

		@Override
		public Constant<?> preSet(Constant<?> arg) throws TypeMismatchException {
			if (arg instanceof ByteConstant) {
				return JavaConstant.makeConstant(((ByteConstant) arg).getValue() + 1);
			}
			if (arg instanceof ShortConstant) {
				return JavaConstant.makeConstant(((ShortConstant) arg).getValue() + 1);
			}
			if (arg instanceof IntegerConstant) {
				return JavaConstant.makeConstant(((IntegerConstant) arg).getValue() + 1);
			}
			if (arg instanceof LongConstant) {
				return JavaConstant.makeConstant(((LongConstant) arg).getValue() + 1);
			}
			throw new TypeMismatchException(this, arg.getEvaluationType(), EvaluationType.ARITHMETIC_INTEGER);
		}

	};

	public static final JavaArithmeticUnaryOperator PRE_DECREMENT = new PreIncrementOrDecrementOperator() {
		@Override
		public String getName() {
			return "pre_decrement";
		}

		@Override
		public Constant<?> preSet(Constant<?> arg) throws TypeMismatchException {
			if (arg instanceof ByteConstant) {
				return JavaConstant.makeConstant(((ByteConstant) arg).getValue() - 1);
			}
			if (arg instanceof ShortConstant) {
				return JavaConstant.makeConstant(((ShortConstant) arg).getValue() - 1);
			}
			if (arg instanceof IntegerConstant) {
				return JavaConstant.makeConstant(((IntegerConstant) arg).getValue() - 1);
			}
			if (arg instanceof LongConstant) {
				return JavaConstant.makeConstant(((LongConstant) arg).getValue() - 1);
			}
			throw new TypeMismatchException(this, arg.getEvaluationType(), EvaluationType.ARITHMETIC_INTEGER);
		}

	};

	public static final JavaArithmeticUnaryOperator POST_INCREMENT = new PostIncrementOrDecrementOperator() {
		@Override
		public String getName() {
			return "post_increment";
		}

		@Override
		public Constant<?> postSet(Constant<?> arg) throws TypeMismatchException {
			if (arg instanceof ByteConstant) {
				return JavaConstant.makeConstant(((ByteConstant) arg).getValue() + 1);
			}
			if (arg instanceof ShortConstant) {
				return JavaConstant.makeConstant(((ShortConstant) arg).getValue() + 1);
			}
			if (arg instanceof IntegerConstant) {
				return JavaConstant.makeConstant(((IntegerConstant) arg).getValue() + 1);
			}
			if (arg instanceof LongConstant) {
				return JavaConstant.makeConstant(((LongConstant) arg).getValue() + 1);
			}
			throw new TypeMismatchException(this, arg.getEvaluationType(), EvaluationType.ARITHMETIC_INTEGER);
		}

	};

	public static final JavaArithmeticUnaryOperator POST_DECREMENT = new PostIncrementOrDecrementOperator() {
		@Override
		public String getName() {
			return "post_decrement";
		}

		@Override
		public Constant<?> postSet(Constant<?> arg) throws TypeMismatchException {
			if (arg instanceof ByteConstant) {
				return JavaConstant.makeConstant(((ByteConstant) arg).getValue() - 1);
			}
			if (arg instanceof ShortConstant) {
				return JavaConstant.makeConstant(((ShortConstant) arg).getValue() - 1);
			}
			if (arg instanceof IntegerConstant) {
				return JavaConstant.makeConstant(((IntegerConstant) arg).getValue() - 1);
			}
			if (arg instanceof LongConstant) {
				return JavaConstant.makeConstant(((LongConstant) arg).getValue() - 1);
			}
			throw new TypeMismatchException(this, arg.getEvaluationType(), EvaluationType.ARITHMETIC_INTEGER);
		}

	};

}
