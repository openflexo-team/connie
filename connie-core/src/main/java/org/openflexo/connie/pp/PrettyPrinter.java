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

package org.openflexo.connie.pp;

import java.io.PrintStream;

import org.openflexo.connie.cg.AlgorithmicUnit;
import org.openflexo.connie.cg.Class;
import org.openflexo.connie.cg.Conditional;
import org.openflexo.connie.cg.ControlGraph;
import org.openflexo.connie.cg.Flow;
import org.openflexo.connie.cg.Instruction;
import org.openflexo.connie.cg.Loop;
import org.openflexo.connie.cg.Procedure;
import org.openflexo.connie.cg.Sequence;
import org.openflexo.connie.expr.Expression;

public abstract class PrettyPrinter {

	private ExpressionPrettyPrinter expressionPrettyPrinter;

	public PrettyPrinter(ExpressionPrettyPrinter expressionPrettyPrinter) {
		super();
		this.expressionPrettyPrinter = expressionPrettyPrinter;
	}

	public void print(ControlGraph expression, PrintStream out) {
		out.print(getStringRepresentation(expression));
	}

	public String getStringRepresentation(AlgorithmicUnit algorithmicUnit) {
		if (algorithmicUnit == null) {
			return "null";
		}
		if (algorithmicUnit instanceof ControlGraph) {
			return makeStringRepresentation((ControlGraph) algorithmicUnit);
		}
		if (algorithmicUnit instanceof Procedure) {
			return makeStringRepresentation((Procedure) algorithmicUnit);
		}
		if (algorithmicUnit instanceof Class) {
			return makeStringRepresentation((Class) algorithmicUnit);
		}
		return algorithmicUnit.toString();
	}

	public String makeStringRepresentation(ControlGraph statement) {
		if (statement == null) {
			return "null";
		}
		if (statement instanceof Conditional) {
			return makeStringRepresentation((Conditional) statement);
		}
		if (statement instanceof Instruction) {
			return makeStringRepresentation((Instruction) statement);
		}
		if (statement instanceof Loop) {
			return makeStringRepresentation((Loop) statement);
		}
		if (statement instanceof Sequence) {
			return makeStringRepresentation((Sequence) statement);
		}
		if (statement instanceof Flow) {
			return makeStringRepresentation((Flow) statement);
		}
		return statement.toString();
	}

	public void print(Procedure procedure, PrintStream out) {
		out.print(makeStringRepresentation(procedure));
	}

	public abstract String makeStringRepresentation(Class aClass);

	public abstract String makeStringRepresentation(Procedure procedure);

	public abstract String makeStringRepresentation(Conditional conditional);

	public abstract String makeStringRepresentation(Loop loop);

	public abstract String makeStringRepresentation(Sequence sequence);

	public abstract String makeStringRepresentation(Flow sequence);

	public abstract String makeStringRepresentation(Instruction instruction);

	public String getStringRepresentation(Expression expression) {
		return expressionPrettyPrinter.getStringRepresentation(expression);
	}

}
