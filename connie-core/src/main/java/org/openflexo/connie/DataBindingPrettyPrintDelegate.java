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

package org.openflexo.connie;

/**
 * Writes the text of a {@link DataBinding} as it was parsed: what the user wrote ({@code 1+2}, parentheses, qualified names...), the parts that
 * changed since being written again. The language that parsed the binding installs it with
 * {@link DataBinding#setPrettyPrintDelegate(DataBindingPrettyPrintDelegate)}; {@link DataBinding#getPrettyPrint()} then uses it.
 * {@link DataBinding#toString()} keeps giving the normalized text, written from the expression alone, on which the equality of bindings relies.
 * <p>
 * A delegate is attached to the expression it was built for: when the binding holds another expression, it gives the normalized text.
 * 
 * @author sylvain
 */
public interface DataBindingPrettyPrintDelegate {

	/**
	 * The text of the supplied binding as it was parsed, its changed parts written again
	 * 
	 * @param binding
	 *            the binding this delegate is installed on
	 * @return the text, never null ({@code binding.toString()} when the parsed text does not apply)
	 */
	String getPrettyPrint(DataBinding<?> binding);
}
