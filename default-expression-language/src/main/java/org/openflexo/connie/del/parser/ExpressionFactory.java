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

package org.openflexo.connie.del.parser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.del.expr.DELArithmeticBinaryOperator;
import org.openflexo.connie.del.expr.DELArithmeticUnaryOperator;
import org.openflexo.connie.del.expr.DELBinaryOperatorExpression;
import org.openflexo.connie.del.expr.DELBooleanBinaryOperator;
import org.openflexo.connie.del.expr.DELBooleanUnaryOperator;
import org.openflexo.connie.del.expr.DELCastExpression;
import org.openflexo.connie.del.expr.DELConditionalExpression;
import org.openflexo.connie.del.expr.DELConstant.BooleanConstant;
import org.openflexo.connie.del.expr.DELConstant.FloatConstant;
import org.openflexo.connie.del.expr.DELConstant.FloatSymbolicConstant;
import org.openflexo.connie.del.expr.DELConstant.IntegerConstant;
import org.openflexo.connie.del.expr.DELConstant.ObjectSymbolicConstant;
import org.openflexo.connie.del.expr.DELConstant.StringConstant;
import org.openflexo.connie.del.expr.DELUnaryOperatorExpression;
import org.openflexo.connie.del.expr.TypeReference;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.parser.analysis.DepthFirstAdapter;
import org.openflexo.connie.expr.parser.node.AAcosFuncFunction;
import org.openflexo.connie.expr.parser.node.AAddExprExpr2;
import org.openflexo.connie.expr.parser.node.AAnd2ExprExpr3;
import org.openflexo.connie.expr.parser.node.AAndExprExpr3;
import org.openflexo.connie.expr.parser.node.AAsinFuncFunction;
import org.openflexo.connie.expr.parser.node.AAtanFuncFunction;
import org.openflexo.connie.expr.parser.node.ABasicTypeReference;
import org.openflexo.connie.expr.parser.node.ABindingTerm;
import org.openflexo.connie.expr.parser.node.ACastTerm;
import org.openflexo.connie.expr.parser.node.ACharsValueTerm;
import org.openflexo.connie.expr.parser.node.ACondExprExpr;
import org.openflexo.connie.expr.parser.node.AConstantNumber;
import org.openflexo.connie.expr.parser.node.ACosFuncFunction;
import org.openflexo.connie.expr.parser.node.ADecimalNumberNumber;
import org.openflexo.connie.expr.parser.node.ADivExprExpr3;
import org.openflexo.connie.expr.parser.node.AEq2ExprExpr;
import org.openflexo.connie.expr.parser.node.AEqExprExpr;
import org.openflexo.connie.expr.parser.node.AExpFuncFunction;
import org.openflexo.connie.expr.parser.node.AExpr2Expr;
import org.openflexo.connie.expr.parser.node.AExpr3Expr2;
import org.openflexo.connie.expr.parser.node.AExprTerm;
import org.openflexo.connie.expr.parser.node.AFalseConstant;
import org.openflexo.connie.expr.parser.node.AFunctionTerm;
import org.openflexo.connie.expr.parser.node.AGtExprExpr;
import org.openflexo.connie.expr.parser.node.AGteExprExpr;
import org.openflexo.connie.expr.parser.node.AIdentifierTypeReferencePath;
import org.openflexo.connie.expr.parser.node.ALogFuncFunction;
import org.openflexo.connie.expr.parser.node.ALtExprExpr;
import org.openflexo.connie.expr.parser.node.ALteExprExpr;
import org.openflexo.connie.expr.parser.node.AModExprExpr3;
import org.openflexo.connie.expr.parser.node.AMultExprExpr3;
import org.openflexo.connie.expr.parser.node.ANegativeTerm;
import org.openflexo.connie.expr.parser.node.ANeqExprExpr;
import org.openflexo.connie.expr.parser.node.ANotExprExpr3;
import org.openflexo.connie.expr.parser.node.ANullConstant;
import org.openflexo.connie.expr.parser.node.ANumberTerm;
import org.openflexo.connie.expr.parser.node.AOr2ExprExpr2;
import org.openflexo.connie.expr.parser.node.AOrExprExpr2;
import org.openflexo.connie.expr.parser.node.AParameteredTypeReference;
import org.openflexo.connie.expr.parser.node.APiConstant;
import org.openflexo.connie.expr.parser.node.APowerExprExpr3;
import org.openflexo.connie.expr.parser.node.APreciseNumberNumber;
import org.openflexo.connie.expr.parser.node.AScientificNotationNumberNumber;
import org.openflexo.connie.expr.parser.node.ASinFuncFunction;
import org.openflexo.connie.expr.parser.node.ASqrtFuncFunction;
import org.openflexo.connie.expr.parser.node.AStringValueTerm;
import org.openflexo.connie.expr.parser.node.ASubExprExpr2;
import org.openflexo.connie.expr.parser.node.ATailTypeReferencePath;
import org.openflexo.connie.expr.parser.node.ATanFuncFunction;
import org.openflexo.connie.expr.parser.node.ATermExpr3;
import org.openflexo.connie.expr.parser.node.ATrueConstant;
import org.openflexo.connie.expr.parser.node.ATypeReferenceAdditionalArg;
import org.openflexo.connie.expr.parser.node.ATypeReferenceArgList;
import org.openflexo.connie.expr.parser.node.Node;
import org.openflexo.connie.expr.parser.node.PBinding;
import org.openflexo.connie.expr.parser.node.PTypeReference;
import org.openflexo.connie.expr.parser.node.PTypeReferenceAdditionalArg;
import org.openflexo.connie.expr.parser.node.PTypeReferenceArgList;
import org.openflexo.connie.expr.parser.node.PTypeReferencePath;
import org.openflexo.connie.expr.parser.node.TCharsValue;
import org.openflexo.connie.expr.parser.node.TDecimalNumber;
import org.openflexo.connie.expr.parser.node.TPreciseNumber;
import org.openflexo.connie.expr.parser.node.TScientificNotationNumber;
import org.openflexo.connie.expr.parser.node.TStringValue;

