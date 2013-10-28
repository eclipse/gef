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
package org.eclipse.gef4.swtfx.examples;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.eclipse.gef4.swtfx.AbstractFigure;
import org.eclipse.gef4.swtfx.IFigure;
import org.eclipse.gef4.swtfx.INode;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.gc.CycleMethod;
import org.eclipse.gef4.swtfx.gc.LinearGradient;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class GradientCacheExample implements IExample {

	public static void main(String[] args) {
		new Example(new GradientCacheExample());
	}

	private LinearGradient gradient;

	@Override
	public void addUi(final IParent c) {
		resetGradient();
		// gradient.computeAuxVars();
		c.addChildNodes(createGradientRect(30, 30),
				createGradientRect(30, 250), createGradientRect(360, 130));

		final Button button = new Button(c.getScene(), SWT.TOGGLE);
		button.setText("Cache Gradient");
		button.setBounds(480, 400, 125, 25);
		button.setSelection(false);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetGradient();
				if (button.getSelection()) {
					gradient.computeAuxVars();
					for (INode n : c.getChildNodes()) {
						if (n instanceof IFigure) {
							((IFigure) n).setFill(gradient);
						}
					}
				}
			}
		});
	}

	private AbstractFigure createGradientRect(int tx, int ty) {
		AbstractFigure fig = new ShapeFigure(new RoundedRectangle(0, 0, 300,
				200, 20, 20));
		fig.setFill(gradient);
		fig.relocate(tx, ty);
		return fig;
	}

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "Gradient Color-Cache";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	private void resetGradient() {
		gradient = new LinearGradient(new Point(0, 0), new Point(100, 100),
				CycleMethod.REFLECT).addStop(0, new RgbaColor(255, 255, 0))
				.addStop(1, new RgbaColor(0, 255, 255));
	}

}
