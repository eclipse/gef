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

import javafx.embed.swt.FXCanvas;

import org.eclipse.swt.widgets.Composite;

public class SwtFXCanvas extends FXCanvas {

	public SwtFXCanvas(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public void setScene(javafx.scene.Scene scene) {
		if (scene instanceof SwtFXScene) {
			((SwtFXScene) scene).setFXCanvas(this);
		} else {
			throw new IllegalArgumentException(
					"The given Scene is not a SwtFXScene!");
		}
		super.setScene(scene);
	}

}
