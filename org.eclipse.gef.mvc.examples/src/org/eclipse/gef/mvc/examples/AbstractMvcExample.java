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
package org.eclipse.gef.mvc.examples;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.mvc.fx.domain.FXDomain;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;

import com.google.inject.Guice;
import com.google.inject.Module;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class AbstractMvcExample extends Application {

	protected final String title;
	private Stage primaryStage;
	private FXDomain domain;

	public AbstractMvcExample(String title) {
		this.title = title;
	}

	protected abstract Module createModule();

	protected FXViewer getContentViewer() {
		FXViewer viewer = domain.getAdapter(AdapterKey.get(FXViewer.class, FXDomain.CONTENT_VIEWER_ROLE));
		return viewer;
	}

	protected FXDomain getDomain() {
		return domain;
	}

	protected Stage getPrimaryStage() {
		return primaryStage;
	}

	protected void hookViewers() {
		primaryStage.setScene(new Scene(getContentViewer().getCanvas()));
	}

	protected abstract void populateViewerContents();

	@Override
	public void start(final Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;

		// create domain using guice
		this.domain = Guice.createInjector(createModule()).getInstance(FXDomain.class);

		// create viewers
		hookViewers();

		// set-up stage
		primaryStage.setResizable(true);
		primaryStage.setWidth(640);
		primaryStage.setHeight(480);
		primaryStage.setTitle(title);
		primaryStage.sizeToScene();
		primaryStage.show();

		// activate domain
		domain.activate();

		// load contents
		populateViewerContents();
	}
}