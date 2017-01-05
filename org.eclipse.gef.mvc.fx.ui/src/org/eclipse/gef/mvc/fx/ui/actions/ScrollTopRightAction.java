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
 * aligns the contents of the viewer with the top/right viewport position, i.e.
 * the top/right corner of the contents will be at the top/right corner of the
 * viewport after performing this action.
 *
 * @author mwienand
 *
 */
public class ScrollTopRightAction extends AbstractScrollAction {

	/**
	 *
	 */
	public ScrollTopRightAction() {
		super("Scroll Top/Right");
	}

	@Override
	protected Point determinePivotPoint(Bounds bounds) {
		return new Point(bounds.getMaxX(), bounds.getMinY());
	}
}