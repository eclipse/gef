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

	private static final GeometricShape HANDLE_PROTO = new GeometricShape(
			GeometricModel.createHandleShapeGeometry(), new AffineTransform(1, 0, 0, 1, 23, 5), Color.WHITE,
			GeometricModel.GEF_SHADOW_EFFECT);
	private static final GeometricShape CURSOR_PROTO = new GeometricShape(
			GeometricModel.createCursorShapeGeometry(), new AffineTransform(1, 0, 0, 1, 5, 32), Color.WHITE, 2,
			Color.BLACK, GeometricModel.GEF_SHADOW_EFFECT);
	private static final GeometricShape E_PROTO = new GeometricShape(GeometricModel.createEShapeGeometry(),
			new AffineTransform(1, 0, 0, 1, 100, 22), GeometricModel.GEF_COLOR_BLUE,
			GeometricModel.GEF_SHADOW_EFFECT);
	private static final GeometricShape F_PROTO = new GeometricShape(GeometricModel.createFShapeGeometry(),
			new AffineTransform(1, 0, 0, 1, 175, 22), GeometricModel.GEF_COLOR_BLUE,
			GeometricModel.GEF_SHADOW_EFFECT);
	private static final GeometricShape DOT_PROTO = new GeometricShape(GeometricModel.createDotShapeGeometry(),
			new AffineTransform(1, 0, 0, 1, 87, 104), GeometricModel.GEF_COLOR_BLUE,
			GeometricModel.GEF_SHADOW_EFFECT);

	private final List<GeometricShape> creatableShapes = new ArrayList<>();

	public PaletteModel() {
		initCreatableGeometries();
	}

	public List<GeometricShape> getCreatableShapes() {
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
