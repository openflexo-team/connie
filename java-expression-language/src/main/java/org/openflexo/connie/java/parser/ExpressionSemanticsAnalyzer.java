/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

package org.openflexo.connie.java.parser;

import java.util.Hashtable;
import java.util.Map;

import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.java.expr.JavaArithmeticBinaryOperator;
import org.openflexo.connie.java.expr.JavaArithmeticUnaryOperator;
import org.openflexo.connie.java.expr.JavaBinaryOperatorExpression;
import org.openflexo.connie.java.expr.JavaBooleanBinaryOperator;
import org.openflexo.connie.java.expr.JavaConditionalExpression;
import org.openflexo.connie.java.expr.JavaConstant.BooleanConstant;
import org.openflexo.connie.java.expr.JavaConstant.ByteConstant;
import org.openflexo.connie.java.expr.JavaConstant.DoubleConstant;
import org.openflexo.connie.java.expr.JavaConstant.FloatConstant;
import org.openflexo.connie.java.expr.JavaConstant.IntegerConstant;
import org.openflexo.connie.java.expr.JavaConstant.LongConstant;
import org.openflexo.connie.java.expr.JavaConstant.ShortConstant;
import org.openflexo.connie.java.expr.JavaUnaryOperatorExpression;
import org.openflexo.connie.java.parser.analysis.DepthFirstAdapter;
import org.openflexo.connie.java.parser.node.AConditionalExpression;
import org.openflexo.connie.java.parser.node.AEqEqualityExp;
import org.openflexo.connie.java.parser.node.AFalseLiteral;
import org.openflexo.connie.java.parser.node.AFloatingPointLiteral;
import org.openflexo.connie.java.parser.node.AGtRelationalExp;
import org.openflexo.connie.java.parser.node.AGteqRelationalExp;
import org.openflexo.connie.java.parser.node.AIdentifierPrimary;
import org.openflexo.connie.java.parser.node.AInstanceofRelationalExp;
import org.openflexo.connie.java.parser.node.AIntegerLiteral;
import org.openflexo.connie.java.parser.node.ALiteralPrimaryNoId;
import org.openflexo.connie.java.parser.node.ALtRelationalExp;
import org.openflexo.connie.java.parser.node.ALteqRelationalExp;
import org.openflexo.connie.java.parser.node.AMethodPrimaryNoId;
import org.openflexo.connie.java.parser.node.AMinusAddExp;
import org.openflexo.connie.java.parser.node.AMinusUnaryExp;
import org.openflexo.connie.java.parser.node.ANeqEqualityExp;
import org.openflexo.connie.java.parser.node.APlusAddExp;
import org.openflexo.connie.java.parser.node.APostfixUnaryExpNotPlusMinus;
import org.openflexo.connie.java.parser.node.APrimaryNoIdPrimary;
import org.openflexo.connie.java.parser.node.APrimaryPostfixExp;
import org.openflexo.connie.java.parser.node.AQmarkConditionalExp;
import org.openflexo.connie.java.parser.node.ASimpleAddExp;
import org.openflexo.connie.java.parser.node.ASimpleAndExp;
import org.openflexo.connie.java.parser.node.ASimpleConditionalAndExp;
import org.openflexo.connie.java.parser.node.ASimpleConditionalExp;
import org.openflexo.connie.java.parser.node.ASimpleConditionalOrExp;
import org.openflexo.connie.java.parser.node.ASimpleEqualityExp;
import org.openflexo.connie.java.parser.node.ASimpleExclusiveOrExp;
import org.openflexo.connie.java.parser.node.ASimpleInclusiveOrExp;
import org.openflexo.connie.java.parser.node.ASimpleMultExp;
import org.openflexo.connie.java.parser.node.ASimpleRelationalExp;
import org.openflexo.connie.java.parser.node.ASimpleShiftExp;
import org.openflexo.connie.java.parser.node.ATrueLiteral;
import org.openflexo.connie.java.parser.node.AUnaryUnaryExp;
import org.openflexo.connie.java.parser.node.Node;
import org.openflexo.connie.java.parser.node.PUnaryExp;
import org.openflexo.toolbox.StringUtils;

/**
 * This class implements the semantics analyzer for a parsed AnTAR expression.<br>
 * Its main purpose is to build a syntax tree with AnTAR expression model from a parsed AST.
 * 
 * @author sylvain
 * 
 */
class ExpressionSemanticsAnalyzer extends DepthFirstAdapter {

