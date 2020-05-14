/*******************************************************************************
 * Copyright (c) 2011, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.examples.scalerotate;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractScaleRotateExample implements PaintListener,
		MouseWheelListener, MouseMoveListener, MouseListener, Listener {

	// TODO: The new angle interface is easier to use and should be used here!

	protected abstract class AbstractScaleRotateShape {
		private Canvas canvas;
		private Angle rotationAngle = Angle.fromDeg(0);
		private double zoomFactor = 1;

		public AbstractScaleRotateShape(Canvas c) {
			canvas = c;
		}

		public abstract boolean contains(Point p);

		public abstract IGeometry createGeometry();

		public abstract void draw(GC gc);

		public Canvas getCanvas() {
			return canvas;
		}

		public Point getCenter() {
			return new Point(canvas.getClientArea().width / 2,
					canvas.getClientArea().height / 2);
		}

		public Angle getRotationAngle() {
			return rotationAngle;
		}

		public double getZoomFactor() {
			return zoomFactor;
		}
	}

	private final int GEOMETRY_FILL_COLOR = SWT.COLOR_WHITE;

	private Shell shell;
	private AbstractScaleRotateShape shape;
	private Vector dragBegin;
	private Angle dragBeginAngle = Angle.fromDeg(0);

	/**
	 *
	 */
	public AbstractScaleRotateExample(String title) {
		Display display = new Display();
		shell = new Shell(display, SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
		shell.setText(title);
		shell.setBounds(0, 0, 640, 480);
		shell.setBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		// open the shell before creating the controllable shapes so that their
		// default coordinates are not changed due to the resize of their canvas
		shell.open();

		shape = createShape(shell);

		shell.addPaintListener(this);
		shell.addMouseListener(this);
		shell.addMouseMoveListener(this);
		shell.addMouseWheelListener(this);
		shell.addListener(SWT.Resize, this);
		shell.redraw(); // triggers a PaintEvent platform independently

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected abstract AbstractScaleRotateShape createShape(Canvas canvas);

	@Override
	public void handleEvent(Event e) {
		switch (e.type) {
		case SWT.Resize:
			shell.redraw();
			break;
		}
	}

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		shape.zoomFactor += 0.1;
		shell.redraw();
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (shape.contains(new Point(e.x, e.y))) {
			dragBegin = new Vector(shape.getCenter(), new Point(e.x, e.y));
		} // else {
			// dragBeginAngle = shape.rotationAngle;
			// dragBegin = null;
			// }
	}

	@Override
	public void mouseMove(MouseEvent e) {
		if (dragBegin != null) {
			Point center = shape.getCenter();
			Vector toMouse = new Vector(center, new Point(e.x, e.y));
			shape.rotationAngle = dragBegin.getAngleCW(toMouse)
					.getAdded(dragBeginAngle);
			shell.redraw();
		}
	}

	@Override
	public void mouseScrolled(MouseEvent e) {
		shape.zoomFactor += (double) e.count / 30;
		shell.redraw();
	}

	@Override
	public void mouseUp(MouseEvent e) {
		dragBeginAngle = shape.rotationAngle;
		dragBegin = null;
	}

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.setAntialias(SWT.ON);
		e.gc.setBackground(
				Display.getCurrent().getSystemColor(GEOMETRY_FILL_COLOR));
		shape.draw(e.gc);
	}
}
