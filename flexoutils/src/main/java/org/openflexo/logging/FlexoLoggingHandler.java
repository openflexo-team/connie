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

import java.util.logging.Handler;

/**
 * Flexo logging handler for dynamic logging
 * 
 * @author sguerin
 */
public class FlexoLoggingHandler extends Handler {

	public FlexoLoggingHandler() {
		super();
	}

	private boolean isPublishing = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
	 */
	@Override
	public void publish(java.util.logging.LogRecord record) {
		synchronized (this) {
			if (isPublishing) {
				return;
			}
		}
		if (FlexoLoggingManager.instance(this) != null) {
			synchronized (this) {
				isPublishing = true;
			}
			org.openflexo.logging.LogRecord flexoRecord = new org.openflexo.logging.LogRecord(record, FlexoLoggingManager.instance(this));
			FlexoLoggingManager.instance(this).logRecords.add(flexoRecord, FlexoLoggingManager.instance());
			synchronized (this) {
				isPublishing = false;
			}
		}
	}

	public void publishUnhandledException(java.util.logging.LogRecord record, Exception e) {
		if (FlexoLoggingManager.instance(this) != null) {
			FlexoLoggingManager.instance(this).logRecords.add(
					new org.openflexo.logging.LogRecord(record, e, FlexoLoggingManager.instance(this)), FlexoLoggingManager.instance());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.logging.Handler#flush()
	 */
	@Override
	public void flush() {
		// nothing special to do : the handler is logging "in memory"
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.logging.Handler#close()
	 */
	@Override
	public void close() throws SecurityException {
		// nothing special to do : the handler is logging "in memory"
	}

}
