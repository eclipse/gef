/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples;

import java.util.List;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.inject.AdapterMapInjector;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;

public abstract class AbstractMvcExample extends Application {

	protected final String title;

	public AbstractMvcExample(String title) {
		this.title = title;
	}

	protected abstract Module createModule();

	@Override
	public void start(final Stage primaryStage) throws Exception {
		Injector injector = Guice.createInjector(createModule());
		FXDomain domain = injector.getInstance(FXDomain.class);

		// hook the (single) viewer into the stage
		FXViewer viewer = domain.getAdapter(FXViewer.class);
		primaryStage.setScene(new Scene(viewer.getCanvas()));

		primaryStage.setResizable(true);
		primaryStage.setWidth(640);
		primaryStage.setHeight(480);
		primaryStage.setTitle(title);
		primaryStage.sizeToScene();
		primaryStage.show();

		// activate domain only after viewers have been hooked
		domain.activate();

		// set viewer contents
		viewer.getAdapter(ContentModel.class).setContents(createContents());
	}

	protected abstract List<? extends Object> createContents();
}