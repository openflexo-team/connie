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

import java.lang.reflect.Type;

import org.openflexo.connie.expr.BinaryOperatorExpression;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.CastExpression;
import org.openflexo.connie.expr.ConditionalExpression;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionGrammar;
import org.openflexo.connie.expr.ExpressionPrettyPrinter;
import org.openflexo.connie.expr.OperatorNotSupportedException;
import org.openflexo.connie.expr.UnaryOperatorExpression;
import org.openflexo.connie.java.JavaGrammar;
import org.openflexo.connie.java.expr.JavaConstant.BooleanConstant;
import org.openflexo.connie.java.expr.JavaConstant.ByteConstant;
import org.openflexo.connie.java.expr.JavaConstant.CharConstant;
import org.openflexo.connie.java.expr.JavaConstant.DoubleConstant;
import org.openflexo.connie.java.expr.JavaConstant.EnumConstant;
import org.openflexo.connie.java.expr.JavaConstant.FloatConstant;
import org.openflexo.connie.java.expr.JavaConstant.IntegerConstant;
import org.openflexo.connie.java.expr.JavaConstant.LongConstant;
import org.openflexo.connie.java.expr.JavaConstant.ObjectConstant;
import org.openflexo.connie.java.expr.JavaConstant.ShortConstant;
import org.openflexo.connie.java.expr.JavaConstant.StringConstant;
import org.openflexo.connie.java.expr.JavaUnaryOperator.PostSettableUnaryOperator;
import org.openflexo.connie.type.TypeUtils;

public class JavaPrettyPrinter extends ExpressionPrettyPrinter {

	private static final JavaPrettyPrinter instance = new JavaPrettyPrinter();

	// private StringEncoder.DateConverter dateConverter = new StringEncoder.DateConverter();
	// private Duration.DurationStringConverter durationConverter = new Duration.DurationStringConverter();

	public static JavaPrettyPrinter getInstance() {
		return instance;
	}

	public JavaPrettyPrinter() {
		this(new JavaGrammar());
	}

	protected JavaPrettyPrinter(ExpressionGrammar grammar) {
		super(grammar);
	}

	@Override
	public String getStringRepresentation(Expression expression) {
		if (expression instanceof JavaInstanceOfExpression) {
			return makeStringRepresentation((JavaInstanceOfExpression) expression);
		}
		return super.getStringRepresentation(expression);
	}

