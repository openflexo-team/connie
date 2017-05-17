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
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.connie.annotations.NotificationUnsafe;
import org.openflexo.connie.binding.BindingDefinition;
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.binding.LazyBindingValueChangeListener;
import org.openflexo.connie.binding.TargetObject;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.CastExpression;
import org.openflexo.connie.expr.ConditionalExpression;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.Constant.StringConstant;
import org.openflexo.connie.expr.EvaluationType;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionTransformer;
import org.openflexo.connie.expr.ExpressionVisitor;
import org.openflexo.connie.expr.UnresolvedExpression;
import org.openflexo.connie.expr.VisitorException;
import org.openflexo.connie.expr.parser.ExpressionParser;
import org.openflexo.connie.expr.parser.ParseException;
import org.openflexo.connie.type.ExplicitNullType;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.StringUtils;

/**
 * Representation of a data binding.<br>
 * A data binding is defined as a symbolic expression defined on a model abstraction (the binding model), which can be evaluated given a
 * {@link BindingEvaluationContext}.
 * 
 * @author sylvain
 * 
 * @param <T>
 */
public class DataBinding<T> implements HasPropertyChangeSupport, PropertyChangeListener, Cloneable {

	private static final Logger LOGGER = Logger.getLogger(DataBinding.class.getPackage().getName());

	/**
	 * Defines the access type of a binding, which is generally related to the purpose of the binding
	 * <ul>
	 * <li>GET: a binding used to retrieve a data</li>
	 * <li>SET: a binding used to set a data</li>
	 * <li>GET_SET: a binding used to retrieve and set a data</li>
	 * <li>EXECUTE: a binding used to execute some code</li>
	 * </ul>
	 */
	public enum BindingDefinitionType {
		GET, /* GET: a binding used to retrieve a data */
		SET, /* SET: a binding used to set a data */
		GET_SET, /* GET_SET: a binding used to retrieve and set a data */
		EXECUTE /* */
	}

	public enum CachingStrategy {
		NO_CACHING, /* Do not cache, execute unconditionally */
		OPTIMIST_CACHE, /*
						* Always cache executed value, fully rely on
						* notification schemes
						*/
		PRAGMATIC_CACHE /*
						* Do no cache when a property is declared as not safe
						* according to notification schemes, see
						* NotificationUnsafe annotation
						*/
	}

	private static final CachingStrategy DEFAULT_CACHING_STRATEGY = CachingStrategy.NO_CACHING; // CachingStrategy.PRAGMATIC_CACHE;

	private Bindable owner;
	private String unparsedBinding;
	private BindingDefinition bindingDefinition;
	private Expression expression;

	private Type declaredType = null;
	private DataBinding.BindingDefinitionType bdType = null;
	private boolean mandatory = false;
	// TODO : XtoF, first attempt to have better performances
	// Sylvain: the caching is now performed for a given BindingModel
	// We assume here that the type model is not dynamic, only BindingVariable
	// name and type changing are notified
	// If type model is not dynamic, use setCacheable(false)
	private boolean wasValid = false;
	private BindingModel bindingModelOnWhichValidityWasTested = null;
	private String invalidBindingReason;

	private boolean needsParsing = false;
	private String bindingName;

	private CachingStrategy cachingStrategy = DEFAULT_CACHING_STRATEGY;

	private PropertyChangeSupport pcSupport;

	private Map<BindingEvaluationContext, T> cachedValues = null;
	private Map<BindingEvaluationContext, BindingValueChangeListener<T>> cachedBindingValueChangeListeners = null;

	public DataBinding(Bindable owner, Type declaredType, DataBinding.BindingDefinitionType bdType) {
		this.declaredType = declaredType;
		this.bdType = bdType;
		pcSupport = new PropertyChangeSupport(this);
		setOwner(owner);
		initCache();
	}