	private final Map<Node, Expression> expressionNodes;
	private Node topLevel = null;

	public ExpressionSemanticsAnalyzer() {
		expressionNodes = new Hashtable<>();
	}

	public Expression getExpression() {
		if (topLevel != null) {
			return expressionNodes.get(topLevel);
		}
		return null;
	}

	private void registerExpressionNode(Node n, Expression e) {
		// System.out.println("REGISTER " + e + " for node " + n + " as " + n.getClass());
		expressionNodes.put(n, e);
		topLevel = n;
		/*if (n.parent() != null) {
			registerExpressionNode(n.parent(), e);
		}*/
	}

	protected Expression getExpression(Node n) {
		if (n != null) {
			Expression returned = expressionNodes.get(n);

			if (returned == null) {
				if (n instanceof AConditionalExpression) {
					return getExpression(((AConditionalExpression) n).getConditionalExp());
				}
				if (n instanceof ASimpleConditionalExp) {
					return getExpression(((ASimpleConditionalExp) n).getConditionalOrExp());
				}
				if (n instanceof ASimpleConditionalOrExp) {
					return getExpression(((ASimpleConditionalOrExp) n).getConditionalAndExp());
				}
				if (n instanceof ASimpleConditionalAndExp) {
					return getExpression(((ASimpleConditionalAndExp) n).getInclusiveOrExp());
				}
				if (n instanceof ASimpleInclusiveOrExp) {
					return getExpression(((ASimpleInclusiveOrExp) n).getExclusiveOrExp());
				}
				if (n instanceof ASimpleExclusiveOrExp) {
					return getExpression(((ASimpleExclusiveOrExp) n).getAndExp());
				}
				if (n instanceof ASimpleAndExp) {
					return getExpression(((ASimpleAndExp) n).getEqualityExp());
				}
				if (n instanceof ASimpleEqualityExp) {
					return getExpression(((ASimpleEqualityExp) n).getRelationalExp());
				}
				if (n instanceof ASimpleRelationalExp) {
					return getExpression(((ASimpleRelationalExp) n).getShiftExp());
				}
				if (n instanceof ASimpleShiftExp) {
					return getExpression(((ASimpleShiftExp) n).getAddExp());
				}
				if (n instanceof ASimpleAddExp) {
					return getExpression(((ASimpleAddExp) n).getMultExp());
				}
				if (n instanceof ASimpleMultExp) {
					return getExpression(((ASimpleMultExp) n).getUnaryExp());
				}
				if (n instanceof AUnaryUnaryExp) {
					return getExpression(((AUnaryUnaryExp) n).getUnaryExpNotPlusMinus());
				}
				if (n instanceof APostfixUnaryExpNotPlusMinus) {
					return getExpression(((APostfixUnaryExpNotPlusMinus) n).getPostfixExp());
				}
				if (n instanceof APrimaryPostfixExp) {
					return getExpression(((APrimaryPostfixExp) n).getPrimary());
				}
				if (n instanceof APrimaryNoIdPrimary) {
					return getExpression(((APrimaryNoIdPrimary) n).getPrimaryNoId());
				}
				if (n instanceof ALiteralPrimaryNoId) {
					return getExpression(((ALiteralPrimaryNoId) n).getLiteral());
				}

				System.out.println("No expression registered for " + n + " of  " + n.getClass());
			}
			return returned;
		}
		return null;
	}

	int ident = 0;

	@Override
	public void defaultIn(Node node) {
		super.defaultIn(node);
		ident++;
		System.out.println(StringUtils.buildWhiteSpaceIndentation(ident) + " > " + node.getClass().getSimpleName());
	}

	@Override
	public void defaultOut(Node node) {
		// TODO Auto-generated method stub
		super.defaultOut(node);
		ident--;
	}

	/*@Override
	public void outAIdentifierPrefix(AIdentifierPrefix node) {
		// TODO Auto-generated method stub
		super.outAIdentifierPrefix(node);
		System.out.println("Tiens on sort avec " + node + " of " + node.getClass().getSimpleName());
	}
	
	@Override
	public void outACompositeIdent(ACompositeIdent node) {
		// TODO Auto-generated method stub
		super.outACompositeIdent(node);
		System.out.println("On sort de CompositeIdent avec " + node + " of " + node.getClass().getSimpleName());
	}
	
	@Override
	public void outAPrimaryMethodInvocation(APrimaryMethodInvocation node) {
		// TODO Auto-generated method stub
		super.outAPrimaryMethodInvocation(node);
		System.out.println("On sort de PrimaryMethodInvocation avec " + node + " of " + node.getClass().getSimpleName());
	}*/

