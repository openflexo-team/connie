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

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Vector;

import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.type.ExplicitNullType;
import org.openflexo.toolbox.Duration;

public abstract class Constant<V> extends Expression {

	public static Constant<?> makeConstant(Object value) {
		if (value == null) {
			return Constant.ObjectSymbolicConstant.NULL;
		}
		if (value instanceof Boolean) {
			if ((Boolean) value) {
				return Constant.BooleanConstant.TRUE;
			}
			return Constant.BooleanConstant.FALSE;
		}
		else if (value instanceof Character) {
			return new Constant.StringConstant(((Character) value).toString());
		}
		else if (value instanceof String) {
			return new Constant.StringConstant((String) value);
		}
		else if (value.getClass().isEnum()) {
			return new Constant.EnumConstant<>(((Enum<?>) value));
		}
		else if (value instanceof Float) {
			return new Constant.FloatConstant(((Float) value).doubleValue());
		}
		else if (value instanceof Double) {
			return new Constant.FloatConstant(((Double) value).doubleValue());
		}
		else if (value instanceof Integer) {
			return new Constant.IntegerConstant(((Integer) value).longValue());
		}
		else if (value instanceof Short) {
			return new Constant.IntegerConstant(((Short) value).longValue());
		}
		else if (value instanceof Long) {
			return new Constant.IntegerConstant(((Long) value).longValue());
		}
		else if (value instanceof Byte) {
			return new Constant.IntegerConstant(((Byte) value).longValue());
		}
		else if (value instanceof Date) {
			return new Constant.DateConstant((Date) value);
		} /*else if (value instanceof DurationValue) {
			return new Constant.DurationConstant(((DurationValue) value).getDurationValue());
			}*/
		return new Constant.ObjectConstant(value);
	}

	/*@Override
	public Expression evaluate(EvaluationContext context, Bindable bindable) {
		return this;
	}*/

	@Override
	public Expression transform(ExpressionTransformer transformer) throws TransformException {
		return transformer.performTransformation(this);
	}

	@Override
	public void visit(ExpressionVisitor visitor) throws VisitorException {
		visitor.visit(this);
	}

	@Override
	public int getDepth() { // NOPMD by beugnard on 31/10/14 00:05
		return 0;
	}

	@Override
	protected Vector<Expression> getChilds() { // NOPMD by beugnard on 31/10/14 00:05
		return null;
	}

	public abstract V getValue();

	public Type getType() {
		return getEvaluationType().getType();
	}

	@Override
	public Type getAccessedType() {
		return getEvaluationType().getType();
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	public static abstract class BooleanConstant extends Constant<Boolean> {
		public static BooleanConstant get(boolean value) {
			if (value) {
				return TRUE;
			}
			return FALSE;
		}

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.BOOLEAN;
		}

		@Override
		public abstract Boolean getValue();

		public static final BooleanConstant TRUE = new BooleanConstant() {
			@Override
			public Boolean getValue() {
				return true;
			}

		};

		public static final BooleanConstant FALSE = new BooleanConstant() {
			@Override
			public Boolean getValue() {
				return false;
			}

		};
	}

	public static class StringConstant extends Constant<String> {
		private String value;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.STRING;
		}

		public StringConstant(String value) {
			super();
			this.value = value;
		}

		@Override
		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

	public static class ObjectConstant extends Constant<Object> {
		private Object value;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.LITERAL;
		}

		public ObjectConstant(Object value) {
			super();
			this.value = value;
		}

