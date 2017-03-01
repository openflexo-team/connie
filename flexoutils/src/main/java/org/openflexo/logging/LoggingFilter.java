/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2012-2012, AgileBirds
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

package org.openflexo.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.toolbox.StringUtils;

public class LoggingFilter {

	static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getPackage().getName());

	public String filterName;
	public FilterType type = FilterType.Highlight;
	public MessageFilterType messageFilterType = MessageFilterType.Contains;
	public String filteredContent;
	public Level filteredLevel = null;
	public SourceFilterType sourceFilterType = null;
	public String filteredSourceContent;
	public DateFilterType dateFilterType = null;
	public String filteredDate1;
	public String filteredDate2;
	public String filteredThread;
	public String filteredStacktrace;
	public int startSequence = -1;
	public int endSequence = -1;

	public static enum FilterType {
		OnlyKeep, Highlight, Dismiss
	}

	public static enum MessageFilterType {
		Contains, StartsWith, EndsWith
	}

	public static enum SourceFilterType {
		Package, Class, Method
	}

	public static enum DateFilterType {
		Before, After, Between
	}

	public LoggingFilter(String filterName) {
		this.filterName = filterName;
	}

	public boolean getHasFilteredMessage() {
		return messageFilterType != null;
	}

	public void setHasFilteredMessage(boolean aFlag) {
		if (aFlag) {
			messageFilterType = MessageFilterType.Contains;
		}
		else {
			messageFilterType = null;
		}
	}

	public boolean getHasFilteredLevel() {
		return filteredLevel != null;
	}

	public void setHasFilteredLevel(boolean aFlag) {
		if (aFlag) {
			filteredLevel = Level.INFO;
		}
		else {
			filteredLevel = null;
		}
	}

	public boolean getHasFilteredSource() {
		return sourceFilterType != null;
	}

	public void setHasFilteredSource(boolean aFlag) {
		if (aFlag) {
			sourceFilterType = SourceFilterType.Class;
		}
		else {
			sourceFilterType = null;
		}
	}

	public boolean getHasFilteredDate() {
		return dateFilterType != null;
	}

	public void setHasFilteredDate(boolean aFlag) {
		if (aFlag) {
			dateFilterType = DateFilterType.After;
		}
		else {
			dateFilterType = null;
		}
	}

	public boolean getHasFilteredThread() {
		return filteredThread != null;
	}

	public void setHasFilteredThread(boolean aFlag) {
		if (aFlag) {
			filteredThread = "10";
		}
		else {
			filteredThread = null;
		}
	}

	public boolean getHasFilteredStacktrace() {
		return filteredStacktrace != null;
	}

	public void setHasFilteredStacktrace(boolean aFlag) {
		if (aFlag) {
			filteredStacktrace = "Searched content";
		}
		else {
			filteredStacktrace = null;
		}
	}

	public boolean getHasFilteredSequence() {
		return startSequence > -1;
	}

	public void setHasFilteredSequence(boolean aFlag) {
		if (aFlag) {
			startSequence = 0;
			endSequence = 0;
		}
		else {
			startSequence = -1;
			endSequence = -1;
		}
	}

	private boolean messageMatches(LogRecord record) {
		if (StringUtils.isEmpty(record.message)) {
			return true;
		}
		switch (messageFilterType) {
			case Contains:
				if (record.message.contains(filteredContent)) {
					return true;
				}
				break;
			case StartsWith:
				if (record.message.startsWith(filteredContent)) {
					return true;
				}
				break;
			case EndsWith:
				if (record.message.endsWith(filteredContent)) {
					return true;
				}
				break;
			default:
				break;
		}
		return false;
	}

	private boolean sourceMatches(LogRecord record) {
		switch (sourceFilterType) {
			case Package:
				if (record.logger.contains(filteredSourceContent)) {
					return true;
				}
				break;
			case Class:
				if (record.classAsString().contains(filteredSourceContent)) {
					return true;
				}
				break;
			case Method:
				if (record.methodName.contains(filteredSourceContent)) {
					return true;
				}
				break;
			default:
				break;
		}
		return false;
	}

	private boolean levelMatches(LogRecord record) {
		return record.level == filteredLevel;
	}

	private boolean threadMatches(LogRecord record) {
		return record.threadAsString().equals(filteredThread);
	}

	private boolean stacktraceMatches(LogRecord record) {
		return record.getStackTraceAsString().contains(filteredStacktrace);
	}

	private boolean sequenceMatches(LogRecord record) {
		return record.sequence >= startSequence && record.sequence <= endSequence;
	}

	private static boolean dateMatches(LogRecord record) {
		LOGGER.warning("Not implemented ");
		return true;
	}

	public boolean filterDoesApply(LogRecord record) {
		if (getHasFilteredMessage()) {
			if (!messageMatches(record)) {
				return false;
			}
		}
		if (getHasFilteredLevel()) {
			if (!levelMatches(record)) {
				return false;
			}
		}
		if (getHasFilteredDate()) {
			if (!dateMatches(record)) {
				return false;
			}
		}
		if (getHasFilteredSequence()) {
			if (!sequenceMatches(record)) {
				return false;
			}

		}
		if (getHasFilteredSource()) {
			if (!sourceMatches(record)) {
				return false;
			}
		}
		if (getHasFilteredStacktrace()) {
			if (!stacktraceMatches(record)) {
				return false;
			}
		}
		if (getHasFilteredThread()) {
			if (!threadMatches(record)) {
				return false;
			}
		}
		return true;
	}

	public String getFilterDescription() {
		StringBuffer returned = new StringBuffer();
		if (getHasFilteredMessage()) {
			returned.append("message " + messageFilterType + " " + filteredContent + " ");
		}
		if (getHasFilteredLevel()) {
			returned.append("level=" + filteredLevel + " ");
		}
		if (getHasFilteredSource()) {
			returned.append(sourceFilterType + " contains " + filteredSourceContent + " ");
		}
		if (getHasFilteredDate()) {
			returned.append("date " + dateFilterType + " " + filteredDate1
					+ (dateFilterType == DateFilterType.Between ? " and " + filteredDate2 : "") + " ");
		}
		if (getHasFilteredStacktrace()) {
			returned.append("stacktrace contains " + filteredStacktrace + " ");
		}
		if (getHasFilteredThread()) {
			returned.append("thread=" + filteredThread + " ");
		}
		if (getHasFilteredSequence()) {
			returned.append("sequence between " + startSequence + " and " + endSequence + " ");
		}
		return returned.toString();
	}
}
