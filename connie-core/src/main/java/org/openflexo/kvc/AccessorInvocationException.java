/**
 * 
 * Copyright (c) 2014, Openflexo
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


package org.openflexo.kvc;

import java.lang.reflect.InvocationTargetException;

/**
 * <p>
 * Exception thrown when invoking a class accessor during coding or decoding process.
 * </p>
 * The <code>message</code> (see {@link #getMessage()}) contains the error description, and <code>targetException</code> (see
 * {@link #getTargetException()})
 * 
 * @author <a href="mailto:Sylvain.Guerin@enst-bretagne.fr">Sylvain Guerin</a>
 * @see org.openflexo.kvc.KeyValueProperty
 */
public class AccessorInvocationException extends RuntimeException {

	protected Throwable targetException;

	/**
	 * Creates a new <code>AccessorInvocationException</code> instance.
	 * 
	 * @param exception target exception.
	 * 
	 */
	public AccessorInvocationException(InvocationTargetException exception) {

		super("Exception raised during accessors execution: " + exception.getTargetException().getMessage());
		targetException = exception.getTargetException();
	}

	/**
	 * Creates a new <code>AccessorInvocationException</code> instance given a message <code>aMessage</code>
	 * 
	 * @param aMessage
	 *            a <code>String</code> value
	 * @param exception target exception.
	 */
	public AccessorInvocationException(String aMessage, InvocationTargetException exception) {

		super(aMessage + " : " + exception.getTargetException().getClass().getName() + "[message="
				+ exception.getTargetException().getMessage() + "]");
		targetException = exception.getTargetException();
	}

	/**
	 * Return the exception thrown during accessor invocation
	 */
	public Throwable getTargetException() {
		return targetException;
	}

	@Override
	public Throwable getCause() {
		return getTargetException();
	}

}
