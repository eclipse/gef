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
import org.eclipse.gef4.swtfx.AbstractFigure;
import org.eclipse.gef4.swtfx.ControlNode;
import org.eclipse.gef4.swtfx.IFigure;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.LineCap;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class MouseExample implements IExample {

	static class FigureDragger {
		private boolean dragging;
		private Point offset;

		public FigureDragger(final IFigure f) {
			f.addEventHandler(MouseEvent.MOUSE_PRESSED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							f.requestFocus();
							dragging = true;
							offset = new Point(e.getX(), e.getY());
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
								Point cursor = new Point(e.getX(), e.getY());
								f.localToParent(cursor, cursor);
								f.relocate(cursor.x - offset.x, cursor.y
										- offset.y);
							}
						}
					});

			// FIXME: The update will we done automatically in the future.
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
		private Point start;
		private AffineTransform startTx;

		public GroupTransformer(final IParent parent) {
			final AffineTransform tx = new AffineTransform();
			parent.getTransforms().add(tx);

			parent.addEventHandler(MouseEvent.MOUSE_PRESSED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							if (event.getButton() == 3) {
								dragging = true;
								start = tx.getTransformed(new Point(event
										.getX(), event.getY()));
								startTx = tx.getCopy();
							}
						}
					});
			parent.addEventHandler(MouseEvent.MOUSE_RELEASED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							dragging = false;
						}
					});
			parent.addEventHandler(MouseEvent.MOUSE_MOVED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							if (dragging) {
								double x = event.getX();
								double y = event.getY();

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
										.translate(start.x, start.y)
										.rotate(Angle.fromDeg(angleDeg).rad())
										.scale(zoom, zoom)
										.translate(-start.x, -start.y)
										.concatenate(startTx);

								tx.setTransform(newTx);

								parent.requestRedraw();
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
	private IParent root;

	@Override
	public void addUi(final IParent root) {
		this.root = root;
		root.addChildNodes(rectFigure, ovalFigure);
		new FigureDragger(rectFigure);
		new FigureDragger(ovalFigure);
		new GroupTransformer(root);

		ControlNode<Button> resetButton = new ControlNode<Button>(new Button(
				root.getSwtComposite(), SWT.PUSH));
		resetButton.getControl().setText("Reset");
		resetButton.relocate(20, 300);
		resetButton.addEventHandler(ActionEvent.SELECTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						resetFigures();
					}
				});

		ControlNode<Button> quitButton = new ControlNode<Button>(new Button(
				root.getSwtComposite(), SWT.PUSH));
		quitButton.getControl().setText("Quit");
		quitButton.relocate(300, 300);
		quitButton.addEventHandler(ActionEvent.SELECTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						Composite compo = root.getSwtComposite();
						while (compo != null && !(compo instanceof Shell)) {
							compo = compo.getParent();
						}
						if (compo instanceof Shell) {
							((Shell) compo).close();
						}
					}
				});

		root.addChildNodes(resetButton, quitButton);

		resetFigures();
		NestedControls.showAbsoluteBounds(root);
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
