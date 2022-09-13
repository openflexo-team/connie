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
import org.openflexo.connie.binding.BindingValueChangeListener;
import org.openflexo.connie.binding.LazyBindingValueChangeListener;
import org.openflexo.connie.binding.TargetObject;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.BindingValue;
import org.openflexo.connie.expr.CastExpression;
import org.openflexo.connie.expr.ConditionalExpression;
import org.openflexo.connie.expr.Constant;
import org.openflexo.connie.expr.EvaluationType;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionVisitor;
import org.openflexo.connie.expr.UnresolvedBindingVariable;
import org.openflexo.connie.expr.VisitorException;
import org.openflexo.connie.type.ExplicitNullType;
import org.openflexo.connie.type.TypeUtils;
import org.openflexo.connie.type.UndefinedType;
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

	private static CachingStrategy defaultCachingStrategy = CachingStrategy.NO_CACHING;
	// private static final CachingStrategy DEFAULT_CACHING_STRATEGY = CachingStrategy.PRAGMATIC_CACHE;

	public static CachingStrategy getDefaultCachingStrategy() {
		return defaultCachingStrategy;
	}

	public static void setDefaultCachingStrategy(CachingStrategy aCachingStrategy) {
		defaultCachingStrategy = aCachingStrategy;
	}

	private Bindable owner;
	private Expression expression;

	private Type declaredType = null;
	private DataBinding.BindingDefinitionType bdType = null;
	private boolean mandatory = false;
	private boolean isCacheable = true;
	private Type analyzedType;
	private String invalidBindingReason;
	private boolean isPerformingValidity = false;

	private String unparsedBinding;
	private boolean needsParsing = false;

	private boolean isValid = false;
	private boolean validated = false;

	private boolean trackBindingModelChanges = true;

	private String bindingName;

	private CachingStrategy cachingStrategy = null;

	private PropertyChangeSupport pcSupport;

	private Map<BindingEvaluationContext, T> cachedValues = null;
	private Map<BindingEvaluationContext, BindingValueChangeListener<T>> cachedBindingValueChangeListeners = null;

	private DataBinding() {

		pcSupport = new PropertyChangeSupport(this);

		initCache();
	}

	public DataBinding(Type declaredType, DataBinding.BindingDefinitionType bdType) {
		this();
		this.declaredType = declaredType;
		this.bdType = bdType;
	}

	public DataBinding(Bindable owner, Type declaredType, DataBinding.BindingDefinitionType bdType) {
		this(owner, declaredType, bdType, true);
	}

	public DataBinding(Bindable owner, Type declaredType, DataBinding.BindingDefinitionType bdType, boolean trackBindingModelChanges) {
		this(declaredType, bdType);
		this.trackBindingModelChanges = trackBindingModelChanges;
		setOwner(owner);
	}

	public DataBinding(String unparsed) {
		this(unparsed, null);
	}

	public DataBinding(String unparsed, Bindable owner) {
		this(owner, Object.class, BindingDefinitionType.GET);
		setExpression(parseExpression(unparsed));
		// setUnparsedBinding(unparsed);
	}

	public DataBinding(String unparsed, Bindable owner, Type declaredType, DataBinding.BindingDefinitionType bdType) {
		this(owner, declaredType, bdType);
		setExpression(parseExpression(unparsed));
		// setUnparsedBinding(unparsed);
	}

	public DataBinding(String unparsed, Bindable owner, DataBinding<?> db) {
		this(unparsed, owner, db != null ? db.declaredType : Object.class, db != null ? db.bdType : BindingDefinitionType.GET);
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	public void delete() {
		deleteContainedBindingValues();
		stopListenToBindingModel();
		getPropertyChangeSupport().firePropertyChange(getDeletedProperty(), false, true);
		pcSupport = null;
	}

	public void debug() {
		System.out.println("DEBUG DataBinding ");
		System.out.println("> validated=" + validated);
		System.out.println("> valid=" + isValid);
		System.out.println("> reason=" + invalidBindingReason);
		System.out.println("> expression=" + expression);
		System.out.println("> unparsedBinding=" + unparsedBinding);
		System.out.println("> needsParsing=" + needsParsing);
		// System.out.println("> isParsingExpression=" + isParsingExpression);
		System.out.println("> isPerformingValidity=" + isPerformingValidity);
		System.out.println("> analyzedType=" + analyzedType);
		System.out.println("> owner=" + owner);
		System.out.println("> bindingFactory=" + (owner != null ? owner.getBindingFactory() : null));
		System.out.println("> listenedBindingModel=" + listenedBindingModel);
	}

	private void deleteContainedBindingValues() {
		if (getExpression() != null) {
			try {
				getExpression().visit(new ExpressionVisitor() {
					@Override
					public void visit(Expression e) throws VisitorException {
						if (e instanceof BindingValue) {
							((BindingValue) e).delete();
						}
					}
				});
			} catch (VisitorException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String getDeletedProperty() {
		return "deleted";
	}

	public CachingStrategy getCachingStrategy() {
		if (cachingStrategy == null) {
			return defaultCachingStrategy;
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
		if (needsParsing) {
			return unparsedBinding;
		}
		if (!isSet()) {
			return "";
		}
		return "<undefined>";
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression value) {
		setExpression(value, true);
	}

	public void setExpression(Expression value, boolean notify) {
		// logger.info("setExpression() with " + value);
		validated = false;
		if ((this.expression == null && value != null) || (this.expression != null && !this.expression.equals(value))) {
			// there is a change
			deleteContainedBindingValues();
			Expression oldValue = this.expression;
			this.expression = value;
			if (notify) {
				notifyBindingChanged(oldValue, value);
			}
		}

		checkBindingModelListening();
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

	public void setDeclaredType(Type declaredType) {
		if ((declaredType == null && this.declaredType != null) || (declaredType != null && !declaredType.equals(this.declaredType))) {
			Type oldValue = this.declaredType;
			this.declaredType = declaredType;
			getPropertyChangeSupport().firePropertyChange("declaredType", oldValue, declaredType);
			invalidate();
		}
	}

	public boolean isExecutable() {
		return bdType == BindingDefinitionType.EXECUTE;
	}

	public boolean isGET() {
		return bdType == BindingDefinitionType.GET || bdType == BindingDefinitionType.GET_SET;
	}

	public boolean isSET() {
		return bdType == BindingDefinitionType.SET || bdType == BindingDefinitionType.GET_SET;
	}

	public BindingDefinitionType getBindingDefinitionType() {
		return bdType;
	}

	public void setBindingDefinitionType(BindingDefinitionType aBDType) {
		bdType = aBDType;
		invalidate();
	}

	private Type performComputeAnalyzedType() {

		// LOGGER.info("Analysing " + Integer.toHexString(hashCode()) + " " + (analysisCount++) + " " + (expression != null
		// ? "expression: [" + expression.getClass().getSimpleName() + "]/" + expression : "unparsed: " + unparsedBinding));

		if (isNull()) {
			return ExplicitNullType.INSTANCE;
		}
		if (getExpression() != null) {
			if (getExpression() instanceof BindingValue) {
				return ((BindingValue) getExpression()).getAccessedType();
			}
			else if (getExpression() instanceof CastExpression) {
				return ((CastExpression) getExpression()).getCastType();
			}
			else if (getExpression() instanceof Constant) {
				return ((Constant<?>) getExpression()).getType();
			}
			else if (getExpression() instanceof ConditionalExpression) {
				return ((ConditionalExpression) getExpression()).getAccessedType();
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

	@SuppressWarnings("serial")
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
	 * Explicitly called when a structural modification of data occurs, and when the validity status of the {@link DataBinding} might have
	 * changed<br>
	 * Calling this method will force the next call of isValid() to force recompute the {@link DataBinding} validity status and message
	 */
	public void invalidate() {

		if (debug) {
			System.out.println("DEBUG -- Connie -- invalidate() for " + this);
		}

		validated = false;

		isValid = false;
		if (expression != null) {
			try {
				expression.visit(new ExpressionVisitor() {
					@Override
					public void visit(Expression e) throws InvalidBindingValue {
						if (e instanceof BindingValue) {
							BindingValue bv = (BindingValue) e;
							bv.invalidate();
						}
					}
				});
			} catch (VisitorException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Calling this method forces validity status recomputation<br>
	 * {@link #performComputeValididy()} will be force called
	 */
	public boolean revalidate() {
		invalidate();
		return isValid();
	}

	/**
	 * Return flag indicating whether this {@link DataBinding} is valid or not<br>
	 * 
	 * This method is efficient and relies on a caching scheme.
	 * 
	 * A internal scheme monitor the underlying {@link BindingModel} (typing model), and detect modifications of {@link BindingModel}, which
	 * triggers recomputation of validity status. Thus, calling this method is always safe, regarding status of underlying
	 * {@link BindingModel}
	 * 
	 * @return
	 */
	public boolean isValid() {

		if (needsParsing) {
			if (!isParsing) {
				setExpression(parseExpression(unparsedBinding));
			}
			else {
				invalidBindingReason = "Cannot analyze due to infinite-loop";
				isValid = false;
				return isValid;
			}
		}

		if (!validated) {
			isValid = performComputeValididy();
			if (debug) {
				System.out.println("DEBUG -- Connie -- DONE performComputeValididy for " + this);
			}
			// We mark the DataBinding as validated if and only if both owner and BindingFactory are not null
			if (getOwner() != null && getOwner().getBindingFactory() != null) {
				validated = true;
				if (debug) {
					System.out.println("DEBUG -- Connie -- mark as validated for " + this);
					debug();
				}
			}
		}

		/*if (!valid && isSet()) {
			System.out.println("Invalid binding: " + toString() + " avec " + (getOwner() != null && getOwner().getBindingModel() != null
					? getOwner().getBindingModel().getDebugStructure() : "null"));
			System.out.println("reason: " + invalidBindingReason());
		}*/

		return isValid;
	}

	/**
	 * Internally compute validity<br>
	 * 
	 * This method updates
	 * <ul>
	 * <li>{@link #isValid} flag</li>
	 * <li>{@link #isCacheable} flag</li>
	 * <li>{@link #analyzedType} value</li>
	 * </ul>
	 * 
	 * @return
	 */
	private boolean performComputeValididy() {

		if (debug) {
			System.out.println("DEBUG -- Connie -- performComputeValididy for " + this);
		}

		if (getOwner() == null) {
			invalidBindingReason = "null owner";
			isValid = false;
			return isValid;
		}

		if (getOwner().getBindingFactory() == null) {
			invalidBindingReason = "owner has null BindingFactory";
			isValid = false;
			return false;
		}

		if (getOwner().getBindingModel() == null) {
			invalidBindingReason = "owner has null BindingModel";
			isValid = false;
			return false;
		}

		if (getExpression() == null) {
			invalidBindingReason = "null expression";
			isValid = false;
			return false;
		}

		invalidBindingReason = "unknown";

		isCacheable = true;
		// analyzedType = Object.class;
		// Fixing CONNIE-23
		analyzedType = UndefinedType.INSTANCE;

		if (isPerformingValidity) {
			System.err.println("Stackoverflow prevented while performing validity for " + this);
			Thread.dumpStack();
			return false;
		}

		// TODO FD, I think it always true as if getOwner() was null we already returned except if somebody has change the owner in
		// between...
		if (getOwner() != null) {
			try {
				isPerformingValidity = true;

				expression.visit(new ExpressionVisitor() {
					@Override
					public void visit(Expression e) throws InvalidBindingValue {
						if (e instanceof BindingValue) {
							if (!((BindingValue) e).isValid()) {
								((BindingValue) e).revalidate();
							}
							// TODO is it intentional to recompute isValid?
							if (!((BindingValue) e).isValid()) {
								// System.out.println("Invalid binding " + e);
								throw new InvalidBindingValue((BindingValue) e);
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
				isValid = false;
				return false;
			} catch (VisitorException e) {
				invalidBindingReason = "Unexpected visitor exception: " + e.getMessage();
				LOGGER.warning("TransformException while transforming " + expression);
				isValid = false;
				return false;
			} finally {
				isPerformingValidity = false;
			}
		}

		if (isSET()) {
			if (!getExpression().isSettable()) {
				invalidBindingReason = "Invalid binding because binding declared as settable and definition cannot satisfy it";
				if (LOGGER.isLoggable(Level.FINE)) {
					LOGGER.fine(
							"Invalid binding because binding definition declared as settable and definition cannot satisfy it (binding variable not settable)");
				}
				isValid = false;
				return false;
			}
		}

		if (isNull()) {
			// A null expression is valid (otherwise return Object.class as
			// analyzed type, and type checking will fail in next test
			isValid = true;
			analyzedType = ExplicitNullType.INSTANCE;
			return true;
		}

		analyzedType = performComputeAnalyzedType();

		if (analyzedType == null) {
			invalidBindingReason = "Invalid binding because accessed type is null";
			if (LOGGER.isLoggable(Level.FINE)) {
				LOGGER.fine("Invalid binding because accessed type is null");
			}
			isValid = false;
			return false;
		}

		// NO need to check target type for EXECUTE bindings (we don't need
		// return type nor value)
		if (isExecutable()) {
			isValid = true;
			return true;
		}

		if (getDeclaredType() != null && TypeUtils.isTypeAssignableFrom(getDeclaredType(), analyzedType, true)) {
			// System.out.println("getBindingDefinition().getType()="+getBindingDefinition().getType());
			// System.out.println("getAccessedType()="+getAccessedType());
			invalidBindingReason = "valid binding";
			isValid = true;
			return true;
		}

		invalidBindingReason = "Invalid binding " + this + " because types are not matching searched " + getDeclaredType() + " having "
				+ analyzedType;
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Invalid binding " + this + " because types are not matching searched " + getDeclaredType() + " having "
					+ analyzedType);
		}
		isValid = false;
		return false;
	}

	public String invalidBindingReason() {
		return invalidBindingReason;
	}

	/**
	 * Return boolean indicating if this {@link BindingValue} is notification-safe<br>
	 * 
	 * A {@link BindingValue} is unsafe when any involved method is annotated with {@link NotificationUnsafe} annotation<br>
	 * Otherwise return true
	 * 
	 * @return
	 */
	public boolean isCacheable() {

		// Make sure that validity has been computed
		// Computes it and cache it during isValid() computation
		isValid();

		return isCacheable;
	}

	/**
	 * Infer and return type of this {@link DataBinding}
	 * 
	 * This method is efficient and relies on a caching scheme.
	 * 
	 * A internal scheme monitor the underlying {@link BindingModel} (typing model), and detect modifications of {@link BindingModel}, which
	 * triggers recomputation of validity status. Thus, calling this method is always safe, regarding status of underlying
	 * {@link BindingModel}
	 * 
	 * @return type as infered from {@link DataBinding} analysis
	 */
	public Type getAnalyzedType() {

		// Make sure that validity has been computed
		// Computes it and cache it during isValid() computation
		if (isValid()) {
			return analyzedType;
		}
		else {
			return UndefinedType.INSTANCE;
		}
	}

	public boolean isSet() {
		return getExpression() != null || needsParsing;
	}

	public boolean isUnset() {
		return !isSet();
	}

	public void reset() {
		deleteContainedBindingValues();
		expression = null;
		checkBindingModelListening();
	}

	public boolean isExpression() {
		return getExpression() != null && !(getExpression() instanceof Constant) && !(getExpression() instanceof BindingValue);
	}

	public boolean isBindingValue() {
		return getExpression() != null && getExpression() instanceof BindingValue;
	}

	public boolean isSimpleVariable() {
		if (isBindingValue()) {
			BindingValue bindingPath = (BindingValue) getExpression();
			return bindingPath.getBindingVariable() != null && bindingPath.getBindingPath().size() == 0;
		}
		return false;
	}

	public boolean isNewVariableDeclaration() {
		if (isBindingValue()) {
			BindingValue bindingPath = (BindingValue) getExpression();
			return bindingPath.getBindingVariable() instanceof UnresolvedBindingVariable && bindingPath.getBindingPath().size() == 0;
		}
		return false;
	}

	public boolean isConstant() {
		return getExpression() != null && getExpression() instanceof Constant;
	}

	public boolean isNull() {
		return getExpression() != null && getOwner() != null && getOwner().getBindingFactory() != null
				&& getExpression() == getOwner().getBindingFactory().getNullExpression();
	}

	/*public boolean isStringConstant() {
		return getExpression() != null && getExpression() instanceof StringConstant;
	}*/

	public boolean isCompoundBinding() {
		return isBindingValue() && ((BindingValue) getExpression()).containsAMethodCall();
	}

	public String getUnparsedBinding() {
		return unparsedBinding;
	}

	public void setUnparsedBinding(String unparsedBinding) {
		setExpression(parseExpression(unparsedBinding));
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
				stopListenToBindingModel();
			}
			this.owner = owner;

			if (expression != null) {
				try {
					expression.visit(new ExpressionVisitor() {
						@Override
						public void visit(Expression e) throws InvalidBindingValue {
							if (e instanceof BindingValue) {
								BindingValue bv = (BindingValue) e;
								bv.setOwner(owner);
							}
						}
					});
				} catch (VisitorException e) {
					e.printStackTrace();
				}
			}

			if (owner != null && owner.getPropertyChangeSupport() != null) {
				owner.getPropertyChangeSupport().addPropertyChangeListener(this);
			}

			checkBindingModelListening();

			invalidate();

		}
	}

	private BindingModel listenedBindingModel;
	private List<BindingVariable> listenedBindingVariables = new ArrayList<>();

	/**
	 * Internally called to update listening
	 */
	private void checkBindingModelListening() {
		if (isSet()) {
			if (listenedBindingModel == null && owner != null && owner.getBindingModel() != null) {
				startListenToBindingModel(owner.getBindingModel());
			}
		}
		else {
			if (listenedBindingModel != null) {
				stopListenToBindingModel();
			}
		}
	}

	private void startListenToBindingModel(BindingModel bindingModel) {

		// System.out.println("DEBUT Je suis " + this + " et j'ecoute " + bindingModel);
		// System.out.println("trackBindingModelChanges=" + trackBindingModelChanges);

		listenedBindingModel = bindingModel;
		if (listenedBindingModel != null && listenedBindingModel.getPropertyChangeSupport() != null) {
			listenedBindingModel.getPropertyChangeSupport().addPropertyChangeListener(this);
		}

		if (trackBindingModelChanges) {
			updateListenedBindingVariables();
		}

		// System.out.println("FIN Je suis " + this + " et j'ecoute " + bindingModel);
	}

	private void updateListenedBindingVariables() {

		List<BindingVariable> listenedBindingVariableToDelete = new ArrayList<>(listenedBindingVariables);

		if (listenedBindingModel != null) {
			for (BindingVariable bv : listenedBindingModel.getAccessibleBindingVariables()) {
				if (listenedBindingVariableToDelete.contains(bv)) {
					// This binding variable is already listened, do not delete it
					listenedBindingVariableToDelete.remove(bv);
				}
				else {
					if (bv.getPropertyChangeSupport() != null) {
						bv.getPropertyChangeSupport().addPropertyChangeListener(this);
						listenedBindingVariables.add(bv);
					}
				}
			}
		}

		for (BindingVariable bv : listenedBindingVariableToDelete) {
			if (bv.getPropertyChangeSupport() != null) {
				bv.getPropertyChangeSupport().removePropertyChangeListener(this);
				listenedBindingVariables.remove(bv);
			}
		}

	}

	private void stopListenToBindingModel() {
		for (BindingVariable bv : listenedBindingVariables) {
			if (bv.getPropertyChangeSupport() != null) {
				bv.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
		}
		listenedBindingVariables.clear();
		if (listenedBindingModel != null) {
			listenedBindingModel.getPropertyChangeSupport().removePropertyChangeListener(this);
			listenedBindingModel = null;
		}
	}

	public BindingModel getListenedBindingModel() {
		return listenedBindingModel;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		// if (debug) {
		// System.out.println(">>>>>>>>>>> For " + this + " Received propertyName=" + evt.getPropertyName() + " source=" + evt.getSource()
		// + " evt=" + evt);
		// }

		// Track BindingFactory changes
		// We detect here that the owner of this DataBinding has changed its BindingFactory
		if (evt.getSource() == owner && evt.getPropertyName() != null && evt.getPropertyName().equals(Bindable.BINDING_FACTORY_PROPERTY)) {
			invalidate();
		}

		// Track BindingModel changes
		// We detect here that the owner of this DataBinding has changed its BindingModel
		if (evt.getSource() == owner && evt.getPropertyName() != null && evt.getPropertyName().equals(Bindable.BINDING_MODEL_PROPERTY)) {
			// System.out.println("BindingModel changed for " + getOwner());
			// System.out.println("was: " + evt.getOldValue());
			// System.out.println("now: " + evt.getNewValue());
			if (evt.getOldValue() instanceof BindingModel) {
				if (((BindingModel) evt.getOldValue()).getPropertyChangeSupport() != null) {
					((BindingModel) evt.getOldValue()).getPropertyChangeSupport().removePropertyChangeListener(this);
				}
			}
			if (evt.getNewValue() instanceof BindingModel) {
				if (((BindingModel) evt.getNewValue()).getPropertyChangeSupport() != null) {
					((BindingModel) evt.getNewValue()).getPropertyChangeSupport().addPropertyChangeListener(this);
				}
			}
			invalidate();
		}

		// Track structural changes inside a BindingModel
		if (getOwner() != null && evt.getSource() == owner.getBindingModel()) {

			if (evt.getPropertyName().equals(BindingModel.BINDING_VARIABLE_PROPERTY)) {
				// We detect here that a BindingVariable was added or removed
				// from BindingModel
				if (evt.getNewValue() instanceof BindingVariable) {
					// A new BindingVariable was added
					((BindingVariable) evt.getNewValue()).getPropertyChangeSupport().addPropertyChangeListener(this);
				}
				else if (evt.getOldValue() instanceof BindingVariable) {
					// A new BindingVariable was removed
					((BindingVariable) evt.getOldValue()).getPropertyChangeSupport().removePropertyChangeListener(this);
				}
				invalidate();
			}
			else if (evt.getPropertyName().equals(BindingModel.BINDING_PATH_ELEMENT_NAME_CHANGED)) {
				// We detect here that a BindingVariable has changed its name,
				// we should reanalyze the binding
				invalidate();

			}
			else if (evt.getPropertyName().equals(BindingModel.BINDING_PATH_ELEMENT_TYPE_CHANGED)) {
				// We detect here that a BindingVariable has changed its type,
				// we should reanalyze the binding
				invalidate();
			}
			else if (evt.getPropertyName().equals(BindingModel.BASE_BINDING_MODEL_PROPERTY)) {
				// We detect here that base BindingModel has changed
				updateListenedBindingVariables();
				invalidate();
			}
		}

		// Track structural changes inside a BindingModel
		if (getOwner() != null && evt.getSource() instanceof BindingVariable) {

			if (evt.getPropertyName().equals(BindingVariable.VARIABLE_NAME_PROPERTY)) {
				// We detect here that a BindingVariable has changed its name,
				// we should reanalyze the binding
				invalidate();
			}
			else if (evt.getPropertyName().equals(BindingVariable.TYPE_PROPERTY)) {
				// We detect here that a BindingVariable has changed its type,
				// we should reanalyze the binding
				invalidate();
			}
			/*else if (evt.getPropertyName().equals(BindingVariable.DELETED_PROPERTY)) {
				// We detect here that a BindingVariable has changed its type,
				// we should reanalyze the binding
				markedAsToBeReanalized();
			}*/
		}

	}

	private boolean isParsing = false;

	/**
	 * This method is called whenever we need to parse the binding using string encoded in unparsedBinding field.<br>
	 * Syntaxic checking of the binding is performed here. This phase is followed by the semantics analysis as performed by
	 * {@link #analyseExpressionAfterParsing()} method
	 * 
	 * @return
	 */
	private Expression parseExpression(String unparsed) {

		if (StringUtils.isEmpty(unparsed)) {
			return null;
		}

		try {
			isParsing = true;
			if (getOwner() != null && getOwner().getBindingFactory() != null) {
				try {
					Expression returned = getOwner().getBindingFactory().parseExpression(unparsed, getOwner());
					needsParsing = false;
					this.unparsedBinding = null;
					return returned;
				} catch (ParseException e) {
					// parse error
					// e.printStackTrace();
					LOGGER.warning(e.getMessage() + " while parsing " + unparsed);
					return null;
				}
			}
			else {
				this.unparsedBinding = unparsed;
				needsParsing = true;
				return null;
			}
		} finally {
			isParsing = false;
		}

	}

	// TODO : is this still usefull ? guess no
	private Expression analyseExpressionAfterParsing() {
		if (getOwner() != null && expression != null) {
			// System.out.println("Analysing " + this + " unparsedBinding=" +
			// unparsedBinding);
			try {
				expression.visit(new ExpressionVisitor() {
					@Override
					public void visit(Expression e) {
						if (e instanceof BindingValue) {
							// System.out.println("> Analyse " + e);
							// ((BindingValue) e).buildBindingPathFromParsedBindingPath(/*DataBinding.this*/);
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
			throws TypeMismatchException, NullReferenceException, ReflectiveOperationException {

		if (isGET() && ((getCachingStrategy() == CachingStrategy.OPTIMIST_CACHE)
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

				// Evaluate the expression itself

				Expression evaluatedExpression = expression.evaluate(context);

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

				if (this.isGET() && ((getCachingStrategy() == CachingStrategy.OPTIMIST_CACHE)
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
			} catch (ReflectiveOperationException e) {
				throw e;
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
			throws TypeMismatchException, NullReferenceException, ReflectiveOperationException, NotSettableContextException {
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
				LOGGER.warning("Trying to set value: invalid binding " + this + " reason=" + invalidBindingReason());
			}
			else {
				LOGGER.warning("Trying to set value: not settable binding " + this);
			}
		}
	}

	/**
	 * Execute this binding in run-time evaluation context provided by supplied {@link BindingEvaluationContext} parameter.
	 * 
	 * @param context
	 * @throws TypeMismatchException
	 * @throws NullReferenceException
	 * @throws InvocationTargetException
	 */
	public void execute(final BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException, ReflectiveOperationException {
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
		DataBinding<T> returned = new DataBinding<>(getOwner(), getDeclaredType(), this.bdType);
		if (getExpression() != null) {
			returned.setExpression(parseExpression(getExpression().toString()));
		}
		else if (getUnparsedBinding() != null) {
			returned.setUnparsedBinding(getUnparsedBinding());
		}
		return returned;
	}

	public void clearCacheForBindingEvaluationContext(BindingEvaluationContext context) {
		if (cachedValues != null) {
			cachedValues.remove(context);
		}
	}

	// Used for debug only
	public boolean debug = false;

}
