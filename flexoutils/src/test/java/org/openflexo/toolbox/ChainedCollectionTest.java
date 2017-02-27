package org.openflexo.toolbox;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ChainedCollectionTest {

	@Test
	public void test() {
		List<String> l1 = new ArrayList<>();
		l1.add("String1");
		l1.add("String2");
		l1.add("String3");

		List<String> l2 = new ArrayList<>();
		l2.add("String4");
		l2.add("String5");

		ChainedCollection<String> cc1 = new ChainedCollection<>(l1, l2);
		int count = 0;
		int c = 1;
		for (String s : cc1) {
			assertEquals("String" + c, s);
			c += 1;
			count += 1;
		}
		// checks that collection contains 5 elements
		assertEquals(5, count);

		// creates more complicated collection
		ChainedCollection<String> cc2 = new ChainedCollection<>(cc1);
		cc2.add("String4");
		cc2.add(l1);

		count = 0;
		c = 1;
		for (String s : cc2) {
			assertEquals("String" + c, s);
			c += 1;
			if (c > 5)
				c = 1;
			count += 1;
		}
		assertEquals(9, count);
	}

}
