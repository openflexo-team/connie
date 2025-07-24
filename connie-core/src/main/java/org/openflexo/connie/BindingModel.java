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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.expr.BindingPath;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.StringUtils;

/**
 * A {@link BindingModel} represents a set of {@link BindingVariable}, which are variables accessible in the context of which this binding
 * model is declared. This is the type specification of an evaluation context, determined at run-time by a {@link BindingEvaluationContext}
 * instance<br>
 * 
 * 
 * @author sylvain
 * 
 */
public class BindingModel implements HasPropertyChangeSupport, PropertyChangeListener {

	/**
	 * The {@link BindingModel} (might be null) on which we are based
	 */
	private BindingModel baseBindingModel;

	/**
	 * {@link BindingVariable} list that are explicitely declared in this {@link BindingModel}
	 */
	private final List<BindingVariable> declaredBindingVariables;

	/**
	 * {@link BindingVariable} list that are accessible from this {@link BindingModel}. Overriden variables are not put twice
	 */
	private List<BindingVariable> accessibleBindingVariables;

	public static final String BINDING_VARIABLE_PROPERTY = "bindingVariable";
	public static final String BINDING_PATH_ELEMENT_NAME_CHANGED = "bindingPathElementNameChanged";
	public static final String BINDING_PATH_ELEMENT_TYPE_CHANGED = "bindingPathElementTypeChanged";
	public static final String BASE_BINDING_MODEL_PROPERTY = "baseBindingModel";
	public static final String DELETED_PROPERTY = "deleted";

	private PropertyChangeSupport pcSupport;

	public BindingModel() {
		this((BindingModel) null);
	}

	public BindingModel(BindingModel baseBindingModel) {
		declaredBindingVariables = new Vector<>();
		accessibleBindingVariables = null;
		pcSupport = new PropertyChangeSupport(this);
		setBaseBindingModel(baseBindingModel);
	}

	public BindingModel getBaseBindingModel() {
		return baseBindingModel;
	}

