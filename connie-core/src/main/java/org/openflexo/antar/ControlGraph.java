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

import java.util.logging.Logger;

import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.StringUtils;

public abstract class ControlGraph implements AlgorithmicUnit {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = FlexoLogger.getLogger(ControlGraph.class.getPackage().getName());

	private String headerComment = null;
	private String inlineComment = null;

	/**
	 * Normalize this ControlGraph, by builing a new ControlGraph which is normalized (We don't normalize our ControlGraph, we build an
	 * other one)
	 * 
	 * @return
	 */
	public abstract ControlGraph normalize();

	public String getHeaderComment() {
		return headerComment;
	}

	public void setHeaderComment(String headerComment) {
		// if (this.comment != null) logger.info("replace "+this.comment+" by "+comment);
		this.headerComment = headerComment;
	}

	public void appendHeaderComment(String comment, boolean first) {
		if (this.headerComment == null || this.headerComment.trim().equals("")) {
			setHeaderComment(comment);
		} else {
			this.headerComment = (first ? comment + StringUtils.LINE_SEPARATOR : "") + this.headerComment
					+ (!first ? StringUtils.LINE_SEPARATOR + comment : "");
		}
	}

	public String getInlineComment() {
		return inlineComment;
	}

	public void setInlineComment(String inlineComment) {
		this.inlineComment = inlineComment;
	}

	public boolean hasComment() {
		return getHeaderComment() != null || getInlineComment() != null;
	}
}
