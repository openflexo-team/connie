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

package org.openflexo.connie.exception;

import org.openflexo.connie.BindingEvaluationContext;
import org.openflexo.connie.EvaluationPolicy;

/**
 * Thrown, in a context whose policy is {@link EvaluationPolicy#STRICT}, by an evaluation that would give null in a lenient one: the binding
 * is invalid or not settable, the expression cannot be reduced to a value, a Java method or constructor refuses its arguments or its
 * access, or a collection changed while it was read (CONNIE-F-4).
 * <p>
 * Unchecked: only a context that asked for a strict evaluation receives it, and it travels, unchanged, through the code in between (much
 * of which catches the checked exceptions of an evaluation and gives null), up to the caller that can tell what to do. It holds the text of
 * the binding, not the binding itself.
 */
@SuppressWarnings("serial")
public class BindingEvaluationException extends RuntimeException {

	/** What failed */
	public enum Kind {
		/** The binding is not valid: it cannot be evaluated */
		INVALID_BINDING,
		/** A value is set to a binding that cannot be set */
		NOT_SETTABLE,
		/** The expression cannot be reduced to a value */
		INCOMPLETE_EVALUATION,
		/** The transformation of the expression failed */
		TRANSFORMATION_FAILURE,
		/** A Java method or constructor refused its arguments */
		ILLEGAL_ARGUMENT,
		/** A Java method or constructor is not accessible */
		ILLEGAL_ACCESS,
		/** A collection changed while it was read */
		CONCURRENT_MODIFICATION
	}

	private final Kind kind;
	private final String binding;

	public BindingEvaluationException(Kind kind, Object binding, String reason) {
		this(kind, binding, reason, null);
	}

	public BindingEvaluationException(Kind kind, Object binding, String reason, Throwable cause) {
		super(kind + ": " + text(binding) + (reason != null ? ": " + reason : ""), cause);
		this.kind = kind;
		this.binding = text(binding);
	}

	public Kind getKind() {
		return kind;
	}

	/** The text of the binding (or expression) that failed */
	public String getBinding() {
		return binding;
	}

	private static String text(Object binding) {
		if (binding == null) {
			return "<null>";
		}
		try {
			return binding.toString();
		} catch (RuntimeException e) {
			// The text of a binding must not hide the failure being thrown
			return "<" + binding.getClass().getSimpleName() + ">";
		}
	}

	/**
	 * Throw a {@link BindingEvaluationException} when supplied context is strict; return normally otherwise, the caller then gives its
	 * lenient result
	 */
	public static void failIfStrict(BindingEvaluationContext context, Kind kind, Object binding, String reason, Throwable cause) {
		if (EvaluationPolicy.isStrict(context)) {
			throw new BindingEvaluationException(kind, binding, reason, cause);
		}
	}
}
