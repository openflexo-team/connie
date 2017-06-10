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
import java.util.logging.Logger;

import org.openflexo.connie.expr.Variable;
import org.openflexo.logging.FlexoLogger;

public class Procedure implements AlgorithmicUnit {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = FlexoLogger.getLogger(Procedure.class.getPackage().getName());

	private String procedureName;
	private Vector<ProcedureParameter> parameters;
	private ControlGraph controlGraph;
	private String comment;
	private String returnType;

	public Procedure(String procedureName, ControlGraph controlGraph) {
		super();
		this.procedureName = procedureName;
		this.controlGraph = controlGraph;
		this.parameters = new Vector<>();
	}

	public Procedure(String procedureName, ControlGraph controlGraph, ProcedureParameter... parameters) {
		this(procedureName, controlGraph);
		for (ProcedureParameter p : parameters) {
			addParameter(p);
		}
	}

	public Procedure(String procedureName, ControlGraph controlGraph, String comment, ProcedureParameter... parameters) {
		this(procedureName, controlGraph, parameters);
		setComment(comment);
	}

	public Procedure(String procedureName, Vector<ProcedureParameter> parameters, ControlGraph controlGraph) {
		this(procedureName, controlGraph);
		for (ProcedureParameter p : parameters) {
			addParameter(p);
		}
	}

	public Procedure(String procedureName, Vector<ProcedureParameter> parameters, ControlGraph controlGraph, String comment) {
		this(procedureName, parameters, controlGraph);
		setComment(comment);
	}

	public ControlGraph getControlGraph() {
		return controlGraph;
	}

	public void setControlGraph(ControlGraph controlGraph) {
		this.controlGraph = controlGraph;
	}

	public Vector<ProcedureParameter> getParameters() {
		return parameters;
	}

	public void setParameters(Vector<ProcedureParameter> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(ProcedureParameter parameter) {
		parameters.add(parameter);
	}

	public void removeParameter(ProcedureParameter parameter) {
		parameters.remove(parameter);
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public static class ProcedureParameter {
		private Variable variable;
		private Type type;

		public ProcedureParameter(Variable variable, Type type) {
			super();
			this.variable = variable;
			this.type = type;
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public Variable getVariable() {
			return variable;
		}

		public void setVariable(Variable variable) {
			this.variable = variable;
		}
	}

}
