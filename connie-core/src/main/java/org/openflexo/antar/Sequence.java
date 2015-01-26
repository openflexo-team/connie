/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Connie-core, a component of the software infrastructure 
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

package org.openflexo.antar;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;

public class Sequence extends ControlGraph {

	private static final Logger LOGGER = FlexoLogger.getLogger(Sequence.class.getPackage().getName());

	private Vector<ControlGraph> statements;

	public Sequence() {
		super();
		statements = new Vector<ControlGraph>();
	}

	public Sequence(Vector<ControlGraph> statements) {
		this();
		this.statements.addAll(statements);
	}

	public Vector<ControlGraph> getStatements() {
		return statements;
	}

	public void setStatements(Vector<ControlGraph> statements) {
		this.statements = statements;
	}

	public boolean addToStatements(ControlGraph o) {
		return statements.add(o);
	}

	public void addStatement(int index, ControlGraph element) {
		statements.add(index, element);
	}

	public ControlGraph statementAt(int index) {
		return statements.elementAt(index);
	}

	public Enumeration<ControlGraph> elements() {
		return statements.elements();
	}

	public ControlGraph firstStatement() {
		return statements.firstElement();
	}

	public int indexOf(ControlGraph statement) {
		return statements.indexOf(statement);
	}

	public void insertStatementAt(ControlGraph statement, int index) {
		statements.insertElementAt(statement, index);
	}

	public ControlGraph lastStatement() {
		return statements.lastElement();
	}

	public boolean remove(ControlGraph o) {
		return statements.remove(o);
	}

	public void removeAllStatements() {
		statements.removeAllElements();
	}

	public void removeStatementAt(int index) {
		statements.removeElementAt(index);
	}

	public int size() {
		return statements.size();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		for (ControlGraph statement : statements) {
			sb.append(statement + " ;\n");
		}

		return sb.toString();
	}

	@Override
	public final ControlGraph normalize() {
		Vector<ControlGraph> normalizedList = new Vector<ControlGraph>();

		for (ControlGraph statement : getStatements()) {
			if (statement != null && !(statement instanceof Nop)) {
				ControlGraph normalizedStatement = statement.normalize();
				if (normalizedStatement instanceof Sequence) {
					Vector<ControlGraph> listOfNormalizedStatements = new Vector<ControlGraph>();
					for (ControlGraph s : ((Sequence) normalizedStatement).getStatements()) {
						listOfNormalizedStatements.add(s.normalize());
					}
					if (normalizedStatement.getHeaderComment() != null) {
						if (listOfNormalizedStatements.size() > 0) {
							ControlGraph firstStatement = listOfNormalizedStatements.firstElement();
							if (firstStatement.getHeaderComment() == null) {
								firstStatement.setHeaderComment(normalizedStatement.getHeaderComment());
							} else {
								firstStatement.appendHeaderComment(normalizedStatement.getHeaderComment(), true);
							}
						} else {
							LOGGER.warning("Forgetting comment " + normalizedStatement.getHeaderComment() + ". Implement this.");
							// TODO: implement header comment recovery in this case
						}
						normalizedStatement.setHeaderComment(null);
					} else if (normalizedStatement.getInlineComment() != null) {
						LOGGER.warning("Forgetting comment " + normalizedStatement.getInlineComment() + ". Implement this.");
						// TODO: implement inline comment recovery in this case
					}
					normalizedList.addAll(listOfNormalizedStatements);

				} else {
					normalizedList.add(normalizedStatement);
				}
			} else if (statement instanceof Nop && statement.hasComment()) {
				normalizedList.add(statement);
			}
		}

		ControlGraph returned;
		if (normalizedList.size() == 0) {
			returned = new Nop();
		} else if (normalizedList.size() == 1) {
			returned = normalizedList.firstElement();
		} else {
			returned = new Sequence(normalizedList);
		}
		returned.setInlineComment(getInlineComment());
		returned.appendHeaderComment(getHeaderComment(), true);
		return returned;
	}

	public boolean addAll(Collection<? extends ControlGraph> c) {
		return statements.addAll(c);
	}

}
