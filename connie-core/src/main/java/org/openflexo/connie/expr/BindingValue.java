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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.ParseException;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.Function;
import org.openflexo.connie.binding.Function.FunctionArgument;
import org.openflexo.connie.binding.FunctionPathElement;
import org.openflexo.connie.binding.IBindingPathElement;
import org.openflexo.connie.binding.SettableBindingEvaluationContext;
import org.openflexo.connie.binding.SettableBindingPathElement;
import org.openflexo.connie.binding.SimplePathElement;
import org.openflexo.connie.binding.TargetObject;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.kvc.InvalidKeyValuePropertyException;

/**
 * Represents a binding path, as formed by an access to a binding variable and a path of BindingPathElement<br>
 * A BindingValue may be settable is the last BindingPathElement is itself settable
 * 
 * @author sylvain
 * 
 */
public class BindingValue extends Expression implements PropertyChangeListener, Cloneable {

	private static final Logger LOGGER = Logger.getLogger(BindingValue.class.getPackage().getName());

	private final List<AbstractBindingPathElement> parsedBindingPath;

	private BindingVariable bindingVariable;
	private final List<BindingPathElement> bindingPath;

	private boolean needsAnalysing = true;
	private boolean analysingSuccessfull = true;

	private DataBinding<?> dataBinding;

	private String invalidBindingReason = "not analyzed yet";

	private ExpressionPrettyPrinter prettyPrinter;

	public BindingValue(ExpressionPrettyPrinter prettyPrinter) {
		this(new ArrayList<AbstractBindingPathElement>(), prettyPrinter);
	}

	public BindingValue(List<AbstractBindingPathElement> aBindingPath, ExpressionPrettyPrinter prettyPrinter) {
		super();

		this.prettyPrinter = prettyPrinter;
		this.parsedBindingPath = aBindingPath;
		bindingVariable = null;
		bindingPath = new ArrayList<>();
		needsAnalysing = true;
		analysingSuccessfull = true;
	}

	public BindingValue(String stringToParse, BindingFactory bindingFactory, ExpressionPrettyPrinter prettyPrinter) throws ParseException {
		this(parse(stringToParse, bindingFactory), prettyPrinter);
	}

	@Override
	public ExpressionPrettyPrinter getPrettyPrinter() {
		return prettyPrinter;
	}

	public void delete() {
		clearBindingPathAndBindingVariable();
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

	private static List<AbstractBindingPathElement> parse(String stringToParse, BindingFactory bindingFactory) throws ParseException {
		Expression e = bindingFactory.parseExpression(stringToParse);
		// Expression e = ExpressionParser.parse(stringToParse);
		if (e instanceof BindingValue) {
			return ((BindingValue) e).getParsedBindingPath();
		}
		throw new ParseException("Not parseable as a BindingValue: " + stringToParse);
	}

	public DataBinding<?> getDataBinding() {
		return dataBinding;
	}

	public synchronized void setDataBinding(DataBinding<?> dataBinding) {
		this.dataBinding = dataBinding;
	}

	@Override
	public int getDepth() {
		return 0;
	}

	public List<BindingPathElement> getBindingPath() {
		return bindingPath;
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
		analysingSuccessfull = _checkBindingPathValid();
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
		analysingSuccessfull = _checkBindingPathValid();

		// Note:
		// If adding of this BindingPathElement does not lead to a valid DataBinding,
		// We need to update parsed binding path

		updateParsedBindingPathFromBindingPath();
	}

	public void updateParsedBindingPathFromBindingPath() {

		if (dataBinding != null && dataBinding.debug) {
			System.out.println("DEBUG -- Connie -- updateParsedBindingPathFromBindingPath() for " + this);
			/*System.out.println("Avec BVar=" + getBindingVariable());
			System.out.println("BindingPath=" + bindingPath.size() + " " + bindingPath);
			if (bindingPath.size() == 0) {
				dataBinding.debug();
				debug();
				Thread.dumpStack();
			}*/

		}

		parsedBindingPath.clear();
		if (getBindingVariable() != null) {
			parsedBindingPath.add(new NormalBindingPathElement(getVariableName()));
		}
		for (BindingPathElement e : bindingPath) {
			if (e instanceof SimplePathElement) {
				parsedBindingPath.add(new NormalBindingPathElement(((SimplePathElement) e).getPropertyName()));
			}
			else if (e instanceof FunctionPathElement) {
				FunctionPathElement fpe = (FunctionPathElement) e;
				List<Expression> argList = new ArrayList<>();
				for (FunctionArgument fa : fpe.getArguments()) {
					DataBinding<?> db = fpe.getParameter(fa);
					if (db != null) {
						argList.add(db.getExpression());
					}
				}
				parsedBindingPath.add(new MethodCallBindingPathElement(fpe.getFunction().getName(), argList));
			}
		}
		clearSerializationRepresentation();
		needsAnalysing = false;
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
			parsedBindingPath.clear();
			BindingPathElement removed = bindingPath.remove(bindingPath.size() - 1);
			removeBindingPathElementAfter(requestedLast);
			if (removed.isActivated()) {
				removed.desactivate();
			}
		}
		analysingSuccessfull = _checkBindingPathValid();
	}

