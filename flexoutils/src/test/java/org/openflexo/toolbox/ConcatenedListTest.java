package org.openflexo.toolbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ConcatenedListTest {
	ConcatenedList<Integer> emptyList; // a global variable, visible to all methods
	ConcatenedList<Integer> threeElementsInOrder;
	ConcatenedList<Integer> threeElementsNotInOrder;
	ConcatenedList<Integer> anotherElements;
	ConcatenedList<Integer> yetAnotherElements;

	@Before
	public void createLists() {
		emptyList = new ConcatenedList<>();
		threeElementsInOrder = new ConcatenedList<>();
		// elements added to indices 0, 1, 2, in this order
		threeElementsInOrder.addElement(5);
		threeElementsInOrder.addElement(7);
		threeElementsInOrder.addElement(3);
		// elements added out of order
		threeElementsNotInOrder = new ConcatenedList<>();
		threeElementsNotInOrder.addElement(3);
		threeElementsNotInOrder.addElement(5);
		threeElementsNotInOrder.addElement(7);
		//
		anotherElements = new ConcatenedList<>();
		anotherElements.addElement(11);
		anotherElements.addElementList(threeElementsInOrder);
		anotherElements.addElement(12);
		anotherElements.addElement(13);
		anotherElements.addElement(14);
		anotherElements.addElementList(threeElementsNotInOrder);
		anotherElements.addElement(15);
		//
		yetAnotherElements = new ConcatenedList<>();
		yetAnotherElements.addElement(11);
		yetAnotherElements.addElementList(threeElementsInOrder);
		yetAnotherElements.addElement(12);
		yetAnotherElements.addElementList(anotherElements);
		yetAnotherElements.addElement(14);
		yetAnotherElements.addElementList(threeElementsNotInOrder);
		yetAnotherElements.addElement(15);
	}

	@Test
	public void testConcacList1() {
		assertFalse(anotherElements.isEmpty());
		assertEquals(11, anotherElements.size());
		assertEquals(Integer.valueOf(11), anotherElements.get(0));
		assertEquals(Integer.valueOf(5), anotherElements.get(1));
		assertEquals(Integer.valueOf(7), anotherElements.get(2));
		assertEquals(Integer.valueOf(3), anotherElements.get(3));
		assertEquals(Integer.valueOf(12), anotherElements.get(4));
		assertEquals(Integer.valueOf(13), anotherElements.get(5));
		assertEquals(Integer.valueOf(14), anotherElements.get(6));
		assertEquals(Integer.valueOf(3), anotherElements.get(7));
		assertEquals(Integer.valueOf(5), anotherElements.get(8));
		assertEquals(Integer.valueOf(7), anotherElements.get(9));
		assertEquals(Integer.valueOf(15), anotherElements.get(10));
	}

	@Test
	public void testConcacList2() {
		assertFalse(yetAnotherElements.isEmpty());
		assertEquals(21, yetAnotherElements.size());
		assertEquals(Integer.valueOf(11), yetAnotherElements.get(0));
		assertEquals(Integer.valueOf(5), yetAnotherElements.get(1));
		assertEquals(Integer.valueOf(7), yetAnotherElements.get(2));
		assertEquals(Integer.valueOf(3), yetAnotherElements.get(3));
		assertEquals(Integer.valueOf(12), yetAnotherElements.get(4));

		assertEquals(Integer.valueOf(11), yetAnotherElements.get(5));
		assertEquals(Integer.valueOf(5), yetAnotherElements.get(6));
		assertEquals(Integer.valueOf(7), yetAnotherElements.get(7));
		assertEquals(Integer.valueOf(3), yetAnotherElements.get(8));
		assertEquals(Integer.valueOf(12), yetAnotherElements.get(9));
		assertEquals(Integer.valueOf(13), yetAnotherElements.get(10));
		assertEquals(Integer.valueOf(14), yetAnotherElements.get(11));
		assertEquals(Integer.valueOf(3), yetAnotherElements.get(12));
		assertEquals(Integer.valueOf(5), yetAnotherElements.get(13));
		assertEquals(Integer.valueOf(7), yetAnotherElements.get(14));
		assertEquals(Integer.valueOf(15), yetAnotherElements.get(15));

		assertEquals(Integer.valueOf(14), yetAnotherElements.get(16));
		assertEquals(Integer.valueOf(3), yetAnotherElements.get(17));
		assertEquals(Integer.valueOf(5), yetAnotherElements.get(18));
		assertEquals(Integer.valueOf(7), yetAnotherElements.get(19));
		assertEquals(Integer.valueOf(15), yetAnotherElements.get(20));
	}

	@Test
	public void testConcacList3() {
		assertFalse(yetAnotherElements.isEmpty());
		assertEquals(21, yetAnotherElements.size());
		yetAnotherElements.add(0, 6);
		yetAnotherElements.add(2, 1);
		assertEquals(Integer.valueOf(6), yetAnotherElements.get(0));
		assertEquals(Integer.valueOf(11), yetAnotherElements.get(1));
		assertEquals(Integer.valueOf(1), yetAnotherElements.get(2));
		assertEquals(Integer.valueOf(5), yetAnotherElements.get(3));
		assertEquals(Integer.valueOf(7), yetAnotherElements.get(4));
		assertEquals(Integer.valueOf(3), yetAnotherElements.get(5));
		assertEquals(Integer.valueOf(12), yetAnotherElements.get(6));

		assertEquals(Integer.valueOf(11), yetAnotherElements.get(7));
		assertEquals(Integer.valueOf(5), yetAnotherElements.get(8));
		assertEquals(Integer.valueOf(7), yetAnotherElements.get(9));
		assertEquals(Integer.valueOf(3), yetAnotherElements.get(10));
		assertEquals(Integer.valueOf(12), yetAnotherElements.get(11));
		assertEquals(Integer.valueOf(13), yetAnotherElements.get(12));
		assertEquals(Integer.valueOf(14), yetAnotherElements.get(13));
		assertEquals(Integer.valueOf(3), yetAnotherElements.get(14));
		assertEquals(Integer.valueOf(5), yetAnotherElements.get(15));
		assertEquals(Integer.valueOf(7), yetAnotherElements.get(16));
		assertEquals(Integer.valueOf(15), yetAnotherElements.get(17));

		assertEquals(Integer.valueOf(14), yetAnotherElements.get(18));
		assertEquals(Integer.valueOf(3), yetAnotherElements.get(19));
		assertEquals(Integer.valueOf(5), yetAnotherElements.get(20));
		assertEquals(Integer.valueOf(7), yetAnotherElements.get(21));
		assertEquals(Integer.valueOf(15), yetAnotherElements.get(22));
	}

	@Test
	public void testEmptyList() {
		assertTrue(emptyList.isEmpty());
		assertEquals(0, emptyList.size());
	}

	@Test
	public void testElementsAddedInOrder() {
		assertFalse(threeElementsInOrder.isEmpty());
		assertEquals(3, threeElementsInOrder.size());
		assertEquals(Integer.valueOf(5), threeElementsInOrder.get(0));
		assertEquals(Integer.valueOf(7), threeElementsInOrder.get(1));
		assertEquals(Integer.valueOf(3), threeElementsInOrder.get(2));
	}

	@Test
	public void testElementsAddedNotInOrder() {
		assertFalse(threeElementsNotInOrder.isEmpty());
		assertEquals(3, threeElementsNotInOrder.size());
		assertEquals(Integer.valueOf(3), threeElementsNotInOrder.get(0));
		assertEquals(Integer.valueOf(5), threeElementsNotInOrder.get(1));
		assertEquals(Integer.valueOf(7), threeElementsNotInOrder.get(2));
	}

	// add tests for iterating over the lists
	@Test
	public void testIterator() {
		Integer[] results = { 5, 7, 3 };
		int i = 0;
		for (Integer item : threeElementsInOrder) {
			assertEquals(results[i], item);
			i++;
		}
	}

	@Test
	public void testIterator2() {
		Integer[] results = { 11, 5, 7, 3, 12, 11, 5, 7, 3, 12, 13, 14, 3, 5, 7, 15, 14, 3, 5, 7, 15 };
		int i = 0;
		for (Integer item : yetAnotherElements) {
			assertEquals(results[i], item);
			i++;
		}
	}

	@Test
	public void testIteratorSum() {
		int sum = 0;
		for (Integer item : threeElementsInOrder) {
			sum = sum + item;
		}
		assertEquals(15, sum);
	}

}
