/*******************************************************************************
 * Copyright (c) 2013, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.examples;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class AbstractFxExample extends Application {

	private String title;

	public AbstractFxExample(String title) {
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
