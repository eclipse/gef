/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.mvc.examples.AbstractMvcExample;
import org.eclipse.gef.mvc.examples.logo.model.GeometricModel;
import org.eclipse.gef.mvc.examples.logo.model.GeometricShape;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.inject.Module;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

public class MvcLogoExample extends AbstractMvcExample {

	public static List<GeometricModel> createDefaultContents() {
		return Collections.singletonList(new GeometricModel());
	}

	public static List<GeometricShape> createPaletteContents() {
		final List<GeometricShape> paletteContents = new ArrayList<>();
		final GeometricShape handlePrototype = new GeometricShape(GeometricModel.createHandleShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 23, 5), Color.WHITE, GeometricModel.GEF_SHADOW_EFFECT);
		paletteContents.add(handlePrototype);
		final GeometricShape cursorPrototype = new GeometricShape(GeometricModel.createCursorShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 5, 32), Color.WHITE, 2, Color.BLACK, GeometricModel.GEF_SHADOW_EFFECT);
		paletteContents.add(cursorPrototype);
		final GeometricShape eShapePrototype = new GeometricShape(GeometricModel.createEShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 100, 22), GeometricModel.GEF_COLOR_BLUE,
				GeometricModel.GEF_SHADOW_EFFECT);
		paletteContents.add(eShapePrototype);
		final GeometricShape fShapePrototype = new GeometricShape(GeometricModel.createFShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 175, 22), GeometricModel.GEF_COLOR_BLUE,
				GeometricModel.GEF_SHADOW_EFFECT);
		paletteContents.add(fShapePrototype);
		final GeometricShape dotShapePrototype = new GeometricShape(GeometricModel.createDotShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 87, 104), GeometricModel.GEF_COLOR_BLUE,
				GeometricModel.GEF_SHADOW_EFFECT);
		paletteContents.add(dotShapePrototype);
		return paletteContents;
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public MvcLogoExample() {
		super("GEF MVC Logo Example");
	}

	@Override
	protected Module createModule() {
		return new MvcLogoExampleModule();
	}

	protected IViewer getPaletteViewer() {
		return getDomain().getAdapter(AdapterKey.get(IViewer.class, MvcLogoExampleModule.PALETTE_VIEWER_ROLE));
	}

	@Override
	protected void hookViewers() {
		getPrimaryStage().setScene(
				new Scene(new MvcLogoExampleViewersComposite(getContentViewer(), getPaletteViewer()).getComposite()));
	}

	@Override
	protected void populateViewerContents() {
		getContentViewer().getContents().setAll(createDefaultContents());
		getPaletteViewer().getContents().setAll(createPaletteContents());
		getPrimaryStage().sizeToScene();
	}

}
