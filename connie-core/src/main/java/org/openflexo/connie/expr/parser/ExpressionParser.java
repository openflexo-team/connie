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

package org.openflexo.connie.expr.parser;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.logging.Logger;

import org.openflexo.connie.expr.ArithmeticUnaryOperator;
import org.openflexo.connie.expr.Constant.ArithmeticConstant;
import org.openflexo.connie.expr.Constant.FloatConstant;
import org.openflexo.connie.expr.Constant.IntegerConstant;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.UnaryOperatorExpression;
import org.openflexo.connie.expr.parser.node.Start;
import org.openflexo.connie.parser.ParserEntryPoint;
import org.openflexo.connie.parser.ParseException;

/**
 * This class provides the parsing service for Connie expressions and bindings. This includes syntactic and semantics analyzer.<br>
 *
 * SableCC is used to generate the grammar located in connie-parser.<br>
 *
 * @author sylvain
 */
public class ExpressionParser {

	private static final Logger LOGGER = Logger.getLogger(ExpressionParser.class.getPackage().getName());

	/**
	 * This is the method to invoke to perform a parsing. Syntactic and (some) semantics analyzer are performed and returned value is an
	 * Expression conform to expression abstract syntactic tree
	 *
	 * @param anExpression
	 * @return
	 * @throws ParseException
	 *             if parsing expression lead to an error
	 */
	public static Expression parse(String anExpression) throws ParseException {
            // System.out.println("Parsing: " + anExpression);
            // Parse the input.
            Start tree = ParserEntryPoint.parse(anExpression);

            // Apply the semantics analyzer.
            ExpressionSemanticsAnalyzer t = new ExpressionSemanticsAnalyzer();
            tree.apply(t);

            return postSemanticAnalysisReduction(t.getExpression());
	}

	/**
	 * This method is invoked at the end of the parsing to perform some trivial reductions (eg, a combination of a minus and an arithmetic
	 * value results in a negative arithmetic value)
	 *
	 * @param e
	 * @return
	 */
	private static Expression postSemanticAnalysisReduction(Expression e) {
		if (e != null && e instanceof UnaryOperatorExpression
				&& ((UnaryOperatorExpression) e).getOperator() == ArithmeticUnaryOperator.UNARY_MINUS
				&& ((UnaryOperatorExpression) e).getArgument() instanceof ArithmeticConstant) {
			// In this case, we will reduce this into a negative single arithmetic constant
			ArithmeticConstant<?> c = (ArithmeticConstant<?>) ((UnaryOperatorExpression) e).getArgument();
			if (c instanceof IntegerConstant) {
				return new IntegerConstant(-((IntegerConstant) c).getValue());
			}
			else if (c instanceof FloatConstant) {
				return new FloatConstant(-((FloatConstant) c).getValue());
			}
			else {
				LOGGER.warning("Unexpected " + c);
			}
		}
		return e;
	}
}
