package org.openflexo.connie.java.parser;

import org.openflexo.connie.expr.BindingPath;
import org.openflexo.connie.java.expr.JavaBinaryOperatorExpression;

public class TestBindingPathParser extends ParserTestCase {

	public void testSimpleIdentifier() {
		tryToParse("foo", "foo", BindingPath.class, null, false);
	}

	public void testUnderscoredIdentifier() {
		tryToParse("foo_foo2", "foo_foo2", BindingPath.class, null, false);
	}

	public void testComposedIdentifier() {
		tryToParse("foo.foo2.foo3", "foo.foo2.foo3", BindingPath.class, null, false);
	}

	public void testSimpleMethodNoArgs() {
		tryToParse("method()", "method()", BindingPath.class, null, false);
	}

	public void testSimpleMethodWith1Arg() {
		tryToParse("method(1)", "method(1)", BindingPath.class, null, false);
	}

	public void testSimpleMethodWith3Args() {
		tryToParse("method(1,2,3)", "method(1,2,3)", BindingPath.class, null, false);
	}

	public void testFullQualifiedMethod() {
		tryToParse("a.b.c.method(1)", "a.b.c.method(1)", BindingPath.class, null, false);
	}

	public void testImbricatedMethods() {
		tryToParse("a.b.c.method(m1(1),d.e.f.m2(1))", "a.b.c.method(m1(1),d.e.f.m2(1))", BindingPath.class, null, false);
	}

	public void testPipelineMethodField() {
		tryToParse("m().f", "m().f", BindingPath.class, null, false);
	}

	public void testPipelineMethods1() {
		tryToParse("m1().m2()", "m1().m2()", BindingPath.class, null, false);
	}

	public void testPipelineMethods2() {
		tryToParse("substring(23,27).toUpperCase()", "substring(23,27).toUpperCase()", BindingPath.class, null, false);
	}

	public void testExpressionWithBindings() {
		tryToParse("a.b.c.method1(1).method2(2)+c.d.e", "a.b.c.method1(1).method2(2) + c.d.e", JavaBinaryOperatorExpression.class, null,
				false);
	}

	public void testBindingWithExpressions() {
		tryToParse("i.am.a(1,2+3,7.8,\"foo\",'a').little.test(1).foo()", "i.am.a(1,5,7.8,\"foo\",'a').little.test(1).foo()",
				BindingPath.class, null, false);
	}

	public void testExpressionWithBindings2() {
		tryToParse("beginDate.toString.substring(0,(beginDate.toString.length - 9))",
				"beginDate.toString.substring(0,beginDate.toString.length - 9)", BindingPath.class, null, false);

	}

	public void testWithSuper1() {
		tryToParse("super.a", "super.a", BindingPath.class, null, false);
	}

	public void testWithSuper2() {
		tryToParse("a.super.b", "a.super.b", BindingPath.class, null, false);
	}

	public void testWithSuper3() {
		tryToParse("a.b.super.c", "a.b.super.c", BindingPath.class, null, false);
	}

	public void testWithSuper4() {
		tryToParse("a.super.b.c", "a.super.b.c", BindingPath.class, null, false);
	}

	public void testWithSuper5() {
		tryToParse("super(1)", "super(1)", BindingPath.class, null, false);
	}

	public void testWithSuper6() {
		tryToParse("super.a(1)", "super.a(1)", BindingPath.class, null, false);
	}

	public void testWithSuper7() {
		tryToParse("a.super.b(1)", "a.super.b(1)", BindingPath.class, null, false);
	}

	public void testAccentCharacter() {
		tryToParse("flexoConcept.unité", "flexoConcept.unité", BindingPath.class, null, false);
	}

	/*public void testClassMethod1() {
		tryToParse("Class.forName(\"Toto\")", "toto--", BindingPath.class, null, false);
	}
	
	public void testClassMethod2() {
		tryToParse("java.Class.forName(\"Toto\")", "toto--", BindingPath.class, null, false);
	}*/

}
