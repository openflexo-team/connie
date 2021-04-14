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

import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.BindingVariable;
import org.openflexo.connie.DataBinding;
import org.openflexo.connie.DefaultBindable;
import org.openflexo.connie.del.DELBindingFactory;

import com.google.common.reflect.TypeToken;

import junit.framework.TestCase;

public class TestBindingAnalysing extends TestCase {

	private final BindingFactory BINDING_FACTORY = new DELBindingFactory();
	private final TestBindable BINDABLE = new TestBindable();
	private final TestBindingModel BINDING_MODEL = new TestBindingModel();

	public class TestBindable extends DefaultBindable {

		@Override
		public BindingFactory getBindingFactory() {
			return BINDING_FACTORY;
		}

		@Override
		public BindingModel getBindingModel() {
			return BINDING_MODEL;
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> dataBinding) {
		}

		@Override
		public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}
	}

	public class TestBindingModel extends BindingModel {
		private BindingVariable aString;

		public TestBindingModel() {
			addToBindingVariables(aString = new BindingVariable("aString", String.class));
			addToBindingVariables(new BindingVariable("aBoolean", Boolean.TYPE));
			addToBindingVariables(new BindingVariable("anInt", Integer.TYPE));
			addToBindingVariables(new BindingVariable("aList", new TypeToken<List<String>>() {
			}.getType()));
		}
	}

	public DataBinding<?> genericTest(String bindingPath, boolean expectedValidity, Type expectedType) {

		System.out.println("Evaluate " + bindingPath);

		DataBinding<?> dataBinding = new DataBinding<>(bindingPath, BINDABLE, expectedType, DataBinding.BindingDefinitionType.GET);

		/*	BINDING_FACTORY.setBindable(BINDABLE);
			AbstractBinding binding = BINDING_FACTORY.convertFromString(bindingPath);
			binding.setBindingDefinition(new BindingDefinition("test", expectedType, BindingDefinitionType.GET, true));*/

		if (dataBinding.getExpression() != null) {
			System.out.println(
					"Parsed " + dataBinding + " as " + dataBinding.getExpression() + " of " + dataBinding.getExpression().getClass());

			assertEquals(expectedValidity, dataBinding.isValid());

			if (dataBinding.isValid()) {
				assertEquals(expectedType, dataBinding.getAnalyzedType());
			}

			return dataBinding;

		}
		System.out.println("Could not Parse " + dataBinding + " defined as " + dataBinding.getUnparsedBinding());
		fail("Unparseable binding");
		return null;
	}

	public void testTrivialCase() {
		System.out.println("*********** testTrivialCase");

		genericTest("aString", true, String.class);

	}

	public void testFailTrivialCase() {
		System.out.println("*********** testFailTrivialCase");

		genericTest("aString2", false, null);

	}

	public void testAddRemoveBindingVariables() {
		System.out.println("*********** testAddRemoveBindingVariables");

		DataBinding<?> db = genericTest("aString+anInt+aBoolean", true, String.class);

		BINDING_MODEL.removeFromBindingVariables(BINDING_MODEL.aString);
		assertFalse(db.isValid());

		BINDING_MODEL.addToBindingVariables(BINDING_MODEL.aString);
		assertTrue(db.isValid());

	}

	public void testChangeVariableName() {
		System.out.println("*********** testChangeVariableName");

		DataBinding<?> db = genericTest("aString+anInt+aBoolean", true, String.class);

		BINDING_MODEL.aString.setVariableName("anOtherString");

		// Binding has been updated
		assertEquals("((anOtherString + anInt) + aBoolean)", db.toString());
		assertTrue(db.isValid());
	}

}
