package org.openflexo.connie.java.parser;

import org.openflexo.connie.expr.BinaryOperatorExpression;
import org.openflexo.connie.expr.UnaryOperatorExpression;

public class TestAssociativity extends ParserTestCase {

	public void testAssociativity() {
		tryToParse("a+b*c", "a + b * c", BinaryOperatorExpression.class, null, false);
	}

	public void testAssociativity2() {
		tryToParse("a+b+c", "a + b + c", BinaryOperatorExpression.class, null, false);
	}

	public void testAssociativity3() {
		tryToParse("a+(b+c)", "a + (b + c)", BinaryOperatorExpression.class, null, false);
	}

	public void testAssociativity4() {
		tryToParse("(a+b)+c", "a + b + c", BinaryOperatorExpression.class, null, false);
	}

	public void testAssociativity10() {
		tryToParse("(a+b)*c", "(a + b) * c", BinaryOperatorExpression.class, null, false);
	}

	public void testAssociativity11() {
		tryToParse("a*b+c", "a * b + c", BinaryOperatorExpression.class, null, false);
	}

	public void testAssociativity20() {
		tryToParse("-a+b", "-a + b", BinaryOperatorExpression.class, null, false);
	}

	public void testAssociativity21() {
		tryToParse("-(a+b)", "-(a + b)", UnaryOperatorExpression.class, null, false);
	}

}
