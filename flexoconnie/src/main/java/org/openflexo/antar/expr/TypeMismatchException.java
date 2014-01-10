/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.antar.expr;


/**
 * This exception is thrown when an operator is invoked in a type mismatch context
 * 
 * @author sylvain
 * 
 */
@SuppressWarnings("serial")
public class TypeMismatchException extends TransformException {

	private Operator concernedOperator;
	private EvaluationType suppliedType;
	private EvaluationType leftSuppliedType;
	private EvaluationType rightSuppliedType;
	private EvaluationType[] expectedTypes;

	private String message;

	public TypeMismatchException(UnaryOperator operator, EvaluationType suppliedType, EvaluationType... expectedTypes) {
		super();
		concernedOperator = operator;
		this.suppliedType = suppliedType;
		this.expectedTypes = expectedTypes;
		message = "TypeMismatchException on operator " + operator.getName() + " : supplied type is " + suppliedType
				+ " while expected type(s) is(are) " + typesAsString(expectedTypes);
	}

	public TypeMismatchException(BinaryOperator operator, EvaluationType leftSuppliedType, EvaluationType rightSuppliedType,
			EvaluationType... expectedTypes) {
		super();
		concernedOperator = operator;
		this.leftSuppliedType = leftSuppliedType;
		this.rightSuppliedType = rightSuppliedType;
		this.expectedTypes = expectedTypes;
		message = "TypeMismatchException on operator " + operator.getName() + " : supplied types are " + leftSuppliedType + " and "
				+ rightSuppliedType + " while expected type(s) is(are) " + typesAsString(expectedTypes);
	}

	private TypeMismatchException() {
		super();
	}

	public static TypeMismatchException buildIncompatibleEvaluationTypeException(EvaluationType type1, EvaluationType type2) {
		TypeMismatchException returned = new TypeMismatchException();
		returned.leftSuppliedType = type1;
		returned.rightSuppliedType = type2;
		returned.message = "Incompatible types: " + type1 + " and " + type2;
		return returned;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getLocalizedMessage() {
		if (concernedOperator instanceof BinaryOperator) {
			return "type_mismatch_on_operator_(" + concernedOperator.getLocalizedName() + ")_supplied_types_are_("
					+ leftSuppliedType.getLocalizedName() + ")_and_(" + rightSuppliedType.getLocalizedName()
					+ ")_while_expected_types_are_(" + typesAsString(expectedTypes) + ")";
		} else {
			return "type_mismatch_on_operator_(" + concernedOperator.getLocalizedName() + ")_supplied_type_is_("
					+ suppliedType.getLocalizedName() + ")_while_expected_types_are_(" + typesAsString(expectedTypes) + ")";
		}
	}

	public String getHTMLLocalizedMessage() {
		return "<html>" + getLocalizedMessage() + "</html>";
	}

	private String typesAsString(EvaluationType... types) {
		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		for (EvaluationType t : types) {
			sb.append(isFirst ? t.getLocalizedName() : "," + t.getLocalizedName());
			isFirst = false;
		}
		return sb.toString();
	}
}