	@Override
	protected String makeStringRepresentation(Constant<?> constant) {
		if (constant instanceof BooleanConstant) {
			return makeStringRepresentation((BooleanConstant) constant);
		}
		else if (constant instanceof FloatConstant) {
			return makeStringRepresentation((FloatConstant) constant);
		}
		else if (constant instanceof DoubleConstant) {
			return makeStringRepresentation((DoubleConstant) constant);
		}
		else if (constant instanceof ByteConstant) {
			return makeStringRepresentation((ByteConstant) constant);
		}
		else if (constant instanceof ShortConstant) {
			return makeStringRepresentation((ShortConstant) constant);
		}
		else if (constant instanceof IntegerConstant) {
			return makeStringRepresentation((IntegerConstant) constant);
		}
		else if (constant instanceof LongConstant) {
			return makeStringRepresentation((LongConstant) constant);
		}
		else if (constant instanceof CharConstant) {
			return makeStringRepresentation((CharConstant) constant);
		}
		else if (constant instanceof StringConstant) {
			return makeStringRepresentation((StringConstant) constant);
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
		return Float.toString(constant.getValue());
	}

	protected String makeStringRepresentation(DoubleConstant constant) {
		return Double.toString(constant.getValue());
	}

	protected String makeStringRepresentation(ByteConstant constant) {
		return Byte.toString(constant.getValue());
	}

	protected String makeStringRepresentation(ShortConstant constant) {
		return Short.toString(constant.getValue());
	}

	protected String makeStringRepresentation(IntegerConstant constant) {
		return Integer.toString(constant.getValue());
	}

	protected String makeStringRepresentation(LongConstant constant) {
		return Long.toString(constant.getValue());
	}

	protected String makeStringRepresentation(StringConstant constant) {
		return '"' + constant.getValue() + '"';
	}

	protected String makeStringRepresentation(CharConstant constant) {
		return "'" + constant.getValue() + "'";
	}

	@Override
	protected String makeStringRepresentation(UnaryOperatorExpression expression) {

		int currentPriority = expression.getPriority();
		int argPriority = expression.getArgument().getPriority();
		boolean parenthesisRequired = argPriority > currentPriority;

		if (expression.getOperator() instanceof PostSettableUnaryOperator) {
			try {
				return (parenthesisRequired ? "(" : "") + getStringRepresentation(expression.getArgument())
						+ (parenthesisRequired ? ")" : "") + getSymbol(expression.getOperator());
			} catch (OperatorNotSupportedException e) {
				return "<unsupported>";
			}
		}
		try {
			return getSymbol(expression.getOperator()) + (parenthesisRequired ? "(" : "")
					+ getStringRepresentation(expression.getArgument()) + (parenthesisRequired ? ")" : "");
		} catch (OperatorNotSupportedException e) {
			return "<unsupported>";
		}
	}

	@Override
	protected String makeStringRepresentation(BinaryOperatorExpression expression) {

		// System.out.println(
		// "----> Prettyprint " + expression.getClass().getSimpleName() + " priority " + expression.getOperator().getPriority());

		try {
			int currentPriority = expression.getPriority();
			int leftPriority = expression.getLeftArgument().getPriority();
			int rightPriority = expression.getRightArgument().getPriority();
			boolean parenthesisLeftRequired = leftPriority > currentPriority;
			boolean parenthesisRightRequired = rightPriority >= currentPriority;

			return (parenthesisLeftRequired ? "(" : "") + getStringRepresentation(expression.getLeftArgument())
					+ (parenthesisLeftRequired ? ")" : "") + " " + getSymbol(expression.getOperator()) + " "
					+ (parenthesisRightRequired ? "(" : "") + getStringRepresentation(expression.getRightArgument())
					+ (parenthesisRightRequired ? ")" : "");
		} catch (OperatorNotSupportedException e) {
			return "<unsupported>";
		}
	}

	protected final String makeStringRepresentation(EnumConstant<?> constant) {
		return constant.getName();
	}

	protected String makeStringRepresentation(ObjectConstant constant) {
		return "[Object:" + constant.getValue().toString() + "]";
	}

	@Override
	protected String makeStringRepresentation(BindingValue bv) {
		return bv.toString();
	}

	@Override
	protected String makeStringRepresentation(ConditionalExpression expression) {
		return "(" + getStringRepresentation(expression.getCondition()) + " ? " + getStringRepresentation(expression.getThenExpression())
				+ " : " + getStringRepresentation(expression.getElseExpression()) + ")";
	}

	@Override
	protected String makeStringRepresentation(CastExpression expression) {
		return "(" + makeStringRepresentation(expression.getCastType()) + ")" + getStringRepresentation(expression.getArgument());
	}

	private String makeStringRepresentation(JavaInstanceOfExpression expression) {
		return getStringRepresentation(expression.getArgument()) + " instanceof " + makeStringRepresentation(expression.getType());
	}

	@Override
	protected String makeStringRepresentation(Type type) {
		/*StringBuffer sb = new StringBuffer();
		sb.append("$" + tr.getBaseType());
		if (tr.getParameters().size() > 0) {
			sb.append("<");
			boolean isFirst = true;
			for (TypeReference param : tr.getParameters()) {
				sb.append((isFirst ? "" : ",") + makeStringRepresentation(param));
				isFirst = false;
			}
			sb.append(">");
		}
		return sb.toString();*/

		return TypeUtils.simpleRepresentation(type);
	}
}
