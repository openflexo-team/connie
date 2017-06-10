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

package org.openflexo.connie.exception;

import org.openflexo.connie.expr.BinaryOperator;
import org.openflexo.connie.expr.EvaluationType;
import org.openflexo.connie.expr.Operator;
import org.openflexo.connie.expr.UnaryOperator;

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
		}
		else if (concernedOperator != null) {
			return "type_mismatch_on_operator_(" + concernedOperator.getLocalizedName() + ")_supplied_type_is_("
					+ suppliedType.getLocalizedName() + ")_while_expected_types_are_(" + typesAsString(expectedTypes) + ")";
		}
		else {
			return "unexpected_null_operator";
		}
	}

	public String getHTMLLocalizedMessage() {
		return "<html>" + getLocalizedMessage() + "</html>";
	}

	private static String typesAsString(EvaluationType... types) {
		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		for (EvaluationType t : types) {
			sb.append(isFirst ? t.getLocalizedName() : "," + t.getLocalizedName());
			isFirst = false;
		}
		return sb.toString();
	}
}
