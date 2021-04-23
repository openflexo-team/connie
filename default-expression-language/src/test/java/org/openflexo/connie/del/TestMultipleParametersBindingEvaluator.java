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

package org.openflexo.connie.del;

import java.lang.reflect.InvocationTargetException;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.binding.javareflect.InvalidKeyValuePropertyException;
import org.openflexo.connie.del.DELBindingFactory;
import org.openflexo.connie.del.util.DELMultipleParametersBindingEvaluator;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;

import junit.framework.TestCase;

public class TestMultipleParametersBindingEvaluator extends TestCase {

	public void test1() throws InvalidKeyValuePropertyException, TypeMismatchException, NullReferenceException, InvocationTargetException {
		String variable1 = "Hello";
		String variable2 = "World";

		BindingFactory bindingFactory = new DELBindingFactory();
		assertEquals("Hello World !", DELMultipleParametersBindingEvaluator.evaluateBinding("{$variable1}+' '+{$variable2}+' !'",
				bindingFactory, new Object(), variable1, variable2));

	}

	public void test2() throws InvalidKeyValuePropertyException, TypeMismatchException, NullReferenceException, InvocationTargetException {
		String variable3 = "Hello World";

		BindingFactory bindingFactory = new DELBindingFactory();
		assertEquals("llo Wo", DELMultipleParametersBindingEvaluator.evaluateBinding("substring({$startIndex},{$endIndex})", bindingFactory,
				variable3, 2, 8));

	}

	public void test3() throws InvalidKeyValuePropertyException, TypeMismatchException, NullReferenceException, InvocationTargetException {
		String variable4 = "Hello World";

		try {
			BindingFactory bindingFactory = new DELBindingFactory();
			DELMultipleParametersBindingEvaluator.evaluateBinding("regionMatches({$toffset},{$other},{$ooffset},{$len})", bindingFactory,
					variable4, 0, null, 1, 2);
			fail();
		} catch (InvocationTargetException e) {
			Throwable targetException = e.getTargetException();
			assertTrue(targetException.getStackTrace()[0].toString().contains("java.lang.String.regionMatches"));
		}

	}

}
