/**
 * 
 * Copyright (c) 2026, Openflexo
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

package org.openflexo.connie;

import org.openflexo.connie.exception.BindingEvaluationException;

/**
 * How a {@link BindingEvaluationContext} wants the failures of an evaluation to be signalled (CONNIE-F-4).
 * <ul>
 * <li>{@link #LENIENT} (the default): a binding that cannot be evaluated gives null, as Connie always did. Suited to a user interface that
 * evaluates its bindings all the time, some of them before their data is there.</li>
 * <li>{@link #STRICT}: such a failure throws a {@link BindingEvaluationException}, which tells what failed and why. Suited to the
 * execution of a language, where a null would hide the fault and go on.</li>
 * </ul>
 * The policy is the one of the context the evaluation runs in, so it also applies to the nested evaluations (arguments, path elements).
 */
public enum EvaluationPolicy {

	LENIENT, STRICT;

	/** Tell if supplied context asks for a strict evaluation */
	public static boolean isStrict(BindingEvaluationContext context) {
		return context != null && context.getEvaluationPolicy() == STRICT;
	}
}
