package org.openflexo.rm;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
		assertTrue(((FileResourceImpl) r).getFile().getAbsolutePath().contains("target"));
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

		assertTrue(((FileResourceImpl) r).getFile().getAbsolutePath().contains("target"));
		assertTrue(((FileResourceImpl) r2).getFile().getAbsolutePath().contains("src/main/resources"));

		assertSame(r2, r3);

	}

}
