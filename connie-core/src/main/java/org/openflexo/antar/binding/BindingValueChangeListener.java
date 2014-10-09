package org.openflexo.antar.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * A listener that tracks evaluated value of a DataBinding, given a run-time context given by a {@link BindingEvaluationContext}
 * 
 * @author sylvain
 * 
 */
public abstract class BindingValueChangeListener<T> implements PropertyChangeListener, Observer {

	private static final Logger logger = FlexoLogger.getLogger(BindingValueChangeListener.class.getName());

	private DataBinding<T> dataBinding;
	private BindingEvaluationContext context;
	private List<TargetObject> dependingObjects;
	protected T lastNotifiedValue;
	private boolean deleted = false;

	// Use to prevent initial null value not to be fired (causing listener to not listen this value)
	private boolean lastNotifiedValueWasFired = false;

	public BindingValueChangeListener(DataBinding<T> dataBinding, BindingEvaluationContext context) {
		super();
		this.dataBinding = dataBinding;
		this.context = context;
		this.dependingObjects = new ArrayList<TargetObject>();
		T newValue;
		try {
			newValue = evaluateValue();
		} catch (NullReferenceException e) {
			// Don't warn since this may happen
			// logger.warning("Could not evaluate " + dataBinding + " with context " + context +
			// " because NullReferenceException has raised");
			newValue = null;
		}
		refreshObserving(false);
	}

	public void delete() {
		dataBinding = null;
		context = null;
		dependingObjects.clear();
		dependingObjects = null;
		deleted = true;
	}

	public List<TargetObject> getChainedBindings(TargetObject object) {
		return null;
	}

	public DataBinding<T> getDataBinding() {
		return dataBinding;
	}

	public BindingEvaluationContext getContext() {
		return context;
	}

	public void refreshObserving() {
		refreshObserving(false);
	}

