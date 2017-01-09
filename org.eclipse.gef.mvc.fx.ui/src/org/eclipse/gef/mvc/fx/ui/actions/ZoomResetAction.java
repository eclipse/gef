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

import org.eclipse.gef.mvc.fx.ui.MvcFxUiBundle;
import org.eclipse.swt.widgets.Event;

/**
 * The {@link ZoomResetAction} is an {@link AbstractZoomAction} that resets the
 * zoom level to <code>1.0</code> while preserving the center of the diagram.
 *
 * @author mwienand
 *
 */
public class ZoomResetAction extends AbstractZoomAction {

	/**
	 * Creates a new {@link ZoomResetAction}.
	 */
	public ZoomResetAction() {
		super("Reset Zoom");
		setImageDescriptor(MvcFxUiBundle.getDefault().getImageRegistry()
				.getDescriptor(MvcFxUiBundle.IMG_ICONS_ZOOM_RESET));
	}

	@Override
	protected double determineZoomFactor(double currentZoomFactor,
			Event event) {
		return 1d / currentZoomFactor;
	}
}
