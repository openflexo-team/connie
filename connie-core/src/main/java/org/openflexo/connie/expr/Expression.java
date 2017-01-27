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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.expr.parser.ExpressionParser;
import org.openflexo.connie.expr.parser.ParseException;

/**
 * Represents a symbolic expression
 * 
 * @author sylvain
 * 
 */
public abstract class Expression {

	private static final Logger LOGGER = Logger.getLogger(Expression.class.getPackage().getName());

	public abstract void visit(ExpressionVisitor visitor) throws VisitorException;

	public abstract Expression transform(ExpressionTransformer transformer) throws TransformException;

	public final Expression evaluate() throws TypeMismatchException, NullReferenceException {
		try {
			return transform(new ExpressionEvaluator());
		} catch (TypeMismatchException e) {
			throw e;
		} catch (NullReferenceException e) {
			throw e;
		} catch (TransformException e) {
			LOGGER.warning("Unexpected exception occured during evaluation " + e);
			e.printStackTrace();
			return null;
		}
	}

	public abstract int getDepth();

	@Override
	public String toString() {
		return debugPP.getStringRepresentation(this);
	}

	private static final DefaultExpressionPrettyPrinter debugPP = new DefaultExpressionPrettyPrinter();

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		else if (obj instanceof Expression) {
			return getClass().equals(obj.getClass()) && toString().equalsIgnoreCase(((Expression) obj).toString());
		}
		return super.equals(obj);
	}

	public abstract EvaluationType getEvaluationType() throws TypeMismatchException;

	public boolean isSemanticallyAcceptable() {
		try {
			getEvaluationType();
			return true;
		} catch (TypeMismatchException e) {
			return false;
		}
	}

	protected void _checkSemanticallyAcceptable() throws TypeMismatchException {
		getEvaluationType();
	}

	/**
	 * Return a list containing all {@link BindingValue} used in this expression
	 * 
	 * @return
	 */
	public List<BindingValue> getAllBindingValues() {

		final List<BindingValue> returned = new ArrayList<>();

		try {
			visit(new ExpressionVisitor() {
				@Override
				public void visit(Expression e) {
					if (e instanceof BindingValue) {
						returned.add((BindingValue) e);
					}
				}
			});
		} catch (VisitorException e) {
			LOGGER.warning("Unexpected " + e);
		}

		return returned;
	}

	/**
	 * Returns an iterator on atomic expressions, which are generally all expressions that are not decomposable into smaller expressions (an
	 * atomic expression is a constant or a binding value, or a variable). An atomic expression has no child.
	 */
	public Iterator<Expression> atomicExpressions() {
		return getAllAtomicExpressions().iterator();
	}

	/**
	 * Returns all atomic expressions defined in this expression, which are generally all expressions that are not decomposable into smaller
	 * expressions (an atomic expression is a constant or a binding value, or a variable). An atomic expression has no child.
	 */
	public Vector<Expression> getAllAtomicExpressions() {
		Vector<Expression> returned = new Vector<>();
		appendAllAtomicExpressions(returned, this);
		return returned;
	}

	private static void appendAllAtomicExpressions(Vector<Expression> buildVector, Expression current) {
		if (current.getChilds() == null) {
			buildVector.add(current);
		}
		else {
			for (Expression e : current.getChilds()) {
				appendAllAtomicExpressions(buildVector, e);
			}
		}
	}

	/**
	 * Return the direct childs of this expression, which are involved in the direct decomposition of this expression. An atomic expression
	 * has no child.
	 * 
	 * @return
	 */
	protected abstract Vector<Expression> getChilds();

	/**
	 * Evaluate expression considering some declared variables
	 * 
	 * @param variables
	 * @return
	 * @throws TypeMismatchException
	 */
	@Deprecated
	public Expression evaluate(final Hashtable<String, ?> variables) throws TypeMismatchException {
		try {
			Expression resolvedExpression = transform(new ExpressionTransformer() {
				@Override
				public Expression performTransformation(Expression e) throws TransformException {
					if (e instanceof BindingValue) {
						BindingValue bv = (BindingValue) e;
						if (bv.isSimpleVariable() && variables.get(bv.toString()) != null) {
							return Constant.makeConstant(variables.get(bv.toString()));
						}
					}
					return e;
				}
			});
			return resolvedExpression.evaluate();
		} catch (TransformException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Deprecated
	public boolean evaluateCondition(final Hashtable<String, ?> variables) throws TypeMismatchException, UnresolvedExpressionException {
		// logger.info("evaluate "+this);
		// logger.info("variables "+variables);

		Expression evaluation = evaluate(variables);
		// logger.info("evaluation "+evaluation);
		if (evaluation == Constant.BooleanConstant.TRUE) {
			return true;
		}
		if (evaluation == Constant.BooleanConstant.FALSE) {
			return false;
		}
		LOGGER.warning("Unresolved expression: " + evaluation);
		throw new UnresolvedExpressionException();
	}

	@Deprecated
	public static List<BindingValue> extractBindingValues(String anExpression) throws ParseException, TypeMismatchException {

		return extractBindingValues(ExpressionParser.parse(anExpression));
	}

	@Deprecated
	public static List<BindingValue> extractBindingValues(final Expression expression) throws ParseException, TypeMismatchException {
		return expression.getAllBindingValues();
	}

	@Override
	public int hashCode() {
		return (getClass().getName() + "@[" + toString() + "]").hashCode();
	}

	public abstract boolean isSettable();

	public abstract Type getAccessedType();

}
