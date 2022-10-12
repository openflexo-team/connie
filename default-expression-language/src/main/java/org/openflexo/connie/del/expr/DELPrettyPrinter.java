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

package org.openflexo.connie.del.expr;

import java.lang.reflect.Type;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.del.DELGrammar;
import org.openflexo.connie.del.expr.DELConstant.BooleanConstant;
import org.openflexo.connie.del.expr.DELConstant.DateConstant;
import org.openflexo.connie.del.expr.DELConstant.DurationConstant;
import org.openflexo.connie.del.expr.DELConstant.EnumConstant;
import org.openflexo.connie.del.expr.DELConstant.FloatConstant;
import org.openflexo.connie.del.expr.DELConstant.IntegerConstant;
import org.openflexo.connie.del.expr.DELConstant.ObjectConstant;
import org.openflexo.connie.del.expr.DELConstant.StringConstant;
import org.openflexo.connie.expr.BinaryOperatorExpression;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.CastExpression;
import org.openflexo.connie.expr.ConditionalExpression;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.ExpressionGrammar;
import org.openflexo.connie.expr.ExpressionPrettyPrinter;
import org.openflexo.connie.expr.OperatorNotSupportedException;
import org.openflexo.connie.expr.UnaryOperatorExpression;
import org.openflexo.connie.type.TypeUtils;

public class DELPrettyPrinter extends ExpressionPrettyPrinter {

	private static final DELPrettyPrinter instance = new DELPrettyPrinter();

	// private StringEncoder.DateConverter dateConverter = new StringEncoder.DateConverter();
	// private Duration.DurationStringConverter durationConverter = new Duration.DurationStringConverter();

	public static DELPrettyPrinter getInstance() {
		return instance;
	}

	public DELPrettyPrinter() {
		this(new DELGrammar());
	}

	protected DELPrettyPrinter(ExpressionGrammar grammar) {
		super(grammar);
	}

	@Override
	protected String makeStringRepresentation(Constant<?> constant, Bindable context) {
		if (constant instanceof DELSymbolicConstant) {
			return makeStringRepresentation((DELSymbolicConstant) constant);
		}
		else if (constant instanceof BooleanConstant) {
			return makeStringRepresentation((BooleanConstant) constant);
		}
		else if (constant instanceof FloatConstant) {
			return makeStringRepresentation((FloatConstant) constant);
		}
		else if (constant instanceof IntegerConstant) {
			return makeStringRepresentation((IntegerConstant) constant);
		}
		else if (constant instanceof StringConstant) {
			return makeStringRepresentation((StringConstant) constant);
		}
		else if (constant instanceof DateConstant) {
			return makeStringRepresentation((DateConstant) constant);
		}
		else if (constant instanceof DurationConstant) {
			return makeStringRepresentation((DurationConstant) constant);
		}
		else if (constant instanceof EnumConstant) {
			return makeStringRepresentation((EnumConstant<?>) constant);
		}
		else if (constant instanceof ObjectConstant) {
			return makeStringRepresentation((ObjectConstant) constant);
		}
		return "???";
	}

	protected String makeStringRepresentation(BooleanConstant constant) {
		if (constant == BooleanConstant.FALSE) {
			return "false";
		}
		else if (constant == BooleanConstant.TRUE) {
			return "true";
		}
		return "???";
	}

	protected String makeStringRepresentation(FloatConstant constant) {
		return Double.toString(constant.getValue());
	}

	protected String makeStringRepresentation(IntegerConstant constant) {
		return Long.toString(constant.getValue());
	}

	protected String makeStringRepresentation(StringConstant constant) {
		return '"' + constant.getValue() + '"';
	}

	protected String makeStringRepresentation(DELSymbolicConstant constant) {
		return constant.getSymbol();
	}

	@Override
	protected String makeStringRepresentation(UnaryOperatorExpression expression, Bindable context) {
		try {
			return "(" + getSymbol(expression.getOperator()) + "(" + getStringRepresentation(expression.getArgument(), context) + ")" + ")";
		} catch (OperatorNotSupportedException e) {
			return "<unsupported>";
		}
	}

	@Override
	protected String makeStringRepresentation(BinaryOperatorExpression expression, Bindable context) {
		try {
			return "(" + getStringRepresentation(expression.getLeftArgument(), context) + " " + getSymbol(expression.getOperator()) + " "
					+ getStringRepresentation(expression.getRightArgument(), context) + ")";
		} catch (OperatorNotSupportedException e) {
			return "<unsupported>";
		}
	}

	protected String makeStringRepresentation(DateConstant constant) {
		if (constant == null || constant.getDate() == null) {
			return "[null]";
		}
		// TODO: reimplement this
		// return "[" + dateConverter.convertToString(constant.getDate()) + "]";
		return constant.toString();
	}

	protected String makeStringRepresentation(DurationConstant constant) {
		if (constant == null || constant.getDuration() == null) {
			return "[null]";
		}
		// TODO: reimplement this
		// return "[" + durationConverter.convertToString(constant.getDuration()) + "]";
		return constant.toString();
	}

	protected final String makeStringRepresentation(EnumConstant<?> constant) {
		return constant.getName();
	}

	protected String makeStringRepresentation(ObjectConstant constant) {
		return "[Object:" + constant.getValue().toString() + "]";
	}

	@Override
	protected String makeStringRepresentation(BindingValue bv, Bindable context) {
		return bv.toString();
	}

	@Override
	protected String makeStringRepresentation(ConditionalExpression expression, Bindable context) {
		return "(" + getStringRepresentation(expression.getCondition(), context) + " ? "
				+ getStringRepresentation(expression.getThenExpression(), context) + " : "
				+ getStringRepresentation(expression.getElseExpression(), context) + ")";
	}

	@Override
	protected String makeStringRepresentation(CastExpression expression, Bindable context) {
		return "(" + makeStringRepresentation(expression.getCastType(), context) + ")"
				+ getStringRepresentation(expression.getArgument(), context);
	}

	@Override
	protected String makeStringRepresentation(Type type, Bindable context) {
		return TypeUtils.simpleRepresentation(type);
	}
}
