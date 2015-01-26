/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Flexoutils, a component of the software infrastructure 
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

package org.openflexo.letparser;

import java.util.Arrays;
import java.util.List;

/*
 * Created on 4 janv. 2006 by sguerin
 *
 * Flexo Application Suite
 * (c) Denali 2003-2005
 */

public class Operator implements AbstractToken {

	public static final Operator AND = new Operator("AND", "&&", 2);
	public static final Operator OR = new Operator("OR", "||", 3);
	public static final Operator EQU = new Operator("=", "==", 1);
	public static final Operator NEQ = new Operator("!=", 1);

	public static final List<Operator> KNOWN_OPERATORS = Arrays.asList(AND, OR, EQU, NEQ);

	public static List<Operator> getKnownOperators() {
		return KNOWN_OPERATORS;
	}

	private String _symbol;
	private String _alternativeSymbol;
	private int _priority;

	public Operator(String symbol, String alternativeSymbol, int priority) {
		this(symbol, priority);
		_alternativeSymbol = alternativeSymbol;
	}

	public Operator(String symbol, int priority) {
		_priority = priority;
		_symbol = symbol;
		_alternativeSymbol = null;
	}

	public String getSymbol() {
		return _symbol;
	}

	public String getAlternativeSymbol() {
		return _alternativeSymbol;
	}

	@Override
	public String toString() {
		return _symbol;
	}

	public int getPriority() {
		return _priority;
	}

}
