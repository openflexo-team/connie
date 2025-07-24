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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.connie.Bindable;
import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.exception.TypeMismatchException;

/**
 * Represents a expression
 * 
 * @author sylvain
 * 
 */
public abstract class Expression {

	private static final Logger LOGGER = Logger.getLogger(Expression.class.getPackage().getName());

	public abstract void visit(ExpressionVisitor visitor) throws VisitorException;

	public abstract Expression transform(ExpressionTransformer transformer) throws TransformException;

	public final Expression evaluate(BindingEvaluationContext context)
			throws TypeMismatchException, NullReferenceException, ReflectiveOperationException {
		try {
			return transform(context.getEvaluator());
		} catch (TypeMismatchException e) {
			String expressionAsString;
			try {
				expressionAsString = toString();
			} catch (Exception e2) {
				expressionAsString = "<???>";
			}
			LOGGER.warning("Unexpected TypeMismatchException occured during evaluation of " + expressionAsString);
			throw e;
		} catch (NullReferenceException e) {
			throw e;
		} catch (InvocationTargetTransformException e) {
			// LOGGER.warning("Unexpected exception occurred during evaluation " + e.getException());
			if (e.getException() != null) {
				throw e.getException();
			}
			throw new InvocationTargetException(e.getException());
		} catch (TransformException e) {
			String expressionAsString;
			try {
				expressionAsString = toString();
			} catch (Exception e2) {
				expressionAsString = "<???>";
			}
			LOGGER.warning("Unexpected exception occured during evaluation of " + expressionAsString);
			e.printStackTrace();
			return null;
		}
	}

	public abstract int getPriority();

	public abstract int getDepth();

	@Override
	public String toString() {
		return getPrettyPrinter().getStringRepresentation(this, null);
	}

	public String toString(Bindable context) {
		return getPrettyPrinter().getStringRepresentation(this, context);
	}

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
	 * Return a list containing all {@link BindingPath} used in this expression
	 * 
	 * @return
	 */
	public List<BindingPath> getAllBindingValues() {
		final List<BindingPath> returned = new ArrayList<>();
		try {
			visit(new ExpressionVisitor() {
				@Override
				public void visit(Expression e) {
					if (e instanceof BindingPath) {
						returned.add((BindingPath) e);
					}
				}
			});
		} catch (VisitorException e) {
			LOGGER.warning("Unexpected " + e);
		}
		return returned;
	}

	/**
	 * Return a list containing all {@link BindingVariables} used in this expression
	 * 
	 * @return
	 */
	public List<BindingVariable> getAllBindingVariables() {
		final List<BindingVariable> returned = new ArrayList<>();
		try {
			visit(new ExpressionVisitor() {
				@Override
				public void visit(Expression e) {
					if (e instanceof BindingPath) {
						returned.add(((BindingPath) e).getBindingVariable());
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

	@Override
	public int hashCode() {
		return (getClass().getName() + "@[" + toString() + "]").hashCode();
	}

	public abstract boolean isSettable();

	public abstract Type getAccessedType();

	public abstract ExpressionPrettyPrinter getPrettyPrinter();
}
