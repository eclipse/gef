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
package org.eclipse.gef4.swt.fx.examples;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.ShapeFigure;

public class FillExample implements IExample {

	public static void main(String[] args) {
		new Example(new FillExample());
	}

	@Override
	public void addUi(final IParent root) {
		root.addChildNodes(new ShapeFigure(new Ellipse(0, 0, 100, 100)) {
			@Override
			public Rectangle getLayoutBounds() {
				// XXX: this is a hack, root should be an AnchorPane and we
				// should set constraints on the ellipse
				return root.getLayoutBounds();
			}

			@Override
			public boolean isResizable() {
				return true;
			}

			@Override
			public void resize(double width, double height) {
				// resize the underlying shape
				((Ellipse) getShape()).setSize(width, height);
			}
		});
	}

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "Free Space Fill";
	}

	@Override
	public int getWidth() {
		return 640;
	}

}
