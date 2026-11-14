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

package org.openflexo.connie.del;

import java.lang.reflect.InvocationTargetException;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.EvaluationPolicy;
import org.openflexo.connie.del.TestBinding.TestBindingContext;
import org.openflexo.connie.exception.BindingEvaluationException;
import org.openflexo.connie.exception.InvocationTargetTransformException;
import org.openflexo.connie.exception.NotSettableContextException;
import org.openflexo.connie.exception.TransformException;
import org.openflexo.connie.expr.VisitorException;

import junit.framework.TestCase;

/**
 * The evaluation policy of a context (CONNIE-F-4): a lenient context gets null where a strict one gets a
 * {@link BindingEvaluationException}; and the exceptions of Connie keep their message and their cause.
 */
public class TestEvaluationPolicy extends TestCase {

	private static final TestBindingContext LENIENT = new TestBindingContext();

	private static final TestBindingContext STRICT = new TestBindingContext() {
		@Override
		public EvaluationPolicy getEvaluationPolicy() {
			return EvaluationPolicy.STRICT;
		}
	};

	private static DataBinding<Object> binding(String text) {
		return new DataBinding<>(text, LENIENT, Object.class, DataBinding.BindingDefinitionType.GET);
	}

	private static DataBinding<Object> settableBinding(String text) {
		return new DataBinding<>(text, LENIENT, Object.class, DataBinding.BindingDefinitionType.GET_SET);
	}

	public void testDefaultPolicyIsLenient() {
		assertEquals(EvaluationPolicy.LENIENT, LENIENT.getEvaluationPolicy());
		assertFalse(EvaluationPolicy.isStrict(LENIENT));
		assertTrue(EvaluationPolicy.isStrict(STRICT));
		assertFalse(EvaluationPolicy.isStrict(null));
	}

	public void testValidBindingIsEvaluatedInBothPolicies() throws Exception {
		assertEquals(14, ((Number) binding("aString.length()").getBindingValue(LENIENT)).intValue());
		assertEquals(14, ((Number) binding("aString.length()").getBindingValue(STRICT)).intValue());
	}

	public void testInvalidBindingGivesNullOrFails() throws Exception {
		DataBinding<Object> invalid = binding("zzUnknownVariable.foo");
		assertFalse(invalid.isValid());
		assertNull(invalid.getBindingValue(LENIENT));
		try {
			invalid.getBindingValue(STRICT);
			fail("A strict evaluation of an invalid binding must fail");
		} catch (BindingEvaluationException e) {
			assertEquals(BindingEvaluationException.Kind.INVALID_BINDING, e.getKind());
			assertEquals("zzUnknownVariable.foo", e.getBinding());
			assertTrue(e.getMessage(), e.getMessage().contains("zzUnknownVariable"));
		}
	}

	public void testUnsetBindingGivesNullInBothPolicies() throws Exception {
		DataBinding<Object> unset = new DataBinding<>(LENIENT, Object.class, DataBinding.BindingDefinitionType.GET);
		assertFalse(unset.isSet());
		assertNull(unset.getBindingValue(LENIENT));
		assertNull(unset.getBindingValue(STRICT));
	}

	public void testSettingAnInvalidBindingIsIgnoredOrFails() throws Exception {
		DataBinding<Object> invalid = settableBinding("zzUnknownVariable");
		assertFalse(invalid.isValid());
		invalid.setBindingValue("x", LENIENT);
		try {
			invalid.setBindingValue("x", STRICT);
			fail("A strict assignment of an invalid binding must fail");
		} catch (BindingEvaluationException e) {
			assertEquals(BindingEvaluationException.Kind.INVALID_BINDING, e.getKind());
		}
	}

	public void testSettingANotSettableBindingIsIgnoredOrFails() throws Exception {
		DataBinding<Object> notSettable = settableBinding("anInt + 1");
		if (notSettable.isValid() && !notSettable.isSettable()) {
			notSettable.setBindingValue(3, LENIENT);
			try {
				notSettable.setBindingValue(3, STRICT);
				fail("A strict assignment of a binding that cannot be set must fail");
			} catch (BindingEvaluationException e) {
				assertEquals(BindingEvaluationException.Kind.NOT_SETTABLE, e.getKind());
			}
		}
		else {
			// A GET_SET binding on a computed expression is reported invalid: a strict assignment still fails
			try {
				notSettable.setBindingValue(3, STRICT);
				fail("A strict assignment of a binding that cannot be set must fail");
			} catch (BindingEvaluationException e) {
				assertTrue(e.getKind() == BindingEvaluationException.Kind.NOT_SETTABLE
						|| e.getKind() == BindingEvaluationException.Kind.INVALID_BINDING);
			}
		}
	}

	public void testExceptionsKeepTheirMessageAndCause() {
		assertEquals("m", new TransformException("m").getMessage());
		RuntimeException cause = new RuntimeException("c");
		assertSame(cause, new TransformException("m", cause).getCause());
		assertEquals("m", new NotSettableContextException("m").getMessage());
		assertEquals("m", new VisitorException("m").getMessage());
		InvocationTargetException target = new InvocationTargetException(new IllegalStateException("inner"));
		InvocationTargetTransformException itte = new InvocationTargetTransformException(target);
		assertSame(target, itte.getCause());
		assertSame(target, itte.getException());
		assertTrue(itte.getMessage(), itte.getMessage().contains("inner"));
	}
}