	public void removeBindingPathAt(int index) {
		BindingPathElement removed = bindingPath.remove(index);
		if (removed.isActivated()) {
			removed.desactivate();
		}
		analysingSuccessfull = _checkBindingPathValid();
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

	public BindingVariable getBindingVariable() {
		return bindingVariable;
	}

	public void setBindingVariable(BindingVariable bindingVariable) {
		clearBindingPathAndBindingVariable();
		internallySetBindingVariable(bindingVariable);
		// Fixed CORE-145
		updateParsedBindingPathFromBindingPath();
		analysingSuccessfull = _checkBindingPathValid();
	}

	private void internallySetBindingVariable(BindingVariable aBindingVariable) {
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
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(BindingVariable.VARIABLE_NAME_PROPERTY)) {

			clearSerializationRepresentation();

			// System.out.println(">>> In binding value " + this);
			// System.out.println(">>> Detecting that variable " + getBindingVariable().getVariableName() + " change to "
			// + evt.getNewValue());

			if (getBindingVariable() != null && getBindingVariable().getVariableName() != null
					&& getBindingVariable().getVariableName().equals(evt.getNewValue())) {
				// In this case, we detect that our current BindingVariable name has changed
				// It's really important to also change value in parsed binding path, otherwise if for any reason, the binding
				// value is set again to be reanalyzed, the binding variable might not be found again
				if (getParsedBindingPath().size() > 0 && getParsedBindingPath().get(0) instanceof NormalBindingPathElement) {
					((NormalBindingPathElement) getParsedBindingPath().get(0)).property = (String) evt.getNewValue();
				}
			}
			else {
				// Does't matter, but mark data binding as being reanalyzed
				if (dataBinding != null) {
					dataBinding.markedAsToBeReanalized();
				}
			}
		}
		else if (evt.getPropertyName().equals(BindingVariable.TYPE_PROPERTY)) {

			clearSerializationRepresentation();

			// In this case, we detect that our current BindingVariable type has changed
			// We need to mark the DataBinding as being reanalyzed
			if (dataBinding != null) {
				dataBinding.markedAsToBeReanalized();
			}
		}
	}

