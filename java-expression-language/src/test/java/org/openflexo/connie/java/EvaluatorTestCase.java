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

package org.openflexo.connie.java;

import java.lang.reflect.InvocationTargetException;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.exception.NullReferenceException;
import org.openflexo.connie.exception.TypeMismatchException;
import org.openflexo.connie.java.util.JavaBindingEvaluator;
import org.openflexo.kvc.InvalidKeyValuePropertyException;

import junit.framework.TestCase;

public abstract class EvaluatorTestCase extends TestCase {

	public static void genericTest(String bindingPath, Object object, Object expectedResult) {

		System.out.println("Evaluate " + bindingPath);

		BindingFactory bindingFactory = new JavaBindingFactory();

		Object evaluatedResult = null;
		try {
			evaluatedResult = JavaBindingEvaluator.evaluateBinding(bindingPath, object, bindingFactory);
		} catch (InvalidKeyValuePropertyException e) {
			e.printStackTrace();
			fail();
		} catch (TypeMismatchException e) {
			e.printStackTrace();
			fail();
		} catch (NullReferenceException e) {
			fail();
		} catch (InvocationTargetException e) {
			fail();
		}
		System.out.println("Evaluated as " + evaluatedResult);

		if (expectedResult instanceof Number) {
			if (evaluatedResult instanceof Number) {
				assertEquals(((Number) expectedResult).doubleValue(), ((Number) evaluatedResult).doubleValue());
			}
			else {
				fail("Evaluated value is not a number (expected: " + expectedResult + ") but " + evaluatedResult);
			}
		}
		else {
			assertEquals(expectedResult, evaluatedResult);
		}
	}

}