	/*@Override
	public void inAIdentifierPrimary(AIdentifierPrimary node) {
		super.inAIdentifierPrimary(node);
		System.out.println(">> On entre dans primary/{identifier} avec " + node + " of " + node.getClass().getSimpleName());
	}*/

	@Override
	public void outAIdentifierPrimary(AIdentifierPrimary node) {
		super.outAIdentifierPrimary(node);
		// System.out.println("<< On sort de primary/{identifier} avec " + node + " of " + node.getClass().getSimpleName());
		registerExpressionNode(node, BindingValueAnalyzer.makeBindingValue(node, this));
	}

	/*@Override
	public void outAPrimaryNoIdPrimary(APrimaryNoIdPrimary node) {
		super.outAPrimaryNoIdPrimary(node);
		registerExpressionNode(node, BindingValueAnalyzer.makeBindingValue(node, this));
	}*/

	@Override
	public void outAMethodPrimaryNoId(AMethodPrimaryNoId node) {
		super.outAMethodPrimaryNoId(node);
		System.out.println("******** Hop on construit un binding avec " + node);
		registerExpressionNode(node, BindingValueAnalyzer.makeBindingValue(node, this));
	}

	public Constant<?> makeConstant(Number number) {
		if (number instanceof Byte) {
			return new ByteConstant((Byte) number);
		}
		if (number instanceof Short) {
			return new ShortConstant((Short) number);
		}
		if (number instanceof Integer) {
			return new IntegerConstant((Integer) number);
		}
		if (number instanceof Long) {
			return new LongConstant((Long) number);
		}
		if (number instanceof Float) {
			return new FloatConstant((Float) number);
		}
		if (number instanceof Double) {
			return new DoubleConstant((Double) number);
		}
		return null;
	}

	@Override
	public void outATrueLiteral(ATrueLiteral node) {
		super.outATrueLiteral(node);
		registerExpressionNode(node, BooleanConstant.TRUE);
	}

	@Override
	public void outAFalseLiteral(AFalseLiteral node) {
		super.outAFalseLiteral(node);
		registerExpressionNode(node, BooleanConstant.FALSE);
	}

	@Override
	public void outAIntegerLiteral(AIntegerLiteral node) {
		super.outAIntegerLiteral(node);

		String valueText = node.getLitInteger().getText();
		Number value;

		if (valueText.startsWith("0x") || valueText.startsWith("0X")) {
			valueText = valueText.substring(2);
			try {
				value = Integer.parseInt(valueText, 16);
			} catch (NumberFormatException e) {
				value = Long.parseLong(valueText, 16);
			}
		}
		else if (valueText.startsWith("0") && valueText.length() > 1) {
			valueText = valueText.substring(1);
			try {
				value = Integer.parseInt(valueText, 8);
			} catch (NumberFormatException e) {
				value = Long.parseLong(valueText, 8);
			}
		}
		else if (valueText.endsWith("L") || valueText.endsWith("l")) {
			valueText = valueText.substring(0, valueText.length() - 1);
			value = Long.parseLong(valueText);
		}
		else {
			value = Integer.parseInt(valueText);
			// value = NumberFormat.getNumberInstance().parse(valueText);
			// System.out.println("Pour " + valueText + " j'obtiens " + value + " of " + value.getClass());
		}
		registerExpressionNode(node, makeConstant(value));
	}

