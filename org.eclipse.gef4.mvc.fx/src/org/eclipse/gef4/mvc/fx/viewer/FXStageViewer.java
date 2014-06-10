/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.viewer;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXStageViewer extends AbstractFXViewer {

	private final Stage stage;

	public FXStageViewer(Stage stage) {
		this.stage = stage;
	}

	@Override
	protected void setScene(Scene scene) {
		super.setScene(scene);
		stage.setScene(scene);
	}

}
