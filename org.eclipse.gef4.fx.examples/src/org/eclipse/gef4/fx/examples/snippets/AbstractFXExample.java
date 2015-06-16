/*******************************************************************************
 * Copyright (c) 2013, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.examples.snippets;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class AbstractFXExample extends Application {

	private String title;

	public AbstractFXExample(String title) {
		this.title = title;
	}

	public abstract Scene createScene();

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(createScene());
		primaryStage.sizeToScene();
		primaryStage.setTitle(title);
		primaryStage.show();
	}

}
