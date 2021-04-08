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

import java.util.ArrayList;
import java.util.List;

import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.BindingValue.AbstractBindingPathElement;
import org.openflexo.connie.expr.BindingValue.MethodCallBindingPathElement;
import org.openflexo.connie.expr.BindingValue.NormalBindingPathElement;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.java.parser.analysis.DepthFirstAdapter;
import org.openflexo.connie.java.parser.node.ACompositeIdent;
import org.openflexo.connie.java.parser.node.AIdentifierPrefix;
import org.openflexo.connie.java.parser.node.AIdentifierPrimary;
import org.openflexo.connie.java.parser.node.AManyArgumentList;
import org.openflexo.connie.java.parser.node.AMethodPrimaryNoId;
import org.openflexo.connie.java.parser.node.AOneArgumentList;
import org.openflexo.connie.java.parser.node.APrimaryMethodInvocation;
import org.openflexo.connie.java.parser.node.Node;
import org.openflexo.connie.java.parser.node.PArgumentList;
import org.openflexo.connie.java.parser.node.PExpression;
import org.openflexo.toolbox.StringUtils;

/**
 * This class implements the semantics analyzer for a parsed {@link BindingValue}<br>
 * Its main purpose is to structurally build a binding from a parsed AST (an instance of PPrimary).<br>
 * No semantics nor type checking is performed at this stage
 * 
 * @author sylvain
 * 
 */
class BindingValueAnalyzer extends DepthFirstAdapter {

	private final ExpressionSemanticsAnalyzer expressionAnalyzer;
	private final List<AbstractBindingPathElement> path;
	private final Node rootNode;

	/**
	 * This flag is used to escape binding processing that may happen in call args handling
	 */
	// private boolean weAreDealingWithTheRightBinding = true;

	public static BindingValue makeBindingValue(Node node, ExpressionSemanticsAnalyzer expressionAnalyzer) {

		/*BindingValueAnalyzer bsa = new BindingValueAnalyzer(node);
		node.apply(bsa);
		
		// System.out.println("Make binding value with bsa as " + bsa.getPath());
		
		BindingValue returned = new BindingValue(bsa.getPath());
		// System.out.println("Made binding as " + bsa.getPath());
		
		return returned;*/

		return new BindingValue(makeBindingPath(node, expressionAnalyzer));
	}

	private static List<AbstractBindingPathElement> makeBindingPath(Node node, ExpressionSemanticsAnalyzer expressionAnalyzer) {

		BindingValueAnalyzer bsa = new BindingValueAnalyzer(node, expressionAnalyzer);
		node.apply(bsa);

		return bsa.getPath();
	}

	private BindingValueAnalyzer(Node node, ExpressionSemanticsAnalyzer expressionAnalyzer) {
		this.expressionAnalyzer = expressionAnalyzer;
		this.rootNode = node;
		path = new ArrayList<>();
		// System.out.println(">>>> node=" + node + " of " + node.getClass());
	}

	private List<BindingValue.AbstractBindingPathElement> getPath() {
		return path;
	};

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
	public void inAPrimaryNoIdPrimary(APrimaryNoIdPrimary node) {
		// TODO Auto-generated method stub
		super.inAPrimaryNoIdPrimary(node);
		depth++;
	}
	
	@Override
	public void outAPrimaryNoIdPrimary(APrimaryNoIdPrimary node) {
		// TODO Auto-generated method stub
		super.outAPrimaryNoIdPrimary(node);
		depth--;
	}*/

	@Override
	public void inAIdentifierPrimary(AIdentifierPrimary node) {
		super.inAIdentifierPrimary(node);
		depth++;
	}

	@Override
	public void outAIdentifierPrimary(AIdentifierPrimary node) {
		super.outAIdentifierPrimary(node);
		depth--;
	}

	@Override
	public void inAMethodPrimaryNoId(AMethodPrimaryNoId node) {
		super.inAMethodPrimaryNoId(node);
		depth++;
	}

	@Override
	public void outAMethodPrimaryNoId(AMethodPrimaryNoId node) {
		super.outAMethodPrimaryNoId(node);
		depth--;
	}

	@Override
	public void outAIdentifierPrefix(AIdentifierPrefix node) {
		// TODO Auto-generated method stub
		super.outAIdentifierPrefix(node);
		// System.out.println("Tiens on tombe sur " + node.getLidentifier().getText());
		if (weAreDealingWithTheRightBinding()) {
			NormalBindingPathElement pathElement = new NormalBindingPathElement(node.getLidentifier().getText());
			path.add(pathElement);
		}
	}

	@Override
	public void outACompositeIdent(ACompositeIdent node) {
		super.outACompositeIdent(node);
		// System.out.println("Finalement on tombe sur l'identifiant " + node.getIdentifier().getText());
		if (weAreDealingWithTheRightBinding()) {
			NormalBindingPathElement pathElement = new NormalBindingPathElement(node.getIdentifier().getText());
			path.add(pathElement);
		}
	}

