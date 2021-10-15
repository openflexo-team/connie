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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.UnresolvedBindingVariable;
import org.openflexo.connie.java.expr.JavaPrettyPrinter;
import org.openflexo.connie.java.parser.node.ABasicJavaInstanceCreationInvokation;
import org.openflexo.connie.java.parser.node.AClassMethodMethodInvocation;
import org.openflexo.connie.java.parser.node.ACompositeIdent;
import org.openflexo.connie.java.parser.node.AFieldLeftHandSide;
import org.openflexo.connie.java.parser.node.AFieldPrimaryNoId;
import org.openflexo.connie.java.parser.node.AIdentifierLeftHandSide;
import org.openflexo.connie.java.parser.node.AIdentifierPrefix;
import org.openflexo.connie.java.parser.node.AIdentifierPrimary;
import org.openflexo.connie.java.parser.node.AInnerJavaInstanceCreationInvokation;
import org.openflexo.connie.java.parser.node.AJavaInstanceCreationPrimaryNoId;
import org.openflexo.connie.java.parser.node.AManyArgumentList;
import org.openflexo.connie.java.parser.node.AMethodPrimaryNoId;
import org.openflexo.connie.java.parser.node.AOneArgumentList;
import org.openflexo.connie.java.parser.node.APrimaryFieldAccess;
import org.openflexo.connie.java.parser.node.APrimaryMethodInvocation;
import org.openflexo.connie.java.parser.node.APrimaryNoIdPrimary;
import org.openflexo.connie.java.parser.node.AReferenceSuperFieldAccess;
import org.openflexo.connie.java.parser.node.ASuperFieldAccess;
import org.openflexo.connie.java.parser.node.ASuperMethodInvocation;
import org.openflexo.connie.java.parser.node.Node;
import org.openflexo.connie.java.parser.node.PArgumentList;
import org.openflexo.connie.java.parser.node.PCompositeIdent;
import org.openflexo.connie.java.parser.node.PExpression;
import org.openflexo.connie.java.parser.node.PFieldAccess;
import org.openflexo.connie.java.parser.node.PIdentifierPrefix;
import org.openflexo.connie.java.parser.node.PJavaInstanceCreationInvokation;
import org.openflexo.connie.java.parser.node.PLeftHandSide;
import org.openflexo.connie.java.parser.node.PMethodInvocation;
import org.openflexo.connie.java.parser.node.PPrimary;
import org.openflexo.connie.java.parser.node.PPrimaryNoId;
import org.openflexo.connie.java.parser.node.TKwSuper;
import org.openflexo.connie.java.parser.node.TLidentifier;

/**
 * This class implements the semantics analyzer for a parsed {@link BindingValue}<br>
 * Its main purpose is to structurally build a binding from a parsed AST<br>
 * 
 * The goal is here to linearize the AST to obtain a chain of {@link BindingPathElement}
 * 
 * @author sylvain
 * 
 */
public class BindingPathFactory {

	private final ExpressionFactory expressionFactory;

	private BindingVariable bindingVariable;
	private List<BindingPathElement> bindingPathElements;

	private final Node rootNode;

	public static BindingValue makeBindingPath(Node node, ExpressionFactory expressionFactory) {

		// ASTDebugger.debug(node);
		BindingPathFactory bindingPathFactory = new BindingPathFactory(node, expressionFactory);
		bindingPathFactory.explore();
		return new BindingValue(bindingPathFactory.bindingVariable, bindingPathFactory.bindingPathElements,
				bindingPathFactory.getBindable(), JavaPrettyPrinter.getInstance());
	}

	private BindingPathFactory(Node node, ExpressionFactory expressionFactory) {
		this.expressionFactory = expressionFactory;
		this.rootNode = node;
		bindingPathElements = new ArrayList<>();
	}

	public Bindable getBindable() {
		return expressionFactory.getBindable();
	}

	private void explore() {

		// System.out.println("Analyzing path " + rootNode);
		// ASTDebugger.debug(rootNode);

		if (rootNode instanceof PPrimaryNoId) {
			appendBindingPath((PPrimaryNoId) rootNode);
		}
		if (rootNode instanceof PPrimary) {
			appendBindingPath((PPrimary) rootNode);
		}
		if (rootNode instanceof PLeftHandSide) {
			appendBindingPath((PLeftHandSide) rootNode);
		}
	}

	private IBindingPathElement popBindingPath() {
		if (bindingPathElements.size() == 0) {
			IBindingPathElement returned = bindingVariable;
			bindingVariable = null;
			return returned;
		}
		return bindingPathElements.remove(bindingPathElements.size() - 1);
	}

