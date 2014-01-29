package org.openflexo.toolbox;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestFileUtils {

	static File tempDirectory;
	static File a;
	static File b;
	static File c;
	static File d;
	static File e;
	static File f;

	@BeforeClass
	public static void setup() throws IOException {
		tempDirectory = File.createTempFile("test", "fileutils");
		tempDirectory.mkdirs();
		System.out.println("Created " + tempDirectory);
		a = new File(tempDirectory, "a");
		b = new File(a, "b");
		c = new File(b, "c");
		d = new File(c, "d");
		e = new File(c, "e");
		f = new File(e, "f");
	}

	@Test
	public void test1() {
		assertEquals(0, FileUtils.distance(a, a));
	}

	@Test
	public void test2() {
		assertEquals(1, FileUtils.distance(d, c));
		assertEquals(1, FileUtils.distance(e, f));
	}

	@Test
	public void test3() {
		assertEquals(1, FileUtils.distance(c, d));
		assertEquals(1, FileUtils.distance(f, e));
	}

	@Test
	public void test4() {
		assertEquals(2, FileUtils.distance(c, f));
		assertEquals(2, FileUtils.distance(f, c));
	}

	@Test
	public void test5() {
		assertEquals(4, FileUtils.distance(a, f));
		assertEquals(4, FileUtils.distance(f, a));
	}

	@Test
	public void test6() {
		assertEquals(3, FileUtils.distance(d, f));
		assertEquals(3, FileUtils.distance(f, d));
	}
}
