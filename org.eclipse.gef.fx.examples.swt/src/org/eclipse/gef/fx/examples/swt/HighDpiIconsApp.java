/*******************************************************************************
 * Copyright (c) 2019, 2019 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.examples.swt;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class HighDpiIconsApp extends AbstractFxSwtExample {

	public static void main(String[] args) {
		new HighDpiIconsApp();
	}

	public HighDpiIconsApp() {
		super("High DPI Icons in SWT/FX");
	}

	@Override
	public Scene createScene() {
		Group root = new Group();
		Button button = new Button();
		button.relocate(100, 100);
		button.getStyleClass().add("icon");
		root.getChildren().add(button);
		root.getStylesheets().add(HighDpiIconsApp.class
				.getResource("styles.css").toExternalForm());
		Scene scene = new Scene(root, 400, 400);
		return scene;
	}
}
