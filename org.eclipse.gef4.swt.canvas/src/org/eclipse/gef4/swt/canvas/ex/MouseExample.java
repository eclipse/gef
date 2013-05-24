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

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.canvas.Group;
import org.eclipse.gef4.swt.canvas.IFigure;
import org.eclipse.gef4.swt.canvas.ShapeFigure;
import org.eclipse.gef4.swt.canvas.ev.MouseAdapter;
import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;
import org.eclipse.gef4.swt.canvas.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;

public class MouseExample implements IExample {

	static class FigureDragger extends MouseAdapter {
		private IFigure figure;
		private boolean dragging;
		private Point start;

		public FigureDragger(IFigure f) {
			figure = f;
		}

		@Override
		protected void onMouseDown(Event e) {
			dragging = true;
			start = new Point(e.x, e.y);
			// figure.captureMouse(e);
			super.onMouseDown(e);
		}

		@Override
		protected void onMouseMove(Event e) {
			if (dragging) {
				figure.getPaintStateByReference().getTransformByReference()
						.translate(e.x - start.x, e.y - start.y);
				start.x = e.x;
				start.y = e.y;
				figure.update();
			}
			super.onMouseMove(e);
		}

		@Override
		protected void onMouseUp(Event e) {
			// figure.releaseMouse(e);
			dragging = false;
			super.onMouseUp(e);
		}
	}

	public static void main(String[] args) {
		new Example(new MouseExample());
	}

	private ShapeFigure rectFigure = new ShapeFigure(new Rectangle(0, 0, 200,
			100));
	private ShapeFigure ovalFigure = new ShapeFigure(
			new Ellipse(0, 0, 100, 200));
	private Group root;

	@Override
	public void addUi(Group root) {
		rectFigure.getPaintStateByReference().getFillByReference()
				.setColor(new RgbaColor(0, 64, 255, 255));
		ovalFigure.getPaintStateByReference().getFillByReference()
				.setColor(new RgbaColor(255, 64, 0, 255));

		rectFigure.addEventListener(new FigureDragger(rectFigure));
		ovalFigure.addEventListener(new FigureDragger(ovalFigure));

		this.root = root;
		root.getFigures().add(rectFigure);
		root.getFigures().add(ovalFigure);
		rectFigure.setContainer(root);
		ovalFigure.setContainer(root);

		Button resetButton = new Button(root, SWT.PUSH);
		resetButton.setText("Reset");
		org.eclipse.swt.graphics.Point size = resetButton.computeSize(
				SWT.DEFAULT, SWT.DEFAULT);
		resetButton.setBounds(20, root.getSize().y - size.y - 20, size.x,
				size.y);
		resetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetFigures();
			}
		});
		resetFigures();
	}

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "Mouse Example";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	@Override
	public void render(GraphicsContext g) {
		g.clearRect(0, 0, root.getSize().x, root.getSize().y);
	}

	private void resetFigures() {
		rectFigure.getPaintStateByReference().getTransformByReference()
				.setToIdentity();
		ovalFigure.getPaintStateByReference().getTransformByReference()
				.setToIdentity();
		root.redraw();
	}

}
