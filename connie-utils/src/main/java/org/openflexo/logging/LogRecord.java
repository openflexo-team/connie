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

package org.openflexo.logging;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * This class is used to encode a simple log record in Flexo
 * 
 * @author sylvain
 */
public class LogRecord {

	public Date date;

	public long millis;

	public long sequence;

	public String logger;

	public String className;

	public String methodName;

	public int threadId;

	public String message;

	public Level level;

	public StackTraceElement[] stackTrace;

	protected String _dateAsString = null;

	protected String _classAsString = null;

	protected String _millisAsString = null;

	protected String _sequenceAsString = null;

	protected String _threadAsString = null;

	public boolean isUnhandledException = false;

	public LogRecord() {
		super();
	}

	public LogRecord(java.util.logging.LogRecord record, FlexoLoggingManager loggingManager) {
		super();
		date = new Date();
		millis = record.getMillis();
		sequence = record.getSequenceNumber();
		logger = record.getLoggerName();
		className = record.getSourceClassName();
		methodName = record.getSourceMethodName();
		threadId = record.getThreadID();
		message = record.getMessage();
		if (message != null) {
			message = message.intern();
		}
		level = record.getLevel();
		if (loggingManager != null && loggingManager.getKeepLogTrace()) {
			stackTrace = new Exception().getStackTrace();
		}
		isUnhandledException = false;
	}

	public LogRecord(java.util.logging.LogRecord record, Exception e, FlexoLoggingManager loggingManager) {
		this(record, loggingManager);
		stackTrace = e.getStackTrace();
		className = stackTrace[0].getClassName();
		methodName = stackTrace[0].getMethodName();
		isUnhandledException = true;
	}

	public String dateAsString() {
		if (_dateAsString == null) {
			_dateAsString = new SimpleDateFormat("HH:mm:ss dd/MM").format(date);
		}
		return _dateAsString;
	}

	public String classAsString() {
		if (_classAsString == null && className != null) {
			StringTokenizer st = new StringTokenizer(className, ".");
			while (st.hasMoreTokens()) {
				_classAsString = st.nextToken();
			}
		}
		return _classAsString;
	}

	public String millisAsString() {
		if (_millisAsString == null) {
			_millisAsString = "" + millis;
		}
		return _millisAsString;
	}

	public String sequenceAsString() {
		if (_sequenceAsString == null) {
			_sequenceAsString = "" + sequence;
		}
		return _sequenceAsString;
	}

	public String threadAsString() {
		if (_threadAsString == null) {
			_threadAsString = "" + threadId;
		}
		return _threadAsString;
	}

	public String getStackTraceAsString() {
		if (_stackTraceAsString != null) {
			return _stackTraceAsString;
		}
		else if (stackTrace != null) {
			StringBuilder returned = new StringBuilder();
			int beginAt;
			if (isUnhandledException) {
				beginAt = 0;
			}
			else {
				beginAt = 6;
			}
			for (int i = beginAt; i < stackTrace.length; i++) {
				// returned += ("\tat " + stackTrace[i] + "\n");
				returned.append("\t").append("at ").append(stackTrace[i]).append('\n');
			}
			return returned.toString();
		}
		else {
			return "StackTrace not available";
		}
	}

	private String _stackTraceAsString;

	public void setStackTraceAsString(String aString) {
		_stackTraceAsString = aString;
	}
}
