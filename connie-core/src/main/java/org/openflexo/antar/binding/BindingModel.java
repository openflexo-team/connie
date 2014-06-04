/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.antar.binding;

import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Vector;

import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * A binding model represents a set of BindingVariable, which are variables accessible in the context of which this binding model is
 * declared
 * 
 * @author sguerin
 * 
 */
public class BindingModel implements HasPropertyChangeSupport {

	private final List<BindingVariable> _bindingVariables;
	private final BindingModel mainBindingModel;

	public static final String BINDING_VARIABLE = "bindingVariable";

	private final PropertyChangeSupport pcSupport;

	public BindingModel() {
		this(null);
	}

	public BindingModel(BindingModel mainBindingModel) {
		_bindingVariables = new Vector<BindingVariable>();
		this.mainBindingModel = mainBindingModel;
		pcSupport = new PropertyChangeSupport(this);
	}

	@Override
	public String getDeletedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	public void clear() {
		_bindingVariables.clear();
	}

	public int getBindingVariablesCount() {
		return _bindingVariables.size() + (mainBindingModel != null ? mainBindingModel.getBindingVariablesCount() : 0);
	}

	public BindingVariable getBindingVariableAt(int index) {
		if (mainBindingModel == null || index < _bindingVariables.size()) {
			return _bindingVariables.get(index);
		} else {
			return mainBindingModel.getBindingVariableAt(index - _bindingVariables.size());
		}
	}

	public void addToBindingVariables(BindingVariable variable) {
		_bindingVariables.add(variable);
		pcSupport.firePropertyChange(BINDING_VARIABLE, null, variable);
	}

	public void removeFromBindingVariables(BindingVariable variable) {
		_bindingVariables.remove(variable);
		pcSupport.firePropertyChange(BINDING_VARIABLE, variable, null);
	}

	public BindingVariable bindingVariableNamed(String variableName) {
		for (int i = 0; i < getBindingVariablesCount(); i++) {
			BindingVariable next = getBindingVariableAt(i);
			if (next != null && next.getVariableName() != null && next.getVariableName().equals(variableName)) {
				return next;
			}
		}
		if (mainBindingModel != null) {
			return mainBindingModel.bindingVariableNamed(variableName);
		}
		return null;
	}

	@Override
	public String toString() {
		return "BindingModel: " + _bindingVariables + (mainBindingModel != null ? "Combined with:\n" + mainBindingModel : "");
	}
}
