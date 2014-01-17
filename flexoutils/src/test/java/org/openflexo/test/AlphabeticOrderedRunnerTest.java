package org.openflexo.test;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AlphabeticOrderedRunner.class)
public class AlphabeticOrderedRunnerTest {

	private static String RESULT;

	@BeforeClass
	public static void setupClass() {
		RESULT="";
	}

	@AfterClass
	public static void tearDownClass() {
		Assert.assertEquals("ABC",RESULT);;
	}

	@Test 
	public void testA() { 
		System.out.println("A"); 
		RESULT += "A";
	}

	@Test 
	public void testC() { 
		System.out.println("C"); 
		RESULT += "C";
	}

	@Test  
	public void testB() { 
		System.out.println("B"); 
		RESULT += "B";
	}

}