	public DataBinding(String unparsed, Bindable owner, Type declaredType, DataBinding.BindingDefinitionType bdType) {
		this(owner, declaredType, bdType);
		setUnparsedBinding(unparsed);
	}

	public DataBinding(String unparsed) {
		super();
		pcSupport = new PropertyChangeSupport(this);
		setUnparsedBinding(unparsed);
		initCache();
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public CachingStrategy getCachingStrategy() {
		if (cachingStrategy == null) {
			return DEFAULT_CACHING_STRATEGY;
		}
		return cachingStrategy;
	}

	public void setCachingStrategy(CachingStrategy cachingStrategy) {
		this.cachingStrategy = cachingStrategy;
		initCache();
	}

	private void initCache() {
		if (cachingStrategy == CachingStrategy.NO_CACHING) {
			cachedValues = null;
			cachedBindingValueChangeListeners = null;
		}
		else {
			cachedValues = new HashMap<>();
			cachedBindingValueChangeListeners = new HashMap<>();
		}
	}

	@Override
	public String toString() {
		if (expression != null) {
			return expression.toString();
		}
		if (StringUtils.isEmpty(unparsedBinding)) {
			return "";
		}
		return unparsedBinding;
	}

	@Deprecated
	public BindingDefinition getBindingDefinition() {
		if (bindingDefinition == null) {
			bindingDefinition = new BindingDefinition("unamed", declaredType, bdType, false);
		}
		return bindingDefinition;
	}

	@Deprecated
	public void setBindingDefinition(BindingDefinition bindingDefinition) {
		this.bindingDefinition = bindingDefinition;
		declaredType = bindingDefinition.getType();
		bdType = bindingDefinition.getBindingDefinitionType();
	}

	public void decode() {
		if (needsParsing) {
			parseExpression();
		}
	}

	public Expression getExpression() {
		decode();
		return expression;
	}

	/*
	 * public void setBinding(AbstractBinding binding) { this.binding = binding;
	 * }
	 */

	public void setExpression(Expression value) {
		// logger.info("setExpression() with " + value);
		needsParsing = false;
		wasValid = false;
		Expression oldValue = this.expression;
		if (oldValue == null) {
			if (value == null) {
				return; // No change
			}
			else {
				this.expression = value;
				unparsedBinding = value != null ? value.toString() : null;
				// analyseExpressionAfterParsing();
				notifyBindingChanged(oldValue, value);
				return;
			}
		}
		else {
			if (oldValue.equals(value)) {
				return; // No change
			}
			else {
				this.expression = value;
				unparsedBinding = value != null ? expression.toString() : null;
				LOGGER.info("Binding takes now value " + value);
				// analyseExpressionAfterParsing();
				notifyBindingChanged(oldValue, value);
				return;
			}
		}
	}

	public boolean isSettable() {
		if (getExpression() != null) {
			return getExpression().isSettable();
		}
		return false;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getBindingName() {
		return bindingName;
	}

	public void setBindingName(String bindingName) {
		// if (this.bindingName != bindingName) {
		if (bindingName != null && !bindingName.equals(this.bindingName)) {
			String oldBindingName = this.bindingName;
			this.bindingName = bindingName;
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange("bindingName", oldBindingName, bindingName);
			}
		}
	}

	public Type getDeclaredType() {
		return declaredType;
	}

	public void setDeclaredType(Type aDeclaredType) {
		declaredType = aDeclaredType;
		if (bindingDefinition != null) {
			bindingDefinition.setType(aDeclaredType);
		}
	}

	public DataBinding.BindingDefinitionType getBindingDefinitionType() {
		return bdType;
	}

	public void setBindingDefinitionType(DataBinding.BindingDefinitionType aBDType) {
		bdType = aBDType;
		if (bindingDefinition != null) {
			bindingDefinition.setBindingDefinitionType(aBDType);
		}
	}

	public Type getAnalyzedType() {
		if (isNull()) {
			return ExplicitNullType.INSTANCE;
		}
		if (getExpression() != null) {
			if (getExpression() instanceof BindingValue) {
				// return ((BindingValue)
				// getExpression()).getAccessedTypeNoValidityCheck();
				return ((BindingValue) getExpression()).getAccessedType();
			}
			else if (getExpression() instanceof CastExpression) {
				return ((CastExpression) getExpression()).getCastType().getType();
			}
			else if (expression instanceof Constant) {
				return ((Constant<?>) expression).getType();
			}
			else if (expression instanceof ConditionalExpression) {
				return ((ConditionalExpression) expression).getAccessedType();
			}
			else {
				try {
					return getExpression().getEvaluationType().getType();
				} catch (TypeMismatchException e) {
					return Object.class;
				}
			}
		}
		return Object.class;
	}

	public static class InvalidBindingValue extends VisitorException {
		private final BindingValue bindingValue;

		public InvalidBindingValue(BindingValue e) {
			bindingValue = e;
		}

		public BindingValue getBindingValue() {
			return bindingValue;
		}
	}

	/**
	 * Explicitely called when a structural modification of data occurs, and when the validity status of the {@link DataBinding} might have
	 * changed<br>
	 * Calling this method will force the next call of isValid() to force recompute the {@link DataBinding} validity status and message
	 */
	// TODO: this should be private
	@Deprecated
	public void markedAsToBeReanalized() {

		bindingModelOnWhichValidityWasTested = null;
		wasValid = false;
		if (expression != null) {
			try {
				expression.visit(new ExpressionVisitor() {
					@Override
					public void visit(Expression e) throws InvalidBindingValue {
						if (e instanceof BindingValue) {
							((BindingValue) e).markedAsToBeReanalized();
						}
					}
				});
			} catch (VisitorException e) {
				e.printStackTrace();
			}
		}

	}

	public void revalidate() {
		markedAsToBeReanalized();
		isValid();
	}

	public boolean isValid() {

		invalidBindingReason = "unknown";

		if (getOwner() == null) {
			invalidBindingReason = "null owner";
			wasValid = false;
			return wasValid;
		}

		if (getOwner().getBindingModel() == null) {
			invalidBindingReason = "owner has null BindingModel";
			wasValid = false;
			return false;
		}

		if (wasValid && getOwner().getBindingModel() == bindingModelOnWhichValidityWasTested) {
			// Use cache info to test DataBinding validity
			return true;
		}
		// TODO: implements and use correct equals() implementation on
		// BindingModel class
		/*
		 * if (cacheable && wasValid) {
		 * logger.info("Already tested for an other BindingModel");
		 * logger.info("Tested on " + bindingModelOnWhichValidityWasTested);
		 * logger.info("Should be now tested on " +
		 * getOwner().getBindingModel()); }
		 */

		if (getExpression() == null) {
			invalidBindingReason = "null expression";
			wasValid = false;
			return false;
		}

		bindingModelOnWhichValidityWasTested = getOwner().getBindingModel();

		isCacheable = true;

		if (getOwner() != null) {

			try {
				expression.visit(new ExpressionVisitor() {
					@Override
					public void visit(Expression e) throws InvalidBindingValue {
						if (e instanceof BindingValue) {
							if (!((BindingValue) e).isValid(DataBinding.this)) {
								((BindingValue) e).markedAsToBeReanalized();
							}
							if (!((BindingValue) e).isValid(DataBinding.this)) {
								// System.out.println("Invalid binding " + e);
								throw new InvalidBindingValue((BindingValue) e);
							}
							else {
								// System.out.println("Valid binding " + e);
							}
							if (!((BindingValue) e).isCacheable()) {
								isCacheable = false;
							}
						}
					}
				});
			} catch (InvalidBindingValue e) {
				invalidBindingReason = "Invalid binding value: " + e.getBindingValue() + " reason: "
						+ e.getBindingValue().invalidBindingReason();
				wasValid = false;
				return false;
			} catch (VisitorException e) {
				invalidBindingReason = "Unexpected visitor exception: " + e.getMessage();
				LOGGER.warning("TransformException while transforming " + expression);
				wasValid = false;
				return false;
			}
		}

		if (getAnalyzedType() == null) {
			invalidBindingReason = "Invalid binding because accessed type is null";
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Invalid binding because accessed type is null");
			}
			wasValid = false;
			return false;
		}

		if (getBindingDefinitionType() == DataBinding.BindingDefinitionType.SET
				|| getBindingDefinitionType() == DataBinding.BindingDefinitionType.GET_SET) {

			if (!getExpression().isSettable()) {
				invalidBindingReason = "Invalid binding because binding declared as settable and definition cannot satisfy it";
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine(
							"Invalid binding because binding definition declared as settable and definition cannot satisfy it (binding variable not settable)");
				}
				wasValid = false;
				return false;
			}
		}

