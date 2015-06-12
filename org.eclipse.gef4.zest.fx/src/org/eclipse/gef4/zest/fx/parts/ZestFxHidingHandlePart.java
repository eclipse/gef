/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import javafx.scene.image.Image;

import org.eclipse.gef4.geometry.planar.BezierCurve;

import com.google.inject.Provider;

public class ZestFxHidingHandlePart extends AbstractHidingHandlePart {

	public static final String IMG_PRUNE = "/collapseall.png";
	public static final String IMG_PRUNE_DISABLED = "/collapseall_disabled.png";

	public ZestFxHidingHandlePart(
			Provider<BezierCurve[]> segmentsInSceneProvider, int segmentIndex,
			double segmentParameter) {
		super(segmentsInSceneProvider, segmentIndex, segmentParameter);
	}

	@Override
	protected Image getHoverImage() {
		return new Image(IMG_PRUNE);
	}

	@Override
	protected Image getImage() {
		return new Image(IMG_PRUNE_DISABLED);
	}

}