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
package org.eclipse.gef4.swt.canvas.examples;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.canvas.Group;
import org.eclipse.gef4.swt.canvas.IFigure;
import org.eclipse.gef4.swt.canvas.ShapeFigure;
import org.eclipse.gef4.swt.canvas.ev.IEventHandler;
import org.eclipse.gef4.swt.canvas.ev.types.MouseEvent;
import org.eclipse.gef4.swt.canvas.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class MouseExample implements IExample {

	static class FigureDragger {
		private IFigure figure;
		private boolean dragging;
		private Point start;

		public FigureDragger(final IFigure f) {
			figure = f;
			f.addEventHandler(MouseEvent.MOUSE_ENTERED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							System.out.println("entered " + f);
						}
					});
			f.addEventHandler(MouseEvent.MOUSE_EXITED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							System.out.println("exited " + f);
						}
					});
			f.addEventHandler(MouseEvent.MOUSE_PRESSED,
					createMousePressedHandler());
			f.addEventHandler(MouseEvent.MOUSE_RELEASED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							dragging = false;
						}
					});
			f.addEventHandler(MouseEvent.MOUSE_MOVED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							if (dragging) {
								figure.getPaintStateByReference()
										.getTransformByReference()
										.translate(e.getX() - start.x,
												e.getY() - start.y);
								start.x = e.getX();
								start.y = e.getY();
								figure.update();
							}
						}
					});
		}

		/**
		 * @return
		 */
		private IEventHandler<MouseEvent> createMousePressedHandler() {
			return new IEventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					dragging = true;
					start = new Point(e.getX(), e.getY());
				}
			};
		}
	}

	public static void main(String[] args) {
		new Example(new MouseExample());
	}

	private static ShapeFigure shape(IShape shape, RgbaColor color) {
		ShapeFigure figure = new ShapeFigure(shape);
		figure.getPaintStateByReference().getFillByReference().setColor(color);
		return figure;
	}

	private ShapeFigure rectFigure = shape(new Rectangle(0, 0, 200, 100),
			new RgbaColor(0, 64, 255, 255));
	private ShapeFigure ovalFigure = shape(new Ellipse(0, 0, 100, 200),
			new RgbaColor(255, 64, 0, 255));
	private Group root;

	@Override
	public void addUi(Group root) {
		FigureDragger rectFigureDragger = new FigureDragger(rectFigure);
		FigureDragger ovalFigureDragger = new FigureDragger(ovalFigure);

		this.root = root;

		// TODO: provide root.add(IFigure... figures);
		root.getFigures().add(rectFigure);
		root.getFigures().add(ovalFigure);

		// TODO: remove this boilerplate
		rectFigure.setContainer(root); // boilerplate
		ovalFigure.setContainer(root); // boilerplate

		// create SWT control
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

	private void resetFigures() {
		rectFigure.getPaintStateByReference().getTransformByReference()
				.setToIdentity();
		ovalFigure.getPaintStateByReference().getTransformByReference()
				.setToIdentity();
		root.redraw();
	}

}
