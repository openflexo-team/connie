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

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.EvaluationType;
import org.openflexo.connie.java.expr.JavaConstant.ArithmeticConstant;
import org.openflexo.connie.java.expr.JavaConstant.BooleanConstant;
import org.openflexo.connie.java.expr.JavaConstant.DoubleConstant;
import org.openflexo.connie.java.expr.JavaConstant.FloatConstant;
import org.openflexo.connie.java.expr.JavaConstant.IntegerConstant;
import org.openflexo.connie.java.expr.JavaConstant.ObjectConstant;
import org.openflexo.connie.java.expr.JavaConstant.ObjectSymbolicConstant;
import org.openflexo.connie.java.expr.JavaConstant.StringConstant;

public abstract class JavaArithmeticBinaryOperator extends JavaBinaryOperator {

	public static final JavaArithmeticBinaryOperator ADDITION = new JavaArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 3;
		}

		@Override
		public Constant<?> evaluate(Constant<?> leftArg, Constant<?> rightArg) throws TypeMismatchException, NullReferenceException {
			if (leftArg instanceof ArithmeticConstant) {
				if (rightArg instanceof ArithmeticConstant) {
					if (leftArg instanceof IntegerConstant && rightArg instanceof IntegerConstant) {
						return new IntegerConstant(((IntegerConstant) leftArg).getValue() + ((IntegerConstant) rightArg).getValue());
					}
					// TODO: handle all number types
					return new DoubleConstant(((ArithmeticConstant<?>) leftArg).getArithmeticValue()
							+ ((ArithmeticConstant<?>) rightArg).getArithmeticValue());
				}
				throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(), EvaluationType.values());
			}
			else if (leftArg instanceof StringConstant) {
				if (rightArg instanceof StringConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue() + ((StringConstant) rightArg).getValue());
				}
				else if (rightArg instanceof IntegerConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue() + ((IntegerConstant) rightArg).getValue());
				}
				else if (rightArg instanceof FloatConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue() + ((FloatConstant) rightArg).getValue());
				}
				else if (rightArg instanceof BooleanConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue() + ((BooleanConstant) rightArg).getValue());
				}
				else if (rightArg == ObjectSymbolicConstant.NULL) {
					return new StringConstant(((StringConstant) leftArg).getValue() + "null");
				}
				else if (rightArg instanceof ObjectConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue() + ((ObjectConstant) rightArg).getValue().toString());
				}
			}

			// Special case to handle String concatenation with null
			if (leftArg == ObjectSymbolicConstant.NULL && rightArg instanceof StringConstant) {
				return evaluate(new StringConstant("null"), rightArg);
			}
			if (leftArg == ObjectSymbolicConstant.NULL && rightArg == ObjectSymbolicConstant.NULL) {
				throw new NullReferenceException();
			}
			// System.out.println("leftArg=" + leftArg + " of " + leftArg.getClass() + " eval type =" + leftArg.getEvaluationType());
			// System.out.println("rightArg=" + rightArg + " of " + rightArg.getClass() + " eval type =" + rightArg.getEvaluationType());
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(), EvaluationType.values());
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isLiteral()) {
				return EvaluationType.LITERAL; // Undecided
			}
			else if (leftOperandType.isArithmetic()) {
				if (rightOperandType.isArithmetic()) {
					if (leftOperandType.isArithmeticInteger() && rightOperandType.isArithmeticInteger()) {
						return EvaluationType.ARITHMETIC_INTEGER;
					}
					return EvaluationType.ARITHMETIC_FLOAT;
				}
				else if (rightOperandType.isLiteral()) {
					return EvaluationType.ARITHMETIC_FLOAT; // Undecided
				}
			}
			else if (leftOperandType.isString()) {
				return EvaluationType.STRING;
			}
			else if (leftOperandType.isDuration()) {
				if (rightOperandType.isDurationOrLiteral()) {
					return EvaluationType.DURATION;
				}
				if (rightOperandType.isDateOrLiteral()) {
					return EvaluationType.DATE;
				}
			}
			else if (leftOperandType.isDate() && rightOperandType.isDurationOrLiteral()) {
				return EvaluationType.DATE;
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER, EvaluationType.DATE, EvaluationType.DURATION, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "addition";
		}
	};

	public static final JavaArithmeticBinaryOperator SUBSTRACTION = new JavaArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 3;
		}

		@Override
		public Constant<?> evaluate(Constant<?> leftArg, Constant<?> rightArg) throws TypeMismatchException, NullReferenceException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				if (leftArg instanceof IntegerConstant && rightArg instanceof IntegerConstant) {
					return new IntegerConstant(((IntegerConstant) leftArg).getValue() - ((IntegerConstant) rightArg).getValue());
				}
				return new DoubleConstant(
						((ArithmeticConstant<?>) leftArg).getArithmeticValue() - ((ArithmeticConstant<?>) rightArg).getArithmeticValue());
			}

			if (rightArg == ObjectSymbolicConstant.NULL) {
				throw new NullReferenceException(SUBSTRACTION);
			}
			if (leftArg == ObjectSymbolicConstant.NULL) {
				throw new NullReferenceException(SUBSTRACTION);
			}

			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER, EvaluationType.DATE, EvaluationType.DURATION);
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {

			if (leftOperandType.isLiteral()) {
				return EvaluationType.LITERAL; // Undecided
			}
			else if (leftOperandType.isArithmetic()) {
				if (rightOperandType.isArithmetic()) {
					if (leftOperandType.isArithmeticInteger() && rightOperandType.isArithmeticInteger()) {
						return EvaluationType.ARITHMETIC_INTEGER;
					}
					return EvaluationType.ARITHMETIC_FLOAT;
				}
				else if (rightOperandType.isLiteral()) {
					return EvaluationType.LITERAL; // Undecided
				}
			}
			else if (leftOperandType.isDuration()) {
				if (rightOperandType.isDurationOrLiteral()) {
					return EvaluationType.DURATION;
				}
			}
			else if (leftOperandType.isDate()) {
				if (rightOperandType.isDurationOrLiteral()) {
					return EvaluationType.DATE;
				}
				if (rightOperandType.isDateOrLiteral()) {
					return EvaluationType.DURATION;
				}
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER, EvaluationType.DATE, EvaluationType.DURATION, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "substraction";
		}
	};

	public static final JavaArithmeticBinaryOperator MULTIPLICATION = new JavaArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Constant<?> evaluate(Constant<?> leftArg, Constant<?> rightArg) throws TypeMismatchException, NullReferenceException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				if (leftArg instanceof IntegerConstant && rightArg instanceof IntegerConstant) {
					return new IntegerConstant(((IntegerConstant) leftArg).getValue() * ((IntegerConstant) rightArg).getValue());
				}
				return new DoubleConstant(
						((ArithmeticConstant<?>) leftArg).getArithmeticValue() * ((ArithmeticConstant<?>) rightArg).getArithmeticValue());
			}
			if (leftArg == ObjectSymbolicConstant.NULL || rightArg == ObjectSymbolicConstant.NULL) {
				throw new NullReferenceException(this);
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER);
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isArithmeticOrLiteral()) {
				if (rightOperandType.isArithmeticOrLiteral()) {
					if (leftOperandType.isArithmeticInteger() && rightOperandType.isArithmeticInteger()) {
						return EvaluationType.ARITHMETIC_INTEGER;
					}
					return EvaluationType.ARITHMETIC_FLOAT;
				}
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "multiplication";
		}
	};

	public static final JavaArithmeticBinaryOperator DIVISION = new JavaArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Constant<?> evaluate(Constant<?> leftArg, Constant<?> rightArg) throws TypeMismatchException, NullReferenceException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return new DoubleConstant(
						((ArithmeticConstant<?>) leftArg).getArithmeticValue() / ((ArithmeticConstant<?>) rightArg).getArithmeticValue());
			}
			if (leftArg == ObjectSymbolicConstant.NULL || rightArg == ObjectSymbolicConstant.NULL) {
				throw new NullReferenceException(this);
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER);
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isArithmeticOrLiteral()) {
				if (rightOperandType.isArithmeticOrLiteral()) {
					return EvaluationType.ARITHMETIC_FLOAT;
				}
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "division";
		}
	};

	public static final JavaArithmeticBinaryOperator MOD = new JavaArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Constant<?> evaluate(Constant<?> leftArg, Constant<?> rightArg) throws TypeMismatchException, NullReferenceException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return new DoubleConstant(
						((ArithmeticConstant<?>) leftArg).getArithmeticValue() % ((ArithmeticConstant<?>) rightArg).getArithmeticValue());
			}
			if (leftArg == ObjectSymbolicConstant.NULL || rightArg == ObjectSymbolicConstant.NULL) {
				throw new NullReferenceException(this);
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER);
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isArithmeticOrLiteral()) {
				if (rightOperandType.isArithmeticOrLiteral()) {
					return EvaluationType.ARITHMETIC_FLOAT;
				}
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "mod";
		}
	};

	public static final JavaArithmeticBinaryOperator POWER = new JavaArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Constant<?> evaluate(Constant<?> leftArg, Constant<?> rightArg) throws TypeMismatchException, NullReferenceException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return new DoubleConstant(Math.pow(((ArithmeticConstant<?>) leftArg).getArithmeticValue(),
						((ArithmeticConstant<?>) rightArg).getArithmeticValue()));
			}
			if (leftArg == ObjectSymbolicConstant.NULL || rightArg == ObjectSymbolicConstant.NULL) {
				throw new NullReferenceException(this);
			}
			throw new TypeMismatchException(this, leftArg.getEvaluationType(), rightArg.getEvaluationType(),
					EvaluationType.ARITHMETIC_FLOAT, EvaluationType.ARITHMETIC_INTEGER);
		}

		@Override
		public EvaluationType getEvaluationType(EvaluationType leftOperandType, EvaluationType rightOperandType)
				throws TypeMismatchException {
			if (leftOperandType.isArithmeticOrLiteral()) {
				if (rightOperandType.isArithmeticOrLiteral()) {
					return EvaluationType.ARITHMETIC_FLOAT;
				}
			}
			throw new TypeMismatchException(this, leftOperandType, rightOperandType, EvaluationType.ARITHMETIC_FLOAT,
					EvaluationType.ARITHMETIC_INTEGER, EvaluationType.LITERAL);
		}

		@Override
		public String getName() {
			return "power";

		}
	};

}
