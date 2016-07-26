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

package org.openflexo.search.view;

import javax.swing.event.CaretListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 * This class is a utility class to make a JTextComponent useable with the search.
 * 
 * @author gpolet
 * 
 */
public class TextComponentAdapater implements ITextComponent {

	private JTextComponent textComponent;

	public TextComponentAdapater(JTextComponent component) {
		this.textComponent = component;
	}

	@Override
	public void addCaretListener(CaretListener listener) {
		textComponent.addCaretListener(listener);
	}

	@Override
	public int getCaretPosition() {
		return textComponent.getCaretPosition();
	}

	@Override
	public boolean isEditable() {
		return textComponent.isEditable();
	}

	@Override
	public void removeCaretListener(CaretListener listener) {
		textComponent.removeCaretListener(listener);
	}

	@Override
	public void select(int start, int end) {
		textComponent.select(start, end);
	}

	@Override
	public Document getDocument() {
		return textComponent.getDocument();
	}

	@Override
	public String getSelectedText() {
		return textComponent.getSelectedText();
	}

	@Override
	public int getSelectionEnd() {
		return textComponent.getSelectionEnd();
	}

	@Override
	public int getSelectionStart() {
		return textComponent.getSelectionStart();
	}

}
