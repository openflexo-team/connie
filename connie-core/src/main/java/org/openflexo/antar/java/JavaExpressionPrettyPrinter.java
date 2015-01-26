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

package org.openflexo.antar.java;

import java.util.Date;

import org.openflexo.antar.expr.Constant.DateSymbolicConstant;
import org.openflexo.antar.expr.Constant.DurationConstant;
import org.openflexo.antar.expr.Constant.FloatSymbolicConstant;
import org.openflexo.antar.expr.DefaultExpressionPrettyPrinter;
import org.openflexo.antar.expr.Expression;
import org.openflexo.antar.expr.SymbolicConstant;
import org.openflexo.toolbox.Duration;

public class JavaExpressionPrettyPrinter extends DefaultExpressionPrettyPrinter {

	public JavaExpressionPrettyPrinter() {
		super(new JavaGrammar());
	}

	@Override
	public String getStringRepresentation(Expression expression) {
		if (expression instanceof JavaPrettyPrintable) {
			return ((JavaPrettyPrintable) expression).getJavaStringRepresentation();
		}
		return super.getStringRepresentation(expression);
	}

	@Override
	protected String makeStringRepresentation(SymbolicConstant constant) {
		if (constant == FloatSymbolicConstant.E) {
			return "Math.E";
		} else if (constant == FloatSymbolicConstant.PI) {
			return "Math.PI";
		} else if (constant == DateSymbolicConstant.NOW) {
			// TODO not implemented
			return "new Date() /* NOW */";
		} else if (constant == DateSymbolicConstant.TODAY) {
			// TODO not implemented
			return "new Date() /* TODAY */";
		}
		return super.makeStringRepresentation(constant);
	}

	@Override
	protected String makeStringRepresentation(DurationConstant constant) {
		return getJavaStringRepresentation(constant.getDuration());
	}

	public static String getJavaStringRepresentation(Duration aDuration) {
		return "new Duration(" + aDuration.getValue() + ",Duration.DurationUnit." + aDuration.getUnit().toString() + ")";
	}

	public static String getJavaStringRepresentation(Date aDate) {
		// TODO not implemented
		return "new Date() /* Please implement date representation for " + aDate + " */";
	}
}
