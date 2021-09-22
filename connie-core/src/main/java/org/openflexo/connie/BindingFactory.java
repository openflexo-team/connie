/**
 * 
 */
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

package org.openflexo.connie;

import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.connie.binding.AbstractConstructor;
import org.openflexo.connie.binding.Function;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.Expression;

/**
 * A factory associated to a given expression language and allowing to build expressions
 * 
 * @author sylvain
 *
 */
public interface BindingFactory {

	/**
	 * Parse supplied expressionAsString, build and return an {@link Expression} according to underlying expression language
	 * 
	 * @param expressionAsString
	 * @return
	 */
	Expression parseExpression(String expressionAsString, Bindable bindable) throws ParseException;

	/**
	 * Return the list of accessible {@link SimplePathElement} which are accessible from supplied parent {@link IBindingPathElement}
	 * 
	 * @param parent
	 * @return
	 */
	List<? extends SimplePathElement> getAccessibleSimplePathElements(IBindingPathElement parent);

	/**
	 * Return the list of accessible {@link FunctionPathElement} which are accessible from supplied parent {@link IBindingPathElement}
	 * 
	 * @param parent
	 * @return
	 */
	List<? extends FunctionPathElement<?>> getAccessibleFunctionPathElements(IBindingPathElement parent);

	/**
	 * Build a new {@link SimplePathElement} with supplied parent and property name
	 * 
	 * @param parent
	 * @param propertyName
	 * @return
	 */
	SimplePathElement makeSimplePathElement(IBindingPathElement parent, String propertyName);

	/**
	 * Build a new {@link FunctionPathElement} with supplied parent, function and arguments
	 * 
	 * @param father
	 * @param function
	 * @param args
	 * @return
	 */
	FunctionPathElement<?> makeFunctionPathElement(IBindingPathElement father, Function function, DataBinding<?> innerAccess,
			List<DataBinding<?>> args);

	/**
	 * Lookup and return {@link Function} denoted by functionName and list of arguments, for declaring Type
	 * 
	 * @param parentType
	 * @param functionName
	 * @param args
	 * @return
	 */
	Function retrieveFunction(Type declaringType, String functionName, List<DataBinding<?>> args);

	/**
	 * Lookup and return a valid constructor for declaring type, functionName, and a list of arguments
	 * 
	 * @param declaringType
	 *            Type of object beeing created by returned constructor
	 * @param functionName
	 *            name of constructor, might be null for anonymous-constructors languages (eg java)
	 * @param args
	 *            list of arguments
	 * @return a {@link AbstractConstructor} usable as a constructor for declaringType
	 */
	AbstractConstructor retrieveConstructor(Type declaringType, String functionName, List<DataBinding<?>> args);

	/**
	 * Lookup and return a valid constructor for declaring type, inner access, functionName, and a list of arguments
	 * 
	 * @param declaringType
	 *            Type of object beeing created by returned constructor
	 * @param innerAccess
	 *            Defines the context in which this new instance should be created (parent object for inner access)
	 * @param functionName
	 *            name of constructor, might be null for anonymous-constructors languages (eg java)
	 * @param args
	 *            list of arguments
	 * @return a {@link AbstractConstructor} usable as a constructor for declaringType
	 */
	AbstractConstructor retrieveConstructor(Type declaringType, DataBinding<?> innerAccess, String functionName, List<DataBinding<?>> args);

	/**
	 * Return Type for supplied object
	 * 
	 * @param object
	 * @return
	 */
	Type getTypeForObject(Object object);

	/**
	 * Make null expression (language dependant)
	 * 
	 * @return
	 */
	Constant<?> getNullExpression();
}
