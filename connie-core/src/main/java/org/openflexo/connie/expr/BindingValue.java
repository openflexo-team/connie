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

package org.openflexo.connie.expr;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.ParseException;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.BindingPathElement.BindingPathCheck;
import org.openflexo.connie.binding.Function.FunctionArgument;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SettableBindingEvaluationContext;
import org.openflexo.connie.binding.SettableBindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.binding.TargetObject;
import org.openflexo.connie.binding.javareflect.InvalidKeyValuePropertyException;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.connie.type.UndefinedType;

/**
 * Represents a binding path, as formed by an access to a binding variable and a path of BindingPathElement<br>
 * A BindingValue may be settable is the last BindingPathElement is itself settable
 * 
 * @author sylvain
 * 
 */
public class BindingValue extends Expression implements PropertyChangeListener, Cloneable {

	private static final Logger LOGGER = Logger.getLogger(BindingValue.class.getPackage().getName());

	public static final boolean DEBUG = true;

	private BindingVariable bindingVariable;
	private final List<BindingPathElement> bindingPath;

	private boolean isValid = false;
	private boolean validated = false;

	private Bindable owner;

	private String invalidBindingReason = "not analyzed yet";

	private ExpressionPrettyPrinter prettyPrinter;

	private Type analyzedType;

	/**
	 * Build a new BindingValue asserting supplied String represent a {@link BindingValue} (might be an Expression)
	 * 
	 * @param stringToParse
	 * @param bindable
	 * @return
	 * @throws ParseException
	 */
	public static BindingValue parse(String stringToParse, Bindable bindable) throws ParseException {
		Expression e = bindable.getBindingFactory().parseExpression(stringToParse, bindable);
		if (e instanceof BindingValue) {
			return (BindingValue) e;
		}
		throw new ParseException("Not parseable as a BindingValue: " + stringToParse);
	}

	public BindingValue(Bindable owner, ExpressionPrettyPrinter prettyPrinter) {
		this(new ArrayList<BindingPathElement>(), owner, prettyPrinter);
	}

	public BindingValue(List<BindingPathElement> aBindingPath, Bindable owner, ExpressionPrettyPrinter prettyPrinter) {
		this(null, aBindingPath, owner, prettyPrinter);
	}

	public BindingValue(BindingVariable aBindingVariable, List<BindingPathElement> aBindingPath, Bindable owner,
			ExpressionPrettyPrinter prettyPrinter) {
		super();

		this.owner = owner;

		this.prettyPrinter = prettyPrinter;
		bindingVariable = aBindingVariable;
		bindingPath = new ArrayList<>(aBindingPath);
		validated = false;
		isValid = false;
	}

	@Override
	public ExpressionPrettyPrinter getPrettyPrinter() {
		return prettyPrinter;
	}

	@Override
	public int getPriority() {
		return -1;
	}

	public void delete() {
		clear();
	}

