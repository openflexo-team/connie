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

import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.NewInstancePathElement;
import org.openflexo.connie.binding.SimpleMethodPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.binding.StaticMethodPathElement;
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
	SimplePathElement<?> makeSimplePathElement(IBindingPathElement parent, String propertyName);

	/**
	 * Build a new {@link SimpleMethodPathElement} with supplied parent, function name and arguments
	 * 
	 * @param parent
	 *            The parent path element
	 * @param functionName
	 *            The name of the function to retrieve
	 * @param args
	 *            The arguments of the function path element
	 * @return
	 */
	SimpleMethodPathElement<?> makeSimpleMethodPathElement(IBindingPathElement parent, String functionName, List<DataBinding<?>> args);

	/**
	 * Build a new static {@link StaticMethodPathElement} with supplied type, function name and arguments
	 * 
	 * @param type
	 *            The type of which static function is defined
	 * @param functionName
	 *            The name of the function to retrieve
	 * @param args
	 *            The arguments of the function path element
	 * @return
	 */
	StaticMethodPathElement<?> makeStaticMethodPathElement(Type type, String functionName, List<DataBinding<?>> args);

	/**
	 * Build a new {@link NewInstancePathElement} (new instance creation) with supplied type, parent, function name and arguments
	 * 
	 * @param type
	 *            The type to instantiate
	 * @param parent
	 *            The parent path element: might be null or specifiates a inner access
	 * @param functionName
	 *            The name of the function to retrieve, might be null when type is sufficient
	 * @param args
	 *            The arguments of the function path element
	 * @return
	 */
	NewInstancePathElement<?> makeNewInstancePathElement(Type type, IBindingPathElement parent, String functionName,
			List<DataBinding<?>> args);

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