/**
 * This class implements the semantics analyzer for a parsed expression.<br>
 * Its main purpose is to build a syntax tree with AnTAR expression model from a parsed AST.
 * 
 * @author sylvain
 * 
 */
class ExpressionFactory extends DepthFirstAdapter {

	private final Map<Node, Expression> expressionNodes;
	private Node topLevel = null;

	private Bindable bindable;

	public ExpressionFactory(Bindable aBindable) {
		expressionNodes = new Hashtable<>();
		this.bindable = aBindable;
	}

	public Bindable getBindable() {
		return bindable;
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
	}

	protected Expression getExpression(Node n) {
		if (n != null) {
			Expression returned = expressionNodes.get(n);
			if (returned == null) {
				System.out.println("No expression registered for " + n + " of  " + n.getClass());
			}
			return returned;
		}
		return null;
	}

	private BindingValue makeBinding(PBinding node) {
		BindingValue returned = BindingPathFactory.makeBindingPath(node, this);
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

	// Following methods manage following grammar fragment
	/*expr =
	  {expr2} expr2 |
	  {cond_expr} [condition]:expr if_token [then]:expr2 else_token [else]:expr2 |
	  {eq_expr} [left]:expr eq [right]:expr2 |
	  {eq2_expr} [left]:expr eq2 [right]:expr2 |
	  {neq_expr} [left]:expr neq [right]:expr2 |
	  {lt_expr} [left]:expr lt [right]:expr2 |
	  {gt_expr} [left]:expr gt [right]:expr2 |
	  {lte_expr} [left]:expr lte [right]:expr2 |
	  {gte_expr} [left]:expr gte [right]:expr2 ;*/

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
		registerExpressionNode(node, new DELConditionalExpression(getExpression(node.getCondition()), getExpression(node.getThen()),
				getExpression(node.getElse())));
	}