	@Override
	public void outAFloatingPointLiteral(AFloatingPointLiteral node) {
		super.outAFloatingPointLiteral(node);

		Number value = null;
		String valueText = node.getLitFloat().getText();
		if (valueText.endsWith("F") || valueText.endsWith("f")) {
			valueText = valueText.substring(0, valueText.length() - 1);
			try {
				value = Float.parseFloat(valueText);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		else if (valueText.endsWith("D") || valueText.endsWith("d")) {
			valueText = valueText.substring(0, valueText.length() - 1);
			try {
				value = Double.parseDouble(valueText);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		else {
			try {
				value = Double.parseDouble(valueText);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		registerExpressionNode(node, makeConstant(value));

	}

	// conditional_exp =
	// {simple} conditional_or_exp
	// | {qmark} conditional_or_exp qmark expression colon conditional_exp
	// ;

	@Override
	public void outAQmarkConditionalExp(AQmarkConditionalExp node) {
		// TODO Auto-generated method stub
		super.outAQmarkConditionalExp(node);
		registerExpressionNode(node, new JavaConditionalExpression(getExpression(node.getConditionalOrExp()),
				getExpression(node.getExpression()), getExpression(node.getConditionalExp())));
	}

	@Override
	public void outAMinusUnaryExp(AMinusUnaryExp node) {
		// TODO Auto-generated method stub
		super.outAMinusUnaryExp(node);

		PUnaryExp unaryExp = node.getUnaryExp();

		registerExpressionNode(node, new JavaUnaryOperatorExpression(JavaArithmeticUnaryOperator.UNARY_MINUS, getExpression(unaryExp)));

	}

	@Override
	public void outAPlusAddExp(APlusAddExp node) {
		super.outAPlusAddExp(node);
		registerExpressionNode(node, new JavaBinaryOperatorExpression(JavaArithmeticBinaryOperator.ADDITION,
				getExpression(node.getAddExp()), getExpression(node.getMultExp())));
	}

	@Override
	public void outAMinusAddExp(AMinusAddExp node) {
		super.outAMinusAddExp(node);
		registerExpressionNode(node, new JavaBinaryOperatorExpression(JavaArithmeticBinaryOperator.SUBSTRACTION,
				getExpression(node.getAddExp()), getExpression(node.getMultExp())));
	}

	// equality_exp =
	// {simple} relational_exp
	// | {eq} equality_exp eq relational_exp
	// | {neq} equality_exp neq relational_exp
	// ;

	@Override
	public void outAEqEqualityExp(AEqEqualityExp node) {
		super.outAEqEqualityExp(node);
		registerExpressionNode(node, new JavaBinaryOperatorExpression(JavaBooleanBinaryOperator.EQUALS,
				getExpression(node.getEqualityExp()), getExpression(node.getRelationalExp())));
	}

	@Override
	public void outANeqEqualityExp(ANeqEqualityExp node) {
		super.outANeqEqualityExp(node);
		registerExpressionNode(node, new JavaBinaryOperatorExpression(JavaBooleanBinaryOperator.NOT_EQUALS,
				getExpression(node.getEqualityExp()), getExpression(node.getRelationalExp())));
	}

	// relational_exp =
	// {simple} shift_exp
	// | {lt} [shift_exp1]:shift_exp lt [shift_expression2]:shift_exp
	// | {gt} [shift_expression1]:shift_exp gt [shift_expression2]:shift_exp
	// | {lteq} [shift_expression1]:shift_exp lteq [shift_expression2]:shift_exp
	// | {gteq} [shift_expression1]:shift_exp gteq [shift_expression2]:shift_exp
	// | {instanceof} shift_exp kw_instanceof type [dims]:dim*
	// ;

	@Override
	public void outALtRelationalExp(ALtRelationalExp node) {
		super.outALtRelationalExp(node);
		registerExpressionNode(node, new JavaBinaryOperatorExpression(JavaBooleanBinaryOperator.LESS_THAN,
				getExpression(node.getShiftExp1()), getExpression(node.getShiftExpression2())));
	}

	@Override
	public void outAGtRelationalExp(AGtRelationalExp node) {
		super.outAGtRelationalExp(node);
		registerExpressionNode(node, new JavaBinaryOperatorExpression(JavaBooleanBinaryOperator.GREATER_THAN,
				getExpression(node.getShiftExpression1()), getExpression(node.getShiftExpression2())));
	}

	@Override
	public void outALteqRelationalExp(ALteqRelationalExp node) {
		super.outALteqRelationalExp(node);
		registerExpressionNode(node, new JavaBinaryOperatorExpression(JavaBooleanBinaryOperator.LESS_THAN_OR_EQUALS,
				getExpression(node.getShiftExpression1()), getExpression(node.getShiftExpression2())));
	}

	@Override
	public void outAGteqRelationalExp(AGteqRelationalExp node) {
		super.outAGteqRelationalExp(node);
		registerExpressionNode(node, new JavaBinaryOperatorExpression(JavaBooleanBinaryOperator.GREATER_THAN_OR_EQUALS,
				getExpression(node.getShiftExpression1()), getExpression(node.getShiftExpression2())));
	}

	@Override
	public void outAInstanceofRelationalExp(AInstanceofRelationalExp node) {
		super.outAInstanceofRelationalExp(node);
	}
}
