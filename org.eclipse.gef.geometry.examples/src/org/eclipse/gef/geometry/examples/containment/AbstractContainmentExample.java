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
package org.eclipse.gef.geometry.examples.containment;

import java.util.ArrayList;

import org.eclipse.gef.geometry.convert.swt.SWT2Geometry;
import org.eclipse.gef.geometry.planar.Ellipse;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractContainmentExample implements PaintListener {

	abstract public class AbstractControllableShape {

		private static final int CONTROL_POINT_COLOR = SWT.COLOR_BLUE;
		private static final int CONTROL_POINT_RADIUS = 5;

		private Canvas canvas;
		private ArrayList<ControlPoint> points;

		public AbstractControllableShape(Canvas canvas) {
			this.canvas = canvas;
			points = new ArrayList<>();
			createControlPoints();
		}

		public ControlPoint addControlPoint(Point p) {
			return addControlPoint(p, CONTROL_POINT_COLOR);
		}

		public ControlPoint addControlPoint(Point p, int color) {
			return addControlPoint(p, color, CONTROL_POINT_RADIUS);
		}

		public ControlPoint addControlPoint(Point p, int color, double radius) {
			ControlPoint cp = new ControlPoint(canvas, p, radius, color);
			for (ControlPoint ocp : points) {
				ocp.addForbiddenArea(cp);
				cp.addForbiddenArea(ocp);
			}
			points.add(cp);
			return cp;
		}

		abstract public void createControlPoints();

		abstract public IGeometry createGeometry();

		public void drawControlPoints(GC gc) {
			for (ControlPoint cp : points) {
				cp.draw(gc);
			}
		}

		abstract public void drawShape(GC gc);

		public void fillShape(GC gc) {
			drawShape(gc);
		}

		public Canvas getCanvas() {
			return canvas;
		}

		public Point[] getControlPoints() {
			Point[] points = new Point[this.points.size()];

			int i = 0;
			for (ControlPoint cp : this.points) {
				points[i++] = cp.getPoint();
			}

			return points;
		}
	}

	/**
	 * A draggable point. On the screen it is represented as an ellipse.
	 *
	 * @author mwienand
	 *
	 */
	class ControlPoint implements MouseListener, MouseMoveListener, Listener {
		private Canvas canvas;
		private int color = SWT.COLOR_BLUE;

		private Ellipse ellipse;
		private boolean isDragged = false;

		// to rescale points on resize
		private double oldShellHeight;
		private double oldShellWidth;

		private ArrayList<ControlPoint> forbidden;
		private ArrayList<ControlPoint> updateLinks;
		private ControlPoint xLink, yLink;
		private double relX, relY;

		private Point p;
		private double radius = 5;

		/**
		 * Creates a new ControlPoint object. Adds event listeners to the given
		 * Canvas object, so that the user can drag the control point with the
		 * mouse.
		 *
		 * @param canvas
		 *            Drawing area
		 */
		public ControlPoint(Canvas canvas) {
			this.canvas = canvas;
			canvas.addMouseListener(this);
			canvas.addMouseMoveListener(this);
			canvas.addListener(SWT.Resize, this);
			oldShellWidth = canvas.getClientArea().width;
			oldShellHeight = canvas.getClientArea().height;
			p = new Point(0, 0);
			updateLinks = new ArrayList<>();
			forbidden = new ArrayList<>();
			update();
		}

		/**
		 * Creates a new ControlPoint object. Adds event listeners to the given
		 * Canvas object, so that the user can drag the control point with the
		 * mouse.
		 *
		 * @param canvas
		 *            Drawing area
		 * @param p
		 *            Exact point
		 * @param radius
		 *            Of the ellipse that represents the point
		 * @param color
		 *            Of the ellipse that represents the point
		 */
		public ControlPoint(Canvas canvas, Point p, double radius, int color) {
			this(canvas);
			this.p = p;
			this.radius = radius;
			this.color = color;
			update();
		}

		public void addForbiddenArea(ControlPoint cp) {
			forbidden.add(cp);
		}

		/**
		 * Draws an ellipse with the given GC at the control points location.
		 *
		 * @param gc
		 */
		public void draw(GC gc) {
			// System.out.println(ellipse.toString());
			gc.setBackground(Display.getCurrent().getSystemColor(color));
			gc.fillOval((int) ellipse.getX(), (int) ellipse.getY(),
					(int) ellipse.getWidth(), (int) ellipse.getHeight());
		}

		/**
		 * Returns the exact Point of this ControlPoint object.
		 *
		 * @return The exact Point of this ControlPoint object.
		 */
		public Point getPoint() {
			return p;
		}

		@Override
		public void handleEvent(Event e) {
			switch (e.type) {
			case SWT.Resize:
				Rectangle bounds = SWT2Geometry.toRectangle(canvas.getBounds());
				p.scale(bounds.getWidth() / oldShellWidth,
						bounds.getHeight() / oldShellHeight);
				oldShellWidth = bounds.getWidth();
				oldShellHeight = bounds.getHeight();
				update();
				break;
			}
		}

		private double inRange(double low, double value, double high) {
			if (value < low) {
				return low;
			} else if (value > high) {
				return high;
			}
			return value;
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}

		@Override
		public void mouseDown(MouseEvent e) {
			if (ellipse.contains(new Point(e.x, e.y))) {
				isDragged = true;
			}
		}

		@Override
		public void mouseMove(MouseEvent e) {
			if (isDragged) {
				relX = e.x - p.x;
				relY = e.y - p.y;
				p.x = e.x;
				p.y = e.y;
				update();
				canvas.redraw();
			}
		}

		@Override
		public void mouseUp(MouseEvent e) {
			isDragged = false;
		}

		public void setXLink(ControlPoint cp) {
			xLink = cp;
			cp.updateLinks.add(this);
		}

		public void setYLink(ControlPoint cp) {
			yLink = cp;
			cp.updateLinks.add(this);
		}

		private void update() {
			double oldX = p.x, oldY = p.y;

			// check canvas pane:
			p.x = inRange(canvas.getClientArea().x + radius, p.x,
					canvas.getClientArea().x + canvas.getClientArea().width
							- radius);
			p.y = inRange(canvas.getClientArea().y + radius, p.y,
					canvas.getClientArea().y + canvas.getClientArea().height
							- radius);

			// check links:
			if (xLink != null) {
				p.x = xLink.p.x;
				p.y += xLink.relY;
			} else if (yLink != null) { // no need to link both x and y
				p.x += yLink.relX;
				p.y = yLink.p.y;
			}

			// check forbidden areas:
			for (ControlPoint cp : forbidden) {
				double minDistance = radius + cp.radius;
				if (p.getDistance(cp.p) < minDistance) {
					if (relX > 0) {
						p.x = cp.p.x - minDistance;
					} else if (relX < 0) {
						p.x = cp.p.x + minDistance;
					} else if (relY > 0) {
						p.y = cp.p.y - minDistance;
					} else {
						p.y = cp.p.y + minDistance;
					}
				}
			}

			relX += p.x - oldX;
			relY += p.y - oldY;

			for (ControlPoint cp : updateLinks) {
				cp.update();
			}

			ellipse = new Ellipse(p.x - radius, p.y - radius, radius * 2,
					radius * 2);
		}
	}

	private static final int INTERSECTS_COLOR = SWT.COLOR_YELLOW;

	private static final int CONTAINS_COLOR = SWT.COLOR_GREEN;

	private AbstractControllableShape controllableShape1, controllableShape2;

	private Shell shell;

	/**
	 *
	 */
	public AbstractContainmentExample(String title) {
		Display display = new Display();
		shell = new Shell(display, SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
		shell.setText(title);
		shell.setBounds(0, 0, 640, 480);
		shell.setBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		// open the shell before creating the controllable shapes so that their
		// default coordinates are not changed due to the resize of their canvas
		shell.open();

		controllableShape1 = createControllableShape1(shell);
		controllableShape2 = createControllableShape2(shell);

		shell.addPaintListener(this);
		shell.redraw(); // triggers a PaintEvent platform independently

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected abstract boolean computeContains(IGeometry g1, IGeometry g2);

	protected abstract boolean computeIntersects(IGeometry g1, IGeometry g2);

	protected abstract AbstractControllableShape createControllableShape1(
			Canvas canvas);

	protected abstract AbstractControllableShape createControllableShape2(
			Canvas canvas);

	@Override
	public void paintControl(PaintEvent e) {
		e.gc.setAntialias(SWT.ON);

		IGeometry cs1geometry = controllableShape1.createGeometry();
		IGeometry cs2geometry = controllableShape2.createGeometry();

		if (computeIntersects(cs1geometry, cs2geometry)) {
			e.gc.setBackground(
					Display.getCurrent().getSystemColor(INTERSECTS_COLOR));
			controllableShape2.fillShape(e.gc);
		}

		if (computeContains(cs1geometry, cs2geometry)) {
			e.gc.setBackground(
					Display.getCurrent().getSystemColor(CONTAINS_COLOR));
			controllableShape2.fillShape(e.gc);
		}

		controllableShape1.drawShape(e.gc);
		controllableShape2.drawShape(e.gc);

		controllableShape1.drawControlPoints(e.gc);
		controllableShape2.drawControlPoints(e.gc);
	}
}
