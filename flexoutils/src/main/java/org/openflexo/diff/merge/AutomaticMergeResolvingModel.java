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

import java.util.Vector;

import org.openflexo.diff.merge.MergeChange.MergeChangeSource;

public class AutomaticMergeResolvingModel implements AutomaticMergeResolvingModelInterface {
	private Vector<AutomaticMergeResolvingRule> _primaryRules;
	private Vector<AutomaticMergeResolvingRule> _detailedRules;

	public AutomaticMergeResolvingModel() {
		super();
		_primaryRules = new Vector<>();
		_detailedRules = new Vector<>();
	}

	public void addToPrimaryRules(AutomaticMergeResolvingRule aRule) {
		_primaryRules.add(aRule);
	}

	public void addToDetailedRules(AutomaticMergeResolvingRule aRule) {
		_detailedRules.add(aRule);
	}

	@Override
	public boolean resolve(MergeChange change) {
		// System.out.println("Resolve "+change);
		if (change.getMergeChangeSource() == MergeChangeSource.Conflict) {
			if (change.getMerge() instanceof DetailedMerge) {
				// This is a change from a detailed merge,
				// review all detailed rules
				for (AutomaticMergeResolvingRule rule : _detailedRules) {
					if (rule.isApplicable(change)) {
						// This rule apply, ok return result
						// return rule.getMergedResult(change);
						change.setAutomaticResolvedMerge(rule.getMergedResult(change));
						change.setAutomaticMergeReason(localizedForKey(rule.getDescription()));
						return true;
					}
				}
				// Not resolvable
				return false;
				// return null;
			}
			else {
				// This is a change that might be tokenized
				// Before to analyse deeply, look if a primary
				// rule may resolve conflict
				for (AutomaticMergeResolvingRule rule : _primaryRules) {
					if (rule.isApplicable(change)) {
						// This rule apply, ok return result
						// return rule.getMergedResult(change);
						change.setAutomaticResolvedMerge(rule.getMergedResult(change));
						change.setAutomaticMergeReason(localizedForKey(rule.getDescription()));
						return true;
					}
				}
				DetailedMerge detailedMerge = change.getDetailedMerge();
				for (MergeChange c : detailedMerge.getChanges()) {
					if (!resolve(c)) {
						// At least one change was not resolvable, return null
						return false;
						// return null;
					}
				}
				// Arriving here means that all changes were resolved,
				// and thus that the supplied change is resolved
				change.setAutomaticResolvedMerge(detailedMerge.getMergedSource().getText());
				change.setAutomaticMergeReason(localizedForKey("all_changes_are_resolved_by_detailed_analysis"));
				return true;
			}
		}
		else {
			return true;
			// return change.getMergeChangeResult().merge;
		}
	}

	protected String localizedForKey(String key) {
		return key;
	}
}