	@Override
	public void outAEqExprExpr(AEqExprExpr node) {
		super.outAEqExprExpr(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELBooleanBinaryOperator.EQUALS, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAEq2ExprExpr(AEq2ExprExpr node) {
		super.outAEq2ExprExpr(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELBooleanBinaryOperator.EQUALS, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outANeqExprExpr(ANeqExprExpr node) {
		super.outANeqExprExpr(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELBooleanBinaryOperator.NOT_EQUALS, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outALtExprExpr(ALtExprExpr node) {
		super.outALtExprExpr(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELBooleanBinaryOperator.LESS_THAN, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outALteExprExpr(ALteExprExpr node) {
		super.outALteExprExpr(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELBooleanBinaryOperator.LESS_THAN_OR_EQUALS,
				getExpression(node.getLeft()), getExpression(node.getRight())));
	}

	@Override
	public void outAGtExprExpr(AGtExprExpr node) {
		super.outAGtExprExpr(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELBooleanBinaryOperator.GREATER_THAN, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAGteExprExpr(AGteExprExpr node) {
		super.outAGteExprExpr(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELBooleanBinaryOperator.GREATER_THAN_OR_EQUALS,
				getExpression(node.getLeft()), getExpression(node.getRight())));
	}

	// Following methods manage following grammar fragment
	/* expr2 =
	  {expr3} expr3 |
	  {or_expr} [left]:expr2 or [right]:expr3 |
	  {or2_expr} [left]:expr2 or2 [right]:expr3 |
	  {add_expr} [left]:expr2 plus [right]:expr3 |
	  {sub_expr} [left]:expr2 minus [right]:expr3; */

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
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELBooleanBinaryOperator.OR, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAOr2ExprExpr2(AOr2ExprExpr2 node) {
		super.outAOr2ExprExpr2(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELBooleanBinaryOperator.OR, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAAddExprExpr2(AAddExprExpr2 node) {
		super.outAAddExprExpr2(node);
		// System.out.println("OUT add with " + node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELArithmeticBinaryOperator.ADDITION, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outASubExprExpr2(ASubExprExpr2 node) {
		super.outASubExprExpr2(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELArithmeticBinaryOperator.SUBSTRACTION,
				getExpression(node.getLeft()), getExpression(node.getRight())));
	}

	// Following methods manage following grammar fragment
	/* expr3 =
		  {term} term |
		  {and_expr} [left]:expr3 and [right]:term |
		  {and2_expr} [left]:expr3 and2 [right]:term |
		  {mult_expr} [left]:expr3 mult [right]:term |
		  {div_expr} [left]:expr3 div [right]:term |
		  {mod_expr} [left]:expr3 mod [right]:term |
	      {power_expr} [left]:expr3 power [right]:term |
		  {not_expr} not term; */

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
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELBooleanBinaryOperator.AND, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAAnd2ExprExpr3(AAnd2ExprExpr3 node) {
		super.outAAnd2ExprExpr3(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELBooleanBinaryOperator.AND, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAMultExprExpr3(AMultExprExpr3 node) {
		super.outAMultExprExpr3(node);
		// System.out.println("OUT mult with " + node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELArithmeticBinaryOperator.MULTIPLICATION,
				getExpression(node.getLeft()), getExpression(node.getRight())));
	}

	@Override
	public void outADivExprExpr3(ADivExprExpr3 node) {
		super.outADivExprExpr3(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELArithmeticBinaryOperator.DIVISION, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAModExprExpr3(AModExprExpr3 node) {
		super.outAModExprExpr3(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELArithmeticBinaryOperator.MOD, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outAPowerExprExpr3(APowerExprExpr3 node) {
		super.outAPowerExprExpr3(node);
		registerExpressionNode(node, new DELBinaryOperatorExpression(DELArithmeticBinaryOperator.POWER, getExpression(node.getLeft()),
				getExpression(node.getRight())));
	}

	@Override
	public void outANotExprExpr3(ANotExprExpr3 node) {
		super.outANotExprExpr3(node);
		registerExpressionNode(node, new DELUnaryOperatorExpression(DELBooleanUnaryOperator.NOT, getExpression(node.getTerm())));
	}

	// Following methods manage following grammar fragment
	/* function =
	  {cos_func} cos l_par expr2 r_par |
	  {acos_func} acos l_par expr2 r_par |
	  {sin_func} sin l_par expr2 r_par |
	  {asin_func} asin l_par expr2 r_par |
	  {tan_func} tan l_par expr2 r_par |
	  {atan_func} atan l_par expr2 r_par |
	  {exp_func} exp l_par expr2 r_par |
	  {log_func} log l_par expr2 r_par |
	  {sqrt_func} sqrt l_par expr2 r_par; */

	@Override
	public void outACosFuncFunction(ACosFuncFunction node) {
		super.outACosFuncFunction(node);
		registerExpressionNode(node, new DELUnaryOperatorExpression(DELArithmeticUnaryOperator.COS, getExpression(node.getExpr2())));
	}

	@Override
	public void outAAcosFuncFunction(AAcosFuncFunction node) {
		super.outAAcosFuncFunction(node);
		registerExpressionNode(node, new DELUnaryOperatorExpression(DELArithmeticUnaryOperator.ACOS, getExpression(node.getExpr2())));
	}

	@Override
	public void outASinFuncFunction(ASinFuncFunction node) {
		super.outASinFuncFunction(node);
		registerExpressionNode(node, new DELUnaryOperatorExpression(DELArithmeticUnaryOperator.SIN, getExpression(node.getExpr2())));
	}

	@Override
	public void outAAsinFuncFunction(AAsinFuncFunction node) {
		super.outAAsinFuncFunction(node);
		registerExpressionNode(node, new DELUnaryOperatorExpression(DELArithmeticUnaryOperator.ASIN, getExpression(node.getExpr2())));
	}

	@Override
	public void outATanFuncFunction(ATanFuncFunction node) {
		super.outATanFuncFunction(node);
		registerExpressionNode(node, new DELUnaryOperatorExpression(DELArithmeticUnaryOperator.TAN, getExpression(node.getExpr2())));
	}

	@Override
	public void outAAtanFuncFunction(AAtanFuncFunction node) {
		super.outAAtanFuncFunction(node);
		registerExpressionNode(node, new DELUnaryOperatorExpression(DELArithmeticUnaryOperator.ATAN, getExpression(node.getExpr2())));
	}

	@Override
	public void outAExpFuncFunction(AExpFuncFunction node) {
		super.outAExpFuncFunction(node);
		registerExpressionNode(node, new DELUnaryOperatorExpression(DELArithmeticUnaryOperator.EXP, getExpression(node.getExpr2())));
	}

	@Override
	public void outALogFuncFunction(ALogFuncFunction node) {
		super.outALogFuncFunction(node);
		registerExpressionNode(node, new DELUnaryOperatorExpression(DELArithmeticUnaryOperator.LOG, getExpression(node.getExpr2())));
	}

	@Override
	public void outASqrtFuncFunction(ASqrtFuncFunction node) {
		super.outASqrtFuncFunction(node);
		registerExpressionNode(node, new DELUnaryOperatorExpression(DELArithmeticUnaryOperator.SQRT, getExpression(node.getExpr2())));
	}

	// Following methods manage following grammar fragment
	/* constant = 
		  {true} true |
		  {false} false |
		  {null} null |
		  {this} this |
		  {pi} pi;*/

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

	// Following methods manage following grammar fragment
	/* number =
		  {decimal_number} decimal_number |
		  {precise_number} precise_number |
		  {scientific_notation_number} scientific_notation_number |
		  {constant} constant; */

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

	// Following methods manage following grammar fragment
	/* term =
		  {negative} minus term |
		  {number} number |
		  {string_value} string_value |
		  {chars_value} chars_value |
		  {function} function |
		  {binding} binding |
		  {expr} l_par expr r_par |
		  {cast} l_par type_reference r_par term;*/

	@Override
	public void outACastTerm(ACastTerm node) {
		super.outACastTerm(node);
		registerExpressionNode(node, new DELCastExpression(makeTypeReference(node.getTypeReference()), getExpression(node.getTerm())));
	}

	@Override
	public void outANegativeTerm(ANegativeTerm node) {
		super.outANegativeTerm(node);
		registerExpressionNode(node, new DELUnaryOperatorExpression(DELArithmeticUnaryOperator.UNARY_MINUS, getExpression(node.getTerm())));
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
	}

}