	public void setBaseBindingModel(BindingModel baseBindingModel) {
		if (this.baseBindingModel != baseBindingModel) {
			BindingModel oldBaseBindingModel = this.baseBindingModel;
			if (oldBaseBindingModel != null && oldBaseBindingModel.getPropertyChangeSupport() != null) {
				oldBaseBindingModel.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			this.baseBindingModel = baseBindingModel;
			clearAccessibleBindingVariables();
			pcSupport.firePropertyChange(BASE_BINDING_MODEL_PROPERTY, oldBaseBindingModel, baseBindingModel);
			if (baseBindingModel != null && baseBindingModel.getPropertyChangeSupport() != null) {
				baseBindingModel.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == baseBindingModel) {
			// Re-forward this notification from this BindingModel
			clearAccessibleBindingVariables();
			getPropertyChangeSupport().firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
	}

	/**
	 * Delete this {@link BindingModel}
	 */
	public void delete() {
		if (baseBindingModel != null && baseBindingModel.getPropertyChangeSupport() != null) {
			baseBindingModel.getPropertyChangeSupport().removePropertyChangeListener(this);
		}

		baseBindingModel = null;

		for (BindingVariable bv : declaredBindingVariables) {
			bv.delete();
		}

		if (pcSupport != null) {
			pcSupport.firePropertyChange(DELETED_PROPERTY, this, null);
		}
		pcSupport = null;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED_PROPERTY;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	public void clear() {
		declaredBindingVariables.clear();
		clearAccessibleBindingVariables();
	}

	public List<BindingVariable> getAccessibleBindingVariables() {
		if (accessibleBindingVariables == null) {
			updateAccessibleBindingVariables();
		}
		return accessibleBindingVariables;
	}

	private void clearAccessibleBindingVariables() {
		accessibleBindingVariables = null;
	}

	private void updateAccessibleBindingVariables() {
		accessibleBindingVariables = new ArrayList<>();
		for (BindingVariable bv : declaredBindingVariables) {
			accessibleBindingVariables.add(bv);
		}
		if (baseBindingModel != null && baseBindingModel.getAccessibleBindingVariables() != null) {
			for (BindingVariable bv : baseBindingModel.getAccessibleBindingVariables()) {
				if (getDeclaredBindingVariableNamed(bv.getVariableName()) == null) {
					// this property is not overriden, take it
					accessibleBindingVariables.add(bv);
				}
			}
		}
	}

	public int getBindingVariablesCount() {
		if (getAccessibleBindingVariables() == null)
			return 0;
		return getAccessibleBindingVariables().size();
	}

	public BindingVariable getBindingVariableAt(int index) {
		if (index >= 0 && index < getBindingVariablesCount()) {
			return getAccessibleBindingVariables().get(index);
		}
		return null;
	}

	public BindingVariable getBindingVariableNamed(String variableName) {
		return bindingVariableNamed(variableName);
	}

	public BindingVariable bindingVariableNamed(String variableName) {
		for (int i = 0; i < getBindingVariablesCount(); i++) {
			BindingVariable next = getBindingVariableAt(i);
			if (next != null && next.getVariableName() != null && next.getVariableName().equals(variableName)) {
				return next;
			}
		}
		if (baseBindingModel != null) {
			return baseBindingModel.bindingVariableNamed(variableName);
		}
		return null;
	}

	private BindingVariable getDeclaredBindingVariableNamed(String variableName) {
		for (BindingVariable bv : declaredBindingVariables) {
			if (bv.getVariableName() != null && bv.getVariableName().equals(variableName)) {
				return bv;
			}
		}
		return null;
	}

	public void addToBindingVariables(BindingVariable variable) {
		declaredBindingVariables.add(variable);
		clearAccessibleBindingVariables();
		pcSupport.firePropertyChange(BINDING_VARIABLE_PROPERTY, null, variable);
	}

	public void removeFromBindingVariables(BindingVariable variable) {
		declaredBindingVariables.remove(variable);
		clearAccessibleBindingVariables();
		pcSupport.firePropertyChange(BINDING_VARIABLE_PROPERTY, variable, null);
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ": " + getAccessibleBindingVariables()
				+ " structured as " + getDebugStructure() + "]";
	}

	public String getDebugStructure() {
		return "[" + getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ": " + getDeclaredBindingVariableListAsString()
				+ (baseBindingModel != null ? " Combined with: " + (baseBindingModel != this ? baseBindingModel.getDebugStructure() : "")
						: "")
				+ "]";
	}

	private String getDeclaredBindingVariableListAsString() {
		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		sb.append("[");
		for (BindingVariable bv : new ArrayList<>(declaredBindingVariables)) {
			sb.append((isFirst ? "" : ",") + bv.getVariableName() + "/" + TypeUtils.simpleRepresentation(bv.getType()));
			isFirst = false;
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Equals method for BindingModel<br>
	 * Two BindingModel are equals if they have the same number of {@link BindingVariable} and if all {@link BindingVariable} match (peer to
	 * peer responding to equals method) TODO: implements hashCode() method respecting this semantics
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BindingModel)) {
			return false;
		}
		BindingModel other = (BindingModel) obj;
		if (getBindingVariablesCount() != other.getBindingVariablesCount()) {
			return false;
		}
		for (int i = 0; i < getBindingVariablesCount(); i++) {
			BindingVariable bv1 = getBindingVariableAt(i);
			if (bv1 != null) {
				BindingVariable bv2 = other.bindingVariableNamed(bv1.getVariableName());
				if (!bv1.equals(bv2)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Compute and return a list of String which are possible starting string values for a BindingPath accessing to supplied type
	 * 
	 * @param string
	 * @param ob
	 * @return
	 */
	public List<String> getBindingValueAvailableCompletion(String startingString, Type desiredType, Bindable bindable) {

		// System.out.println("On cherche les binding qui commencent par " + startingString + " de type " + desiredType);

		List<String> returned = new ArrayList<>();

		if (StringUtils.isEmpty(startingString.trim())) {
			for (BindingVariable bindingVariable : getAccessibleBindingVariables()) {
				returned.add(bindingVariable.getVariableName());
			}
			return returned;
		}

		if (startingString.contains(".")) {
			String startBindingValue = startingString.substring(0, startingString.lastIndexOf("."));
			String toBeCompleted = startingString.substring(startingString.lastIndexOf(".") + 1);

			DataBinding<?> db = new DataBinding<Object>(startBindingValue, bindable, Object.class, BindingDefinitionType.GET);
			if (db.isValid() && db.isBindingPath()) {
				BindingFactory factory = bindable.getBindingFactory();
				BindingPath bv = (BindingPath) db.getExpression();
				IBindingPathElement lastElement = bv.getLastBindingPathElement();
				if (StringUtils.isEmpty(toBeCompleted.trim())) {
					for (SimplePathElement<?> simplePathElement : factory.getAccessibleSimplePathElements(lastElement, bindable)) {
						returned.add(startBindingValue + "." + simplePathElement.getPropertyName());
					}
					for (FunctionPathElement<?> functionPathElement : factory.getAccessibleFunctionPathElements(lastElement, bindable)) {
						returned.add(startBindingValue + "." + functionPathElement.getLabel());
					}
				}
				else {
					for (SimplePathElement<?> simplePathElement : factory.getAccessibleSimplePathElements(lastElement, bindable)) {
						if (simplePathElement.getPropertyName().startsWith(toBeCompleted)) {
							returned.add(startBindingValue + "." + simplePathElement.getPropertyName());
						}
					}
					for (FunctionPathElement<?> functionPathElement : factory.getAccessibleFunctionPathElements(lastElement, bindable)) {
						if (functionPathElement.getLabel().startsWith(toBeCompleted)) {
							returned.add(startBindingValue + "." + functionPathElement.getLabel());
						}
					}
				}
				return returned;
			}
			else {
				// System.out.println("Not valid: " + db + " : " + db.invalidBindingReason());
				return returned;
			}

		}

		else {
			for (BindingVariable bindingVariable : getAccessibleBindingVariables()) {
				if (bindingVariable.getVariableName().startsWith(startingString)) {
					returned.add(bindingVariable.getVariableName());
				}
			}
			return returned;
		}

	}

}
