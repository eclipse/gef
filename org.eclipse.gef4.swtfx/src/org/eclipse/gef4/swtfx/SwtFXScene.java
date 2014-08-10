/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.swtfx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Parent;
import javafx.scene.paint.Paint;

public class SwtFXScene extends javafx.scene.Scene {

	private SimpleObjectProperty<FXCanvas> canvasProperty;

	public SwtFXScene(Parent root) {
		super(root);
	}

	public SwtFXScene(Parent root, double width, double height) {
		super(root, width, height);
	}

	public SwtFXScene(Parent root, double width, double height,
			boolean depthBuffer) {
		super(root, width, height, depthBuffer);
	}

	public SwtFXScene(Parent root, double width, double height, Paint fill) {
		super(root, width, height, fill);
	}

	public SwtFXScene(Parent root, Paint fill) {
		super(root, fill);
	}

	public ObjectProperty<FXCanvas> canvasProperty() {
		if (canvasProperty == null) {
			canvasProperty = new SimpleObjectProperty<FXCanvas>();
		}
		return canvasProperty;
	}

	public FXCanvas getFXCanvas() {
		return canvasProperty().getValue();
	}

	public void setFXCanvas(FXCanvas canvas) {
		canvasProperty().setValue(canvas);
	}

}