		@Override
		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		@Override
		public Type getType() {
			return getValue().getClass();
		}

	}

	public static class EnumConstant<E extends Enum<E>> extends Constant<Enum<E>> {
		private Enum<E> value;
		private String enumName;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.ENUM;
		}

		public EnumConstant(Enum<E> value) {
			super();
			this.value = value;
		}

		public String getName() {
			if (value != null) {
				return value.name();
			}
			return enumName;
		}

		@Override
		public Enum<E> getValue() {
			return value;
		}
	}

	public static abstract class ArithmeticConstant<V extends Number> extends Constant<V> {
		public abstract double getArithmeticValue();

	}

	public static class IntegerConstant extends ArithmeticConstant<Long> {
		private long value;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.ARITHMETIC_INTEGER;
		}

		public IntegerConstant(long value) {
			super();
			this.value = value;
		}

		@Override
		public Long getValue() {
			return value;
		}

		public void setValue(long value) {
			this.value = value;
		}

		@Override
		public double getArithmeticValue() {
			return getValue();
		}

	}

	public static class FloatConstant extends ArithmeticConstant<Double> {
		private double value;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.ARITHMETIC_FLOAT;
		}

		public FloatConstant(double value) {
			super();
			this.value = value;
		}

		@Override
		public Double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		@Override
		public double getArithmeticValue() {
			return getValue();
		}

	}

	public static class FloatSymbolicConstant extends FloatConstant implements SymbolicConstant {
		private String symbol;

		private FloatSymbolicConstant(String symbol, double value) {
			super(value);
			this.symbol = symbol;
		}

		@Override
		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public static final FloatSymbolicConstant PI = new FloatSymbolicConstant("pi", Math.PI);
		public static final FloatSymbolicConstant E = new FloatSymbolicConstant("e", Math.E);

		@Override
		public String getValueAsString() {
			return Double.toString(getValue());
		}

		/*@Override
		public Expression evaluate(EvaluationContext context, Bindable bindable) {
			return new FloatConstant(getValue());
		}*/

	}

	@Override
	public abstract EvaluationType getEvaluationType();

	public static class DateConstant extends Constant<Date> {
		private Date date;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.DATE;
		}

		public DateConstant(Date date) {
			super();
			this.date = date;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		@Override
		public Date getValue() {
			return getDate();
		}

	}

	public static abstract class DateSymbolicConstant extends DateConstant implements SymbolicConstant {
		private String symbol;

		DateSymbolicConstant(String symbol) {
			super(null);
			this.symbol = symbol;
		}

		@Override
		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public static final DateSymbolicConstant TODAY = new DateSymbolicConstant("today") {
			@Override
			public Date computeDateForNow() {
				// TODO replace with new implementation of org.openflexo.toolbox.Date
				return new Date();
			}
		};

		public static final DateSymbolicConstant NOW = new DateSymbolicConstant("now") {
			@Override
			public Date computeDateForNow() {
				// TODO replace with new implementation of org.openflexo.toolbox.Date
				return new Date();
			}
		};

		@Override
		public String getValueAsString() {
			return getSymbol();
		}

		/*@Override
		public Expression evaluate(EvaluationContext context, Bindable bindable) {
			return new DateConstant(computeDateForNow());
		}*/

		public abstract Date computeDateForNow();

	}

	public static class DurationConstant extends Constant<Duration> {
		private Duration duration;

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.DURATION;
		}

		public DurationConstant(Duration duration) {
			super();
			this.duration = duration;
		}

		public Duration getDuration() {
			return duration;
		}

		public void setDuration(Duration duration) {
			this.duration = duration;
		}

		@Override
		public Duration getValue() {
			return getDuration();
		}
	}

	public static class ObjectSymbolicConstant extends Constant<Object> implements SymbolicConstant {
		private String symbol;

		private ObjectSymbolicConstant(String symbol) {
			super();
			this.symbol = symbol;
		}

		@Override
		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public static final ObjectSymbolicConstant NULL = new ObjectSymbolicConstant("null") {
			@Override
			public Type getType() {
				return ExplicitNullType.INSTANCE;
			}
		};
		public static final ObjectSymbolicConstant THIS = new ObjectSymbolicConstant("this");

		@Override
		public String getValueAsString() {
			return getSymbol();
		}

		/*@Override
		public Expression evaluate(EvaluationContext context, Bindable bindable) {
			return this;
		}*/

		@Override
		public EvaluationType getEvaluationType() {
			return EvaluationType.LITERAL;
		}

		@Override
		public Object getValue() {
			// TODO
			return null;
		}

	}

}
