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

package org.openflexo.connie.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Logger;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.toolbox.ToolBox;

public final class MethodDefinition extends Observable implements Function {

	private static final Logger LOGGER = Logger.getLogger(MethodDefinition.class.getPackage().getName());

	private final Type declaringType;
	private final Method method;
	private final ArrayList<Function.FunctionArgument> arguments;
	private static Map<Method, Map<Type, MethodDefinition>> cache = new HashMap<>();

	public static MethodDefinition getMethodDefinition(Type aDeclaringType, Method method) {

		Map<Type, MethodDefinition> mapForMethod = cache.get(method);
		if (mapForMethod == null) {
			mapForMethod = new HashMap<>();
			cache.put(method, mapForMethod);
		}

		MethodDefinition returned = mapForMethod.get(aDeclaringType);
		if (returned == null) {
			returned = new MethodDefinition(aDeclaringType, method);
			mapForMethod.put(aDeclaringType, returned);
		}
		return returned;
	}

	private MethodDefinition(Type aDeclaringType, Method method) {
		super();
		this.method = method;
		this.declaringType = aDeclaringType;
		arguments = new ArrayList<>();
		int i = 0;
		for (Type t : method.getGenericParameterTypes()) {
			String argName = "arg" + i;
			Type argType = TypeUtils.makeInstantiatedType(t, aDeclaringType);
			arguments.add(new DefaultFunctionArgument(this, argName, argType));
			i++;
		}
	}

	public Method getMethod() {
		return method;
	}

	public String getMethodName() {
		return method.getName();
	}

	private String _signatureNFQ;
	private String _signatureFQ;
	private String _parameterListAsStringFQ;
	private String _parameterListAsString;

	public String getSimplifiedSignature() {
		if (_signatureNFQ == null) {
			StringBuilder signature = new StringBuilder();
			signature.append(method.getName());
			signature.append("(");
			signature.append(getParameterListAsString(false));
			signature.append(")");
			_signatureNFQ = signature.toString();
		}
		return _signatureNFQ;
	}

	public String getSignature() {
		if (_signatureFQ == null) {
			// try {
			StringBuffer signature = new StringBuffer();
			signature.append(method.getName());
			signature.append("(");
			signature.append(getParameterListAsString(true));
			signature.append(")");
			_signatureFQ = signature.toString();
			/*}
			catch (InvalidKeyValuePropertyException e) {
				logger.warning("While computing getSignature() for "+method+" and "+declaringType+" message:"+e.getMessage());
				e.printStackTrace();
				return null;
			}*/

		}
		return _signatureFQ;
	}

	/*public String getSimplifiedSignatureInContext(Type context)
	{
		StringBuffer signature = new StringBuffer();
		signature.append(method.getName());
		signature.append("(");
		signature.append(getParameterListAsStringInContext(context, false));
		signature.append(")");
		return signature.toString();
	}
	
	public String getSignatureInContext(Type context)
	{
		StringBuffer signature = new StringBuffer();
		signature.append(method.getName());
		signature.append("(");
		signature.append(getParameterListAsStringInContext(context, true));
		signature.append(")");
		return signature.toString();
	}*/

	private String getParameterListAsString(boolean fullyQualified) {
		String _searched = fullyQualified ? _parameterListAsStringFQ : _parameterListAsString;
		if (_searched == null) {
			StringBuilder returned = new StringBuilder();
			boolean isFirst = true;
			for (Type p : method.getGenericParameterTypes()) {
				Type contextualParamType = TypeUtils.makeInstantiatedType(p, declaringType);
				returned.append((isFirst ? "" : ",") + (fullyQualified ? TypeUtils.fullQualifiedRepresentation(contextualParamType)
						: TypeUtils.simpleRepresentation(contextualParamType)));
				isFirst = false;
			}
			if (fullyQualified) {
				_parameterListAsStringFQ = returned.toString();
			}
			else {
				_parameterListAsString = returned.toString();
			}
		}
		return fullyQualified ? _parameterListAsStringFQ : _parameterListAsString;
	}

	// Warning: no cache for this method
	/*String getParameterListAsStringInContext(Type context, boolean fullyQualified)
	{
		StringBuffer returned = new StringBuffer();
		boolean isFirst = true;
		for (Type p : method.getGenericParameterTypes()) {
			Type typeInContext = TypeUtils.makeInstantiatedType(p, context);
			returned.append((isFirst?"":",")+(fullyQualified?TypeUtils.fullQualifiedRepresentation(typeInContext):TypeUtils.simpleRepresentation(typeInContext)));
			isFirst = false;          	
		}
		return returned.toString();
	}*/

	/*@Override
	public boolean equals(Object obj) {
		if (obj instanceof MethodDefinition) {
			// System.out.println("Compare "+getMethod()+" and "+((MethodDefinition)obj).getMethod());
			return getMethod().equals(((MethodDefinition) obj).getMethod());
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return getMethod().hashCode();
	}*/

	@Override
	public String toString() {
		return "MethodDefinition[" + getSimplifiedSignature() + "]";
	}

	public String getLabel() {
		return getSimplifiedSignature();
	}

	public String getTooltipText(Type resultingType) {
		String returned = "<html>";
		String resultingTypeAsString;
		if (resultingType != null) {
			resultingTypeAsString = TypeUtils.simpleRepresentation(resultingType);
			resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
			resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
		}
		else {
			resultingTypeAsString = "???";
		}
		returned += "<p><b>" + resultingTypeAsString + " " + getSimplifiedSignature() + "</b></p>";
		// returned +=
		// "<p><i>"+(method.getDescription()!=null?method.getDescription():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		returned += "</html>";
		return returned;
	}

	@Override
	public String getName() {
		return method.getName();
	}

	@Override
	public Type getReturnType() {
		return method.getGenericReturnType();
	}

	@Override
	public List<Function.FunctionArgument> getArguments() {
		return arguments;
	}
}