	@Override
	public BindingValue clone() {
		try {
			return (BindingValue) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Bindable getOwner() {
		return owner;
	}

	public void setOwner(Bindable owner) {
		// if ((owner == null && this.owner != null) || (owner != null && !owner.equals(this.owner))) {
		// Bindable oldValue = this.owner;
		this.owner = owner;
		// getPropertyChangeSupport().firePropertyChange("owner", oldValue, owner);
		// }
		invalidate();
	}

	@Override
	public int getDepth() {
		return 0;
	}

	public List<BindingPathElement> getBindingPath() {
		return bindingPath;
	}

	public void setBindingPath(List<BindingPathElement> elements) {
		clearBindingPathElements();
		for (BindingPathElement element : elements) {
			addBindingPathElement(element);
		}
	}

	/**
	 * @param element
	 */
	public Type addBindingPathElement(BindingPathElement element) {
		int index = bindingPath.size();
		setBindingPathElementAtIndex(element, index);
		if (!element.isActivated()) {
			element.activate();
		}
		invalidate();
		return element.getType();
	}

	/**
	 * @param element
	 * @param i
	 */
	public void setBindingPathElementAtIndex(BindingPathElement element, int i) {

		if (i < bindingPath.size() && bindingPath.get(i) == element) {
			return;
		}
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Set property " + element + " at index " + i);
		}
		if (i < bindingPath.size()) {
			bindingPath.set(i, element);
			int size = bindingPath.size();
			for (int j = i + 1; j < size; j++) {
				BindingPathElement removed = bindingPath.remove(i + 1);
				if (removed.isActivated()) {
					removed.desactivate();
				}
			}
		}
		else if (i == bindingPath.size()) {
			bindingPath.add(element);
			if (!element.isActivated()) {
				element.activate();
			}
		}
		else {
			if (LOGGER.isLoggable(Level.WARNING)) {
				LOGGER.warning("Could not set property at index " + i);
			}
		}
		invalidate();
	}

	public BindingPathElement getBindingPathElementAtIndex(int i) {
		if (i < bindingPath.size()) {
			return bindingPath.get(i);
		}
		return null;
	}

	public int getBindingPathElementCount() {
		return bindingPath.size();
	}

	public void removeBindingPathElementAfter(BindingPathElement requestedLast) {
		if (bindingPath != null && bindingPath.get(bindingPath.size() - 1) != null
				&& bindingPath.get(bindingPath.size() - 1).equals(requestedLast)) {
			return;
		}
		else if (bindingPath != null && bindingPath.get(bindingPath.size() - 1) != null) {
			BindingPathElement removed = bindingPath.remove(bindingPath.size() - 1);
			removeBindingPathElementAfter(requestedLast);
			if (removed.isActivated()) {
				removed.desactivate();
			}
		}
		invalidate();
	}

	public void removeBindingPathAt(int index) {
		BindingPathElement removed = bindingPath.remove(index);
		if (removed.isActivated()) {
			removed.desactivate();
		}
		invalidate();
	}

	/**
	 * Return the last binding path element, which is the binding variable itself if the binding path is empty, or the last binding path
	 * element registered in the binding path
	 */
	public IBindingPathElement getLastBindingPathElement() {
		if (getBindingPath() != null && getBindingPath().size() > 0) {
			return getBindingPath().get(getBindingPath().size() - 1);
		}
		return getBindingVariable();
	}

	/**
	 * Return boolean indicating if supplied element is equals to the last binding path element
	 * 
	 * @param element
	 * @return
	 */
	public boolean isLastBindingPathElement(IBindingPathElement element) {

		if (bindingPath.size() == 0) {
			return element.equals(getBindingVariable());
		}

		return bindingPath.get(bindingPath.size() - 1).equals(element);
	}

	public IBindingPathElement getRootPathElement() {
		if (getBindingVariable() != null) {
			return getBindingVariable();
		}
		if (getBindingPath().size() > 0) {
			return getBindingPath().get(0);
		}
		return null;
	}

	public BindingVariable getBindingVariable() {
		return bindingVariable;
	}

	public void setBindingVariable(BindingVariable aBindingVariable) {
		if (bindingVariable != aBindingVariable) {
			clearSerializationRepresentation();
			if (bindingVariable != null && bindingVariable.getPropertyChangeSupport() != null) {
				bindingVariable.getPropertyChangeSupport().removePropertyChangeListener(BindingVariable.TYPE_PROPERTY, this);
				bindingVariable.getPropertyChangeSupport().removePropertyChangeListener(BindingVariable.VARIABLE_NAME_PROPERTY, this);
			}
			bindingVariable = aBindingVariable;
			if (bindingVariable != null && bindingVariable.getPropertyChangeSupport() != null) {
				bindingVariable.getPropertyChangeSupport().addPropertyChangeListener(BindingVariable.TYPE_PROPERTY, this);
				bindingVariable.getPropertyChangeSupport().addPropertyChangeListener(BindingVariable.VARIABLE_NAME_PROPERTY, this);
			}
			invalidate();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BindingVariable.VARIABLE_NAME_PROPERTY)) {

			invalidate();

			// clearSerializationRepresentation();

			// System.out.println(">>> In binding value " + this);
			// System.out.println(">>> Detecting that variable " + getBindingVariable().getVariableName() + " change to "
			// + evt.getNewValue());

			/*if (getBindingVariable() != null && getBindingVariable().getVariableName() != null
					&& getBindingVariable().getVariableName().equals(evt.getNewValue())) {
				// In this case, we detect that our current BindingVariable name has changed
				// It's really important to also change value in parsed binding path, otherwise if for any reason, the binding
				// value is set again to be reanalyzed, the binding variable might not be found again
				//if (getParsedBindingPath().size() > 0 && getParsedBindingPath().get(0) instanceof NormalBindingPathElement) {
				//	((NormalBindingPathElement) getParsedBindingPath().get(0)).property = (String) evt.getNewValue();
				//}
			}
			else {
				markToBeReanalized();
			}*/
		}
		else if (evt.getPropertyName().equals(BindingVariable.TYPE_PROPERTY)) {

			invalidate();

			/*clearSerializationRepresentation();
			
			// In this case, we detect that our current BindingVariable type has changed
			// We need to mark the DataBinding as being reanalyzed
			markToBeReanalized();*/
		}
	}

