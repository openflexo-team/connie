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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.expr.BindingValue;
import org.openflexo.antar.expr.CastExpression;
import org.openflexo.antar.expr.Constant;
import org.openflexo.antar.expr.Constant.StringConstant;
import org.openflexo.antar.expr.EvaluationType;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.ExpressionTransformer;
import org.openflexo.antar.expr.ExpressionVisitor;
import org.openflexo.antar.expr.InvocationTargetTransformException;
import org.openflexo.antar.expr.NotSettableContextException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TransformException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.antar.expr.UnresolvedExpression;
import org.openflexo.antar.expr.VisitorException;
import org.openflexo.antar.expr.parser.ExpressionParser;
import org.openflexo.antar.expr.parser.ParseException;
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
public class DataBinding<T> extends Observable {

	private static final Logger logger = Logger.getLogger(DataBinding.class.getPackage().getName());

	/**
	 * Defines the access type of a binding, which is generally related to the purpose of the binding
	 * <ul>
	 * <li>GET: a binding used to retrieve a data</li>
	 * <li>SET: a binding used to set a data</li>
	 * <li>GET_SET: a binding used to retrieve and set a data</li>
	 * <li>EXECUTE: a binding used to execute some code</li>
	 * </ul>
	 */
	public static enum BindingDefinitionType {
		GET, /* GET: a binding used to retrieve a data */
		SET, /* SET: a binding used to set a data */
		GET_SET, /* GET_SET: a binding used to retrieve and set a data */
		EXECUTE /* */
	}

	private Bindable owner;
	private String unparsedBinding;
	private BindingDefinition bindingDefinition;
	private Expression expression;

	private Type declaredType = null;
	private DataBinding.BindingDefinitionType bdType = null;
	private boolean mandatory = false;
	// TODO : XtoF, first attempt to have better performances
	// Sylvain: the caching is now performed for a given BindingModel
	// We assume here that the type model is not dynamic, only BindingVariable name and type changing are notified
	// If type model is not dynamic, use setCacheable(false)
	private boolean wasValid = false;
	private boolean cacheable = true;
	private BindingModel bindingModelOnWhichValidityWasTested = null;
	private String invalidBindingReason;

	private boolean needsParsing = false;
	private String bindingName;

	public DataBinding(Bindable owner, Type declaredType, DataBinding.BindingDefinitionType bdType) {
		super();
		setOwner(owner);
		this.declaredType = declaredType;
		this.bdType = bdType;
		// setBindingDefinition(new BindingDefinition("unnamed", declaredType, bdType, true));
	}

	public DataBinding(String unparsed, Bindable owner, Type declaredType, DataBinding.BindingDefinitionType bdType) {
		this(owner, declaredType, bdType);
		setUnparsedBinding(unparsed);
	}

	public DataBinding(String unparsed) {
		super();
		setUnparsedBinding(unparsed);
	}

