package org.openflexo.toolbox;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestFlexoVersion extends TestCase {

	@Test
	@TestOrder(1)
	public void testValideVersion() {

		String[] s = { "0.9.0", "0.9.1", "0.9.0RC1", "0.9.0RC2", "0.9.0alpha", "0.9.0beta", "0.10.0", "0.10.1", "0.10.0RC1", "0.10.0RC2",
				"0.10.0alpha", "0.10.0beta", "0.9", "1.0alpha", "1.1alpha", "1.1RC8", "1.1-SNAPSHOT", "1.1.0-SNAPSHOT","1.1.0SNAPSHOT", "1.0SNAPSHOT" };
		for (int i = 0; i < s.length; i++) {
			String string = s[i];
			assertTrue(FlexoVersion.isValidVersionString(string));
			FlexoVersion v = new FlexoVersion(string);
			assertNotNull(v);
		}

	}
	
	@Test
	@TestOrder(2)
	public void testCompareVersions() {

		FlexoVersion v090 = new FlexoVersion("0.9.0");
		FlexoVersion v091 = new FlexoVersion("0.9.1");
		
		assertTrue(v090.isLesserThan(v091));
		assertTrue(v091.isGreaterThan(v090));
	}

}
