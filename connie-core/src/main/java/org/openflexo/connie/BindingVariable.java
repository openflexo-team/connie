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

import java.beans.PropertyChangeSupport;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.connie.binding.SettableBindingEvaluationContext;
import org.openflexo.connie.binding.SettableBindingPathElement;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.BindingPath;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.ToolBox;

/**
 * A {@link BindingVariable} is the declaration of a value accessible through a {@link BindingModel} (and is defined in a
 * {@link BindingModel}).<br>
 * 
 * This is the entry point of a {@link BindingPath} (a comma-separated path)
 * 
 * A {@link BindingVariable} has:
 * <ul>
 * <li>a name (see {@link #getVariableName()/#setVariableName(String)}) uniquely identifying the variable</li>
 * <li>a type (see {@link #getType()/#setType(Type)}</li>
 * </ul>
 * 
 * @author sylvain
 *
 */
public class BindingVariable implements SettableBindingPathElement, HasPropertyChangeSupport {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(BindingVariable.class.getPackage().getName());

	private String variableName;
	protected Type type;
	private boolean settable = false;
	private PropertyChangeSupport pcSupport;
	// Unused private boolean activated = false;

	public static final String VARIABLE_NAME_PROPERTY = "variableName";
	public static final String TYPE_PROPERTY = "type";
	public static final String DELETED_PROPERTY = "deleted";

	public BindingVariable(String variableName, Type type) {
		super();
		this.variableName = variableName;
		this.type = type;
		pcSupport = new PropertyChangeSupport(this);
	}

	public BindingVariable(String variableName, Type type, boolean settable) {
		this(variableName, type);
		setSettable(settable);
	}

	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		if (type != null && !type.equals(this.type)) {
			Type oldType = this.type;
			this.type = type;
			pcSupport.firePropertyChange(TYPE_PROPERTY, oldType, type);
		}
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String aVariableName) {
		if (aVariableName != null && !aVariableName.equals(variableName)) {
			String oldVariableName = variableName;
			this.variableName = aVariableName;
			pcSupport.firePropertyChange(VARIABLE_NAME_PROPERTY, oldVariableName, variableName);
		}
	}

	@Override
	public boolean isResolved() {
		return true;
	}

	@Override
	public void resolve() {
	}

	@Override
	public String toString() {
		return getVariableName() /* + "/" + TypeUtils.simpleRepresentation(getType())*/;
	}

	@Override
	public String getSerializationRepresentation() {
		return getVariableName();
	}

	@Override
	public String getLabel() {
		return getVariableName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		String returned = "<html>";
		String resultingTypeAsString;
		if (getType() != null) {
			resultingTypeAsString = TypeUtils.simpleRepresentation(getType());
			resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
			resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
		}
		else {
			resultingTypeAsString = "???";
		}
		returned += "<p><b>" + resultingTypeAsString + " " + getVariableName() + "</b></p>";
		// returned +=
		// "<p><i>"+(bv.getDescription()!=null?bv.getDescription():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		returned += "</html>";
		return returned;
	}

	@Override
	public boolean isSettable() {
		return settable;
	}

	public void setSettable(boolean settable) {
		this.settable = settable;
	}

	/*@Override
	public boolean equals(Object obj) {
		if (obj instanceof BindingVariable) {
			String vname = getVariableName();
			if (vname != null) {
				return vname.equals(((BindingVariable) obj).getVariableName()) && getType() != null
						&& getType().equals(((BindingVariable) obj).getType());
			}
			else
				return false;
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		if (toString() != null) {
			return (toString()).hashCode();
		}
		return super.hashCode();
	}*/

	@Override
	public Object getBindingValue(Object owner, BindingEvaluationContext context) {
		return context.getValue(this);
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException {
		if (isSettable() && context instanceof SettableBindingEvaluationContext) {
			((SettableBindingEvaluationContext) context).setValue(value, this);
		}
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED_PROPERTY;
	}

	private boolean cacheable = true;

	/**
	 * Return boolean indicating if this {@link BindingVariable} should be cached<br>
	 * Default behaviour is cacheable
	 * 
	 * @return
	 */
	public boolean isCacheable() {
		return cacheable;
	}

	/**
	 * Sets boolean indicating if this {@link BindingVariable} should be cached<br>
	 * Default behaviour is cacheable
	 * 
	 */
	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}

	@Override
	public boolean isNotifyingBindingPathChanged() {
		return false;
	}

	public void delete() {
		if (pcSupport != null) {
			getPropertyChangeSupport().firePropertyChange(DELETED_PROPERTY, this, null);
			pcSupport = null;
		}
		variableName = null;
		type = null;
	}

	@Deprecated
	// Caused by parameters management: change this !!!
	public void hasBeenResolved(BindingPath bindingPath) {
	}
}
