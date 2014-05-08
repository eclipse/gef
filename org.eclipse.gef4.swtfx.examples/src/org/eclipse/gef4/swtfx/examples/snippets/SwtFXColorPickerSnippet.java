/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
package org.eclipse.gef4.swtfx.examples.snippets;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import org.eclipse.gef4.fx.controls.AbstractFXColorPicker;
import org.eclipse.gef4.swtfx.SwtFXScene;
import org.eclipse.gef4.swtfx.examples.SwtFXApplication;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;

public class SwtFXColorPickerSnippet extends SwtFXApplication {

	public static void main(String[] args) {
		new SwtFXColorPickerSnippet();
	}

	@Override
	public SwtFXScene createScene() {
		Pane root = new Pane();
		SwtFXScene scene = new SwtFXScene(root, 400, 300);

		AbstractFXColorPicker colorPicker = new AbstractFXColorPicker() {
			@Override
			public Color pickColor() {
				Color currentColor = getColor();
				ColorDialog cd = new ColorDialog(shell);
				RGB rgb = new RGB((int) (255 * currentColor.getRed()),
						(int) (255 * currentColor.getGreen()),
						(int) (255 * currentColor.getBlue()));
				cd.setRGB(rgb);
				RGB newRgb = cd.open();
				if (newRgb != null) {
					return Color.rgb(newRgb.red, newRgb.green, newRgb.blue);
				}
				return null;
			}
		};
		root.getChildren().add(colorPicker);

		return scene;
	}

}