	protected synchronized void refreshObserving(boolean debug) {

		// Kept for future debug use
		/*if (dataBinding.toString().equals("data.canUndo()")) {
			debug = true;
			Thread.dumpStack();
		}*/

		if (debug) {
			logger.info("-------------> refreshObserving() for " + dataBinding + " context=" + context);
			logger.info("-------------> DependencyBindings:");
		}

		List<TargetObject> updatedDependingObjects = new ArrayList<TargetObject>();
		List<TargetObject> targetObjects = dataBinding.getTargetObjects(context);

		if (debug) {
			for (TargetObject to : targetObjects) {
				logger.info("-------------> TargetObject: " + to.target + " property: " + to.propertyName);
			}
			logger.info("dependingObjects = " + dependingObjects);
		}

		if (debug) {
			logger.info("1-updatedDependingObjects = " + updatedDependingObjects);
		}

		if (targetObjects != null) {
			updatedDependingObjects.addAll(targetObjects);
			if (targetObjects.size() > 0) {
				List<TargetObject> chainedBindings = getChainedBindings(targetObjects.get(targetObjects.size() - 1));
				if (chainedBindings != null) {
					updatedDependingObjects.addAll(chainedBindings);
				}
			}
		}

		if (debug) {
			logger.info("2-updatedDependingObjects = " + updatedDependingObjects);
		}

		Set<HasPropertyChangeSupport> set = new HashSet<HasPropertyChangeSupport>();
		for (TargetObject o : updatedDependingObjects) {
			if (o.target instanceof HasPropertyChangeSupport) {
				set.add((HasPropertyChangeSupport) o.target);
			}
		}
		for (HasPropertyChangeSupport hasPCSupport : set) {
			if (hasPCSupport.getDeletedProperty() != null) {
				updatedDependingObjects.add(new TargetObject(hasPCSupport, hasPCSupport.getDeletedProperty()));
			}
		}

		if (debug) {
			logger.info("3-updatedDependingObjects = " + updatedDependingObjects);
		}

		List<TargetObject> newDependingObjects = new ArrayList<TargetObject>();
		List<TargetObject> oldDependingObjects = new ArrayList<TargetObject>(dependingObjects);
		for (TargetObject o : updatedDependingObjects) {
			if (oldDependingObjects.contains(o)) {
				oldDependingObjects.remove(o);
			} else {
				newDependingObjects.add(o);
			}
		}

		if (debug) {
			logger.info("oldDependingObjects = " + oldDependingObjects);
			logger.info("newDependingObjects = " + newDependingObjects);
		}

		for (TargetObject o : oldDependingObjects) {
			dependingObjects.remove(o);
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Observer of " + dataBinding + " remove property change listener: " + o.target + " property:"
							+ o.propertyName);
				}
				if (debug) {
					logger.info("-------------> Observer of " + dataBinding + " remove property change listener: " + o.target
							+ " property:" + o.propertyName);
				}
				pcSupport.removePropertyChangeListener(o.propertyName, this);
			} else if (o.target instanceof Observable) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Observer of " + dataBinding + " remove observable: " + o);
				}
				if (debug) {
					logger.info("Observer of " + dataBinding + " remove observable: " + o);
				}
				((Observable) o.target).deleteObserver(this);
			}
		}
		for (TargetObject o : newDependingObjects) {
			dependingObjects.add(o);
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("-------------> Observer of " + dataBinding + " add property change listener: " + o.target + " property:"
							+ o.propertyName);
				}
				if (debug) {
					logger.info("-------------> Observer of " + dataBinding + " add property change listener: " + o.target + " property:"
							+ o.propertyName);
				}
				pcSupport.addPropertyChangeListener(o.propertyName, this);
			} else if (o.target instanceof Observable) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Observer of " + dataBinding + " add observable: " + o.target);
				}
				if (debug) {
					logger.info("Observer of " + dataBinding + " add observable: " + o.target + " for " + o.propertyName);
				}
				((Observable) o.target).addObserver(this);
			}
		}

		if (debug) {
			logger.info("-------------> dependingObjects:");
			for (TargetObject o : dependingObjects) {
				logger.info("> " + o.target.getClass().getSimpleName() + " / " + o.propertyName + "\n");
			}
		}

	}

	public synchronized void startObserving() {
		refreshObserving(false);
	}

	public synchronized void stopObserving() {
		for (TargetObject o : dependingObjects) {
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				// logger.info("Widget "+getWidget()+" remove property change listener: "+o.target+" property:"+o.propertyName);
				pcSupport.removePropertyChangeListener(o.propertyName, this);
			} else if (o.target instanceof Observable) {
				// logger.info("Widget "+getWidget()+" remove observable: "+o);
				((Observable) o.target).deleteObserver(this);
			}
		}
		dependingObjects.clear();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("BindingValueChangeListener for " + dataBinding + " context=" + context + "\n");
		for (TargetObject o : dependingObjects) {
			sb.append("> " + o + "\n");
		}
		return sb.toString();
	}

	public T evaluateValue() throws NullReferenceException {
		try {
			return dataBinding.getBindingValue(context);
		} catch (TypeMismatchException e) {
			logger.warning("Unexpected exception raised. See logs for details.");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			logger.warning("Unexpected exception raised. See logs for details.");
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!deleted) {
			fireChange(new PropertyChangeEvent(o, null, null, null));
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		// Kept for future debug use
		/*if (getDataBinding().toString().equals("data.currentStep.issueMessageIcon.image")) {
			System.out.println("Received propertyChange with " + evt);
			System.out.println("deleted=" + deleted);
			// Thread.dumpStack();
			System.out.println("property: " + evt.getPropertyName());
			System.out.println("oldValue: " + evt.getOldValue());
			System.out.println("newValue: " + evt.getNewValue());
			for (TargetObject o : dependingObjects) {
				System.out.println("TargetObject " + o.propertyName + " " + o.target);
			}
		}*/

		if (!deleted) {
			fireChange(evt);
		}
	}

	protected void fireChange(PropertyChangeEvent evt) {

		// Kept for future debug use
		/*if (getDataBinding().toString().equals("data.currentStep.issueMessageIcon.image")) {
			System.out.println(">>>>>>>>>> fireChange for " + getDataBinding().toString());
			refreshObserving(false);
		}*/

		T newValue;
		try {
			dataBinding.clearCacheForBindingEvaluationContext(context);
			newValue = evaluateValue();
		} catch (NullReferenceException e) {
			logger.warning("Could not evaluate " + dataBinding + " with context " + context + " because NullReferenceException has raised");
			newValue = null;
		}

		// Kept for future debug use
		/*if (getDataBinding().toString().equals("data.currentStep.issueMessageIcon.image")) {
			System.out.println("lastNotifiedValue=" + lastNotifiedValue);
			System.out.println("newValue=" + newValue);
		}*/

		// Prevent initial null value not to be fired (causing listener to not listen this value)
		if (newValue != lastNotifiedValue || !lastNotifiedValueWasFired) {
			lastNotifiedValueWasFired = true;
			lastNotifiedValue = newValue;
			bindingValueChanged(evt.getSource(), newValue);
			refreshObserving(false);
		} else {
			// This change will not cause the change of the object, but a different path may lead to the same value
			// If we do nothing, we might no longer observe the right objects
			for (TargetObject o : new ArrayList<TargetObject>(dependingObjects)) {
				if (evt.getSource() == o.target && evt.getPropertyName().equals(o.propertyName)) {
					bindingValueChanged(evt.getSource(), newValue);
					refreshObserving(false);
				}
			}
		}
	}

	public abstract void bindingValueChanged(Object source, T newValue);

	public List<TargetObject> getDependingObjects() {
		return dependingObjects;
	}
}
