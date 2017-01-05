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

import javafx.geometry.Bounds;

/**
 * The {@link ScrollTopRightAction} is an {@link AbstractScrollAction} that
 * aligns the contents of the viewer with the bottom/right viewport position,
 * i.e. the bottom/right corner of the contents will be at the bottom/right
 * corner of the viewport after performing this action.
 *
 * @author mwienand
 *
 */
public class ScrollBottomLeftAction extends AbstractScrollAction {

	/**
	 *
	 */
	public ScrollBottomLeftAction() {
		super("Scroll Bottom/Left");
	}

	@Override
	protected Point determinePivotPoint(Bounds bounds) {
		return new Point(bounds.getMinX(), bounds.getMaxY());
	}
}