	@Override
	public Type getAccessedType() {
		/*if (isValid() && getLastBindingPathElement() != null) {
			return getLastBindingPathElement().getType();
		}*/
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

	public List<AbstractBindingPathElement> getParsedBindingPath() {
		return parsedBindingPath;
	}

	public boolean containsAMethodCall() {
		for (AbstractBindingPathElement e : getParsedBindingPath()) {
			if (e instanceof MethodCallBindingPathElement) {
				return true;
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
				FunctionPathElement functionPathElement = (FunctionPathElement) pathElement;
				for (FunctionArgument functionArgument : functionPathElement.getArguments()) {
					if (functionPathElement.getParameter(functionArgument) != null
							&& !functionPathElement.getParameter(functionArgument).isCacheable()) {
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
		return getParsedBindingPath().size() == 1 && getParsedBindingPath().get(0) instanceof NormalBindingPathElement;
	}

	public boolean isCompoundBinding() {
		for (BindingPathElement e : getBindingPath()) {
			if (e instanceof FunctionPathElement) {
				return true;
			}
		}
		return false;
	}

	private BindingValue makeTransformationForValidBindingValue(ExpressionTransformer transformer) throws TransformException {
		BindingValue bv = new BindingValue(prettyPrinter);
		bv.setDataBinding(getDataBinding());
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
		bv.needsAnalysing = false;
		bv.analysingSuccessfull = analysingSuccessfull;
		updateParsedBindingPathFromBindingPath();
		return bv;
	}

	private BindingValue makeTransformationForInvalidBindingValue(ExpressionTransformer transformer) throws TransformException {
		ArrayList<AbstractBindingPathElement> newBindingPath = new ArrayList<>();
		for (AbstractBindingPathElement e : getParsedBindingPath()) {
			if (e instanceof NormalBindingPathElement) {
				newBindingPath.add(new NormalBindingPathElement(((NormalBindingPathElement) e).property));
			}
			else if (e instanceof MethodCallBindingPathElement) {
				ArrayList<Expression> newArgs = new ArrayList<>();
				for (Expression arg : ((MethodCallBindingPathElement) e).args) {
					Expression transformedExpression = arg.transform(transformer);
					newArgs.add(transformedExpression);
				}
				newBindingPath.add(new MethodCallBindingPathElement(((MethodCallBindingPathElement) e).method, newArgs));
			}
		}

		BindingValue bv = new BindingValue(newBindingPath, prettyPrinter);
		bv.needsAnalysing = true;
		bv.setDataBinding(getDataBinding());

		return bv;
	}

	public boolean containsMethodCallWithParameters() {
		for (AbstractBindingPathElement e : new ArrayList<>(getParsedBindingPath())) {
			if (e instanceof MethodCallBindingPathElement && ((MethodCallBindingPathElement) e).args.size() > 0) {
				return true;
			}
		}
		return false;
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
		if (containsMethodCallWithParameters()) {
			if (isValid()) {
				bv = makeTransformationForValidBindingValue(transformer);
			}
			else {
				bv = makeTransformationForInvalidBindingValue(transformer);
			}

		}
		else {
			bv = this;
		}

		return transformer.performTransformation(bv);

	}

	@Override
	public void visit(ExpressionVisitor visitor) throws VisitorException {
		if (containsMethodCallWithParameters()) {
			for (BindingPathElement bpe : getBindingPath()) {
				if (bpe instanceof FunctionPathElement) {
					for (FunctionArgument arg : ((FunctionPathElement) bpe).getArguments()) {
						DataBinding<?> parameter = ((FunctionPathElement) bpe).getParameter(arg);
						if (parameter != null && parameter.getExpression() != null) {
							visitor.visit(parameter.getExpression());
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

	public boolean isValid() {
		return isValid(getDataBinding());
	}

	public void markedAsToBeReanalized() {
		if (dataBinding != null && dataBinding.debug) {
			System.out.println("DEBUG -- Connie -- markedAsToBeReanalized() for " + this);
		}
		needsAnalysing = true;
		clearSerializationRepresentation();
		clearBindingPathAndBindingVariable();
	}

	private void clearBindingPathAndBindingVariable() {
		if (dataBinding != null && dataBinding.debug) {
			System.out.println("DEBUG -- Connie -- clearBindingPathAndBindingVariable() for " + this);
		}
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
		bindingVariable = null;
	}

	/* Unused
	private boolean needsAnalysing() {
		return needsAnalysing;
	}
	 */

	public boolean isValid(DataBinding<?> dataBinding) {

		setDataBinding(dataBinding);
		if (dataBinding == null) {
			invalidBindingReason = "binding value has no referenced data binding";
			return false;
		}

		if (dataBinding.getOwner() == null) {
			invalidBindingReason = "binding value referenced data binding has no owner";
			return false;
		}

		if (dataBinding.getOwner().getBindingModel() == null) {
			invalidBindingReason = "binding value referenced data binding owner has no binding model";
			return false;
		}

		if (dataBinding.getOwner().getBindingFactory() == null) {
			invalidBindingReason = "binding value referenced data binding owner has no binding factory";
			return false;
		}

		if (needsAnalysing) {
			buildBindingPathFromParsedBindingPath(dataBinding);
			needsAnalysing = false;
		}

		if (!analysingSuccessfull) {
			return false;
		}

		if (LOGGER.isLoggable(Level.FINEST)) {
			LOGGER.finest("Is BindingValue valid ?");
		}

		if (getBindingVariable() == null) {
			invalidBindingReason = "binding value has no binding variable";
			if (LOGGER.isLoggable(Level.FINER)) {
				LOGGER.finer("Invalid binding because _bindingVariable is null");
			}
			return false;
		}
		if (!_checkBindingPathValid()) {
			if (LOGGER.isLoggable(Level.FINER)) {
				LOGGER.finer("Invalid binding because binding path not valid");
			}
			return false;
		}

		/*if (!TypeUtils.isTypeAssignableFrom(dataBinding.getDeclaredType(), getLastBindingPathElement().getType())) {
			invalidBindingReason = "wrong type: type " + dataBinding.getDeclaredType() + " is not assignable from "
					+ getLastBindingPathElement().getType();
			return false;
		}*/

		analysingSuccessfull = true;

		return true;
	}

	private String serializationRepresentation = null;

	public void clearSerializationRepresentation() {
		if (dataBinding != null && dataBinding.debug) {
			System.out.println("DEBUG -- Connie -- clearSerializationRepresentation() for " + this);
		}
		serializationRepresentation = null;
	}

	private String makeSerializationRepresentation() {
		StringBuffer sb = new StringBuffer();
		if (getBindingVariable() != null && analysingSuccessfull) {
			sb.append(getBindingVariable().getVariableName());
			for (BindingPathElement e : getBindingPath()) {
				sb.append("." + e.getSerializationRepresentation());
			}
		}
		else {
			boolean isFirst = true;
			for (AbstractBindingPathElement apbe : parsedBindingPath) {
				sb.append((isFirst ? "" : ".") + apbe.getSerializationRepresentation());
				isFirst = false;
			}
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

	private Type analyzedType;

	private boolean _checkBindingPathValid() {

		analyzedType = Object.class;

		if (dataBinding != null && dataBinding.debug) {
			System.out.println("DEBUG -- Connie -- _checkBindingPathValid() for " + this);
		}

		if (getBindingVariable() == null) {
			invalidBindingReason = "binding variable is null";
			return false;
		}
		Type currentType = getBindingVariable().getType();

		IBindingPathElement currentElement = getBindingVariable();
		if (currentType == null) {
			invalidBindingReason = "currentType is null";
			return false;
		}

		for (int i = 0; i < bindingPath.size(); i++) {
			BindingPathElement element = bindingPath.get(i);
			if (element instanceof FunctionPathElement) {
				// We have to check that all arguments are valid
				FunctionPathElement functionPathElement = (FunctionPathElement) element;
				if (functionPathElement.getFunction() == null) {
					invalidBindingReason = "invalid function";
					return false;
				}
				// System.out.println("Checking for functionPathElement= " +
				// functionPathElement);
				for (FunctionArgument arg : functionPathElement.getFunction().getArguments()) {
					DataBinding<?> argValue = functionPathElement.getParameter(arg);
					// System.out.println("Checking " + argValue + " valid="
					// + argValue.isValid());
					if (argValue == null) {
						invalidBindingReason = "Parameter value for function: " + functionPathElement.getFunction() + " : "
								+ "invalid null argument " + arg.getArgumentName();
						return false;
					}
					if (!argValue.isValid()) {
						invalidBindingReason = "Parameter value for function: " + functionPathElement.getFunction() + " : "
								+ "invalid argument " + arg.getArgumentName() + " reason=" + argValue.invalidBindingReason();
						return false;
					}
				}
			}
			if (element.getParent() == null) {
				invalidBindingReason = "No parent for: " + currentElement;
				return false;
			}
			if (!TypeUtils.isTypeAssignableFrom(currentElement.getType(), element.getParent().getType(), true)) {
				invalidBindingReason = "Mismatched: " + currentElement.getType() + " and " + element.getParent().getType();
				return false;
			}
			currentElement = element;

			currentType = TypeUtils.makeInstantiatedType(currentElement.getType(), currentType);

		}

		analyzedType = currentType;

		clearSerializationRepresentation();

		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BindingValue) {
			BindingValue e = (BindingValue) obj;
			return dataBinding == e.dataBinding && getParsedBindingPath().equals(e.getParsedBindingPath());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * Called to perform analysis of a parsed binding path in the particular context provided by the {@link DataBinding} this
	 * {@link BindingValue} is related to.
	 * 
	 * @param dataBinding
	 * @return
	 */
	// Developer's note: This method is flagged as synchronized and it is important, as it is a critical code
	// Many threads may access to this at same time, causing BindingPath to be mixed
	public synchronized boolean buildBindingPathFromParsedBindingPath(DataBinding<?> dataBinding) {

		if (dataBinding != null && dataBinding.debug) {
			System.out.println("DEBUG -- Connie -- buildBindingPathFromParsedBindingPath() for " + this);
		}

		// logger.info("buildBindingPathFromParsedBindingPath() for " +
		// getParsedBindingPath());

		if (dataBinding.getOwner() == null) {
			// LOGGER.warning("DataBinding has no owner");
			invalidBindingReason = "DataBinding has no owner";
			return false;
		}

		if (dataBinding.getOwner().getBindingModel() == null) {
			// LOGGER.warning("DataBinding owner has no binding model, owner=" + dataBinding.getOwner());
			invalidBindingReason = "DataBinding owner has no binding model, binding=" + dataBinding + " owner=" + dataBinding.getOwner();
			return false;
		}

		if (dataBinding.getOwner().getBindingFactory() == null) {
			// LOGGER.warning("DataBinding owner has no binding factory, owner=" + dataBinding.getOwner());
			invalidBindingReason = "DataBinding owner has no binding factory, binding=" + dataBinding + " owner=" + dataBinding.getOwner();
			return false;
		}

		needsAnalysing = false;
		// analyzedWithBindingModel = dataBinding.getOwner().getBindingModel();
		setDataBinding(dataBinding);

		clearBindingPathAndBindingVariable();

		if (getDataBinding() != null && getParsedBindingPath().size() > 0
				&& getParsedBindingPath().get(0) instanceof NormalBindingPathElement) {
			// Seems to be valid
			internallySetBindingVariable(dataBinding.getOwner().getBindingModel()
					.bindingVariableNamed(((NormalBindingPathElement) getParsedBindingPath().get(0)).property));
			IBindingPathElement current = bindingVariable;
			// System.out.println("Found binding variable " + bindingVariable);
			if (bindingVariable == null) {
				invalidBindingReason = "cannot find binding variable " + ((NormalBindingPathElement) getParsedBindingPath().get(0)).property
						+ " BindingModel=";// + dataBinding.getOwner().getBindingModel();
				analysingSuccessfull = false;
				return false;
			}
			int i = 0;

			// System.out.println("Analysing BindingValue " + Integer.toHexString(hashCode()) + " with " + getParsedBindingPath());

			for (AbstractBindingPathElement pathElement : new ArrayList<>(getParsedBindingPath())) {
				if (i > 0) {
					if (pathElement instanceof NormalBindingPathElement) {
						SimplePathElement newPathElement = dataBinding.getOwner().getBindingFactory().makeSimplePathElement(current,
								((NormalBindingPathElement) pathElement).property);
						if (newPathElement != null) {
							if (!newPathElement.isActivated()) {
								newPathElement.activate();
							}
							bindingPath.add(newPathElement);
							current = newPathElement;
							// System.out.println("> SIMPLE " + pathElement);
						}
						else {
							analysingSuccessfull = false;
							invalidBindingReason = "cannot find property " + ((NormalBindingPathElement) pathElement).property
									+ " for element " + current + " and type " + TypeUtils.simpleRepresentation(current.getType())
									+ " owner=" + dataBinding.getOwner() + " factory=" + dataBinding.getOwner().getBindingFactory();
							return false;
						}
					}
					else if (pathElement instanceof MethodCallBindingPathElement) {
						MethodCallBindingPathElement methodCall = (MethodCallBindingPathElement) pathElement;
						List<DataBinding<?>> args = new ArrayList<>();
						int argIndex = 0;
						for (Expression arg : methodCall.args) {
							DataBinding<?> argDataBinding = new DataBinding<>(dataBinding.getOwner(), Object.class,
									DataBinding.BindingDefinitionType.GET);
							argDataBinding.setBindingName("arg" + argIndex);
							if (arg != null) {
								// Avoid to notify yet, otherwise it may loop
								argDataBinding.setExpression(arg, false);
							}
							else {
								if (getDataBinding() != null && getDataBinding().getOwner() != null) {
									argDataBinding.setExpression(getDataBinding().getOwner().getBindingFactory().getNullExpression());
								}
							}
							// argDataBinding.setDeclaredType(Object.class);
							// IMPORTANT/HACK: following statement (call to
							// isValid()) is required to get access to analyzed
							// type and
							// declares it
							// TODO: find a better solution
							argDataBinding.isValid();
							if (argDataBinding.getAnalyzedType() != null) {
								argDataBinding.setDeclaredType(argDataBinding.getAnalyzedType());
							}
							args.add(argDataBinding);
							argIndex++;
						}
						Function function = dataBinding.getOwner().getBindingFactory().retrieveFunction(current.getType(),
								((MethodCallBindingPathElement) pathElement).method, args);
						if (function != null) {
							FunctionPathElement newPathElement = dataBinding.getOwner().getBindingFactory().makeFunctionPathElement(current,
									function, args);
							if (newPathElement != null) {
								if (!newPathElement.isActivated()) {
									newPathElement.activate();
								}
								bindingPath.add(newPathElement);
								current = newPathElement;
								// System.out.println("> FUNCTION " +
								// pathElement);
							}
							else {
								invalidBindingReason = "(2) cannot find method " + ((MethodCallBindingPathElement) pathElement).method
										+ " for type " + TypeUtils.simpleRepresentation(current.getType()) + " and args=" + args;
								analysingSuccessfull = false;
								return false;
							}
						}
						else {
							invalidBindingReason = "(1) cannot find method " + ((MethodCallBindingPathElement) pathElement).method
									+ " for type " + TypeUtils.simpleRepresentation(current.getType()) + " and args=" + args;
							analysingSuccessfull = false;
							return false;
						}
					}
					else {
						// LOGGER.warning("Unexpected " + pathElement);
						invalidBindingReason = "unexpected path element: " + pathElement;
						analysingSuccessfull = false;
						return false;
					}
				}
				i++;
			}
			invalidBindingReason = "Valid";
			analysingSuccessfull = true;
		}
		else {
			invalidBindingReason = "Empty binding";
			analysingSuccessfull = false;
		}

		return analysingSuccessfull;
	}

	public Object getBindingValue(BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException, InvocationTargetTransformException {

		// System.out.println(" > evaluate BindingValue " + this +
		// " in context " + context);
		if (isValid() && context != null) {
			if (getBindingVariable() == null) {
				return null;
			}
			Object current = context.getValue(getBindingVariable());
			if (current == null) {
				// If the binding variable is null, just return null
				return null;
			}
			IBindingPathElement previous = getBindingVariable();
			try {
				for (BindingPathElement e : getBindingPath()) {
					if (current == null) {
						if (!e.supportsNullValues()) {
							throw new NullReferenceException("NullReferenceException while evaluating BindingValue "
									+ getParsedBindingPath() + ": null occured when evaluating " + previous);
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
			// System.out.println(" > return "+current);
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
			throw new NullReferenceException("while evaluating " + getParsedBindingPath() + ": null occured when evaluating "
					+ lastEvaluatedPathElement + " from " + context);
		}
		// System.out.println("For variable "+_bindingVariable+" object is "+returned);

		for (BindingPathElement element : getBindingPath()) {
			if (element != getLastBindingPathElement()) {
				// System.out.println("Apply "+element);
				lastEvaluatedPathElement = element;
				returned = element.getBindingValue(returned, context);
				if (returned == null) {
					throw new NullReferenceException("while evaluating " + getParsedBindingPath() + ": null occured when evaluating "
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
		returned.add(new TargetObject(context, getBindingVariable().getVariableName()));

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
				FunctionPathElement functionPathElement = (FunctionPathElement) element;
				for (FunctionArgument arg : functionPathElement.getArguments()) {
					DataBinding<?> value = functionPathElement.getParameter(arg);
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
			System.out.println("parsedBindingPath=" + parsedBindingPath);
			System.out.println("bvar=" + bindingVariable);
			System.out.println("bpath=" + bindingPath);
			System.out.println("needsAnalysing=" + needsAnalysing);
			System.out.println("analysingSuccessfull=" + analysingSuccessfull);
		}
	 */

	public static abstract class AbstractBindingPathElement {
		public abstract String getSerializationRepresentation();
	}

	public static class NormalBindingPathElement extends AbstractBindingPathElement {
		public String property;

		public NormalBindingPathElement(String aProperty) {
			property = aProperty;
		}

		@Override
		public String toString() {
			return "Normal[" + property + "]";
		}

		@Override
		public String getSerializationRepresentation() {
			return property;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (property == null ? 0 : property.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NormalBindingPathElement) {
				NormalBindingPathElement e = (NormalBindingPathElement) obj;
				return property.equals(e.property);
			}
			return super.equals(obj);
		}
	}

	public static class MethodCallBindingPathElement extends AbstractBindingPathElement {
		public String method;
		public List<Expression> args;

		public MethodCallBindingPathElement(String aMethod, List<Expression> someArgs) {
			method = aMethod;
			args = someArgs;
		}

		@Override
		public String toString() {
			return "Call[" + method + "(" + args + ")" + "]";
		}

		@Override
		public String getSerializationRepresentation() {
			StringBuffer sb = new StringBuffer();
			sb.append(method + "(");
			boolean isFirst = true;
			for (Expression arg : args) {
				sb.append((isFirst ? "" : ",") + arg);
				isFirst = false;
			}
			sb.append(")");
			return sb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 37;
			int result = 1;
			result = prime * result + (method == null ? 0 : method.hashCode());
			result = prime * result + (args == null ? 0 : args.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof MethodCallBindingPathElement) {
				MethodCallBindingPathElement e = (MethodCallBindingPathElement) obj;
				return method.equals(e.method) && args.equals(e.args);
			}
			return super.equals(obj);
		}

	}

}
