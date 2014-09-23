package org.openflexo.toolbox;

import java.beans.PropertyChangeSupport;

public abstract class PropertyChangedSupportDefaultImplementation implements HasPropertyChangeSupport {

	private final PropertyChangeSupport pcSupport = new PropertyChangeSupport(this);

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

}