		// NO need to check target type for EXECUTE bindings (we don't need
		// return type nor value)
		if (getBindingDefinitionType() == DataBinding.BindingDefinitionType.EXECUTE) {
			wasValid = true;
			return true;
		}

		if (isNull()) {
			// A null expression is valid (otherwise return Object.class as
			// analyzed type, and type checking will fail in next test
			wasValid = true;
			return true;
		}

		if (getDeclaredType() != null && TypeUtils.isTypeAssignableFrom(getDeclaredType(), getAnalyzedType(), true)) {
			// System.out.println("getBindingDefinition().getType()="+getBindingDefinition().getType());
			// System.out.println("getAccessedType()="+getAccessedType());
			invalidBindingReason = "valid binding";
			wasValid = true;
			return true;
		}

		invalidBindingReason = "Invalid binding " + this + " because types are not matching searched " + getDeclaredType() + " having "
				+ getAnalyzedType();
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Invalid binding " + this + " because types are not matching searched " + getDeclaredType() + " having "
					+ getAnalyzedType());
		}
		wasValid = false;
		return false;
	}

	public String invalidBindingReason() {
		return invalidBindingReason;
	}

	private boolean isCacheable = true;

	/**
	 * Return boolean indicating if this {@link BindingValue} is notification-safe<br>
	 * 
	 * A {@link BindingValue} is unsafe when any involved method is annotated with {@link NotificationUnsafe} annotation<br>
	 * Otherwise return true
	 * 
	 * @return
	 */
	public boolean isCacheable() {

		// Computes it and cache it during isValid() computation
		isValid();

		return isCacheable;

	}

	public boolean isSet() {
		return unparsedBinding != null || getExpression() != null;
	}

	public boolean isUnset() {
		return unparsedBinding == null && getExpression() == null;
	}

	public void reset() {
		unparsedBinding = null;
		expression = null;
	}

	public boolean isExpression() {
		return getExpression() != null && !(getExpression() instanceof Constant) && !(getExpression() instanceof BindingValue);
	}

	public boolean isBindingValue() {
		return getExpression() != null && getExpression() instanceof BindingValue;
	}

	public boolean isConstant() {
		return getExpression() != null && getExpression() instanceof Constant;
	}

	public boolean isNull() {
		return getExpression() != null && getExpression() == Constant.ObjectSymbolicConstant.NULL;
	}

	public boolean isStringConstant() {
		return getExpression() != null && getExpression() instanceof StringConstant;
	}

	public boolean isCompoundBinding() {
		return isBindingValue() && ((BindingValue) getExpression()).isCompoundBinding();
	}

	public String getUnparsedBinding() {
		return unparsedBinding;
	}

	public void setUnparsedBinding(String unparsedBinding) {
		if (StringUtils.isEmpty(unparsedBinding)) {
			this.unparsedBinding = null;
			expression = null;
			needsParsing = false;
		}
		else {
			this.unparsedBinding = unparsedBinding;
			expression = null;
			needsParsing = true;
		}
	}

	public Bindable getOwner() {
		return owner;
	}

	public void setOwner(Bindable owner) {
		if (this.owner != owner) {

			// If owner change, we have to listen both owner and owner's binding
			// model

			if (this.owner != null && this.owner.getPropertyChangeSupport() != null) {
				this.owner.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			if (this.owner != null && this.owner.getBindingModel() != null) {
				this.owner.getBindingModel().getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			this.owner = owner;
			if (owner != null && owner.getPropertyChangeSupport() != null) {
				owner.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			if (owner != null && owner.getBindingModel() != null && owner.getBindingModel().getPropertyChangeSupport() != null) {
				owner.getBindingModel().getPropertyChangeSupport().addPropertyChangeListener(this);
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// System.out.println("> Received propertyName=" + evt.getPropertyName()
		// + " evt=" + evt);

		// Track BindingModel changes
		// We detect here that the owner of this DataBinding has changed its
		// BindingModel
		if (evt.getSource() == owner && evt.getPropertyName() != null && evt.getPropertyName().equals(Bindable.BINDING_MODEL_PROPERTY)) {
			// System.out.println("BindingModel changed for " + getOwner());
			// System.out.println("was: " + evt.getOldValue());
			// System.out.println("now: " + evt.getNewValue());
			if (evt.getOldValue() instanceof BindingModel) {
				((BindingModel) evt.getOldValue()).getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			if (evt.getNewValue() instanceof BindingModel) {
				((BindingModel) evt.getNewValue()).getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			markedAsToBeReanalized();
		}

		// Track structural changes inside a BindingModel
		if (getOwner() != null && evt.getSource() == owner.getBindingModel()) {
			if (evt.getPropertyName().equals(BindingModel.BINDING_VARIABLE_PROPERTY)) {
				// We detect here that a BindingVariable was added or removed
				// from BindingModel
				markedAsToBeReanalized();
			}
			else if (evt.getPropertyName().equals(BindingModel.BINDING_VARIABLE_NAME_CHANGED)) {
				// We detect here that a BindingVariable has changed its name,
				// we should reanalyze the binding
				markedAsToBeReanalized();
			}
			else if (evt.getPropertyName().equals(BindingModel.BINDING_VARIABLE_TYPE_CHANGED)) {
				// We detect here that a BindingVariable has changed its type,
				// we should reanalyze the binding
				markedAsToBeReanalized();
			}
			else if (evt.getPropertyName().equals(BindingModel.BINDING_PATH_ELEMENT_NAME_CHANGED)) {
				// We detect here that a BindingVariable has changed its name,
				// we should reanalyze the binding
				markedAsToBeReanalized();
			}
			else if (evt.getPropertyName().equals(BindingModel.BINDING_PATH_ELEMENT_TYPE_CHANGED)) {
				// We detect here that a BindingVariable has changed its type,
				// we should reanalyze the binding
				markedAsToBeReanalized();
			}
			else if (evt.getPropertyName().equals(BindingModel.BASE_BINDING_MODEL_PROPERTY)) {
				// We detect here that base BindingModel has changed
				markedAsToBeReanalized();
			}
		}
	}

	/**
	 * This method is called whenever we need to parse the binding using string encoded in unparsedBinding field.<br>
	 * Syntaxic checking of the binding is performed here. This phase is followed by the semantics analysis as performed by
	 * {@link #analyseExpressionAfterParsing()} method
	 * 
	 * @return
	 */
	private Expression parseExpression() {
		if (getUnparsedBinding() == null) {
			return expression = null;
		}

		if (getOwner() != null) {
			try {
				expression = ExpressionParser.parse(getUnparsedBinding());
			} catch (ParseException e1) {
				// parse error
				expression = null;
				// logger.warning(e1.getMessage());
				return null;
			}
			needsParsing = false;
			analyseExpressionAfterParsing();
		}
		needsParsing = false;

		/*if (!isValid()) {
			LOGGER.warning("Invalid binding " + getUnparsedBinding() + " reason: " + invalidBindingReason() + " " + (getOwner() != null
					? "BindingModel=" + getOwner().getBindingModel() + " BindingFactory=" + getOwner().getBindingFactory() : ""));
			//
			// Bindable owner = getOwner(); BindingModel bm =
			// getOwner().getBindingModel(); BindingFactory bf =
			// getOwner().getBindingFactory(); logger.info("Breakpoint");
			//
		}*/

		return expression;
	}

	private Expression analyseExpressionAfterParsing() {
		if (getOwner() != null) {
			// System.out.println("Analysing " + this + " unparsedBinding=" +
			// unparsedBinding);
			try {
				expression.visit(new ExpressionVisitor() {
					@Override
					public void visit(Expression e) {
						if (e instanceof BindingValue) {
							// System.out.println("> Analyse " + e);
							((BindingValue) e).buildBindingPathFromParsedBindingPath(DataBinding.this);
						}
					}
				});
			} catch (VisitorException e) {
				LOGGER.warning("Unexpected " + e);
			}
		}

		notifyBindingDecoded();
		return expression;
	}

	public void notifyBindingChanged(Expression oldValue, Expression newValue) {
		if (getOwner() != null) {
			getOwner().notifiedBindingChanged(this);
			// logger.info("notifyBindingChanged from " + oldValue + " to " +
			// newValue + " of " + newValue.getClass());
		}
	}

	public void notifyBindingDecoded() {
		if (getOwner() != null) {
			getOwner().notifiedBindingDecoded(this);
		}
	}

	/**
	 * Evaluate this binding in run-time evaluation context provided by supplied {@link BindingEvaluationContext} parameter. This evaluation
	 * is performed in READ_ONLY mode.
	 * 
	 * @param context
	 * @return
	 * @throws TypeMismatchException
	 * @throws NullReferenceException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public T getBindingValue(final BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException, InvocationTargetException {

		if (((getBindingDefinitionType() == BindingDefinitionType.GET) || (getBindingDefinitionType() == BindingDefinitionType.GET_SET))
				&& ((getCachingStrategy() == CachingStrategy.OPTIMIST_CACHE)
						|| (getCachingStrategy() == CachingStrategy.PRAGMATIC_CACHE && isCacheable()))) {
			// Caching will be done ONLY if:
			// - Type of binding should have GET feature (EXECUTE bindings
			// should NEVER be cached, or a new execution will be fired)
			// - Caching Strategy if either OPTIMIST_CACHE or PRAGMATIC_CACHE if
			// the binding is declared as NotificationSafe
			if (cachedValues.containsKey(context)) {
				// System.out.println("[CACHED] " + this + " value=" +
				// cachedValues.get(context) + " for " + context);
				return cachedValues.get(context);
			}
		}

		// System.out.println("Evaluating " + this + " in context " + context);

		// boolean debug = false;

		/*
		 * if (expression instanceof BindingValue &&
		 * toString().equals("data.layout") && context.getValue(((BindingValue)
		 * expression
		 * ).getBindingVariable()).getClass().getSimpleName().equals("FIBPanel"
		 * )) { System.out.println("Getting the point..."); debug = true; }
		 */

		// First we check that the binding is valid, otherwise we don't go
		// further
		if (isValid()) {

			try {

				// We then apply a transformation on all BindingValue found in
				// binding's expression, to evaluate them in the run-time
				// context provided by supplied {@link BindingEvaluationContext}
				// parameter
				// If a NullReferenceException is raised, the underlying
				// expression is replaced by an UnresolvedExpression, in order
				// to
				// keep the later ability to eventually resolve the binding even
				// if a part of the expression cannot be resolved.
				// Think for example to AND operator. If left operand is FALSE,
				// any evaluation of right operand is valid to determine
				// that binding value is FALSE.

				Expression resolvedExpression = expression.transform(new ExpressionTransformer() {
					@Override
					public Expression performTransformation(Expression e) throws TransformException {
						if (e instanceof BindingValue) {
							((BindingValue) e).setDataBinding(DataBinding.this);
							try {
								Object o = ((BindingValue) e).getBindingValue(context);
								// System.out.println("For " + e + " getting " +
								// o);
								return Constant.makeConstant(o);
							} catch (NullReferenceException nre) {
								// System.out.println("NullReferenceException for "
								// + e);
								return new UnresolvedExpression();
							}
						}
						return e;
					}
				});

				// At this point, all BindingValue are resolved, then evaluate
				// the expression itself

				Expression evaluatedExpression = resolvedExpression.evaluate();

				T returned = null;

				if (evaluatedExpression instanceof CastExpression) {
					Expression argument = ((CastExpression) evaluatedExpression).getArgument();
					if (argument instanceof Constant) {
						// Special case for Files to be converted from Strings
						if (declaredType == File.class && argument.getEvaluationType() == EvaluationType.STRING) {
							return (T) new File((String) ((Constant<?>) argument).getValue());
						}
						returned = (T) ((Constant<?>) ((CastExpression) evaluatedExpression).getArgument()).getValue();
					}
				}

				else if (evaluatedExpression instanceof Constant) {
					Class<?> baseClassForType = TypeUtils.getBaseClass(getDeclaredType());
					if (baseClassForType != null && Number.class.isAssignableFrom(baseClassForType)) {
						return (T) TypeUtils.castTo(((Constant<?>) evaluatedExpression).getValue(), getDeclaredType());
					}
					returned = (T) ((Constant<?>) evaluatedExpression).getValue();
				}

				else {
					// We do not warn anymore since this situation happens very
					// often
					// logger.warning("Cannot evaluate " + expression +
					// " max reduction is " + evaluatedExpression +
					// " resolvedExpression="
					// + resolvedExpression);

					return null;
				}

				// System.out.println("[EXECUTE] " + this + " value=" + returned
				// + " for " + context);

				if (((getBindingDefinitionType() == BindingDefinitionType.GET)
						|| (getBindingDefinitionType() == BindingDefinitionType.GET_SET))
						&& ((getCachingStrategy() == CachingStrategy.OPTIMIST_CACHE)
								|| (getCachingStrategy() == CachingStrategy.PRAGMATIC_CACHE && isCacheable()))) {

					// Caching will be done ONLY if:
					// - Type of binding should have GET feature (EXECUTE
					// bindings should NEVER be cached, or a new execution will
					// be fired)
					// - Caching Strategy if either OPTIMIST_CACHE or
					// PRAGMATIC_CACHE if the binding is declared as
					// NotificationSafe
					cachedValues.put(context, returned);
					// System.out.println("[CACHING] " + this + " value=" +
					// cachedValues.get(context) + " for " + context);

					BindingValueChangeListener<T> listener = cachedBindingValueChangeListeners.get(context);
					if (listener == null) {
						listener = new LazyBindingValueChangeListener<T>(this, context) {
							@Override
							public void bindingValueChanged(Object source) {
								// System.out.println("Detected DataBinding evaluation changed for "
								// + DataBinding.this);
							}
						};
						cachedBindingValueChangeListeners.put(context, listener);
					}
				}

				return returned;

			} catch (NullReferenceException e1) {
				throw e1;
			} catch (TypeMismatchException e1) {
				throw e1;
			} catch (InvocationTargetTransformException e1) {
				throw e1.getException();
			} catch (TransformException e1) {
				LOGGER.warning("Unexpected TransformException while evaluating " + expression + " " + e1.getMessage());
				e1.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * Evaluate this binding in run-time evaluation context provided by supplied {@link BindingEvaluationContext} parameter. This evaluation
	 * is performed in WRITE mode.
	 * 
	 * @param context
	 * @throws TypeMismatchException
	 * @throws NullReferenceException
	 * @throws InvocationTargetException
	 * @throws NotSettableContextException
	 */
	public void setBindingValue(Object value, BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException, InvocationTargetException, NotSettableContextException {
		if (isValid() && isSettable()) {
			if (isBindingValue()) {
				// BindingValue is settable
				try {
					((BindingValue) getExpression()).setBindingValue(value, context);
				} catch (InvocationTargetTransformException e) {
					throw e.getException();
				}
			}
			else if ((getExpression() instanceof CastExpression)
					&& (((CastExpression) getExpression()).getArgument() instanceof BindingValue)) {
				// A Cast expression for a BindingValue is also settable
				try {
					((BindingValue) ((CastExpression) getExpression()).getArgument()).setBindingValue(value, context);
				} catch (InvocationTargetTransformException e) {
					throw e.getException();
				}
			}
			else {
				LOGGER.warning("Don't know how to set binding: " + this);
			}
		}
		else {
			if (!isValid()) {
				LOGGER.warning("Trying to set value: invalid binding " + getUnparsedBinding() + " reason=" + invalidBindingReason());
			}
			else {
				LOGGER.warning("Trying to set value: not settable binding " + this);
			}
		}
	}

	public void execute(final BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException, InvocationTargetException {
		getBindingValue(context);
	}

	/**
	 * Build and return a list of objects involved in the computation of this data binding with supplied binding evaluation context
	 * 
	 * @param context
	 * @return
	 */
	public List<Object> getConcernedObjects(final BindingEvaluationContext context) {
		if (!isValid()) {
			return Collections.emptyList();
		}
		if (!isSettable()) {
			return Collections.emptyList();
		}

		final List<Object> returned = new ArrayList<>();

		try {
			expression.visit(new ExpressionVisitor() {
				@Override
				public void visit(Expression e) {
					if (e instanceof BindingValue) {
						returned.addAll(((BindingValue) e).getConcernedObjects(context));
					}
				}
			});
		} catch (VisitorException e) {
			LOGGER.warning("Unexpected " + e);
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
	public List<TargetObject> getTargetObjects(final BindingEvaluationContext context) {
		if (!isValid()) {
			return Collections.emptyList();
		}

		final ArrayList<TargetObject> returned = new ArrayList<>();

		try {
			expression.visit(new ExpressionVisitor() {
				@Override
				public void visit(Expression e) {
					if (e instanceof BindingValue) {
						List<TargetObject> targetObjects = ((BindingValue) e).getTargetObjects(context);
						if (targetObjects != null) {
							returned.addAll(targetObjects);
						}
					}
				}
			});
		} catch (VisitorException e) {
			LOGGER.warning("Unexpected " + e);
		}

		return returned;
	}

	@Override
	public DataBinding<T> clone() {
		DataBinding<T> returned = new DataBinding<>(getOwner(), getDeclaredType(), getBindingDefinitionType());
		if (isSet()) {
			returned.setUnparsedBinding(toString());
		}
		if (getOwner() != null) {
			returned.decode();
		}
		return returned;
	}

	public static DataBinding<Boolean> makeTrueBinding() {
		return new DataBinding<>("true");
	}

	public static DataBinding<Boolean> makeFalseBinding() {
		return new DataBinding<>("false");
	}

	/*
	 * public boolean isCacheable() { return cacheable; }
	 * 
	 * public void setCacheable(boolean cacheable) { this.cacheable = cacheable;
	 * }
	 */

	public void clearCacheForBindingEvaluationContext(BindingEvaluationContext context) {
		if (cachedValues != null) {
			cachedValues.remove(context);
		}
		/*
		 * BindingValueChangeListener<?> l =
		 * cachedBindingValueChangeListeners.get(context); if (l != null) {
		 * l.refreshObserving(); }
		 */
	}
}
