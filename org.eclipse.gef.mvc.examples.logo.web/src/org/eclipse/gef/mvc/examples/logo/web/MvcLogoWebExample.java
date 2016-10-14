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
package org.eclipse.gef.mvc.examples.logo.web;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.examples.AbstractMvcExample;
import org.eclipse.gef.mvc.examples.logo.MvcLogoExampleModule;
import org.eclipse.gef.mvc.examples.logo.MvcLogoExampleViewersComposite;
import org.eclipse.gef.mvc.examples.logo.model.GeometricModel;
import org.eclipse.gef.mvc.examples.logo.model.PaletteModel;
import org.eclipse.gef.mvc.fx.models.ContentModel;
import org.eclipse.gef.mvc.fx.viewer.Viewer;

import com.google.inject.Module;

import javafx.application.Application;
import javafx.scene.Scene;

// TODO: Better extend MvcLogoExample (same code).
public class MvcLogoWebExample extends AbstractMvcExample {

	public static List<GeometricModel> createDefaultContents() {
		return Collections.singletonList(new GeometricModel());
	}

	public static List<PaletteModel> createPaletteContents() {
		return Collections.singletonList(new PaletteModel());
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

	protected Viewer getPaletteViewer() {
		return getDomain().getAdapter(AdapterKey.get(Viewer.class,
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
		getContentViewer().getAdapter(ContentModel.class).getContents()
				.setAll(createDefaultContents());
		getPaletteViewer().getAdapter(ContentModel.class).getContents()
				.setAll(createPaletteContents());
	}

}
