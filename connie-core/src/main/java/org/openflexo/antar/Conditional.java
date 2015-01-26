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

package org.openflexo.antar;

import org.openflexo.antar.expr.Expression;

public class Conditional extends ControlGraph {

	private Expression condition;
	private ControlGraph thenStatement;
	private ControlGraph elseStatement; // Might be null when no "else" statement

	public Conditional(Expression condition, ControlGraph thenStatement) {
		this(condition, thenStatement, (ControlGraph) null);
	}

	public Conditional(Expression condition, ControlGraph thenStatement, String headerComment) {
		this(condition, thenStatement);
		setHeaderComment(headerComment);
	}

	public Conditional(Expression condition, ControlGraph thenStatement, ControlGraph elseStatement) {
		super();
		this.condition = condition;
		this.thenStatement = thenStatement;
		this.elseStatement = elseStatement;
	}

	public Conditional(Expression condition, ControlGraph thenStatement, ControlGraph elseStatement, String headerComment) {
		this(condition, thenStatement, elseStatement);
		setHeaderComment(headerComment);
	}

	public Conditional(Expression condition, ControlGraph thenStatement, ControlGraph elseStatement, String headerComment,
			String inlineComment) {
		this(condition, thenStatement, elseStatement, headerComment);
		setInlineComment(inlineComment);
	}

	public Expression getCondition() {
		return condition;
	}

	public void setCondition(Expression condition) {
		this.condition = condition;
	}

	public ControlGraph getElseStatement() {
		return elseStatement;
	}

	public void setElseStatement(ControlGraph elseStatement) {
		this.elseStatement = elseStatement;
	}

	public ControlGraph getThenStatement() {
		return thenStatement;
	}

	public void setThenStatement(ControlGraph thenStatement) {
		this.thenStatement = thenStatement;
	}

	@Override
	public String toString() {
		return "IF (" + condition + ") THEN { \n" + thenStatement + " } "
				+ (elseStatement != null ? "ELSE { \n" + elseStatement + " } " : "");
	}

	@Override
	public ControlGraph normalize() {
		return new Conditional(condition, thenStatement.normalize(), elseStatement != null ? elseStatement.normalize() : null,
				getHeaderComment(), getInlineComment());
	}

}
