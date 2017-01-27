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
import java.util.List;
import java.util.Vector;

import org.openflexo.toolbox.HasPropertyChangeSupport;

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

	private final List<BindingVariable> _bindingVariables;
	private BindingModel baseBindingModel;
	// private Bindable mainBindable;

	public static final String BINDING_VARIABLE_PROPERTY = "bindingVariable";
	public static final String BINDING_VARIABLE_NAME_CHANGED = "bindingVariableNameChanged";
	public static final String BINDING_VARIABLE_TYPE_CHANGED = "bindingVariableTypeChanged";
	public static final String BINDING_PATH_ELEMENT_NAME_CHANGED = "bindingPathElementNameChanged";
	public static final String BINDING_PATH_ELEMENT_TYPE_CHANGED = "bindingPathElementTypeChanged";
	public static final String BASE_BINDING_MODEL_PROPERTY = "baseBindingModel";
	public static final String DELETED_PROPERTY = "deleted";

	private PropertyChangeSupport pcSupport;

	public BindingModel() {
		this((BindingModel) null);
	}

	public BindingModel(BindingModel baseBindingModel) {
		_bindingVariables = new Vector<>();
		pcSupport = new PropertyChangeSupport(this);
		setBaseBindingModel(baseBindingModel);
	}

	public BindingModel getBaseBindingModel() {
		return baseBindingModel;
	}

	public void setBaseBindingModel(BindingModel baseBindingModel) {
		if (this.baseBindingModel != baseBindingModel) {
			BindingModel oldBaseBindingModel = this.baseBindingModel;
			this.baseBindingModel = baseBindingModel;
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

		for (BindingVariable bv : _bindingVariables) {
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
		_bindingVariables.clear();
	}

	public int getBindingVariablesCount() {
		return _bindingVariables.size() + (baseBindingModel != null ? baseBindingModel.getBindingVariablesCount() : 0);
	}

	public BindingVariable getBindingVariableAt(int index) {
		if (baseBindingModel == null || index < _bindingVariables.size()) {
			return _bindingVariables.get(index);
		}
		else {
			return baseBindingModel.getBindingVariableAt(index - _bindingVariables.size());
		}
	}

	public void addToBindingVariables(BindingVariable variable) {
		_bindingVariables.add(variable);
		pcSupport.firePropertyChange(BINDING_VARIABLE_PROPERTY, null, variable);
	}

	public void removeFromBindingVariables(BindingVariable variable) {
		_bindingVariables.remove(variable);
		pcSupport.firePropertyChange(BINDING_VARIABLE_PROPERTY, variable, null);
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

	@Override
	public String toString() {
		return "[ " + getClass().getSimpleName() + ": " + _bindingVariables
				+ (baseBindingModel != null ? " Combined with: " + baseBindingModel : "") + "]";
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
			BindingVariable bv2 = other.bindingVariableNamed(bv1.getVariableName());
			if (!bv1.equals(bv2)) {
				return false;
			}
		}
		return true;
	}

}
