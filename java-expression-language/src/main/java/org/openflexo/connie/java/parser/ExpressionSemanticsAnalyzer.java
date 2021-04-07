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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Hashtable;
import java.util.Map;

import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.Constant.FloatConstant;
import org.openflexo.connie.expr.Constant.IntegerConstant;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.java.parser.analysis.DepthFirstAdapter;
import org.openflexo.connie.java.parser.node.AConditionalExpression;
import org.openflexo.connie.java.parser.node.AFloatingPointLiteral;
import org.openflexo.connie.java.parser.node.AIdentifierPrimary;
import org.openflexo.connie.java.parser.node.AIntegerLiteral;
import org.openflexo.connie.java.parser.node.ALiteralPrimaryNoId;
import org.openflexo.connie.java.parser.node.AMethodPrimaryNoId;
import org.openflexo.connie.java.parser.node.APostfixUnaryExpNotPlusMinus;
import org.openflexo.connie.java.parser.node.APrimaryNoIdPrimary;
import org.openflexo.connie.java.parser.node.APrimaryPostfixExp;
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
import org.openflexo.connie.java.parser.node.AUnaryUnaryExp;
import org.openflexo.connie.java.parser.node.Node;

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
					System.out.println("Prout pour " + n);
					return getExpression(((AConditionalExpression) n).getConditionalExp());
				}
				if (n instanceof ASimpleConditionalExp) {
					System.out.println("Prout2 pour " + n);
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

	/*int ident = 0;
	
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
	}*/

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
			return new IntegerConstant((Byte) number);
		}
		if (number instanceof Short) {
			return new IntegerConstant((Short) number);
		}
		if (number instanceof Integer) {
			return new IntegerConstant((Integer) number);
		}
		if (number instanceof Long) {
			return new IntegerConstant((Long) number);
		}
		if (number instanceof Float) {
			return new FloatConstant((Float) number);
		}
		if (number instanceof Double) {
			return new FloatConstant((Double) number);
		}
		return null;
	}

	@Override
	public void outAIntegerLiteral(AIntegerLiteral node) {
		super.outAIntegerLiteral(node);

		try {
			String valueText = node.getLitInteger().getText();
			Number value;

			if (valueText.startsWith("0x") || valueText.startsWith("0X")) {
				valueText = valueText.substring(2);
				value = Long.parseLong(valueText, 16);
			}
			else if (valueText.startsWith("0")) {
				valueText = valueText.substring(1);
				value = Long.parseLong(valueText, 8);
				System.out.println("value=" + value);
			}
			else {
				value = NumberFormat.getNumberInstance().parse(valueText);
			}
			registerExpressionNode(node, makeConstant(value));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void outAFloatingPointLiteral(AFloatingPointLiteral node) {
		super.outAFloatingPointLiteral(node);

		Number value = null;
		String valueText = node.getLitFloat().getText();
		System.out.println("valueText=" + valueText);
		try {
			value = Double.parseDouble(valueText);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		System.out.println("value = " + value);
		registerExpressionNode(node, makeConstant(value));

	}

	/*@Override
	public void inAMethodPrimaryNoId(AMethodPrimaryNoId node) {
		// TODO Auto-generated method stub
		super.inAMethodPrimaryNoId(node);
		System.out.println(">> On entre de primary_no_id/{method} avec " + node + " of " + node.getClass().getSimpleName());
	}
	
	@Override
	public void outAMethodPrimaryNoId(AMethodPrimaryNoId node) {
		// TODO Auto-generated method stub
		super.outAMethodPrimaryNoId(node);
		System.out.println("<< On sort de primary_no_id/{method} avec " + node + " of " + node.getClass().getSimpleName());
	}*/

	/*	private BindingValue makeBinding(PBinding node) {
			// System.out.println("Make binding with " + node);
	
			// Apply the translation.
			BindingSemanticsAnalyzer bsa = new BindingSemanticsAnalyzer(node);
	
			// System.out.println("Built bsa as " + bsa.getPath());
	
			node.apply(bsa);
	
			// System.out.println("Make binding value with bsa as " + bsa.getPath());
	
			BindingValue returned = new BindingValue(bsa.getPath());
			// System.out.println("Made binding as " + bsa.getPath());
	
			registerExpressionNode(node, returned);
			return returned;
		}
	
		private TypeReference makeTypeReference(PTypeReference node) {
			if (node instanceof ABasicTypeReference) {
				return makeBasicTypeReference((ABasicTypeReference) node);
			}
			else if (node instanceof AParameteredTypeReference) {
				return makeParameteredTypeReference((AParameteredTypeReference) node);
			}
			System.err.println("Unexpected " + node);
			return null;
		}
	
		private String makeReferencePath(PTypeReferencePath path) {
			if (path instanceof AIdentifierTypeReferencePath) {
				return ((AIdentifierTypeReferencePath) path).getIdentifier().getText();
			}
			else if (path instanceof ATailTypeReferencePath) {
				return ((ATailTypeReferencePath) path).getIdentifier().getText() + "."
						+ makeReferencePath(((ATailTypeReferencePath) path).getTypeReferencePath());
			}
			System.err.println("Unexpected " + path);
			return null;
		}
	
		private TypeReference makeBasicTypeReference(ABasicTypeReference node) {
			return new TypeReference(makeReferencePath(node.getTypeReferencePath()));
		}
	
		private TypeReference makeParameteredTypeReference(AParameteredTypeReference node) {
			PTypeReferenceArgList argList = node.getTypeReferenceArgList();
			List<TypeReference> args = new ArrayList<>();
			if (argList instanceof ATypeReferenceArgList) {
				args.add(makeTypeReference(((ATypeReferenceArgList) argList).getTypeReference()));
				for (PTypeReferenceAdditionalArg aa : ((ATypeReferenceArgList) argList).getTypeReferenceAdditionalArgs()) {
					ATypeReferenceAdditionalArg additionalArg = (ATypeReferenceAdditionalArg) aa;
					args.add(makeTypeReference(additionalArg.getTypeReference()));
				}
			}
			return new TypeReference(makeReferencePath(node.getTypeReferencePath()), args);
		}
	
		private IntegerConstant makeDecimalNumber(TDecimalNumber node) {
			// System.out.println("Make decimal number with " + node + " as " + Long.parseLong(node.getText()));
			IntegerConstant returned = new IntegerConstant(Long.parseLong(node.getText()));
			registerExpressionNode(node, returned);
			return returned;
		}
	
		private FloatConstant makePreciseNumber(TPreciseNumber node) {
			// System.out.println("Make precise number with " + node + " as " + Double.parseDouble(node.getText()));
			FloatConstant returned = new FloatConstant(Double.parseDouble(node.getText()));
			registerExpressionNode(node, returned);
			return returned;
		}
	
		private FloatConstant makeScientificNotationNumber(TScientificNotationNumber node) {
			// System.out.println("Make scientific notation number with " + node + " as " + Double.parseDouble(node.getText()));
			FloatConstant returned = new FloatConstant(Double.parseDouble(node.getText()));
			registerExpressionNode(node, returned);
			return returned;
		}
	
		private StringConstant makeStringValue(TStringValue node) {
			// System.out.println("Make string value with " + node);
			StringConstant returned = new StringConstant(node.getText().substring(1, node.getText().length() - 1));
			registerExpressionNode(node, returned);
			return returned;
		}
	
		private StringConstant makeCharsValue(TCharsValue node) {
			// System.out.println("Make chars value with " + node);
			StringConstant returned = new StringConstant(node.getText().substring(1, node.getText().length() - 1));
			registerExpressionNode(node, returned);
			return returned;
		}
	
	
		@Override
		public void outAExpr2Expr(AExpr2Expr node) {
			super.outAExpr2Expr(node);
			registerExpressionNode(node, getExpression(node.getExpr2()));
		}
	
		@Override
		public void outACondExprExpr(ACondExprExpr node) {
			super.outACondExprExpr(node);
			// System.out.println("On chope une conditionnelle avec cond:" + node.getCondition() + " then:" + node.getThen() + " else:"+
			// node.getElse());
			registerExpressionNode(node, new ConditionalExpression(getExpression(node.getCondition()), getExpression(node.getThen()),
					getExpression(node.getElse())));
		}
	
		@Override
		public void outAEqExprExpr(AEqExprExpr node) {
			super.outAEqExprExpr(node);
			registerExpressionNode(node,
					new BinaryOperatorExpression(BooleanBinaryOperator.EQUALS, getExpression(node.getLeft()), getExpression(node.getRight())));
		}
	
		@Override
		public void outAEq2ExprExpr(AEq2ExprExpr node) {
			super.outAEq2ExprExpr(node);
			registerExpressionNode(node,
					new BinaryOperatorExpression(BooleanBinaryOperator.EQUALS, getExpression(node.getLeft()), getExpression(node.getRight())));
		}
	
		@Override
		public void outANeqExprExpr(ANeqExprExpr node) {
			super.outANeqExprExpr(node);
			registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.NOT_EQUALS, getExpression(node.getLeft()),
					getExpression(node.getRight())));
		}
	
		@Override
		public void outALtExprExpr(ALtExprExpr node) {
			super.outALtExprExpr(node);
			registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.LESS_THAN, getExpression(node.getLeft()),
					getExpression(node.getRight())));
		}
	
		@Override
		public void outALteExprExpr(ALteExprExpr node) {
			super.outALteExprExpr(node);
			registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.LESS_THAN_OR_EQUALS, getExpression(node.getLeft()),
					getExpression(node.getRight())));
		}
	
		@Override
		public void outAGtExprExpr(AGtExprExpr node) {
			super.outAGtExprExpr(node);
			registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.GREATER_THAN, getExpression(node.getLeft()),
					getExpression(node.getRight())));
		}
	
		@Override
		public void outAGteExprExpr(AGteExprExpr node) {
			super.outAGteExprExpr(node);
			registerExpressionNode(node, new BinaryOperatorExpression(BooleanBinaryOperator.GREATER_THAN_OR_EQUALS,
					getExpression(node.getLeft()), getExpression(node.getRight())));
		}
	
	
		@Override
		public void outAExpr3Expr2(AExpr3Expr2 node) {
			// System.out.println("OUT Expr3-Expr2 with " + node);
			super.outAExpr3Expr2(node);
			registerExpressionNode(node, getExpression(node.getExpr3()));
			// System.out.println("***** AExpr3Expr2 " + node + "expression=" + getExpression(node.getExpr3()));
		}
	
		@Override
		public void outAOrExprExpr2(AOrExprExpr2 node) {
			super.outAOrExprExpr2(node);
			registerExpressionNode(node,
					new BinaryOperatorExpression(BooleanBinaryOperator.OR, getExpression(node.getLeft()), getExpression(node.getRight())));
		}
	
		@Override
		public void outAOr2ExprExpr2(AOr2ExprExpr2 node) {
			super.outAOr2ExprExpr2(node);
			registerExpressionNode(node,
					new BinaryOperatorExpression(BooleanBinaryOperator.OR, getExpression(node.getLeft()), getExpression(node.getRight())));
		}
	
		@Override
		public void outAAddExprExpr2(AAddExprExpr2 node) {
			super.outAAddExprExpr2(node);
			// System.out.println("OUT add with " + node);
			registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.ADDITION, getExpression(node.getLeft()),
					getExpression(node.getRight())));
		}
	
		@Override
		public void outASubExprExpr2(ASubExprExpr2 node) {
			super.outASubExprExpr2(node);
			registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.SUBSTRACTION, getExpression(node.getLeft()),
					getExpression(node.getRight())));
		}
	
	
		@Override
		public void outATermExpr3(ATermExpr3 node) {
			// System.out.println("OUT Term-Expr3 with " + node + " term=" + node.getTerm() + " of " + node.getTerm().getClass());
			super.outATermExpr3(node);
			registerExpressionNode(node, getExpression(node.getTerm()));
			// System.out.println("***** ATermExpr3 " + node + "expression=" + getExpression(node.getTerm()));
		}
	
		@Override
		public void outAAndExprExpr3(AAndExprExpr3 node) {
			super.outAAndExprExpr3(node);
			registerExpressionNode(node,
					new BinaryOperatorExpression(BooleanBinaryOperator.AND, getExpression(node.getLeft()), getExpression(node.getRight())));
		}
	
		@Override
		public void outAAnd2ExprExpr3(AAnd2ExprExpr3 node) {
			super.outAAnd2ExprExpr3(node);
			registerExpressionNode(node,
					new BinaryOperatorExpression(BooleanBinaryOperator.AND, getExpression(node.getLeft()), getExpression(node.getRight())));
		}
	
		@Override
		public void outAMultExprExpr3(AMultExprExpr3 node) {
			super.outAMultExprExpr3(node);
			// System.out.println("OUT mult with " + node);
			registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.MULTIPLICATION, getExpression(node.getLeft()),
					getExpression(node.getRight())));
		}
	
		@Override
		public void outADivExprExpr3(ADivExprExpr3 node) {
			super.outADivExprExpr3(node);
			registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.DIVISION, getExpression(node.getLeft()),
					getExpression(node.getRight())));
		}
	
		@Override
		public void outAModExprExpr3(AModExprExpr3 node) {
			super.outAModExprExpr3(node);
			registerExpressionNode(node,
					new BinaryOperatorExpression(ArithmeticBinaryOperator.MOD, getExpression(node.getLeft()), getExpression(node.getRight())));
		}
	
		@Override
		public void outAPowerExprExpr3(APowerExprExpr3 node) {
			super.outAPowerExprExpr3(node);
			registerExpressionNode(node, new BinaryOperatorExpression(ArithmeticBinaryOperator.POWER, getExpression(node.getLeft()),
					getExpression(node.getRight())));
		}
	
		@Override
		public void outANotExprExpr3(ANotExprExpr3 node) {
			super.outANotExprExpr3(node);
			registerExpressionNode(node, new UnaryOperatorExpression(BooleanUnaryOperator.NOT, getExpression(node.getTerm())));
		}
	
	
		@Override
		public void outACosFuncFunction(ACosFuncFunction node) {
			super.outACosFuncFunction(node);
			registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.COS, getExpression(node.getExpr2())));
		}
	
		@Override
		public void outAAcosFuncFunction(AAcosFuncFunction node) {
			super.outAAcosFuncFunction(node);
			registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.ACOS, getExpression(node.getExpr2())));
		}
	
		@Override
		public void outASinFuncFunction(ASinFuncFunction node) {
			super.outASinFuncFunction(node);
			registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.SIN, getExpression(node.getExpr2())));
		}
	
		@Override
		public void outAAsinFuncFunction(AAsinFuncFunction node) {
			super.outAAsinFuncFunction(node);
			registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.ASIN, getExpression(node.getExpr2())));
		}
	
		@Override
		public void outATanFuncFunction(ATanFuncFunction node) {
			super.outATanFuncFunction(node);
			registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.TAN, getExpression(node.getExpr2())));
		}
	
		@Override
		public void outAAtanFuncFunction(AAtanFuncFunction node) {
			super.outAAtanFuncFunction(node);
			registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.ATAN, getExpression(node.getExpr2())));
		}
	
		@Override
		public void outAExpFuncFunction(AExpFuncFunction node) {
			super.outAExpFuncFunction(node);
			registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.EXP, getExpression(node.getExpr2())));
		}
	
		@Override
		public void outALogFuncFunction(ALogFuncFunction node) {
			super.outALogFuncFunction(node);
			registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.LOG, getExpression(node.getExpr2())));
		}
	
		@Override
		public void outASqrtFuncFunction(ASqrtFuncFunction node) {
			super.outASqrtFuncFunction(node);
			registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.SQRT, getExpression(node.getExpr2())));
		}
	
	
		@Override
		public void outATrueConstant(ATrueConstant node) {
			super.outATrueConstant(node);
			registerExpressionNode(node, BooleanConstant.TRUE);
		}
	
		@Override
		public void outAFalseConstant(AFalseConstant node) {
			super.outAFalseConstant(node);
			registerExpressionNode(node, BooleanConstant.FALSE);
		}
	
		@Override
		public void outAPiConstant(APiConstant node) {
			super.outAPiConstant(node);
			registerExpressionNode(node, FloatSymbolicConstant.PI);
		}
	
		@Override
		public void outANullConstant(ANullConstant node) {
			super.outANullConstant(node);
			registerExpressionNode(node, ObjectSymbolicConstant.NULL);
		}
	
		@Override
		public void outADecimalNumberNumber(ADecimalNumberNumber node) {
			super.outADecimalNumberNumber(node);
			registerExpressionNode(node, makeDecimalNumber(node.getDecimalNumber()));
		}
	
		@Override
		public void outAPreciseNumberNumber(APreciseNumberNumber node) {
			super.outAPreciseNumberNumber(node);
			registerExpressionNode(node, makePreciseNumber(node.getPreciseNumber()));
		}
	
		@Override
		public void outAScientificNotationNumberNumber(AScientificNotationNumberNumber node) {
			super.outAScientificNotationNumberNumber(node);
			registerExpressionNode(node, makeScientificNotationNumber(node.getScientificNotationNumber()));
		}
	
		@Override
		public void outAConstantNumber(AConstantNumber node) {
			super.outAConstantNumber(node);
			registerExpressionNode(node, getExpression(node.getConstant()));
		}
	
	
		@Override
		public void outACastTerm(ACastTerm node) {
			super.outACastTerm(node);
			registerExpressionNode(node, new CastExpression(makeTypeReference(node.getTypeReference()), getExpression(node.getTerm())));
		}
	
		@Override
		public void outANegativeTerm(ANegativeTerm node) {
			super.outANegativeTerm(node);
			registerExpressionNode(node, new UnaryOperatorExpression(ArithmeticUnaryOperator.UNARY_MINUS, getExpression(node.getTerm())));
		}
	
		@Override
		public void outANumberTerm(ANumberTerm node) {
			super.outANumberTerm(node);
			registerExpressionNode(node, getExpression(node.getNumber()));
		}
	
		@Override
		public void outAStringValueTerm(AStringValueTerm node) {
			super.outAStringValueTerm(node);
			registerExpressionNode(node, makeStringValue(node.getStringValue()));
		}
	
		@Override
		public void outACharsValueTerm(ACharsValueTerm node) {
			super.outACharsValueTerm(node);
			registerExpressionNode(node, makeCharsValue(node.getCharsValue()));
		}
	
		@Override
		public void outAFunctionTerm(AFunctionTerm node) {
			super.outAFunctionTerm(node);
			registerExpressionNode(node, getExpression(node.getFunction()));
		}
	
		@Override
		public void outABindingTerm(ABindingTerm node) {
			super.outABindingTerm(node);
			registerExpressionNode(node, makeBinding(node.getBinding()));
		}
	
		@Override
		public void outAExprTerm(AExprTerm node) {
			super.outAExprTerm(node);
			registerExpressionNode(node, getExpression(node.getExpr()));
		}*/

}
