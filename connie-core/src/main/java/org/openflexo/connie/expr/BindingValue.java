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

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DataBinding.BindingDefinitionType;
import org.openflexo.connie.ParseException;
import org.openflexo.connie.binding.BindingPathElement;
import org.openflexo.connie.binding.BindingPathElement.BindingPathCheck;
import org.openflexo.connie.binding.Function;
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

	private final List<AbstractBindingPathElement> parsedBindingPath;

	private BindingVariable bindingVariable;
	private final List<BindingPathElement> bindingPath;

	private boolean requiresSemanticsBuilding = true;
	private boolean requiresSemanticsAnalysis = true;
	private boolean requiresValidityAnalysis = true;
	private boolean isValid = false;

	// Suggestion: virer ca et le remplacer par Bindable/owner !!!
	// private DataBinding<?> dataBinding2;
	private Bindable owner;

	private String invalidBindingReason = "not analyzed yet";

	private ExpressionPrettyPrinter prettyPrinter;

	private Type analyzedType;

	public BindingValue(Bindable owner, ExpressionPrettyPrinter prettyPrinter) {
		this(new ArrayList<AbstractBindingPathElement>(), owner, prettyPrinter);
	}

	public BindingValue(List<AbstractBindingPathElement> aBindingPath, Bindable owner, ExpressionPrettyPrinter prettyPrinter) {
		super();

		this.owner = owner;

		this.prettyPrinter = prettyPrinter;
		this.parsedBindingPath = aBindingPath;
		bindingVariable = null;
		bindingPath = new ArrayList<>();
		requiresValidityAnalysis = true;
		requiresSemanticsBuilding = true;
		requiresSemanticsAnalysis = true;
		isValid = false;

		if (DEBUG) {
			checkInternalConsistency();
		}
	}

	public BindingValue(String stringToParse, Bindable owner, ExpressionPrettyPrinter prettyPrinter) throws ParseException {
		this(parse(stringToParse, owner), owner, prettyPrinter);
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

	private static List<AbstractBindingPathElement> parse(String stringToParse, Bindable bindable) throws ParseException {
		Expression e = bindable.getBindingFactory().parseExpression(stringToParse, bindable);
		// Expression e = ExpressionParser.parse(stringToParse);
		if (e instanceof BindingValue) {
			return ((BindingValue) e).getParsedBindingPath();
		}
		throw new ParseException("Not parseable as a BindingValue: " + stringToParse);
	}

	/*public DataBinding<?> getDataBinding() {
		return dataBinding;
	}
	
	public synchronized void setDataBinding(DataBinding<?> dataBinding) {
		this.dataBinding = dataBinding;
	}*/

	public Bindable getOwner() {
		return owner;
	}

	public void setOwner(Bindable owner) {
		// if ((owner == null && this.owner != null) || (owner != null && !owner.equals(this.owner))) {
		// Bindable oldValue = this.owner;
		this.owner = owner;
		// getPropertyChangeSupport().firePropertyChange("owner", oldValue, owner);
		// }
		if (DEBUG) {
			checkInternalConsistency();
		}

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
		/*analysingSuccessfull =*/ performSemanticsAnalysis();
		updateParsedBindingPathFromBindingPath();

		if (DEBUG) {
			checkInternalConsistency();
		}

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
		/*analysingSuccessfull =*/ performSemanticsAnalysis();

		// Note:
		// If adding of this BindingPathElement does not lead to a valid DataBinding,
		// We need to update parsed binding path

		if (DEBUG) {
			checkInternalConsistency();
		}

		updateParsedBindingPathFromBindingPath();
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
		/*analysingSuccessfull =*/ performSemanticsAnalysis();

		if (DEBUG) {
			checkInternalConsistency();
		}

		updateParsedBindingPathFromBindingPath();
	}

	public void removeBindingPathAt(int index) {
		BindingPathElement removed = bindingPath.remove(index);
		if (removed.isActivated()) {
			removed.desactivate();
		}
		/*analysingSuccessfull =*/ performSemanticsAnalysis();

		if (DEBUG) {
			checkInternalConsistency();
		}

		updateParsedBindingPathFromBindingPath();
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

	public void setBindingVariable(BindingVariable bindingVariable) {
		clearBindingPathAndBindingVariable();
		internallySetBindingVariable(bindingVariable);
		// Fixed CORE-145
		updateParsedBindingPathFromBindingPath();
		/*analysingSuccessfull =*/ performSemanticsAnalysis();

		if (DEBUG) {
			checkInternalConsistency();
		}

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

	private void markToBeReanalized() {
		// Does't matter, but mark data binding as being reanalyzed
		// if (dataBinding != null) {
		// dataBinding.markedAsToBeReanalized();
		// }
		// TODO: refaire ce qu'il faut faire la !
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
				markToBeReanalized();
			}
		}
		else if (evt.getPropertyName().equals(BindingVariable.TYPE_PROPERTY)) {

			clearSerializationRepresentation();

			// In this case, we detect that our current BindingVariable type has changed
			// We need to mark the DataBinding as being reanalyzed
			markToBeReanalized();
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
				FunctionPathElement<?> functionPathElement = (FunctionPathElement<?>) pathElement;
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
		// bv.needsAnalysing = false;
		// bv.analysingSuccessfull = analysingSuccessfull;
		bv.updateParsedBindingPathFromBindingPath();
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
				if (((MethodCallBindingPathElement) e).args != null) {
					for (Expression arg : ((MethodCallBindingPathElement) e).args) {
						Expression transformedExpression = arg.transform(transformer);
						newArgs.add(transformedExpression);
					}
				}
				newBindingPath.add(new MethodCallBindingPathElement(((MethodCallBindingPathElement) e).method, newArgs));
			}
		}

		BindingValue bv = new BindingValue(newBindingPath, getOwner(), prettyPrinter);
		// bv.needsAnalysing = true;
		// bv.setOwner(getOwner());

		return bv;
	}

	public boolean containsMethodCallWithParameters() {
		for (AbstractBindingPathElement e : new ArrayList<>(getParsedBindingPath())) {
			if (e instanceof MethodCallBindingPathElement && ((MethodCallBindingPathElement) e).args != null
					&& ((MethodCallBindingPathElement) e).args.size() > 0) {
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
				// System.out.println("makeTransformationForValidBindingValue: " + this);
				bv = makeTransformationForValidBindingValue(transformer);
			}
			else {
				// System.out.println("makeTransformationForInvalidBindingValue: " + this);
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
					for (FunctionArgument arg : ((FunctionPathElement<?>) bpe).getArguments()) {
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

	public void invalidate() {
		requiresSemanticsAnalysis = true;
		requiresValidityAnalysis = true;
		clearSerializationRepresentation();
	}

	public void revalidate() {

		// clearSerializationRepresentation();
		// clearBindingPathAndBindingVariable();
		// requiresSemanticsAnalysis = true;
		// requiresValidityAnalysis = true;

		invalidate();

		isValid();

		if (DEBUG) {
			checkInternalConsistency();
		}

	}

	private void clearBindingPathAndBindingVariable() {
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
		requiresSemanticsBuilding = true;
		bindingVariable = null;

	}

	public boolean isValid() {

		if (!requiresValidityAnalysis) {
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

		if (requiresSemanticsBuilding) {
			if (!buildBindingPathFromParsedBindingPath()) {
				return false;
			}
		}

		if (requiresSemanticsAnalysis) {
			if (!performSemanticsAnalysis()) {
				return false;
			}
		}

		isValid = true;
		invalidBindingReason = "valid binding value";
		requiresValidityAnalysis = false;

		return true;
	}

	private void checkInternalConsistency() {

		// System.out.println("Check internal consistency");
		// System.out.println("Parsed BindingPath : " + parsedBindingPath);
		// System.out.println("BindingVariable : " + getBindingVariable());
		// System.out.println("BindingPath : " + getBindingPath());

		if (requiresValidityAnalysis) {
			// Validity was not checked

		}

		else {

			// System.out.println("Valid=" + isValid + " reason=" + invalidBindingReason());

			if (isValid) {
				if (getBindingPath().size() + (getBindingVariable() != null ? 1 : 0) != parsedBindingPath.size()) {
					System.out.println("Inconsistent BindingPath");
					Thread.dumpStack();
					// System.exit(-1);
				}
			}
		}
	}

	private String serializationRepresentation = null;

	public void clearSerializationRepresentation() {
		serializationRepresentation = null;

	}

	private String makeSerializationRepresentation() {
		if (isValid()) {
			return buildSerializationRepresentationFromBindingPath();
		}
		else {
			return buildSerializationRepresentationFromParsedBindingPath();
		}
	}

	private String buildSerializationRepresentationFromParsedBindingPath() {
		StringBuffer sb = new StringBuffer();
		boolean isFirst = true;
		for (AbstractBindingPathElement apbe : parsedBindingPath) {
			sb.append((isFirst ? "" : ".") + apbe.getSerializationRepresentation());
			isFirst = false;
		}
		return sb.toString();
	}

	private String buildSerializationRepresentationFromBindingPath() {
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
			currentType = getBindingVariable().getType();
			currentElement = getBindingVariable();
			if (currentType == null) {
				invalidBindingReason = "currentType is null";
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

		requiresSemanticsAnalysis = false;

		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BindingValue) {
			BindingValue e = (BindingValue) obj;
			return owner == e.getOwner() && getParsedBindingPath().equals(e.getParsedBindingPath());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
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

	// TODO: deprecated as public, turns it to private
	@Deprecated
	public void updateParsedBindingPathFromBindingPath() {

		// System.out.println("updateParsedBindingPathFromBindingPath() called from " + buildSerializationRepresentationFromBindingPath());

		// String before = buildSerializationRepresentationFromBindingPath();

		parsedBindingPath.clear();
		if (getBindingVariable() != null) {
			parsedBindingPath.add(new NormalBindingPathElement(getVariableName()));
		}
		for (BindingPathElement e : bindingPath) {
			parsedBindingPath.add(e.makeUnparsed());
		}
		clearSerializationRepresentation();

		/*String after = buildSerializationRepresentationFromParsedBindingPath();
		if (!before.equals(after)) {
			System.out.println("Bizarre, on passe de " + before + " a " + after);
			System.exit(-1);
		}*/

		// System.out.println("Apres updateParsedBindingPathFromBindingPath le parsed=" + getParsedBindingPath());

		if (DEBUG) {
			checkInternalConsistency();
		}
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
	public synchronized boolean buildBindingPathFromParsedBindingPath() {

		if (getOwner() == null) {
			// LOGGER.warning("DataBinding has no owner");
			invalidBindingReason = "DataBinding has no owner";
			return false;
		}

		if (getOwner().getBindingModel() == null) {
			// LOGGER.warning("DataBinding owner has no binding model, owner=" + dataBinding.getOwner());
			invalidBindingReason = "DataBinding owner has no binding model, owner=" + getOwner();
			return false;
		}

		if (getOwner().getBindingFactory() == null) {
			// LOGGER.warning("DataBinding owner has no binding factory, owner=" + dataBinding.getOwner());
			invalidBindingReason = "DataBinding owner has no binding factory, owner=" + getOwner();
			return false;
		}

		clearBindingPathAndBindingVariable();

		// for (AbstractBindingPathElement element : getParsedBindingPath()) {
		// System.out.println(" >> " + element);
		// }

		if (getOwner() != null && getParsedBindingPath().size() > 0) {

			IBindingPathElement current = null;

			if (getParsedBindingPath().get(0) instanceof NormalBindingPathElement) {
				// Seems to be valid
				internallySetBindingVariable(getOwner().getBindingModel()
						.bindingVariableNamed(((NormalBindingPathElement) getParsedBindingPath().get(0)).property));
				current = bindingVariable;
				// System.out.println("Found binding variable " + bindingVariable);
				if (bindingVariable == null) {
					invalidBindingReason = "cannot find binding variable "
							+ ((NormalBindingPathElement) getParsedBindingPath().get(0)).property + " BindingModel="
							+ getOwner().getBindingModel();
					return false;
				}
			}

			int i = 0;

			// System.out.println("Analysing BindingValue " + Integer.toHexString(hashCode()) + " with " + getParsedBindingPath());

			for (AbstractBindingPathElement pathElement : new ArrayList<>(getParsedBindingPath())) {

				if (bindingVariable == null || i > 0) {
					if (pathElement instanceof NormalBindingPathElement) {
						SimplePathElement newPathElement = getOwner().getBindingFactory().makeSimplePathElement(current,
								((NormalBindingPathElement) pathElement).property);

						String p = ((NormalBindingPathElement) pathElement).property;
						if (newPathElement != null) {
							if (!newPathElement.isActivated()) {
								newPathElement.activate();
							}
							bindingPath.add(newPathElement);
							current = newPathElement;
							// System.out.println("> SIMPLE " + pathElement);
						}
						else {
							// analysingSuccessfull = false;
							invalidBindingReason = "cannot find property " + ((NormalBindingPathElement) pathElement).property
									+ " for element " + current + " and type " + TypeUtils.simpleRepresentation(current.getType())
									+ " owner=" + getOwner() + " factory=" + getOwner().getBindingFactory();
							return false;
						}
					}
					else if (pathElement instanceof MethodCallBindingPathElement && current != null) {
						MethodCallBindingPathElement methodCall = (MethodCallBindingPathElement) pathElement;
						List<DataBinding<?>> args = buildArgs(methodCall.args);
						Function function = getOwner().getBindingFactory().retrieveFunction(current.getType(),
								((MethodCallBindingPathElement) pathElement).method, args);
						if (function != null) {
							FunctionPathElement newPathElement = getOwner().getBindingFactory().makeFunctionPathElement(current, function,
									null, args);
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
								invalidBindingReason = "cannot find method " + ((MethodCallBindingPathElement) pathElement).method
										+ " for type " + TypeUtils.simpleRepresentation(current.getType()) + " and args=" + args;
								return false;
							}
						}
					}
					else if (pathElement instanceof StaticMethodCallBindingPathElement) {
						StaticMethodCallBindingPathElement staticMethodCall = (StaticMethodCallBindingPathElement) pathElement;
						List<DataBinding<?>> args = buildArgs(staticMethodCall.args);
						Function staticFunction = getOwner().getBindingFactory().retrieveFunction(staticMethodCall.getType(),
								((StaticMethodCallBindingPathElement) pathElement).method, args);
						if (staticFunction != null) {
							FunctionPathElement newPathElement = getOwner().getBindingFactory().makeFunctionPathElement(current,
									staticFunction, null, args);
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
								invalidBindingReason = "(2) cannot find static method "
										+ ((StaticMethodCallBindingPathElement) pathElement).method + " for type "
										+ TypeUtils.simpleRepresentation(current.getType()) + " and args=" + args;
								// analysingSuccessfull = false;
								return false;
							}
						}
						else {
							invalidBindingReason = "(1) cannot find static method "
									+ ((StaticMethodCallBindingPathElement) pathElement).method + " for type "
									+ TypeUtils.simpleRepresentation(current.getType()) + " and args=" + args;
							return false;
						}
					}
					else if (pathElement instanceof NewInstanceBindingPathElement) {
						NewInstanceBindingPathElement newInstanceCall = (NewInstanceBindingPathElement) pathElement;
						List<DataBinding<?>> newInstanceArgs = buildArgs(newInstanceCall.args);
						Function constructor;
						FunctionPathElement<?> newPathElement;
						if (getBindingVariable() != null || bindingPath.size() > 0) {
							// There is a inner access
							DataBinding<?> innerAccess = makeSubBindingValue(bindingPath.size());
							// System.out.println("Inner access: " + innerAccess);
							// System.out.println("valid: " + innerAccess.isValid());
							// System.out.println("type: " + innerAccess.getAnalyzedType());
							constructor = getOwner().getBindingFactory().retrieveConstructor(newInstanceCall.type, innerAccess,
									newInstanceCall.constructorName, newInstanceArgs);
							newPathElement = getOwner().getBindingFactory().makeFunctionPathElement(current, constructor, innerAccess,
									newInstanceArgs);
						}
						else {
							constructor = getOwner().getBindingFactory().retrieveConstructor(newInstanceCall.type,
									newInstanceCall.constructorName, newInstanceArgs);
							newPathElement = getOwner().getBindingFactory().makeFunctionPathElement(current, constructor, null,
									newInstanceArgs);
						}
						// System.out.println("constructor=" + constructor);

						if (constructor != null) {
							if (newPathElement != null) {
								if (!newPathElement.isActivated()) {
									newPathElement.activate();
								}
								bindingPath.add(newPathElement);
								current = newPathElement;
							}
							else {
								invalidBindingReason = "cannot find constructor " + newInstanceCall.constructorName + " for type "
										+ TypeUtils.simpleRepresentation(newInstanceCall.type) + " and args=" + newInstanceArgs;
								return false;
							}
						}
						else {
							invalidBindingReason = "cannot find constructor " + newInstanceCall.constructorName + " for type "
									+ TypeUtils.simpleRepresentation(newInstanceCall.type) + " and args=" + newInstanceArgs;
							return false;
						}

					}
					else {
						// LOGGER.warning("Unexpected " + pathElement);
						invalidBindingReason = "unexpected path element: " + pathElement;
						return false;
					}
				}
				i++;
			}
			invalidBindingReason = "Valid";
			requiresSemanticsBuilding = false;

			if (DEBUG) {
				checkInternalConsistency();
			}

			return true;
		}
		else {
			invalidBindingReason = "Empty binding";
			return false;
		}
	}

	private List<DataBinding<?>> buildArgs(List<Expression> argExpressions) {

		List<DataBinding<?>> returned = new ArrayList<>();

		if (argExpressions == null) {
			return returned;
		}

		int argIndex = 0;
		for (Expression arg : argExpressions) {
			// System.out.println("--- Ici dans buildArgs, arg=" + arg);
			DataBinding<?> argDataBinding = new DataBinding<>(getOwner(), Object.class, DataBinding.BindingDefinitionType.GET);
			argDataBinding.setBindingName("arg" + argIndex);
			if (arg != null) {
				// Avoid to notify yet, otherwise it may loop
				argDataBinding.setExpression(arg, false);
			}
			else {
				if (getOwner() != null) {
					argDataBinding.setExpression(getOwner().getBindingFactory().getNullExpression());
				}
			}
			// argDataBinding.setDeclaredType(Object.class);
			// IMPORTANT/HACK: following statement (call to
			// isValid()) is required to get access to analyzed
			// type and
			// declares it
			// TODO: find a better solution
			// argDataBinding.isValid();

			if (argDataBinding.getAnalyzedType() != null) {
				argDataBinding.setDeclaredType(argDataBinding.getAnalyzedType());
			}
			returned.add(argDataBinding);
			argIndex++;
		}
		return returned;
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

			if (DEBUG) {
				checkInternalConsistency();
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

		if (DEBUG) {
			checkInternalConsistency();
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
			return "Call[" + method + "(" + (args != null ? args : "") + ")" + "]";
		}

		@Override
		public String getSerializationRepresentation() {
			StringBuffer sb = new StringBuffer();
			sb.append(method + "(");
			boolean isFirst = true;
			if (args != null) {
				for (Expression arg : args) {
					sb.append((isFirst ? "" : ",") + arg);
					isFirst = false;
				}
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

	public static class NewInstanceBindingPathElement extends AbstractBindingPathElement {
		public Type type;
		public String constructorName;
		public List<Expression> args;

		public NewInstanceBindingPathElement(Type aType, String aConstructorName, List<Expression> someArgs) {
			type = aType;
			constructorName = aConstructorName;
			args = someArgs;
		}

		public Type getType() {
			return type;
		}

		@Override
		public String toString() {
			return "NewInstance[" + type + "(" + args + ")" + "]";
		}

		@Override
		public String getSerializationRepresentation() {
			StringBuffer sb = new StringBuffer();
			sb.append("new " + TypeUtils.simpleRepresentation(type) + "(");
			boolean isFirst = true;
			if (args != null) {
				for (Expression arg : args) {
					sb.append((isFirst ? "" : ",") + arg);
					isFirst = false;
				}
			}
			sb.append(")");
			return sb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((args == null) ? 0 : args.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NewInstanceBindingPathElement other = (NewInstanceBindingPathElement) obj;
			if (args == null) {
				if (other.args != null)
					return false;
			}
			else if (!args.equals(other.args))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			}
			else if (!type.equals(other.type))
				return false;
			return true;
		}

	}

	public static class StaticMethodCallBindingPathElement extends AbstractBindingPathElement {
		public Type type;
		public String method;
		public List<Expression> args;

		public StaticMethodCallBindingPathElement(Type aType, String aMethod, List<Expression> someArgs) {
			type = aType;
			method = aMethod;
			args = someArgs;
		}

		public Type getType() {
			return type;
		}

		@Override
		public String toString() {
			return "StaticCall[" + type + "." + method + "(" + args + ")" + "]";
		}

		@Override
		public String getSerializationRepresentation() {
			StringBuffer sb = new StringBuffer();
			sb.append(TypeUtils.simpleRepresentation(type) + "." + method + "(");
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
			final int prime = 31;
			int result = 1;
			result = prime * result + ((args == null) ? 0 : args.hashCode());
			result = prime * result + ((method == null) ? 0 : method.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			StaticMethodCallBindingPathElement other = (StaticMethodCallBindingPathElement) obj;
			if (args == null) {
				if (other.args != null)
					return false;
			}
			else if (!args.equals(other.args))
				return false;
			if (method == null) {
				if (other.method != null)
					return false;
			}
			else if (!method.equals(other.method))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			}
			else if (!type.equals(other.type))
				return false;
			return true;
		}

	}

}
