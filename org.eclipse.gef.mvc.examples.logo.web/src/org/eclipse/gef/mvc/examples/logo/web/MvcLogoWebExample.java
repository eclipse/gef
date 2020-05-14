/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.web;

import java.util.List;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.examples.AbstractMvcExample;
import org.eclipse.gef.mvc.examples.logo.MvcLogoExample;
import org.eclipse.gef.mvc.examples.logo.MvcLogoExampleModule;
import org.eclipse.gef.mvc.examples.logo.MvcLogoExampleViewersComposite;
import org.eclipse.gef.mvc.examples.logo.model.AbstractGeometricElement;
import org.eclipse.gef.mvc.examples.logo.model.GeometricShape;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.inject.Module;

import javafx.application.Application;
import javafx.scene.Scene;

// TODO: Better extend MvcLogoExample (same code).
public class MvcLogoWebExample extends AbstractMvcExample {

	public static List<? extends AbstractGeometricElement<?>> createContentViewerContents() {
		return MvcLogoExample.createContentViewerContents();
	}

	public static List<GeometricShape> createPaletteViewerContents() {
		return MvcLogoExample.createPaletteViewerContents();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	public MvcLogoWebExample() {
		super("GEF MVC Logo Example (Web)");
	}

	@Override
	protected Module createModule() {
		return new MvcLogoExampleModule();
	}

	protected IViewer getPaletteViewer() {
		return getDomain().getAdapter(AdapterKey.get(IViewer.class,
				MvcLogoExampleModule.PALETTE_VIEWER_ROLE));
	}

	@Override
	protected void hookViewers() {
		getPrimaryStage().setScene(
				new Scene(new MvcLogoExampleViewersComposite(getContentViewer(),
						getPaletteViewer()).getComposite()));
	}

	@Override
	protected void populateViewerContents() {
		getContentViewer().getContents().setAll(createContentViewerContents());
		getPaletteViewer().getContents().setAll(createPaletteViewerContents());
	}

}
