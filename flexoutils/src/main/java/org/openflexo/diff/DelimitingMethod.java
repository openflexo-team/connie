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

package org.openflexo.diff;

public interface DelimitingMethod {
	public String getDelimiters();

	public String getNonSignifiantDelimiters();

	public String getName();

	public static final String DEFAULT_NON_SIGNIFIANT_DELIMS = "\t\n\r\f ";

	public static final DelimitingMethod LINES = new DelimitingMethod() {
		private static final String LINES_DELIMS = "\r\n";

		@Override
		public String getDelimiters() {
			return LINES_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "LINES";
		}
	};

	public static final DelimitingMethod DEFAULT = new DelimitingMethod() {
		private static final String DEFAULT_DELIMS = "\t\n\r\f ";

		@Override
		public String getDelimiters() {
			return DEFAULT_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "DEFAULT";
		}
	};

	public static final DelimitingMethod XML = new DelimitingMethod() {
		private static final String XML_DELIMS = "\t\n\r\f=<>?/ " + '"';

		@Override
		public String getDelimiters() {
			return XML_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "XML";
		}
	};

	public static final DelimitingMethod HTML = new DelimitingMethod() {
		private static final String HTML_DELIMS = "\t\n\r\f=<>?/ " + '"';

		@Override
		public String getDelimiters() {
			return HTML_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "HTML";
		}
	};

	public static final DelimitingMethod PLIST = new DelimitingMethod() {
		private static final String PLIST_DELIMS = "\t\n\r\f:={} " + '"';

		@Override
		public String getDelimiters() {
			return PLIST_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "PLIST";
		}
	};

	public static final DelimitingMethod JAVA = new DelimitingMethod() {
		private static final String JAVA_DELIMS = "\t\n\r\f=.,;?<>()[]{}+-/!&*| " + '"';

		@Override
		public String getDelimiters() {
			return JAVA_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "JAVA";
		}
	};

	public static final DelimitingMethod TEX = new DelimitingMethod() {
		private static final String JAVA_DELIMS = "\t\n\r\f=\\{}:[]%& " + '"';

		@Override
		public String getDelimiters() {
			return JAVA_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "TEX";
		}
	};

	public static final DelimitingMethod SQL = new DelimitingMethod() {
		private static final String JAVA_DELIMS = "\t\n\r\f=(); ";

		@Override
		public String getDelimiters() {
			return JAVA_DELIMS;
		}

		@Override
		public String getNonSignifiantDelimiters() {
			return DEFAULT_NON_SIGNIFIANT_DELIMS;
		}

		@Override
		public String getName() {
			return "SQL";
		}
	};

}
