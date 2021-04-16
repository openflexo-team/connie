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

package org.openflexo.connie.java.expr;

import java.lang.reflect.Type;

import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.expr.CastExpression;
import org.openflexo.connie.expr.Expression;
import org.openflexo.connie.expr.ExpressionPrettyPrinter;
import org.openflexo.connie.expr.ExpressionTransformer;

public class JavaCastExpression extends CastExpression {

	public JavaCastExpression(Type castType, Expression argument) {
		super(castType, argument);
	}

	@Override
	public ExpressionPrettyPrinter getPrettyPrinter() {
		return JavaPrettyPrinter.getInstance();
	}

	@Override
	public int getPriority() {
		return 2;
	}

	@Override
	public Expression transform(ExpressionTransformer transformer) throws TransformException {

		Expression expression = this;
		Expression transformedArgument = getArgument().transform(transformer);

		if (!transformedArgument.equals(getArgument())) {
			expression = new JavaCastExpression(getCastType(), transformedArgument);
		}

		return transformer.performTransformation(expression);
	}

}
