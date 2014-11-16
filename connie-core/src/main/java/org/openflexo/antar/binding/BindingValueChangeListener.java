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

	private static final Logger LOGGER= FlexoLogger.getLogger(BindingValueChangeListener.class.getName());

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

	private List<TargetObject> getChainedBindings(TargetObject object) { // NOPMD by beugnard on 30/10/14 17:15
		return null; // local optimization in refreshObserving
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
			LOGGER.info("-------------> refreshObserving() for " + dataBinding + " context=" + context);
			LOGGER.info("-------------> DependencyBindings:");
		}

		List<TargetObject> updatedDependingObjects = new ArrayList<TargetObject>();
		List<TargetObject> targetObjects = dataBinding.getTargetObjects(context);

		if (debug) {
			for (TargetObject to : targetObjects) {
				LOGGER.info("-------------> TargetObject: " + to.target + " property: " + to.propertyName);
			}
			LOGGER.info("dependingObjects = " + dependingObjects);
		}

		if (debug) {
			LOGGER.info("1-updatedDependingObjects = " + updatedDependingObjects);
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
			LOGGER.info("2-updatedDependingObjects = " + updatedDependingObjects);
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
			LOGGER.info("3-updatedDependingObjects = " + updatedDependingObjects);
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
			LOGGER.info("oldDependingObjects = " + oldDependingObjects);
			LOGGER.info("newDependingObjects = " + newDependingObjects);
		}

		for (TargetObject o : oldDependingObjects) {
			dependingObjects.remove(o);
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine("Observer of " + dataBinding + " remove property change listener: " + o.target + " property:"
							+ o.propertyName);
				}
				if (debug) {
					LOGGER.info("-------------> Observer of " + dataBinding + " remove property change listener: " + o.target
							+ " property:" + o.propertyName);
				}
				if (pcSupport != null) {
					pcSupport.removePropertyChangeListener(o.propertyName, this);
				}
			} else if (o.target instanceof Observable) {
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine("Observer of " + dataBinding + " remove observable: " + o);
				}
				if (debug) {
					LOGGER.info("Observer of " + dataBinding + " remove observable: " + o);
				}
				((Observable) o.target).deleteObserver(this);
			}
		}
		for (TargetObject o : newDependingObjects) {
			dependingObjects.add(o);
			if (o.target instanceof HasPropertyChangeSupport) {
				PropertyChangeSupport pcSupport = ((HasPropertyChangeSupport) o.target).getPropertyChangeSupport();
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine("-------------> Observer of " + dataBinding + " add property change listener: " + o.target + " property:"
							+ o.propertyName);
				}
				if (debug) {
					LOGGER.info("-------------> Observer of " + dataBinding + " add property change listener: " + o.target + " property:"
							+ o.propertyName);
				}
				if (pcSupport != null) {
					pcSupport.addPropertyChangeListener(o.propertyName, this);
				}
			} else if (o.target instanceof Observable) {
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine("Observer of " + dataBinding + " add observable: " + o.target);
				}
				if (debug) {
					LOGGER.info("Observer of " + dataBinding + " add observable: " + o.target + " for " + o.propertyName);
				}
				((Observable) o.target).addObserver(this);
			}
		}

		if (debug) {
			LOGGER.info("-------------> dependingObjects:");
			for (TargetObject o : dependingObjects) {
				LOGGER.info("> " + o.target.getClass().getSimpleName() + " / " + o.propertyName + "\n");
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

	final public T evaluateValue() throws NullReferenceException {
		try {
			return dataBinding.getBindingValue(context);
		} catch (TypeMismatchException e) {
			LOGGER.warning("Unexpected exception raised. See logs for details.");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			LOGGER.warning("Unexpected exception raised. See logs for details.");
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
			LOGGER.warning("Could not evaluate " + dataBinding + " with context " + context + " because NullReferenceException has raised");
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
