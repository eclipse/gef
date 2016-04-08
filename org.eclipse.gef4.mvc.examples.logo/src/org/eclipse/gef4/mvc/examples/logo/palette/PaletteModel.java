/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.palette;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.examples.logo.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricModel;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricShape;

import javafx.scene.paint.Color;

public class PaletteModel {

	private static final FXGeometricShape HANDLE_PROTO_GEOMETRY = new FXGeometricShape(
			FXGeometricModel.createHandleShapeGeometry(),
			new AffineTransform(1, 0, 0, 1, 23, 5), Color.WHITE,
			FXGeometricModel.GEF_SHADOW_EFFECT);
	private static final FXGeometricShape CURSOR_PROTO_GEOMETRY = new FXGeometricShape(
			FXGeometricModel.createCursorShapeGeometry(),
			new AffineTransform(1, 0, 0, 1, 5, 32), Color.WHITE, 2, Color.BLACK,
			FXGeometricModel.GEF_SHADOW_EFFECT);
	private static final FXGeometricCurve CURVE_PROTO_GEOMETRY = new FXGeometricCurve(
			new Point[] { new Point(15, 105), new Point(30, 125),
					new Point(55, 100) },
			FXGeometricModel.GEF_COLOR_GREEN, FXGeometricModel.GEF_STROKE_WIDTH,
			new Double[] {}, null);

	private final List<AbstractFXGeometricElement> creatableGeometricElements = new ArrayList<>();

	public PaletteModel() {
		initCreatableGeometries();
	}

	public List<AbstractFXGeometricElement> getCreatableGeometries() {
		return creatableGeometricElements;
	}

	private void initCreatableGeometries() {
		creatableGeometricElements.add(HANDLE_PROTO_GEOMETRY);
		creatableGeometricElements.add(CURSOR_PROTO_GEOMETRY);
		creatableGeometricElements.add(CURVE_PROTO_GEOMETRY);
	}

}
