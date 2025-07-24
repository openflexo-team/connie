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
import java.util.List;

import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.del.expr.DELPrettyPrinter;
import org.openflexo.connie.expr.BindingPath;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.UnresolvedBindingVariable;
import org.openflexo.connie.expr.parser.node.AAdditionalArg;
import org.openflexo.connie.expr.parser.node.ACall;
import org.openflexo.connie.expr.parser.node.ACallBinding;
import org.openflexo.connie.expr.parser.node.AIdentifierBinding;
import org.openflexo.connie.expr.parser.node.ANonEmptyListArgList;
import org.openflexo.connie.expr.parser.node.ATail1Binding;
import org.openflexo.connie.expr.parser.node.ATail2Binding;
import org.openflexo.connie.expr.parser.node.PAdditionalArg;
import org.openflexo.connie.expr.parser.node.PArgList;
import org.openflexo.connie.expr.parser.node.PBinding;
import org.openflexo.connie.expr.parser.node.PCall;
import org.openflexo.connie.expr.parser.node.TIdentifier;

/**
 * This class implements the semantics analyzer for a parsed binding path<br>
 * Its main purpose is to structurally build a {@link BindingPath} from a parsed AST.<br>
 * No semantics nor type checking is performed at this stage
 * 
 * @author sylvain
 * 
 */
class BindingPathFactory extends ExpressionFactory {

	private BindingVariable bindingVariable = null;
	private final List<BindingPathElement> bindingPathElements;

	private final ExpressionFactory expressionFactory;

	public static BindingPath makeBindingPath(PBinding node, ExpressionFactory expressionFactory) {

		// ASTDebugger.debug(node);
		BindingPathFactory bindingPathFactory = new BindingPathFactory(node, expressionFactory);
		return new BindingPath(bindingPathFactory.bindingVariable, bindingPathFactory.bindingPathElements,
				bindingPathFactory.getBindable(), DELPrettyPrinter.getInstance());
	}

	private BindingPathFactory(PBinding node, ExpressionFactory expressionFactory) {
		super(expressionFactory.getBindable());
		this.expressionFactory = expressionFactory;
		bindingPathElements = new ArrayList<>();
		node.apply(this);
		append(node);
	}

	public ExpressionFactory getExpressionFactory() {
		return expressionFactory;
	}

	public BindingVariable getBindingVariable() {
		return bindingVariable;
	}

	public List<BindingPathElement> getPath() {
		return bindingPathElements;
	};

	/* call = 
	  identifier arg_list ;
	
	 arg_list = 
	  l_par expr [additional_args]:additional_arg* r_par;
	
	 additional_arg = 
	  comma expr;
	
	 binding = 
	  {identifier} identifier |
	  {call} call |
	  {tail} identifier dot binding;*/

	private IBindingPathElement makeNormalBindingPathElement(TIdentifier identifier) {
		String identifierAsString = identifier.getText();
		if (bindingVariable == null) {
			if (getBindable().getBindingModel() != null) {
				bindingVariable = getBindable().getBindingModel().bindingVariableNamed(identifierAsString);
			}
			if (bindingVariable == null) {
				// Unresolved
				bindingVariable = new UnresolvedBindingVariable(identifierAsString);
			}
			// System.out.println(" > BV: " + bindingVariable);
			return bindingVariable;
		}
		else if (bindingPathElements.size() == 0) {
			SimplePathElement<?> pathElement = getBindable().getBindingFactory().makeSimplePathElement(bindingVariable, identifierAsString,
					getBindable());
			bindingPathElements.add(pathElement);
			// System.out.println(" > PE: " + pathElement);
			return pathElement;
		}
		else {
			SimplePathElement<?> pathElement = getBindable().getBindingFactory()
					.makeSimplePathElement(bindingPathElements.get(bindingPathElements.size() - 1), identifierAsString, getBindable());
			bindingPathElements.add(pathElement);
			// System.out.println(" > PE: " + pathElement);
			return pathElement;
		}
	}

	private DataBinding<?> makeArgument(Expression exp) {
		DataBinding<?> returned = new DataBinding<>(getBindable(), Object.class, BindingDefinitionType.GET);
		returned.setExpression(exp);
		return returned;
	}

	private List<DataBinding<?>> makeArguments(PCall node) {
		if (node instanceof ACall) {
			PArgList argList = ((ACall) node).getArgList();
			List<DataBinding<?>> args = new ArrayList<>();
			if (argList instanceof ANonEmptyListArgList) {
				args.add(makeArgument(getExpression(((ANonEmptyListArgList) argList).getExpr())));
				for (PAdditionalArg aa : ((ANonEmptyListArgList) argList).getAdditionalArgs()) {
					AAdditionalArg additionalArg = (AAdditionalArg) aa;
					args.add(makeArgument(getExpression(additionalArg.getExpr())));
				}
			}
			return args;
		}
		return null;
	}

	private IBindingPathElement makeMethodCallBindingPathElement(PCall node) {

		if (node instanceof ACall) {
			List<DataBinding<?>> args = makeArguments(node);

			IBindingPathElement parent = null;
			if (bindingPathElements.size() == 0) {
				parent = bindingVariable;
			}
			else {
				parent = bindingPathElements.get(bindingPathElements.size() - 1);
			}

			FunctionPathElement<?> pathElement = null;
			pathElement = getBindable().getBindingFactory().makeSimpleMethodPathElement(parent, ((ACall) node).getIdentifier().getText(),
					args, getBindable());
			bindingPathElements.add(pathElement);
			return pathElement;
		}
		return null;
	}

	private void append(PBinding node) {
		if (node instanceof AIdentifierBinding) {
			makeNormalBindingPathElement(((AIdentifierBinding) node).getIdentifier());
		}
		else if (node instanceof ACallBinding) {
			makeMethodCallBindingPathElement(((ACallBinding) node).getCall());
		}
		else if (node instanceof ATail1Binding) {
			makeNormalBindingPathElement(((ATail1Binding) node).getIdentifier());
			append(((ATail1Binding) node).getBinding());
		}
		else if (node instanceof ATail2Binding) {
			makeMethodCallBindingPathElement(((ATail2Binding) node).getCall());
			append(((ATail2Binding) node).getBinding());
		}
	}

}
