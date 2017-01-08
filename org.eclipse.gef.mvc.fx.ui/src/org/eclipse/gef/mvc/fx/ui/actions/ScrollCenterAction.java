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

import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiBundle;

import javafx.geometry.Bounds;

/**
 * The {@link ScrollTopRightAction} is an {@link AbstractScrollAction} that
 * aligns the contents of the viewer with the center of the viewport, i.e. the
 * center of the contents will be at the center of the viewport after performing
 * this action.
 *
 * @author mwienand
 *
 */
public class ScrollCenterAction extends AbstractScrollAction {

	/**
	 *
	 */
	public ScrollCenterAction() {
		super("Scroll Center");
		setImageDescriptor(MvcFxUiBundle.getDefault().getImageRegistry()
				.getDescriptor(MvcFxUiBundle.IMG_ICONS_SCROLL_CENTER));
	}

	@Override
	protected Point determinePivotPoint(Bounds bounds) {
		return new Point(bounds.getMinX() + bounds.getWidth() / 2,
				bounds.getMinY() + bounds.getHeight() / 2);
	}
}