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
package org.eclipse.gef4.swt.canvas.ex;

import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.eclipse.gef4.swt.canvas.Group;
import org.eclipse.gef4.swt.canvas.gc.CycleMethod;
import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;
import org.eclipse.gef4.swt.canvas.gc.LinearGradient;
import org.eclipse.gef4.swt.canvas.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;

public class GradientCacheExample implements IExample {

	public static void main(String[] args) {
		new Example(new GradientCacheExample());
	}

	private Canvas c;
	private LinearGradient gradient;

	@Override
	public void addUi(Group c) {
		this.c = c;
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		final Button button = new Button(c, SWT.TOGGLE);
		button.setText("Cache Gradient");
		button.setBounds(480, 400, 125, 25);
		button.setSelection(false);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetGradient();
				if (button.getSelection()) {
					gradient.computeAuxVars();
				}
			}
		});
		resetGradient();
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

	@Override
	public void render(GraphicsContext g) {
		long time = System.currentTimeMillis();

		g.clearRect(0, 0, c.getSize().x, c.getSize().y);

		g.setFill(gradient);

		Path rr = new RoundedRectangle(0, 0, 300, 200, 20, 20).toPath();
		g.translate(30, 30);
		g.fillPath(rr);
		g.translate(0, 220);
		g.fillPath(rr);
		g.translate(330, -120);
		g.fillPath(rr);

		System.out.println("render time = "
				+ (System.currentTimeMillis() - time) + "ms");
	}

	private void resetGradient() {
		gradient = new LinearGradient(new Point(0, 0), new Point(100, 100),
				CycleMethod.REFLECT).addStop(0, new RgbaColor(255, 255, 0))
				.addStop(1, new RgbaColor(0, 255, 255));
	}

}
