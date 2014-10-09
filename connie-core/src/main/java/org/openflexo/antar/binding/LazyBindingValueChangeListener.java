package org.openflexo.antar.binding;

import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

/**
 * Lazy implementation of a {@link BindingValueChangeListener}. New value is not automatically recomputed after each notification
 * 
 * @author sylvain
 * 
 */
public abstract class LazyBindingValueChangeListener<T> extends BindingValueChangeListener<T> {

	private static final Logger logger = FlexoLogger.getLogger(LazyBindingValueChangeListener.class.getName());

	public LazyBindingValueChangeListener(DataBinding<T> dataBinding, BindingEvaluationContext context) {
		super(dataBinding, context);
	}

	@Override
	protected void fireChange(PropertyChangeEvent evt) {
		getDataBinding().clearCacheForBindingEvaluationContext(getContext());
		bindingValueChanged(evt.getSource());
		refreshObserving(false);
	}

	@Override
	public void bindingValueChanged(Object source, T newValue) {
		bindingValueChanged(source);
	}

	public abstract void bindingValueChanged(Object source);

}