	private void appendBindingPath(PPrimaryNoId node) {
		if (node instanceof AFieldPrimaryNoId) {
			appendBindingPath(((AFieldPrimaryNoId) node).getFieldAccess());
		}
		else if (node instanceof AMethodPrimaryNoId) {
			appendBindingPath(((AMethodPrimaryNoId) node).getMethodInvocation());
		}
		else if (node instanceof AJavaInstanceCreationPrimaryNoId) {
			appendBindingPath(((AJavaInstanceCreationPrimaryNoId) node).getJavaInstanceCreationInvokation());
		}
	}

	private void appendBindingPath(PFieldAccess node) {
		if (node instanceof APrimaryFieldAccess) {
			appendBindingPath(((APrimaryFieldAccess) node).getPrimaryNoId());
			appendBindingPath(((APrimaryFieldAccess) node).getLidentifier());
		}
		else if (node instanceof AReferenceSuperFieldAccess) {
			appendBindingPath(((AReferenceSuperFieldAccess) node).getIdentifier1());
			appendBindingPath(((AReferenceSuperFieldAccess) node).getKwSuper());
			appendBindingPath(((AReferenceSuperFieldAccess) node).getIdentifier2());
		}
		else if (node instanceof ASuperFieldAccess) {
			appendBindingPath(((ASuperFieldAccess) node).getKwSuper());
			appendBindingPath(((ASuperFieldAccess) node).getLidentifier());
		}
	}

	private void appendBindingPath(PPrimary node) {
		if (node instanceof AIdentifierPrimary) {
			appendBindingPath(((AIdentifierPrimary) node).getCompositeIdent());
		}
		else if (node instanceof APrimaryNoIdPrimary) {
			appendBindingPath(((APrimaryNoIdPrimary) node).getPrimaryNoId());
		}
	}

	private void appendBindingPath(PLeftHandSide node) {
		if (node instanceof AFieldLeftHandSide) {
			appendBindingPath(((AFieldLeftHandSide) node).getFieldAccess());
		}
		else if (node instanceof AIdentifierLeftHandSide) {
			appendBindingPath(((AIdentifierLeftHandSide) node).getCompositeIdent());
		}
	}

	/*private void appendBindingPath(PStatementExpression node) {
		if (node instanceof AMethodInvocationStatementExpression) {
			appendBindingPath(((AMethodInvocationStatementExpression) node).getMethodInvocation());
		}
		else if (node instanceof ANewInstanceStatementExpression) {
			appendBindingPath(((ANewInstanceStatementExpression) node).getNewInstance());
		}
	}*/

	private void appendBindingPath(PMethodInvocation node) {
		if (node instanceof APrimaryMethodInvocation) {
			appendBindingPath(((APrimaryMethodInvocation) node).getPrimary());
			IBindingPathElement lastElement = popBindingPath();
			appendMethodInvocation((APrimaryMethodInvocation) node, lastElement);
		}
		else if (node instanceof ASuperMethodInvocation) {
			appendSuperMethodInvocation((ASuperMethodInvocation) node);
		}
		else if (node instanceof AClassMethodMethodInvocation) {
			appendClassMethodInvocation((AClassMethodMethodInvocation) node);
		}
	}

	private void appendBindingPath(PJavaInstanceCreationInvokation node) {
		if (node instanceof ABasicJavaInstanceCreationInvokation) {
			appendSimpleNewInstanceInvocation((ABasicJavaInstanceCreationInvokation) node);
		}
		else if (node instanceof AInnerJavaInstanceCreationInvokation) {
			appendInnerNewInstanceInvocation((AInnerJavaInstanceCreationInvokation) node);
		}
	}

	private void appendBindingPath(PCompositeIdent node) {
		if (node instanceof ACompositeIdent) {
			for (PIdentifierPrefix pIdentifierPrefix : ((ACompositeIdent) node).getPrefixes()) {
				appendBindingPath(pIdentifierPrefix);
			}
			appendBindingPath(((ACompositeIdent) node).getIdentifier());
		}
	}

	private void appendBindingPath(PIdentifierPrefix node) {
		if (node instanceof AIdentifierPrefix) {
			appendBindingPath(((AIdentifierPrefix) node).getLidentifier());
		}
	}

	private void appendBindingPath(TLidentifier node) {
		makeNormalBindingPathElement(node.getText());
	}

	private void appendBindingPath(TKwSuper node) {
		makeNormalBindingPathElement("super");
	}

	private void appendMethodInvocation(APrimaryMethodInvocation node, IBindingPathElement lastPathElement) {

		List<DataBinding<?>> args = makeArguments(node.getArgumentList());
		appendMethodInvocation(lastPathElement.getLabel(), args);
	}