	@Override
	public Type getAccessedType() {
		if (isValid()) {
			return analyzedType;
		}
		return null;
	}

	public Type getAccessedTypeNoValidityCheck() {
		if (getLastBindingPathElement() != null) {
			return getLastBindingPathElement().getType();
		}
		return null;
	}

	public boolean containsAMethodCall() {
		for (BindingPathElement bindingPathElement : getBindingPath()) {
			if (bindingPathElement instanceof FunctionPathElement) {
				return true;
			}
		}
		return false;
	}

	private boolean containsMethodCallWithArguments() {
		for (BindingPathElement bindingPathElement : getBindingPath()) {
			if (bindingPathElement instanceof FunctionPathElement) {
				if (((FunctionPathElement) bindingPathElement).getArguments().size() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Return boolean indicating if the computation of this {@link BindingValue} should be cached<br>
	 * A {@link BindingValue} is cacheable if
	 * <ul>
	 * <li>related {@link BindingVariable} is cacheable</li>
	 * <li>this {@link BindingValue} is notification-safe (all path elements are notification-safe)</li>
	 * </ul>
	 * 
	 * @return
	 */
	public boolean isCacheable() {
		return fullyRelyOnCacheableBindingVariables() && isNotificationSafe();
	}

	/**
	 * Indicates if this {@link BindingValue} only rely on {@link BindingVariable} identified as cacheable
	 * 
	 * @return
	 */
	private boolean fullyRelyOnCacheableBindingVariables() {
		if (getBindingVariable() == null || !getBindingVariable().isCacheable()) {
			return false;
		}
		for (BindingPathElement pathElement : new ArrayList<>(getBindingPath())) {
			if (pathElement instanceof FunctionPathElement) {
				FunctionPathElement<?> functionPathElement = (FunctionPathElement<?>) pathElement;
				for (FunctionArgument functionArgument : functionPathElement.getFunctionArguments()) {
					if (functionPathElement.getArgumentValue(functionArgument) != null
							&& !functionPathElement.getArgumentValue(functionArgument).isCacheable()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Return boolean indicating if this {@link BindingValue} is notification-safe (all modifications of data are notified using
	 * {@link PropertyChangeSupport} scheme)<br>
	 * 
	 * @return
	 */
	public boolean isNotificationSafe() {
		if (bindingVariable == null) {
			return false;
		}
		for (BindingPathElement pathElement : new ArrayList<>(getBindingPath())) {
			if (!pathElement.isNotificationSafe()) {
				return false;
			}
		}
		return true;
	}

	public String getVariableName() {
		if (getBindingVariable() != null) {
			return getBindingVariable().getVariableName();
		}
		return null;
	}

	public boolean isSimpleVariable() {
		return getBindingPath().size() == 1 && getBindingPath().get(0) instanceof BindingVariable;
	}

	@Override
	public Expression transform(ExpressionTransformer transformer) throws TransformException {

		if (transformer instanceof ExpressionEvaluator) {
			Expression returned = transformer.performTransformation(this);
			if (returned instanceof Constant) {
				return returned;
			}
		}

		BindingValue bv;

		if (containsMethodCallWithArguments()) {
			bv = makeTransformationForArguments(transformer);
		}
		else {
			bv = this;
		}

		Expression returned = transformer.performTransformation(bv);
		return returned;

	}

	private BindingValue makeTransformationForArguments(ExpressionTransformer transformer) throws TransformException {
		BindingValue bv = new BindingValue(getOwner(), prettyPrinter);
		bv.setBindingVariable(getBindingVariable());
		for (BindingPathElement bpe : getBindingPath()) {
			if (bpe instanceof SimplePathElement) {
				// TODO: instantiate a new SimplePathElement
				bv.bindingPath.add(bpe);
			}
			else if (bpe instanceof FunctionPathElement) {
				// TODO: instantiate a new FunctionPathElement
				bv.bindingPath.add(((FunctionPathElement) bpe).transform(transformer));
			}
		}
		return bv;
	}

	@Override
	public void visit(ExpressionVisitor visitor) throws VisitorException {
		if (containsMethodCallWithArguments()) {
			for (BindingPathElement bpe : getBindingPath()) {
				if (bpe instanceof FunctionPathElement) {
					for (DataBinding<?> arg : ((FunctionPathElement<?>) bpe).getArguments()) {
						if (arg != null && arg.getExpression() != null) {
							visitor.visit(arg.getExpression());
						}
					}
				}
			}
		}
		visitor.visit(this);
	}

	@Override
	public EvaluationType getEvaluationType() {
		return TypeUtils.kindOfType(getAccessedType());
	}

	@Override
	protected Vector<Expression> getChilds() {
		return null;
	}

	@Override
	public boolean isSettable() {
		if (getLastBindingPathElement() instanceof SettableBindingPathElement) {
			return ((SettableBindingPathElement) getLastBindingPathElement()).isSettable();
		}
		return false;
	}

	/**
	 * Mark this {@link BindingValue} as invalidated: this means that the validity status should be cleared
	 */
	public void invalidate() {
		validated = false;
		isValid = false;
		clearSerializationRepresentation();
	}

	/**
	 * Revalidate this {@link BindingValue} by invalidate and recompute validity
	 */
	public void revalidate() {

		invalidate();
		isValid();
	}

	public void clearBindingPathElements() {
		if (bindingPath != null) {
			for (BindingPathElement e : bindingPath) {
				if (e.isActivated()) {
					e.desactivate();
				}
			}
			synchronized (this) {
				bindingPath.clear();
			}
		}
		invalidate();

	}

	public void clear() {
		clearBindingPathElements();
		bindingVariable = null;
		invalidate();

	}

	public boolean isValid() {

		if (validated) {
			return isValid;
		}

		if (getOwner() == null) {
			invalidBindingReason = "binding value referenced data binding has no owner";
			return false;
		}

		if (getOwner().getBindingModel() == null) {
			invalidBindingReason = "binding value referenced data binding owner has no binding model";
			return false;
		}

		if (getOwner().getBindingFactory() == null) {
			invalidBindingReason = "binding value referenced data binding owner has no binding factory";
			return false;
		}

		isValid = performSemanticsAnalysis();

		if (isValid) {
			isValid = true;
			invalidBindingReason = "valid binding value";
		}

		validated = true;

		return true;
	}

	private String serializationRepresentation = null;

	public void clearSerializationRepresentation() {
		serializationRepresentation = null;

	}

	private String makeSerializationRepresentation() {
		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		if (getBindingVariable() != null) {
			sb.append(getBindingVariable().getVariableName());
			isFirst = false;
		}
		for (BindingPathElement e : getBindingPath()) {
			sb.append((isFirst ? "" : ".") + e.getSerializationRepresentation());
			isFirst = false;
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		if (serializationRepresentation == null) {
			serializationRepresentation = makeSerializationRepresentation();
		}

		return serializationRepresentation;
	}

	public String invalidBindingReason() {
		return invalidBindingReason;
	}

	private boolean performSemanticsAnalysis() {

		analyzedType = UndefinedType.INSTANCE;

		if (getRootPathElement() == null) {
			invalidBindingReason = "root path element is null (no binding variable, nor constructor or static method)";
			return false;
		}

		Type currentType = null;
		IBindingPathElement currentElement = null;

		if (getBindingVariable() != null) {
			if (getBindingVariable() instanceof UnresolvedBindingVariable) {
				invalidBindingReason = "BindingVariable " + getBindingVariable().getVariableName() + " does not exist";
				return false;
			}
			currentType = getBindingVariable().getType();
			currentElement = getBindingVariable();
			if (currentType == null) {
				invalidBindingReason = "currentType is null";
				return false;
			}
			if (getOwner().getBindingModel() == null) {
				invalidBindingReason = "BindingModel is null";
				return false;
			}
			// Check that the BindingVariable is the right one
			BindingVariable checkedBV = getOwner().getBindingModel().bindingVariableNamed(getBindingVariable().getVariableName());
			if (checkedBV == null) {
				invalidBindingReason = "BindingVariable " + getBindingVariable().getVariableName() + " does not exist";
				return false;
			}
			else if (checkedBV != getBindingVariable()) {
				invalidBindingReason = "inconsistent BindingVariable " + getBindingVariable().getVariableName();
				return false;
			}
		}

		for (int i = 0; i < bindingPath.size(); i++) {
			BindingPathElement element = bindingPath.get(i);

			if (!element.isResolved()) {
				// Try to resolve now
				element.resolve();
				if (!element.isResolved()) {
					invalidBindingReason = "unresolved path element " + element;
					return false;
				}
			}

			BindingPathCheck check = element.checkBindingPathIsValid(currentElement, currentType);
			if (!check.valid) {
				invalidBindingReason = check.invalidBindingReason;
				return false;
			}
			else {
				currentElement = element;
				currentType = check.returnedType;
			}
		}

		analyzedType = currentType;

		clearSerializationRepresentation();

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(getBindingPath(), getOwner());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BindingValue other = (BindingValue) obj;
		return Objects.equals(getBindingPath(), other.getBindingPath()) && Objects.equals(getOwner(), other.getOwner());
	}

	/**
	 * Build a {@link DataBinding} representing a sub {@link BindingValue} extracted from this {@link BindingValue} with binding path
	 * trucated at supplied index
	 * <ul>
	 * <li>If bindingPathIndex values 1, build a {@link BindingValue} with BindingVariable and the first binding path element</li>
	 * <li>If bindingPathIndex values 0, build a {@link BindingValue} with BindingVariable</li>
	 * </ul>
	 * 
	 * @param bindingPathIndex
	 * @return
	 */
	public DataBinding<?> makeSubBindingValue(int bindingPathIndex) {

		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		if (getBindingVariable() != null) {
			sb.append(getBindingVariable().getVariableName());
			isFirst = false;
		}
		for (BindingPathElement e : getBindingPath()) {
			sb.append((isFirst ? "" : ".") + e.getSerializationRepresentation());
			isFirst = false;
		}
		return new DataBinding<>(sb.toString(), getOwner(), Object.class, BindingDefinitionType.GET);
	}

	public Object getBindingValue(BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException, InvocationTargetTransformException {

		// System.out.println(" > evaluate BindingValue " + this +
		// " in context " + context);
		if (isValid() && context != null) {
			Object current = null;
			IBindingPathElement previous = null;
			if (getBindingVariable() != null) {
				current = context.getValue(getBindingVariable());
				previous = getBindingVariable();
			}

			try {
				for (BindingPathElement e : getBindingPath()) {
					if (current == null) {
						if (!e.supportsNullValues()) {
							throw new NullReferenceException("NullReferenceException while evaluating BindingValue " + toString()
									+ ": null occured when evaluating " + previous);
						}
					}
					try {
						current = e.getBindingValue(current, context);
					} catch (InvalidKeyValuePropertyException e2) {
						throw new InvocationTargetTransformException(new InvocationTargetException(e2));
					}
					previous = e;
				}
			} catch (ConcurrentModificationException e) {
				System.err.println("ConcurrentModificationException while executing BindingValue " + this);
				return null;
			}
			return current;
		}
		return null;
	}

	public void setBindingValue(Object value, BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException, InvocationTargetTransformException, NotSettableContextException {

		// logger.info("setBindingValue() for " + this + " with " + value +
		// " context=" + context);
		// logger.info("valid=" + isValid());
		// logger.info("isSettable=" + isSettable());

		if (!isValid()) {
			return;
		}

		if (!isSettable()) {
			return;
		}

		if (getBindingPath().size() == 0 && getBindingVariable() != null) {
			// This is a simple assignation
			if (context instanceof SettableBindingEvaluationContext) {
				((SettableBindingEvaluationContext) context).setValue(value, getBindingVariable());
				return;
			}
			throw new NotSettableContextException(getBindingVariable(), context);
		}

		// System.out.println("Sets value: "+value);
		// System.out.println("Binding: "+getStringRepresentation());

		IBindingPathElement lastEvaluatedPathElement = getBindingVariable();
		Object lastEvaluated = null;
		Object returned = context.getValue(getBindingVariable());

		if (returned == null) {
			throw new NullReferenceException(
					"while evaluating " + toString() + ": null occured when evaluating " + lastEvaluatedPathElement + " from " + context);
		}
		// System.out.println("For variable "+_bindingVariable+" object is "+returned);

		for (BindingPathElement element : getBindingPath()) {
			if (element != getLastBindingPathElement()) {
				// System.out.println("Apply "+element);
				lastEvaluatedPathElement = element;
				returned = element.getBindingValue(returned, context);
				if (returned == null) {
					throw new NullReferenceException("while evaluating " + toString() + ": null occured when evaluating "
							+ lastEvaluatedPathElement + " from " + lastEvaluated);
				}
				lastEvaluated = returned;
				// System.out.println("Obtain "+returned);
			}
		}

		// logger.info("returned="+returned);
		// logger.info("lastElement="+getBindingPath().lastElement());

		if (getLastBindingPathElement() instanceof SettableBindingPathElement
				&& ((SettableBindingPathElement) getLastBindingPathElement()).isSettable()) {
			// System.out.println("Et finalement on applique " +
			// getLastBindingPathElement() + " sur " + returned);
			((SettableBindingPathElement) getLastBindingPathElement()).setBindingValue(value, returned, context);
		}
		else {
			LOGGER.warning("Binding " + this + " is not settable");
		}

	}

	/**
	 * Build and return a list of objects involved in the computation of this data binding with supplied binding evaluation context
	 * 
	 * @param context
	 * @return
	 */
	public List<Object> getConcernedObjects(BindingEvaluationContext context) {
		if (!isValid()) {
			return Collections.emptyList();
		}
		if (!isSettable()) {
			return Collections.emptyList();
		}

		List<Object> returned = new ArrayList<>();

		Object current = context.getValue(getBindingVariable());
		returned.add(current);

		for (BindingPathElement element : getBindingPath()) {
			if (element != getLastBindingPathElement()) {
				try {
					current = element.getBindingValue(current, context);
				} catch (TypeMismatchException e) {
					// silently escape
				} catch (NullReferenceException e) {
					// silently escape
				} catch (InvocationTargetTransformException e) {
					// silently escape
				}
				if (current == null) {
					return returned;
				}
				returned.add(current);
			}
		}
		if (current == null) {
			return null;
		}

		return returned;
	}

	/**
	 * Build and return a list of target objects involved in the computation of this data binding with supplied binding evaluation context
	 * <br>
	 * Those target objects are the combination of an object and the property name involved by this denoted data binding
	 * 
	 * @param context
	 * @return
	 */
	public List<TargetObject> getTargetObjects(BindingEvaluationContext context) {
		boolean debug = false;
		if (debug) {
			LOGGER.info("Computing getTargetObjects() for " + toString() + " with BindingEvaluationContext=" + context);
		}

		if (!isValid()) {
			return null;
		}

		ArrayList<TargetObject> returned = new ArrayList<>();

		Object current = context.getValue(getBindingVariable());
		if (getBindingVariable() != null) {
			returned.add(new TargetObject(context, getBindingVariable().getVariableName()));
		}

		if (debug) {
			LOGGER.info("Computing getTargetObjects(), current=" + current);
		}

		if (current == null) {
			return returned;
		}

		// try {
		for (BindingPathElement element : getBindingPath()) {
			if (debug) {
				LOGGER.info("Computing getTargetObjects(), current=" + current);
			}
			returned.add(new TargetObject(current, element.getLabel()));
			try {
				if (element == getLastBindingPathElement()) {
					// No need to analyze deeply, this is the last one
					current = null;
				}
				else {
					current = element.getBindingValue(current, context);
				}
			} catch (TypeMismatchException e) {
				current = null;
				// We silently escape...
			} catch (NullReferenceException e) {
				current = null;
				// We silently escape...
			} catch (InvocationTargetTransformException e) {
				current = null;
				// We silently escape...
			} catch (InvalidKeyValuePropertyException e) {
				current = null;
				// We silently escape...
			}
			if (element instanceof FunctionPathElement) {
				FunctionPathElement<?> functionPathElement = (FunctionPathElement<?>) element;
				for (FunctionArgument arg : functionPathElement.getFunctionArguments()) {
					DataBinding<?> value = functionPathElement.getArgumentValue(arg);
					returned.addAll(value.getTargetObjects(context));
				}
			}
			if (current == null) {
				return returned;
			}
		}
		return returned;
	}

	/* Unused
	private void debug() {
		System.out.println("DEBUG BindingValue");
		System.out.println("bvar=" + bindingVariable);
		System.out.println("bpath=" + bindingPath);
		System.out.println("validated=" + validated);
		System.out.println("valid=" + valid);
	}
	 */

}
