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

import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

/**
 * The {@link FitToViewportActionGroup} provides two actions --
 * {@link FitToViewportAction} and {@link FitToViewportLockAction} -- which are
 * inserted into the toolbar when
 * {@link #fillActionBars(org.eclipse.ui.IActionBars)} is called.
 *
 * @author mwienand
 *
 */
public class FitToViewportActionGroup extends AbstractViewerActionGroup {

	@Override
	public List<IAdaptable.Bound<IViewer>> createContributions() {
		return Arrays.asList(new FitToViewportAction(),
				new FitToViewportLockAction());
	}
}