	private void appendSuperMethodInvocation(ASuperMethodInvocation node) {
		List<DataBinding<?>> args = makeArguments(node.getArgumentList());
		appendMethodInvocation("super", args);
	}

	private void appendSimpleNewInstanceInvocation(ABasicJavaInstanceCreationInvokation node) {

		Type type = TypeFactory.makeType(node.getType(), expressionFactory);
		List<DataBinding<?>> args = makeArguments(node.getArgumentList());
		appendNewInstanceInvocation(type, null, args);
	}

	private void appendInnerNewInstanceInvocation(AInnerJavaInstanceCreationInvokation node) {

		appendBindingPath(node.getPrimary());

		Type type = TypeFactory.makeType(node.getType(), expressionFactory);
		List<DataBinding<?>> args = makeArguments(node.getArgumentList());
		appendNewInstanceInvocation(type, null, args);
	}

	private void appendClassMethodInvocation(AClassMethodMethodInvocation node) {
		Type type = TypeFactory.makeType(node.getType(), expressionFactory);
		List<DataBinding<?>> args = makeArguments(node.getArgumentList());
		appendClassInstanceInvocation(type, node.getLidentifier().getText(), args);
	}

	private IBindingPathElement makeNormalBindingPathElement(String identifier) {
		if (bindingVariable == null && bindingPathElements.size() == 0) {
			if (getBindable().getBindingModel() != null) {
				bindingVariable = getBindable().getBindingModel().bindingVariableNamed(identifier);
			}
			if (bindingVariable == null) {
				// Unresolved
				bindingVariable = new UnresolvedBindingVariable(identifier);
			}
			// System.out.println(" > BV: " + bindingVariable);
			return bindingVariable;
		}
		else if (bindingPathElements.size() == 0) {
			SimplePathElement pathElement = getBindable().getBindingFactory().makeSimplePathElement(bindingVariable, identifier);
			bindingPathElements.add(pathElement);
			// System.out.println(" > PE: " + pathElement);
			return pathElement;
		}
		else {
			SimplePathElement pathElement = getBindable().getBindingFactory()
					.makeSimplePathElement(bindingPathElements.get(bindingPathElements.size() - 1), identifier);
			bindingPathElements.add(pathElement);
			// System.out.println(" > PE: " + pathElement);
			return pathElement;
		}
	}

	private FunctionPathElement<?> appendMethodInvocation(String methodName, List<DataBinding<?>> args) {
		IBindingPathElement parent = null;
		if (bindingPathElements.size() == 0) {
			parent = bindingVariable;
		}
		else {
			parent = bindingPathElements.get(bindingPathElements.size() - 1);
		}

		FunctionPathElement<?> pathElement = null;
		pathElement = getBindable().getBindingFactory().makeSimpleMethodPathElement(parent, methodName, args);
		bindingPathElements.add(pathElement);

		return pathElement;
	}

	private FunctionPathElement<?> appendNewInstanceInvocation(Type type, String methodName, List<DataBinding<?>> args) {
		IBindingPathElement parent = null;
		if (bindingPathElements.size() == 0) {
			parent = bindingVariable;
		}
		else {
			parent = bindingPathElements.get(bindingPathElements.size() - 1);
		}

		FunctionPathElement<?> pathElement = null;
		pathElement = getBindable().getBindingFactory().makeNewInstancePathElement(type, parent, null, args);
		bindingPathElements.add(pathElement);

		return pathElement;
	}

	private FunctionPathElement<?> appendClassInstanceInvocation(Type type, String methodName, List<DataBinding<?>> args) {
		FunctionPathElement<?> pathElement = null;
		pathElement = getBindable().getBindingFactory().makeStaticMethodPathElement(type, methodName, args);
		bindingPathElements.add(pathElement);

		return pathElement;
	}

	private DataBinding<?> makeArgument(PExpression exp) {
		DataBinding<?> returned = new DataBinding<>(getBindable(), Object.class, BindingDefinitionType.GET);
		returned.setExpression(expressionFactory.getExpression(exp));
		return returned;
	}

	private List<DataBinding<?>> makeArguments(PArgumentList node) {
		List<DataBinding<?>> args = new ArrayList<>();
		appendArguments(node, args);
		return args;
	}

	private void appendArguments(PArgumentList node, List<DataBinding<?>> args) {
		if (node instanceof AOneArgumentList) {
			args.add(makeArgument(((AOneArgumentList) node).getExpression()));
		}
		if (node instanceof AManyArgumentList) {
			appendArguments(((AManyArgumentList) node).getArgumentList(), args);
			args.add(makeArgument(((AManyArgumentList) node).getExpression()));
		}
	}

}
