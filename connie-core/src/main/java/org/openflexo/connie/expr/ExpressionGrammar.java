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

/**
 * General API for an {@link ExpressionGrammar}
 * 
 * @author sylvain
 *
 */
public interface ExpressionGrammar {

	/**
	 * Return an array of all support {@link UnaryOperator} for this grammar
	 * 
	 * @return
	 */
	UnaryOperator[] getAllSupportedUnaryOperators();

	/**
	 * Return an array of all support {@link BinaryOperator} for this grammar
	 * 
	 * @return
	 */
	BinaryOperator[] getAllSupportedBinaryOperators();

	/**
	 * Return {@link String} representation for supplied {@link Operator}
	 * 
	 * @param operator
	 * @return
	 * @throws OperatorNotSupportedException
	 */
	String getSymbol(Operator operator) throws OperatorNotSupportedException;

	/**
	 * Return alternative {@link String} representation for supplied {@link Operator}
	 * 
	 * @param operator
	 * @return
	 * @throws OperatorNotSupportedException
	 */
	@Deprecated
	String getAlternativeSymbol(Operator operator) throws OperatorNotSupportedException;

	/**
	 * Return array of {@link Operator} classified as logical operators
	 * 
	 * @return
	 */
	public abstract Operator[] getLogicalOperators();

	/**
	 * Return array of {@link Operator} classified as comparison operators
	 * 
	 * @return
	 */
	public abstract Operator[] getComparisonOperators();

	/**
	 * Return array of {@link Operator} classified as arithmetic operators
	 * 
	 * @return
	 */
	public abstract Operator[] getArithmeticOperators();

	/**
	 * Return array of {@link Operator} classified as scientific operators
	 * 
	 * @return
	 */
	public abstract Operator[] getScientificOperators();

	/**
	 * Return array of {@link Operator} classified as trigonometric operators
	 * 
	 * @return
	 */
	public abstract Operator[] getTrigonometricOperators();

	/**
	 * Build a new {@link BinaryOperatorExpression} using supplied {@link BinaryOperator} and arguments
	 * 
	 * @param operator
	 * @param leftArgument
	 * @param rightArgument
	 * @return
	 */
	public abstract BinaryOperatorExpression makeBinaryOperatorExpression(BinaryOperator operator, Expression leftArgument,
			Expression rightArgument);

	/**
	 * Build a new {@link UnaryOperatorExpression} using supplied {@link UnaryOperator} and argument
	 * 
	 * @param operator
	 * @param argument
	 * @return
	 */
	public abstract UnaryOperatorExpression makeUnaryOperatorExpression(UnaryOperator operator, Expression argument);

	/**
	 * Build a new {@link ConditionalExpression} using supplied condition and arguments
	 * 
	 * @param condition
	 * @param thenExpression
	 * @param elseExpression
	 * @return
	 */
	public abstract ConditionalExpression makeConditionalExpression(Expression condition, Expression thenExpression,
			Expression elseExpression);

	/**
	 * Return a new {@link Constant} expression representing supplied value
	 * 
	 * @param <O>
	 * @param value
	 * @return
	 */
	public abstract <O> Constant<O> getConstant(O value);

}
