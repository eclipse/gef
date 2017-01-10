/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.IAction;

/**
 *
 * @author mwienand
 *
 */
public class ZoomActionGroup extends AbstractViewerActionGroup {

	private ZoomComboContributionItem zoomCombo;

	/**
	 * @param additionalComboItems
	 *            a
	 */
	public ZoomActionGroup(IAction... additionalComboItems) {
		zoomCombo = new ZoomComboContributionItem(additionalComboItems);
	}

	@Override
	public List<IViewerDependent> createViewerDependents() {
		List<IViewerDependent> dependents = new ArrayList<>(Arrays.asList(
				new ZoomOutAction(), new ZoomScaleContributionItem(),
				new ZoomInAction(), new ZoomResetAction()));
		if (zoomCombo != null) {
			dependents.add(zoomCombo);
		}
		return dependents;
	}
}
