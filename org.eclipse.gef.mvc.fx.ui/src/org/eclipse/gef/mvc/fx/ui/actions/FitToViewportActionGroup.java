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
