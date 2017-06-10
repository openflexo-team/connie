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

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.Constant.ArithmeticConstant;
import org.openflexo.connie.expr.Constant.BooleanConstant;
import org.openflexo.connie.expr.Constant.DateConstant;
import org.openflexo.connie.expr.Constant.DurationConstant;
import org.openflexo.connie.expr.Constant.FloatConstant;
import org.openflexo.connie.expr.Constant.IntegerConstant;
import org.openflexo.connie.expr.Constant.ObjectConstant;
import org.openflexo.connie.expr.Constant.ObjectSymbolicConstant;
import org.openflexo.connie.expr.Constant.StringConstant;
import org.openflexo.toolbox.Duration;

public abstract class ArithmeticBinaryOperator extends BinaryOperator {

	public static final ArithmeticBinaryOperator ADDITION = new ArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 3;
		}

		@Override
		public Constant<?> evaluate(Constant<?> leftArg, Constant<?> rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant) {
				if (rightArg instanceof ArithmeticConstant) {
					if (leftArg instanceof IntegerConstant && rightArg instanceof IntegerConstant) {
						return new IntegerConstant(((IntegerConstant) leftArg).getValue() + ((IntegerConstant) rightArg).getValue());
					}
					return new FloatConstant(((ArithmeticConstant<?>) leftArg).getArithmeticValue()
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
				else if (rightArg instanceof DateConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue() + ((DateConstant) rightArg).getDate().toString());
				}
				else if (rightArg instanceof DurationConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue()
							+ ((DurationConstant) rightArg).getDuration().getSerializationRepresentation());
				}
				else if (rightArg == ObjectSymbolicConstant.NULL) {
					return new StringConstant(((StringConstant) leftArg).getValue() + "null");
				}
				else if (rightArg instanceof ObjectConstant) {
					return new StringConstant(((StringConstant) leftArg).getValue() + ((ObjectConstant) rightArg).getValue().toString());
				}
			}
			else if (leftArg instanceof DurationConstant) {
				if (rightArg instanceof DurationConstant) {
					return new DurationConstant(Duration.durationPlusDuration(((DurationConstant) leftArg).getDuration(),
							((DurationConstant) rightArg).getDuration()));
				}
				else if (rightArg instanceof DateConstant) {
					return new DateConstant(
							Duration.datePlusDuration(((DateConstant) rightArg).getDate(), ((DurationConstant) leftArg).getDuration()));
				}
			}
			else if (leftArg instanceof DateConstant && rightArg instanceof DurationConstant) {
				return new DateConstant(
						Duration.datePlusDuration(((DateConstant) leftArg).getDate(), ((DurationConstant) rightArg).getDuration()));
			}

			// Special case to handle String concatenation with null
			if (leftArg == ObjectSymbolicConstant.NULL && rightArg instanceof StringConstant) {
				return evaluate(new StringConstant("null"), rightArg);
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
					else {
						return EvaluationType.ARITHMETIC_FLOAT;
					}
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

	public static final ArithmeticBinaryOperator SUBSTRACTION = new ArithmeticBinaryOperator() {
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
				return new FloatConstant(
						((ArithmeticConstant<?>) leftArg).getArithmeticValue() - ((ArithmeticConstant<?>) rightArg).getArithmeticValue());
			}
			else if (leftArg instanceof DurationConstant) {
				if (rightArg instanceof DurationConstant) {
					return new DurationConstant(Duration.durationMinusDuration(((DurationConstant) leftArg).getDuration(),
							((DurationConstant) rightArg).getDuration()));
				}
			}
			else if (leftArg instanceof DateConstant) {
				if (rightArg instanceof DurationConstant) {
					return new DateConstant(
							Duration.dateMinusDuration(((DateConstant) leftArg).getDate(), ((DurationConstant) rightArg).getDuration()));
				}
			}
			else if (leftArg instanceof DateConstant && rightArg instanceof DateConstant) {
				return new DurationConstant(
						Duration.dateMinusDate(((DateConstant) leftArg).getDate(), ((DateConstant) rightArg).getDate()));
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
					else {
						return EvaluationType.ARITHMETIC_FLOAT;
					}
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

	public static final ArithmeticBinaryOperator MULTIPLICATION = new ArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Constant<?> evaluate(Constant<?> leftArg, Constant<?> rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				if (leftArg instanceof IntegerConstant && rightArg instanceof IntegerConstant) {
					return new IntegerConstant(((IntegerConstant) leftArg).getValue() * ((IntegerConstant) rightArg).getValue());
				}
				return new FloatConstant(
						((ArithmeticConstant<?>) leftArg).getArithmeticValue() * ((ArithmeticConstant<?>) rightArg).getArithmeticValue());
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
					else {
						return EvaluationType.ARITHMETIC_FLOAT;
					}
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

	public static final ArithmeticBinaryOperator DIVISION = new ArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Constant<?> evaluate(Constant<?> leftArg, Constant<?> rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return new FloatConstant(
						((ArithmeticConstant<?>) leftArg).getArithmeticValue() / ((ArithmeticConstant<?>) rightArg).getArithmeticValue());
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

	public static final ArithmeticBinaryOperator MOD = new ArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Constant<?> evaluate(Constant<?> leftArg, Constant<?> rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return new FloatConstant(
						((ArithmeticConstant<?>) leftArg).getArithmeticValue() % ((ArithmeticConstant<?>) rightArg).getArithmeticValue());
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

	public static final ArithmeticBinaryOperator POWER = new ArithmeticBinaryOperator() {
		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Constant<?> evaluate(Constant<?> leftArg, Constant<?> rightArg) throws TypeMismatchException {
			if (leftArg instanceof ArithmeticConstant && rightArg instanceof ArithmeticConstant) {
				return new FloatConstant(Math.pow(((ArithmeticConstant<?>) leftArg).getArithmeticValue(),
						((ArithmeticConstant<?>) rightArg).getArithmeticValue()));
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
