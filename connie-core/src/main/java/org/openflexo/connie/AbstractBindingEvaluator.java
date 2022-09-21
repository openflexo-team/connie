/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

import org.openflexo.connie.binding.javareflect.InvalidKeyValuePropertyException;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.type.TypingSpace;

/**
 * Abstract utility class allowing to compute binding value over an expression<br>
 * </ul>
 * 
 * @author sylvain
 * 
 */
public abstract class AbstractBindingEvaluator extends DefaultContextualizedBindable implements BindingEvaluationContext {

	// private static final BindingFactory BINDING_FACTORY = new JavaBasedBindingFactory();

	private BindingModel bindingModel;
	private BindingFactory bindingFactory;

	protected AbstractBindingEvaluator(BindingFactory bindingFactory, TypingSpace typingSpace) {
		super(typingSpace);

		this.bindingFactory = bindingFactory;

		bindingModel = new BindingModel();
	}

	public void delete() {
		bindingModel.delete();
		bindingModel = null;
	}

	@Override
	public BindingModel getBindingModel() {
		return bindingModel;
	}

	@Override
	public BindingFactory getBindingFactory() {
		return bindingFactory;
	}

	@Override
	public void notifiedBindingChanged(DataBinding<?> dataBinding) {
	}

	@Override
	public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
	}

	protected abstract Object evaluate(String bindingPath)
			throws InvalidKeyValuePropertyException, TypeMismatchException, NullReferenceException, ReflectiveOperationException;

}
