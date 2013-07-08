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

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.fx.AbstractFigure;
import org.eclipse.gef4.swt.fx.Group;
import org.eclipse.gef4.swt.fx.IFigure;
import org.eclipse.gef4.swt.fx.ShapeFigure;
import org.eclipse.gef4.swt.fx.event.IEventHandler;
import org.eclipse.gef4.swt.fx.event.MouseEvent;
import org.eclipse.gef4.swt.fx.gc.GraphicsContext;
import org.eclipse.gef4.swt.fx.gc.LineCap;
import org.eclipse.gef4.swt.fx.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class MouseExample implements IExample {

	static class FigureDragger {
		private IFigure figure;
		private boolean dragging;
		private Point offset;

		public FigureDragger(final IFigure f) {
			figure = f;
			f.addEventHandler(MouseEvent.MOUSE_PRESSED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							f.requestFocus();
							dragging = true;
							offset = new Point(e.getX(), e.getY());

							// TODO: parentToLocal in SwtEventTargetSelector
							f.parentToLocal(offset, offset);
						}
					});
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
								figure.relocate(e.getX() - offset.x, e.getY()
										- offset.y);
							}
						}
					});

			// XXX: update on any mouse event, this will be done automatically
			// in the future
			f.addEventHandler(MouseEvent.ANY, new IEventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					f.update();
				}
			});
		}
	}

	public class GroupTransformer {
		private boolean dragging = false;
		private Point start = new Point();
		private AffineTransform startTx;

		public GroupTransformer(final Group group) {
			final AffineTransform tx = new AffineTransform();
			group.getTransforms().add(tx);

			group.addEventHandler(MouseEvent.MOUSE_PRESSED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							if (event.getButton() == 3) {
								dragging = true;
								start.x = event.getX();
								start.y = event.getY();

								// group.localToParent(start, start);

								// Point transformed = startTx
								// .inverseTransform(new Point(x, y));
								Point transformed = tx
										.getTransformed(new Point(start.x,
												start.y));
								start.x = transformed.x;
								start.y = transformed.y;

								startTx = tx.getCopy();
							}
						}
					});
			group.addEventHandler(MouseEvent.MOUSE_RELEASED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							dragging = false;
						}
					});
			group.addEventHandler(MouseEvent.MOUSE_MOVED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							// System.out.println("group mouse position = "
							// + event.getX() + ", " + event.getY());
							if (dragging) {
								double y = event.getY();
								double x = event.getX();

								// Point transformed = startTx
								// .inverseTransform(new Point(x, y));
								Point transformed = tx
										.getTransformed(new Point(x, y));
								x = transformed.x;
								y = transformed.y;

								double angleDeg = y - start.y;
								double zoom = 1 + Math.abs(x - start.x) / 100;
								if (x < start.x) {
									zoom = 1 / zoom;
								}
								AffineTransform newTx = new AffineTransform()
										.rotate(Angle.fromDeg(angleDeg).rad(),
												start.x, start.y)
										.translate(start.x, start.y)
										.scale(zoom, zoom)
										.translate(-start.x, -start.y)
										.concatenate(startTx);
								tx.setTransform(newTx);
								group.requestRedraw();
							}
						}
					});
		}
	}

	public static void main(String[] args) {
		new Example(new MouseExample());
	}

	private static AbstractFigure shape(IShape shape, RgbaColor color) {
		AbstractFigure figure = new ShapeFigure(shape) {
			@Override
			public void paint(GraphicsContext g) {
				if (isFocused()) {
					g.setDashes(20, 20);
					g.setLineWidth(5);
					g.setLineCap(LineCap.ROUND);
					g.setStroke(new RgbaColor());
				}
				super.paint(g);
			}
		};
		figure.getPaintStateByReference().getFillByReference().setColor(color);
		return figure;
	}

	private AbstractFigure rectFigure = shape(new Rectangle(0, 0, 200, 100),
			new RgbaColor(0, 64, 255, 255));
	private AbstractFigure ovalFigure = shape(new Ellipse(0, 0, 100, 200),
			new RgbaColor(255, 64, 0, 255));
	private Group root;

	@Override
	public void addUi(Group root) {
		new FigureDragger(rectFigure);
		new FigureDragger(ovalFigure);

		this.root = root;
		root.addFigures(rectFigure, ovalFigure);
		new GroupTransformer(root);

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

		Button quitButton = new Button(root, SWT.PUSH);
		quitButton.setText("quit");
		size = quitButton.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		quitButton.setBounds(root.getSize().x - 20 - size.x, root.getSize().y
				- size.y - 20, size.x, size.y);
		quitButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.exit(0);
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
		rectFigure.relocate(0, 0);
		ovalFigure.relocate(0, 0);
		if (root.getTransforms().size() > 0) {
			root.getTransforms().get(0).setToIdentity();
		}
		root.requestRedraw();
	}

}
