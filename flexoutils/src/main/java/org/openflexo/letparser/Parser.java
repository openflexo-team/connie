/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

package org.openflexo.letparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Vector;

/*
 * Created on 4 janv. 2006 by sguerin
 *
 * Flexo Application Suite
 * (c) Denali 2003-2005
 */

public class Parser {

	public static Token parse(String aString) throws ParseException {
		BufferedReader rdr = new BufferedReader(new StringReader(aString));
		return parse(rdr);
	}

	private static StreamTokenizer initStreamTokenizer(Reader rdr) {
		// Always need to setup StreamTokenizer
		StreamTokenizer input = new StreamTokenizer(rdr);
		input.wordChars('_', '_');
		return input;
	}

	public static Expression parseExpression(String aString) throws ParseException {
		Token result = parse(aString);
		if (result instanceof Expression) {
			return (Expression) result;
		}
		throw new ParseException("Could not parse as an expression " + aString);
	}

	public static Function parseFunction(String aString) throws ParseException {
		Token result = parse(aString);
		if (result instanceof Function) {
			return (Function) result;
		}
		throw new ParseException("Could not parse as an function " + aString);
	}

	private static Token parse(Reader rdr) throws ParseException {
		ListOfToken unparsedList = parseLevel(initStreamTokenizer(rdr));
		if (unparsedList.size() == 1 && unparsedList.firstElement() instanceof Token) {
			return (Token) unparsedList.firstElement();
		}
		try {
			return Function.makeFunction(unparsedList);
		} catch (ParseException e) {
			return Expression.makeExpression(unparsedList);
		}

	}

	/**
	 * Return a vector of AbstractToken (Operator,Word,Value) and Vector elements
	 * 
	 * @return
	 */
	private static ListOfToken parseLevel(StreamTokenizer input) throws ParseException {
		ListOfToken returned = new ListOfToken();

		try {
			// Read input file and build array

			String currentInput = "";
			boolean levelSeemsToBeFinished = false;
			boolean prefixedBy$ = false;

			while (!levelSeemsToBeFinished && input.nextToken() != StreamTokenizer.TT_EOF) {
				// System.out.println("currentInput="+currentInput+" input="+input);

				if (input.ttype == StreamTokenizer.TT_WORD) {
					// System.out.println("Found string: "+ input.sval);
					handlesCurrentInput(returned, currentInput);
					currentInput = "";
					handlesWordAddition(returned, input.sval);
				} else if (input.ttype == StreamTokenizer.TT_NUMBER) {
					// System.out.println("Found double: "+ input.nval);
					handlesCurrentInput(returned, currentInput);
					currentInput = "";
					Value value = Value.createValue(input.nval);
					value.setPrefixedBy$(prefixedBy$);
					returned.add(value);
					prefixedBy$ = false;
				}
				// looks for quotes indicating delimited strings
				else if (input.ttype == '"') {
					// Then the string will be in the sval field
					// System.out.println("Found delimited string: "+ input.sval);
					handlesCurrentInput(returned, currentInput);
					currentInput = "";
					// handlesWordAddition(returned,input.sval);
					Value value = StringValue.createStringValue(input.sval);
					value.setPrefixedBy$(prefixedBy$);
					returned.add(value);
					prefixedBy$ = false;
				} else if (input.ttype == '\'') {
					// Then the string will be in the sval field
					// System.out.println("Found delimited string: "+ input.sval);
					handlesCurrentInput(returned, currentInput);
					currentInput = "";
					// handlesWordAddition(returned,input.sval);
					Value value = CharValue.createCharValue(input.sval.charAt(0));
					value.setPrefixedBy$(prefixedBy$);
					returned.add(value);
					prefixedBy$ = false;
				} else if (input.ttype == '$') {
					// Then the string will be in the sval field
					// System.out.println("Found delimited string: "+ input.sval);
					handlesCurrentInput(returned, currentInput);
					currentInput = "";
					prefixedBy$ = true;
				} else {
					char foundChar = (char) input.ttype;
					// System.out.println("Found Ordinry Character: "+ foundChar);
					if (foundChar == '(') {
						handlesCurrentInput(returned, currentInput);
						currentInput = "";
						returned.add(parseLevel(input));
					} else if (foundChar == ')') {
						levelSeemsToBeFinished = true;
					} else {
						currentInput = currentInput + foundChar;
					}
				}
			}

			handlesCurrentInput(returned, currentInput);
			currentInput = "";

			// System.out.println("Done");

			return returned;
		} catch (IOException exception) {
			throw new ParseException("IOException occured: " + exception.getMessage());
		}

	}

	private static void handlesCurrentInput(Vector<AbstractToken> returned, String currentInput) throws ParseException {
		if (currentInput.equals("")) {
			return;
		}
		if (currentInput.equals(",")) {
			returned.add(new Comma());
			return;
		}
		Operator operator = matchOperator(currentInput);
		if (operator != null) {
			returned.add(operator);
		} else {
			throw new ParseException("Invalid characters : " + currentInput);
		}
	}

	private static void handlesWordAddition(Vector<AbstractToken> returned, String word) throws ParseException {
		if (word.equals("")) {
			return;
		}
		if (word.equalsIgnoreCase("true") || word.equalsIgnoreCase("yes")) {
			returned.add(new BooleanValue(true));
		} else if (word.equalsIgnoreCase("false") || word.equalsIgnoreCase("no")) {
			returned.add(new BooleanValue(false));
		} else if (matchOperator(word) != null) {
			returned.add(matchOperator(word));
		} else {
			returned.add(new Word(word));
		}
	}

	private static Operator matchOperator(String anInput) {
		for (Operator operator : Operator.getKnownOperators()) {
			if (anInput.toUpperCase().equals(operator.getSymbol()) || anInput.toUpperCase().equals(operator.getAlternativeSymbol())) {
				return operator;
			}
		}
		return null;
	}

}
