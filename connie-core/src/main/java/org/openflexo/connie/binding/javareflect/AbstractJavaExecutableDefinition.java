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

package org.openflexo.connie.binding.javareflect;

import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.connie.binding.Function;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.toolbox.ToolBox;

public abstract class AbstractJavaExecutableDefinition<E extends Executable> implements Function {
	private final Type declaringType;
	private final E executable;
	private final ArrayList<Function.FunctionArgument> arguments;

	protected AbstractJavaExecutableDefinition(Type aDeclaringType, E executable) {
		this.executable = executable;
		this.declaringType = aDeclaringType;
		arguments = new ArrayList<>();
		int i = 0;
		for (Type t : executable.getGenericParameterTypes()) {
			String argName = "arg" + i;
			Type argType = TypeUtils.makeInstantiatedType(t, aDeclaringType);
			arguments.add(new DefaultFunctionArgument(this, argName, argType));
			i++;
		}
	}

	public Type getDeclaringType() {
		return declaringType;
	}

	public E getExecutable() {
		return executable;
	}

	public String getExecutableName() {
		return executable.getName();
	}

	private String _parameterListAsStringFQ;
	private String _parameterListAsString;

	protected String getParameterListAsString(boolean fullyQualified) {
		String _searched = fullyQualified ? _parameterListAsStringFQ : _parameterListAsString;
		if (_searched == null) {
			StringBuilder returned = new StringBuilder();
			boolean isFirst = true;
			for (Type p : executable.getGenericParameterTypes()) {
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

	public abstract String getSimplifiedSignature();

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
		return executable.getName();
	}

	@Override
	public List<Function.FunctionArgument> getArguments() {
		return arguments;
	}
}
