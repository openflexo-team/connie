/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.rm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

/**
 * Test (some) ResourceLocator features
 * 
 * @author sylvain
 * 
 */
@RunWith(OrderedRunner.class)
public class TestResourceLocator {

	@Test
	@TestOrder(1)
	public void testLocateResourceInClassPath() {
		Resource r = ResourceLocator.locateResource("Config/logging.properties");
		assertNotNull(r);
		assertTrue(r instanceof FileResourceImpl);
		assertTrue(((FileResourceImpl) r).getFile().getAbsolutePath().contains("Config"));
	}

	@Test
	@TestOrder(2)
	public void testCache() {
		Resource r = ResourceLocator.locateResource("Config/logging.properties");
		Resource r2 = ResourceLocator.locateResource("Config/logging.properties");
		assertSame(r, r2);
	}

	@Test
	@TestOrder(3)
	public void testLocateResourceInSourceCode() {
		Resource r = ResourceLocator.locateResource("Config/logging.properties");
		Resource r2 = ResourceLocator.locateSourceCodeResource(r);
		Resource r3 = ResourceLocator.locateSourceCodeResource("Config/logging.properties");

		assertTrue(r instanceof FileResourceImpl);
		assertTrue(r2 instanceof FileResourceImpl);
		assertTrue(r3 instanceof FileResourceImpl);

		assertTrue(((FileResourceImpl) r).getFile().getAbsolutePath().contains("Config"));
		assertTrue(((FileResourceImpl) r2).getFile().getAbsolutePath()
				.contains("src" + File.separator + "main" + File.separator + "resources"));

		assertSame(r2, r3);

	}

}
