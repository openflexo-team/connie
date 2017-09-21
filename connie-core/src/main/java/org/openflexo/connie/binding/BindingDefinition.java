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

import java.lang.reflect.Type;
import java.text.Collator;
import java.util.Comparator;
import java.util.Observable;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.type.TypeUtils;

/**
 * Represents the specification of a DataBinding
 * 
 * @author sylvain
 * 
 */
@Deprecated
public class BindingDefinition extends Observable {

	static final Logger LOGGER = Logger.getLogger(BindingDefinition.class.getPackage().getName());

	private String variableName;

	private Type type;

	private boolean isMandatory;

	private DataBinding.BindingDefinitionType bindingDefinitionType = DataBinding.BindingDefinitionType.GET;

	public BindingDefinition(String variableName, Type type, DataBinding.BindingDefinitionType bindingType, boolean mandatory) {
		super();
		this.variableName = variableName;
		this.type = type;
		isMandatory = mandatory;
		bindingDefinitionType = bindingType;
	}

	@Override
	public int hashCode() {
		return (variableName == null ? 0 : variableName.hashCode()) + (type == null ? 0 : type.hashCode());
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof BindingDefinition) {
			BindingDefinition bd = (BindingDefinition) object;
			if (variableName == null) {
				if (bd.variableName != null) {
					return false;
				}
			}
			else {
				if (!variableName.equals(bd.variableName)) {
					return false;
				}
			}
			return type == bd.type && isMandatory == bd.isMandatory;
		}
		else {
			return super.equals(object);
		}
	}

	public boolean getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(boolean mandatory) {
		isMandatory = mandatory;
	}

	public boolean getIsSettable() {
		return getBindingDefinitionType() == DataBinding.BindingDefinitionType.SET
				|| getBindingDefinitionType() == DataBinding.BindingDefinitionType.GET_SET;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public static final Comparator<BindingDefinition> BINDING_DEFINITION_COMPARATOR = new BindingDefinitionComparator();

	/**
	 * Used to sort binding definition according to name alphabetic ordering
	 * 
	 * @author sylvain
	 * 
	 */
	public static class BindingDefinitionComparator implements Comparator<BindingDefinition> {

		BindingDefinitionComparator() {

		}

		/**
		 * Implements
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(BindingDefinition o1, BindingDefinition o2) {
			String s1 = o1.getVariableName();
			String s2 = o2.getVariableName();
			if (s1 != null && s2 != null) {
				return Collator.getInstance().compare(s1, s2);
			}
			else {
				return 0;
			}
		}

	}

	public DataBinding.BindingDefinitionType getBindingDefinitionType() {
		return bindingDefinitionType;
	}

	public void setBindingDefinitionType(DataBinding.BindingDefinitionType bdType) {
		bindingDefinitionType = bdType;
	}

	public String getTypeStringRepresentation() {
		if (getType() == null) {
			return "no_type";
		}
		else {
			return TypeUtils.simpleRepresentation(getType());
		}
	}

	@Override
	public String toString() {
		return "BindingDefinition[name=" + variableName + ",type=" + type + ",mandatory=" + isMandatory + ",kind=" + bindingDefinitionType
				+ "]";
	}

	public void notifyBindingDefinitionTypeChanged() {
		// setChanged();
		// notifyObservers(new BindingDefinitionTypeChanged());
	}
}
