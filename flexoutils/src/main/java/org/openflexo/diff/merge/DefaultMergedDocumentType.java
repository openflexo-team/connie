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

package org.openflexo.diff.merge;

import java.util.logging.Logger;

import org.openflexo.diff.DelimitingMethod;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileFormat;
import org.openflexo.toolbox.FileFormat.TextFileFormat;
import org.openflexo.toolbox.TokenMarkerStyle;

public class DefaultMergedDocumentType implements MergedDocumentType {

	protected static final Logger LOGGER = FlexoLogger.getLogger(DefaultMergedDocumentType.class.getPackage().getName());

	private final DelimitingMethod _delimitingMethod;
	private final TokenMarkerStyle _style;
	private final AutomaticMergeResolvingModelInterface _automaticMergeResolvingModel;

	public DefaultMergedDocumentType(DelimitingMethod delimitingMethod, TokenMarkerStyle style,
			AutomaticMergeResolvingModelInterface automaticMergeResolvingModel) {
		_delimitingMethod = delimitingMethod;
		_style = style;
		_automaticMergeResolvingModel = automaticMergeResolvingModel;
	}

	@Override
	public DelimitingMethod getDelimitingMethod() {
		return _delimitingMethod;
	}

	@Override
	public TokenMarkerStyle getStyle() {
		return _style;
	}

	@Override
	public AutomaticMergeResolvingModelInterface getAutomaticMergeResolvingModel() {
		return _automaticMergeResolvingModel;
	}

	public static final MergedDocumentType LINES = new DefaultMergedDocumentType(DelimitingMethod.LINES, null,
			new DefaultAutomaticMergeResolvingModel());

	public static final MergedDocumentType DEFAULT = new DefaultMergedDocumentType(DelimitingMethod.DEFAULT, null,
			new DefaultAutomaticMergeResolvingModel());

	public static final MergedDocumentType JAVA = new DefaultMergedDocumentType(DelimitingMethod.JAVA, TokenMarkerStyle.Java,
			new DefaultAutomaticMergeResolvingModel());

	/*	public static final DefaultAutomaticMergeResolvingModel xmlAMRM
		 = new DefaultAutomaticMergeResolvingModel();
		
		static {
			xmlAMRM.addToDetailedRules(new AutomaticMergeResolvingRule() {
				public String getMergedResult(MergeChange change) {
					DiffSource leftSource = change.getMerge().getLeftSource();
					DiffSource rightSource = change.getMerge().getRightSource();
					try {
					int val1 = Integer.parseInt(leftSource.tokenAt(change.getFirst0()).token);
					int val2 = Integer.parseInt(rightSource.tokenAt(change.getLast2()).token);
					return ""+((int)(val1+val2)/2);
					}
					catch (NumberFormatException e) {
						return leftSource.tokenAt(change.getFirst0()).token;
					}
				}
				public boolean isApplicable(MergeChange change) {
					if ((change.getFirst0() == change.getLast0())
							&& (change.getFirst2() == change.getLast2())) {
						DiffSource leftSource = change.getMerge().getLeftSource();
						DiffSource rightSource = change.getMerge().getRightSource();
						if ((leftSource.tokenAt(change.getFirst0()-3).token.equals("posiX"))
								&& (leftSource.tokenAt(change.getFirst0()-2).token.equals("="))
								&& (leftSource.tokenAt(change.getFirst0()-1).token.equals(""+'"'))
								&& (rightSource.tokenAt(change.getFirst2()-3).token.equals("posiX"))
								&& (rightSource.tokenAt(change.getFirst2()-2).token.equals("="))
								&& (rightSource.tokenAt(change.getFirst2()-1).token.equals(""+'"')))
							return true;
					}
					return false;
				}			
			});
			xmlAMRM.addToDetailedRules(new AutomaticMergeResolvingRule() {
				public String getMergedResult(MergeChange change) {
					DiffSource leftSource = change.getMerge().getLeftSource();
					DiffSource rightSource = change.getMerge().getRightSource();
					try {
					int val1 = Integer.parseInt(leftSource.tokenAt(change.getFirst0()).token);
					int val2 = Integer.parseInt(rightSource.tokenAt(change.getLast2()).token);
					return ""+((int)(val1+val2)/2);
					}
					catch (NumberFormatException e) {
						return leftSource.tokenAt(change.getFirst0()).token;
					}
				}
				public boolean isApplicable(MergeChange change) {
					if ((change.getFirst0() == change.getLast0())
							&& (change.getFirst2() == change.getLast2())) {
						DiffSource leftSource = change.getMerge().getLeftSource();
						DiffSource rightSource = change.getMerge().getRightSource();
						if ((leftSource.tokenAt(change.getFirst0()-3).token.equals("posiY"))
								&& (leftSource.tokenAt(change.getFirst0()-2).token.equals("="))
								&& (leftSource.tokenAt(change.getFirst0()-1).token.equals(""+'"'))
								&& (rightSource.tokenAt(change.getFirst2()-3).token.equals("posiY"))
								&& (rightSource.tokenAt(change.getFirst2()-2).token.equals("="))
								&& (rightSource.tokenAt(change.getFirst2()-1).token.equals(""+'"')))
							return true;
					}
					return false;
				}			
			});
		}
		
		public static final MergedDocumentType XML = new DefaultMergedDocumentType(
				DelimitingMethod.XML,
				TokenMarkerStyle.XML,
				xmlAMRM); */

	public static final MergedDocumentType XML = new DefaultMergedDocumentType(DelimitingMethod.XML, TokenMarkerStyle.XML,
			new DefaultAutomaticMergeResolvingModel());

	public static final MergedDocumentType HTML = new DefaultMergedDocumentType(DelimitingMethod.HTML, TokenMarkerStyle.HTML,
			new DefaultAutomaticMergeResolvingModel());

	public static final MergedDocumentType PLIST = new DefaultMergedDocumentType(DelimitingMethod.PLIST, TokenMarkerStyle.WOD,
			new DefaultAutomaticMergeResolvingModel());

	public static final MergedDocumentType TEX = new DefaultMergedDocumentType(DelimitingMethod.TEX, TokenMarkerStyle.TeX,
			new DefaultAutomaticMergeResolvingModel());

	public static final MergedDocumentType SQL = new DefaultMergedDocumentType(DelimitingMethod.SQL, TokenMarkerStyle.TSQL,
			new DefaultAutomaticMergeResolvingModel() {
				@Override
				protected String localizedForKey(String key) {
					return key;
					// return FlexoLocalization.localizedForKey(key);
				}
			});

	@Override
	public String getName() {
		return getDelimitingMethod().getName();
	}

	public static MergedDocumentType getMergedDocumentType(FileFormat format) {
		if (format instanceof TextFileFormat) {
			switch (((TextFileFormat) format).getSyntax()) {
			case Java:
				return JAVA;
			case XML:
				return XML;
			case HTML:
				return HTML;
			case PList:
				return PLIST;
			case Latex:
				return TEX;
			case SQL:
				return SQL;
			case CSS:
				return LINES;
			case JavaScript:
				return LINES;
			case Plain:
				return LINES;
			default:
				return DEFAULT;
			}
		}

		if (!format.isBinary()) {
			return DEFAULT;
		}

		LOGGER.warning("No merged document type for binary files");

		return null;
	}
}
