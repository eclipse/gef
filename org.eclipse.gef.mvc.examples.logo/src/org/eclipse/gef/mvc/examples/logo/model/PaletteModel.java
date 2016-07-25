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
package org.eclipse.gef.mvc.examples.logo.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.geometry.planar.AffineTransform;

import javafx.scene.paint.Color;

public class PaletteModel {

	private static final FXGeometricShape HANDLE_PROTO = new FXGeometricShape(
			FXGeometricModel.createHandleShapeGeometry(), new AffineTransform(1, 0, 0, 1, 23, 5), Color.WHITE,
			FXGeometricModel.GEF_SHADOW_EFFECT);
	private static final FXGeometricShape CURSOR_PROTO = new FXGeometricShape(
			FXGeometricModel.createCursorShapeGeometry(), new AffineTransform(1, 0, 0, 1, 5, 32), Color.WHITE, 2,
			Color.BLACK, FXGeometricModel.GEF_SHADOW_EFFECT);
	private static final FXGeometricShape E_PROTO = new FXGeometricShape(FXGeometricModel.createEShapeGeometry(),
			new AffineTransform(1, 0, 0, 1, 100, 22), FXGeometricModel.GEF_COLOR_BLUE,
			FXGeometricModel.GEF_SHADOW_EFFECT);
	private static final FXGeometricShape F_PROTO = new FXGeometricShape(FXGeometricModel.createFShapeGeometry(),
			new AffineTransform(1, 0, 0, 1, 175, 22), FXGeometricModel.GEF_COLOR_BLUE,
			FXGeometricModel.GEF_SHADOW_EFFECT);
	private static final FXGeometricShape DOT_PROTO = new FXGeometricShape(FXGeometricModel.createDotShapeGeometry(),
			new AffineTransform(1, 0, 0, 1, 87, 104), FXGeometricModel.GEF_COLOR_BLUE,
			FXGeometricModel.GEF_SHADOW_EFFECT);

	private final List<FXGeometricShape> creatableShapes = new ArrayList<>();

	public PaletteModel() {
		initCreatableGeometries();
	}

	public List<FXGeometricShape> getCreatableShapes() {
		return creatableShapes;
	}

	private void initCreatableGeometries() {
		creatableShapes.add(HANDLE_PROTO);
		creatableShapes.add(CURSOR_PROTO);
		creatableShapes.add(E_PROTO);
		creatableShapes.add(F_PROTO);
		creatableShapes.add(DOT_PROTO);
	}

}