	@Override
	public void outAPrimaryMethodInvocation(APrimaryMethodInvocation node) {
		super.outAPrimaryMethodInvocation(node);
		if (weAreDealingWithTheRightBinding()) {
			List<AbstractBindingPathElement> bindingPath = makeBindingPath(node.getPrimary(), expressionAnalyzer);

			for (int i = 0; i < bindingPath.size() - 1; i++) {
				path.add(bindingPath.get(i));
			}
			String identifier = ((NormalBindingPathElement) bindingPath.get(bindingPath.size() - 1)).property;

			List<Expression> args = new ArrayList<>();
			PArgumentList argumentList = node.getArgumentList();
			if (argumentList == null) {
				// No argument
			}
			else if (argumentList instanceof AOneArgumentList) {
				// One argument
				PExpression pExpression = ((AOneArgumentList) argumentList).getExpression();
				// System.out.println("Tiens je cherche pour " + pExpression + " of " + pExpression.getClass().getSimpleName());
				// System.out.println("Et je tombe sur: " + expressionAnalyzer.getExpression(pExpression) + " of "
				// + expressionAnalyzer.getExpression(pExpression).getClass().getSimpleName());
				args.add(expressionAnalyzer.getExpression(pExpression));
			}
			else if (argumentList instanceof AManyArgumentList) {
				List<PExpression> arguments = makeArguments((AManyArgumentList) argumentList);
				for (PExpression pExpression : arguments) {
					// System.out.println("Tiens je cherche pour " + pExpression + " of " + pExpression.getClass().getSimpleName());
					// System.out.println("Et je tombe sur: " + expressionAnalyzer.getExpression(pExpression) + " of "
					// + expressionAnalyzer.getExpression(pExpression).getClass().getSimpleName());
					args.add(expressionAnalyzer.getExpression(pExpression));
				}
			}

			MethodCallBindingPathElement returned = new MethodCallBindingPathElement(identifier, args);
			path.add(returned);

		}
	}

	private List<PExpression> makeArguments(AManyArgumentList args) {
		List<PExpression> returned = new ArrayList<>();
		buildArguments(args, returned);
		return returned;
	}

	private void buildArguments(PArgumentList argList, List<PExpression> expressions) {
		if (argList instanceof AOneArgumentList) {
			expressions.add(0, ((AOneArgumentList) argList).getExpression());
		}
		else if (argList instanceof AManyArgumentList) {
			expressions.add(0, ((AManyArgumentList) argList).getExpression());
			buildArguments(((AManyArgumentList) argList).getArgumentList(), expressions);
		}
	}

	/*@Override
	public void outAMethodPrimaryNoId(AMethodPrimaryNoId node) {
		// TODO Auto-generated method stub
		super.outAMethodPrimaryNoId(node);
	}*/

	/*protected BindingValue.NormalBindingPathElement makeNormalBindingPathElement(TIdentifier identifier) {
		BindingValue.NormalBindingPathElement returned = new BindingValue.NormalBindingPathElement(identifier.getText());
		if (weAreDealingWithTheRightBinding()) {
			path.add(0, returned);
		}
		return returned;
	}
	
	public BindingValue.MethodCallBindingPathElement makeMethodCallBindingPathElement(ACall node) {
		PArgList argList = node.getArgList();
		List<Expression> args = new ArrayList<>();
		if (argList instanceof ANonEmptyListArgList) {
			args.add(getExpression(((ANonEmptyListArgList) argList).getExpr()));
			for (PAdditionalArg aa : ((ANonEmptyListArgList) argList).getAdditionalArgs()) {
				AAdditionalArg additionalArg = (AAdditionalArg) aa;
				args.add(getExpression(additionalArg.getExpr()));
			}
		}
		BindingValue.MethodCallBindingPathElement returned = new BindingValue.MethodCallBindingPathElement(node.getIdentifier().getText(),
				args);
		if (weAreDealingWithTheRightBinding()) {
			path.add(0, returned);
		}
		return returned;
	}
	
	@Override
	public void outAIdentifierBinding(AIdentifierBinding node) {
		super.outAIdentifierBinding(node);
		if (weAreDealingWithTheRightBinding()) {
			makeNormalBindingPathElement(node.getIdentifier());
		}
	}
	
	@Override
	public void outACallBinding(ACallBinding node) {
		super.outACallBinding(node);
		if (weAreDealingWithTheRightBinding()) {
			makeMethodCallBindingPathElement((ACall) node.getCall());
		}
	}
	
	@Override
	public void outATail1Binding(ATail1Binding node) {
		super.outATail1Binding(node);
		if (weAreDealingWithTheRightBinding()) {
			makeNormalBindingPathElement(node.getIdentifier());
		}
	}
	
	@Override
	public void outATail2Binding(ATail2Binding node) {
		super.outATail2Binding(node);
		if (weAreDealingWithTheRightBinding()) {
			makeMethodCallBindingPathElement((ACall) node.getCall());
		}
	}*/

	private int depth = -1;

	/*@Override
	public void inABindingTerm(ABindingTerm node) {
		super.inABindingTerm(node);
		// System.out.println("IN binding " + node);
		// weAreDealingWithTheRightBinding = false;
		depth++;
	}
	
	@Override
	public void outABindingTerm(ABindingTerm node) {
		super.outABindingTerm(node);
		// System.out.println("OUT binding " + node);
		// weAreDealingWithTheRightBinding = true;
		depth--;
	}*/

	private boolean weAreDealingWithTheRightBinding() {
		return depth == 0;
	}
}
