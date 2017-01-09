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

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.actions.ActionGroup;

/**
 * The {@link FitToViewportActionGroup} provides two actions --
 * {@link FitToViewportAction} and {@link FitToViewportLockAction} -- which are
 * inserted into the toolbar when
 * {@link #fillActionBars(org.eclipse.ui.IActionBars)} is called.
 *
 * @author mwienand
 *
 */
public class FitToViewportActionGroup extends ActionGroup {

	private FitToViewportAction fitToViewport = new FitToViewportAction();
	private FitToViewportAction fitToViewportLock = new FitToViewportLockAction();

	/**
	 * Constructs a new {@link FitToViewportActionGroup}.
	 */
	public FitToViewportActionGroup() {
	}

	@Override
	public void dispose() {
		if (fitToViewport != null) {
			fitToViewport.dispose();
			fitToViewport = null;
			fitToViewportLock.dispose();
			fitToViewportLock = null;
		}
		super.dispose();
	}

	@Override
	public void fillActionBars(org.eclipse.ui.IActionBars actionBars) {
		IToolBarManager tbm = actionBars.getToolBarManager();
		tbm.add(fitToViewport);
		tbm.add(fitToViewportLock);
	}
}
