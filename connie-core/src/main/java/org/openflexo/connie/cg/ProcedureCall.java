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

package org.openflexo.connie.cg;

import java.util.Vector;

import org.openflexo.connie.expr.Expression;

public class ProcedureCall extends Instruction {

	private Procedure procedure;
	private Vector<Expression> arguments;

	public ProcedureCall(Procedure procedure) {
		super();
		this.procedure = procedure;
		this.arguments = new Vector<>();
	}

	public ProcedureCall(Procedure procedure, Expression... arguments) {
		this(procedure);
		for (Expression arg : arguments) {
			addArgument(arg);
		}
	}

	public ProcedureCall(Procedure procedure, Vector<Expression> arguments) {
		this(procedure);
		for (Expression arg : arguments) {
			addArgument(arg);
		}
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public Vector<Expression> getArguments() {
		return arguments;
	}

	public void setArguments(Vector<Expression> arguments) {
		this.arguments = arguments;
	}

	public void addArgument(Expression arg) {
		arguments.add(arg);
	}

	public void removeArgument(Expression arg) {
		arguments.remove(arg);
	}

	@Override
	public ProcedureCall clone() {
		ProcedureCall returned = new ProcedureCall(procedure, arguments);
		returned.setHeaderComment(getHeaderComment());
		returned.setInlineComment(getInlineComment());
		return returned;
	}

}
