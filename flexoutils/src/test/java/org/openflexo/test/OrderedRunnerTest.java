package org.openflexo.test;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(OrderedRunner.class)
public class OrderedRunnerTest {

	private static String RESULT;

	@BeforeClass
	public static void setupClass() {
		RESULT="";
	}

	@AfterClass
	public static void tearDownClass() {
		Assert.assertEquals("CBA",RESULT);;
	}

	@Test 
	@TestOrder(3)
	public void testA() { 
		System.out.println("A"); 
		RESULT += "A";
	}

	@Test 
	@TestOrder(1)
	public void testC() { 
		System.out.println("C"); 
		RESULT += "C";
	}

	@Test  
	@TestOrder(2)
	public void testB() { 
		System.out.println("B"); 
		RESULT += "B";
	}

}
