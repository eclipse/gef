/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example.parts;

import javafx.scene.effect.ColorAdjust;

import org.eclipse.gef4.geometry.planar.Point;

public class FXInsertAnchorPointHandlePart extends FXWayPointHandlePart {

	public FXInsertAnchorPointHandlePart(int wayPointIndex, Point wayPoint) {
		super(wayPointIndex, wayPoint, true);
	}

	@Override
	protected void createVisual() {
		super.createVisual();
		ColorAdjust scale = new ColorAdjust();
		scale.setSaturation(-1);
		visual.setEffect(scale);
	}

}
