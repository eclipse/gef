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

import org.eclipse.gef.mvc.fx.ui.MvcFxUiBundle;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;

/**
 * The {@link ZoomInAction} is an {@link AbstractZoomAction} that increases the
 * zoom level by multiplying it with <code>1.25</code> while preserving the
 * center of the diagram.
 *
 * @author mwienand
 *
 */
public class ZoomInAction extends AbstractZoomAction {

	/**
	 * Constructs a new {@link ZoomInAction}.
	 */
	public ZoomInAction() {
		this("Zoom In", IAction.AS_PUSH_BUTTON,
				MvcFxUiBundle.getDefault().getImageRegistry()
						.getDescriptor(MvcFxUiBundle.IMG_ICONS_ZOOM_IN));
	}

	/**
	 * Constructs a new {@link ZoomInAction} with the given text and style. Also
	 * sets the given {@link ImageDescriptor} for this action.
	 *
	 * @param text
	 *            Text for the action.
	 * @param style
	 *            Style for the action, see {@link IAction} for details.
	 * @param imageDescriptor
	 *            {@link ImageDescriptor} specifying the icon for the action.
	 */
	protected ZoomInAction(String text, int style,
			ImageDescriptor imageDescriptor) {
		super(text, style, imageDescriptor);
	}

	@Override
	protected double determineZoomFactor(double currentZoomFactor,
			Event event) {
		return 1.25;
	}
}