/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.IAction;

/**
 * The {@link ZoomActionGroup} is an {@link AbstractViewerActionGroup} that
 * combines {@link ZoomOutAction}, {@link ZoomScaleContributionItem},
 * {@link ZoomInAction}, {@link ZoomResetAction}, and
 * {@link ZoomComboContributionItem}. Upon construction, you can add additional
 * actions that are put into the {@link ZoomComboContributionItem} (see
 * {@link #ZoomActionGroup(IAction...)}.
 *
 * @author mwienand
 *
 */
public class ZoomActionGroup extends AbstractViewerActionGroup {

	private ZoomComboContributionItem zoomCombo;

	/**
	 * Constructs a new {@link ZoomActionGroup} and adds the given additional
	 * actions to the {@link ZoomComboContributionItem} that is contained in
	 * this action group.
	 *
	 * @param additionalComboItems
	 *            The additional actions for the
	 *            {@link ZoomComboContributionItem}.
	 */
	public ZoomActionGroup(IAction... additionalComboItems) {
		zoomCombo = new ZoomComboContributionItem(additionalComboItems);
	}

	@Override
	public List<IAdaptable.Bound<IViewer>> createContributions() {
		List<IAdaptable.Bound<IViewer>> dependents = new ArrayList<>(Arrays
				.asList(new ZoomOutAction(), new ZoomScaleContributionItem(),
						new ZoomInAction(), new ZoomResetAction()));
		if (zoomCombo != null) {
			dependents.add(zoomCombo);
		}
		return dependents;
	}
}