	@Override
	public String toString() {
		if (expression != null) {
			/*if (StringUtils.isEmpty(expression.toString())) {
				System.out.println("Pourquoi ya rien ?");
				System.out.println("l'expression est une " + expression.getClass());
				if (expression instanceof BindingValue) {
					BindingValue bv = (BindingValue) expression;
					bv.debug();
				}
			}*/
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

	/*public void setBinding(AbstractBinding binding) 
	{
		this.binding = binding;
	}*/

	public void setExpression(Expression value) {
		// logger.info("setExpression() with " + value);
		needsParsing = false;
		wasValid = false;
		Expression oldValue = this.expression;
		if (oldValue == null) {
			if (value == null) {
				return; // No change
			} else {
				this.expression = value;
				unparsedBinding = value != null ? value.toString() : null;
				// analyseExpressionAfterParsing();
				notifyBindingChanged(oldValue, value);
				return;
			}
		} else {
			if (oldValue.equals(value)) {
				return; // No change
			} else {
				this.expression = value;
				unparsedBinding = value != null ? expression.toString() : null;
				logger.info("Binding takes now value " + value);
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
		this.bindingName = bindingName;
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
		if (getExpression() != null) {
			if (getExpression() instanceof BindingValue) {
				// return ((BindingValue) getExpression()).getAccessedTypeNoValidityCheck();
				return ((BindingValue) getExpression()).getAccessedType();
			} else if (getExpression() instanceof CastExpression) {
				return ((CastExpression) getExpression()).getCastType().getType();
			} else if (expression instanceof Constant) {
				return ((Constant) expression).getType();
			} else {
				try {
					/*System.out.println("****** expression=" + getExpression());
					System.out.println("****** eval type=" + getExpression().getEvaluationType());
					if (getExpression() instanceof BinaryOperatorExpression) {
						BinaryOperatorExpression bope = (BinaryOperatorExpression) getExpression();
						System.out.println("**** left=" + bope.getLeftArgument() + " of " + bope.getLeftArgument().getEvaluationType());
						System.out.println("**** right=" + bope.getRightArgument() + " of " + bope.getRightArgument().getEvaluationType());
						BindingValueAsExpression left = (BindingValueAsExpression) bope.getLeftArgument();
						BindingValueAsExpression right = (BindingValueAsExpression) bope.getRightArgument();
						// left.isValid(this);
						// right.isValid(this);
						System.out.println("**** a gauche, " + left.getAccessedType());
						System.out.println("**** a droite, " + right.getAccessedType());
					}*/

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
	 * Called when the typing model has changed
	 */
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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

		if (cacheable && wasValid && getOwner().getBindingModel() == bindingModelOnWhichValidityWasTested) {
			// Use cache info to test DataBinding validity
			return true;
		}
		// TODO: implements and use correct equals() implementation on BindingModel class
		/*if (cacheable && wasValid) {
			logger.info("Already tested for an other BindingModel");
			logger.info("Tested on " + bindingModelOnWhichValidityWasTested);
			logger.info("Should be now tested on " + getOwner().getBindingModel());
		}*/

		if (getExpression() == null) {
			invalidBindingReason = "null expression";
			wasValid = false;
			return false;
		}

		bindingModelOnWhichValidityWasTested = getOwner().getBindingModel();

		if (getOwner() != null) {
			try {
				expression.visit(new ExpressionVisitor() {
					@Override
					public void visit(Expression e) throws InvalidBindingValue {
						if (e instanceof BindingValue) {
							if (!((BindingValue) e).isValid(DataBinding.this)) {
								// System.out.println("Invalid binding " + e);
								throw new InvalidBindingValue((BindingValue) e);
							} else {
								// System.out.println("Valid binding " + e);
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
				logger.warning("TransformException while transforming " + expression);
				wasValid = false;
				return false;
			}
		}

		if (getAnalyzedType() == null) {
			invalidBindingReason = "Invalid binding because accessed type is null";
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Invalid binding because accessed type is null");
			}
			wasValid = false;
			return false;
		}

		if (getBindingDefinitionType() == DataBinding.BindingDefinitionType.SET
				|| getBindingDefinitionType() == DataBinding.BindingDefinitionType.GET_SET) {

			if (!getExpression().isSettable()) {
				invalidBindingReason = "Invalid binding because binding declared as settable and definition cannot satisfy it";
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Invalid binding because binding definition declared as settable and definition cannot satisfy it (binding variable not settable)");
				}
				wasValid = false;
				return false;
			}
		}

		// NO need to check target type for EXECUTE bindings (we don't need return type nor value)
		if (getBindingDefinitionType() == DataBinding.BindingDefinitionType.EXECUTE) {
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

		if (isNull()) {
			// A null expression is valid (otherwise return Object.class as analyzed type, and type checking will fail in next test
			wasValid = true;
			return true;
		}

		invalidBindingReason = "Invalid binding " + this + " because types are not matching searched " + getDeclaredType() + " having "
				+ getAnalyzedType();
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Invalid binding " + this + " because types are not matching searched " + getDeclaredType() + " having "
					+ getAnalyzedType());
		}
		wasValid = false;
		return false;
	}

	public String invalidBindingReason() {
		return invalidBindingReason;
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
		} else {
			this.unparsedBinding = unparsedBinding;
			expression = null;
			needsParsing = true;
		}
	}

	public Bindable getOwner() {
		return owner;
	}

	public void setOwner(Bindable owner) {
		this.owner = owner;
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

		if (!isValid()) {
			logger.warning("Invalid binding "
					+ getUnparsedBinding()
					+ " reason: "
					+ invalidBindingReason()
					+ " "
					+ (getOwner() != null ? "BindingModel=" + getOwner().getBindingModel() + " BindingFactory="
							+ getOwner().getBindingFactory() : ""));
			// System.out.println("BreakPoint in DataBinding");
			/*Bindable owner = getOwner();
			BindingModel bm = getOwner().getBindingModel();
			BindingFactory bf = getOwner().getBindingFactory();
			logger.info("Breakpoint");*/
		}

		return expression;
	}

	private Expression analyseExpressionAfterParsing() {
		if (getOwner() != null) {
			// System.out.println("Analysing " + this + " unparsedBinding=" + unparsedBinding);
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
				logger.warning("Unexpected " + e);
			}
		}

		notifyBindingDecoded();
		return expression;
	}

	private void notifyBindingChanged(Expression oldValue, Expression newValue) {
		getOwner().notifiedBindingChanged(this);
		// logger.info("notifyBindingChanged from " + oldValue + " to " + newValue + " of " + newValue.getClass());
	}

	private void notifyBindingDecoded() {
		getOwner().notifiedBindingDecoded(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DataBinding) {
			if (toString() == null) {
				return false;
			}
			return toString().equals(obj.toString());
		} else {
			return super.equals(obj);
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
	public T getBindingValue(final BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException,
			InvocationTargetException {

		// System.out.println("Evaluating " + this + " in context " + context);

		// boolean debug = false;

		/*if (expression instanceof BindingValue && toString().equals("data.layout")
				&& context.getValue(((BindingValue) expression).getBindingVariable()).getClass().getSimpleName().equals("FIBPanel")) {
			System.out.println("Getting the point...");
			debug = true;
		}*/

		// First we check that the binding is valid, otherwise we don't go further
		if (isValid()) {

			try {

				// We then apply a transformation on all BindingValue found in binding's expression, to evaluate them in the run-time
				// context provided by supplied {@link BindingEvaluationContext} parameter
				// If a NullReferenceException is raised, the underlying expression is replaced by an UnresolvedExpression, in order to
				// keep the later ability to eventually resolve the binding even if a part of the expression cannot be resolved.
				// Think for example to AND operator. If left operand is FALSE, any evaluation of right operand is valid to determine
				// that binding value is FALSE.

				Expression resolvedExpression = expression.transform(new ExpressionTransformer() {
					@Override
					public Expression performTransformation(Expression e) throws TransformException {
						if (e instanceof BindingValue) {
							((BindingValue) e).setDataBinding(DataBinding.this);
							try {
								Object o = ((BindingValue) e).getBindingValue(context);
								// System.out.println("For " + e + " getting " + o);
								return Constant.makeConstant(o);
							} catch (NullReferenceException nre) {
								// System.out.println("NullReferenceException for " + e);
								return new UnresolvedExpression();
							}
						}
						return e;
					}
				});

				// At this point, all BindingValue are resolved, then evaluate the expression itself

				Expression evaluatedExpression = resolvedExpression.evaluate();

				if (evaluatedExpression instanceof CastExpression) {
					Expression argument = ((CastExpression) evaluatedExpression).getArgument();
					if (argument instanceof Constant) {
						// Special case for Files to be converted from Strings
						if (declaredType == File.class && argument.getEvaluationType() == EvaluationType.STRING) {
							return (T) new File((String) ((Constant) argument).getValue());
						}
						return (T) ((Constant) ((CastExpression) evaluatedExpression).getArgument()).getValue();
					}
				}

				if (evaluatedExpression instanceof Constant) {
					Class baseClassForType = TypeUtils.getBaseClass(getDeclaredType());
					if (baseClassForType != null && Number.class.isAssignableFrom(baseClassForType)) {
						return (T) TypeUtils.castTo(((Constant) evaluatedExpression).getValue(), getDeclaredType());
					}
					return (T) ((Constant) evaluatedExpression).getValue();
				}

				// We do not warn anymore since this situation happens very often
				// logger.warning("Cannot evaluate " + expression + " max reduction is " + evaluatedExpression + " resolvedExpression="
				// + resolvedExpression);

				return null;

			} catch (NullReferenceException e1) {
				throw e1;
			} catch (TypeMismatchException e1) {
				throw e1;
			} catch (InvocationTargetTransformException e1) {
				throw e1.getException();
			} catch (TransformException e1) {
				logger.warning("Unexpected TransformException while evaluating " + expression + " " + e1.getMessage());
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
	 * @return
	 * @throws TypeMismatchException
	 * @throws NullReferenceException
	 * @throws InvocationTargetException
	 * @throws NotSettableContextException
	 */
	public void setBindingValue(Object value, BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException,
			InvocationTargetException, NotSettableContextException {
		if (isSettable()) {
			if (isBindingValue()) {
				// At this time, only BindingValue is settable
				try {
					((BindingValue) getExpression()).setBindingValue(value, context);
				} catch (InvocationTargetTransformException e) {
					throw e.getException();
				}
			}
		}
	}

	public void execute(final BindingEvaluationContext context) throws TypeMismatchException, NullReferenceException,
			InvocationTargetException {
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

		final List<Object> returned = new ArrayList<Object>();

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
			logger.warning("Unexpected " + e);
		}

		return returned;
	}

	/**
	 * Build and return a list of target objects involved in the computation of this data binding with supplied binding evaluation context<br>
	 * Those target objects are the combination of an object and the property name involved by this denoted data binding
	 * 
	 * @param context
	 * @return
	 */
	public List<TargetObject> getTargetObjects(final BindingEvaluationContext context) {
		if (!isValid()) {
			return Collections.emptyList();
		}

		final ArrayList<TargetObject> returned = new ArrayList<TargetObject>();

		try {
			expression.visit(new ExpressionVisitor() {
				@Override
				public void visit(Expression e) {
					if (e instanceof BindingValue) {
						returned.addAll(((BindingValue) e).getTargetObjects(context));
					}
				}
			});
		} catch (VisitorException e) {
			logger.warning("Unexpected " + e);
		}

		return returned;
	}

	@Override
	public DataBinding<T> clone() {
		DataBinding<T> returned = new DataBinding(getOwner(), getDeclaredType(), getBindingDefinitionType());
		/*if (!isSet()) {
			System.out.println("On essaie de me cloner alors que je suis null");
			Thread.dumpStack();
			System.exit(-1);
		}*/
		if (isSet()) {
			returned.setUnparsedBinding(toString());
		}
		returned.decode();
		return returned;
	}

	public static DataBinding<Boolean> makeTrueBinding() {
		return new DataBinding<Boolean>("true");
	}

	public static DataBinding<Boolean> makeFalseBinding() {
		return new DataBinding<Boolean>("false");
	}

	public boolean isCacheable() {
		return cacheable;
	}

	public void setCacheable(boolean cacheable) {
		this.cacheable = cacheable;
	}
}
