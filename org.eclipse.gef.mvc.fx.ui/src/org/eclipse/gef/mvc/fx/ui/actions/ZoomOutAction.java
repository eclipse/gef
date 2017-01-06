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
 * The {@link ZoomOutAction} is an {@link AbstractZoomAction} that decreases the
 * zoom level by multiplying it with <code>0.8</code> while preserving the
 * center of the diagram.
 *
 * @author mwienand
 *
 */
public class ZoomOutAction extends AbstractZoomAction {

	/**
	 *
	 */
	public ZoomOutAction() {
		super("Zoom Out");
		setImageDescriptor(MvcFxUiBundle.getDefault().getImageRegistry()
				.getDescriptor(MvcFxUiBundle.IMG_ICONS_ZOOM_OUT));
	}

	@Override
	protected double determineZoomFactor(double currentZoomFactor,
			Event event) {
		return 0.8;
	}
}
