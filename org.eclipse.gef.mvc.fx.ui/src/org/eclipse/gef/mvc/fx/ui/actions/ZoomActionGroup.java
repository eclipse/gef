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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.ActionGroup;

/**
 *
 * @author mwienand
 *
 */
public class ZoomActionGroup extends ActionGroup {

	private ZoomOutAction zoomOut = new ZoomOutAction();
	private ZoomScaleContributionItem zoomScale = new ZoomScaleContributionItem();
	private ZoomInAction zoomIn = new ZoomInAction();
	private ZoomResetAction zoomReset = new ZoomResetAction();
	private ZoomComboContributionItem zoomCombo;

	/**
	 * @param additionalComboItems
	 *            a
	 */
	public ZoomActionGroup(IAction... additionalComboItems) {
		zoomCombo = new ZoomComboContributionItem(additionalComboItems);
	}

	@Override
	public void dispose() {
		if (zoomOut != null) {
			zoomOut.dispose();
			zoomOut = null;
		}
		super.dispose();
	}

	@Override
	public void fillActionBars(org.eclipse.ui.IActionBars actionBars) {
		IToolBarManager tbm = actionBars.getToolBarManager();
		tbm.add(zoomOut);
		tbm.add(zoomScale);
		tbm.add(zoomIn);
		tbm.add(zoomReset);
		tbm.add(zoomCombo);
	}
}
