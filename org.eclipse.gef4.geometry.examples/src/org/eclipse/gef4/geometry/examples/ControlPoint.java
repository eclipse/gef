package org.eclipse.gef4.geometry.examples;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

public class ControlPoint {

	private Point p;
	private Rectangle bounds; // remember bounds

	public ControlPoint() {
		p = new Point();
	}

	public ControlPoint(Point p) {
		this.p = p.getCopy();
	}

	public ControlPoint(double x, double y) {
		p = new Point(x, y);
	}

	public double getX() {
		return p.x;
	}

	public double getY() {
		return p.y;
	}

	public void setX(double x) {
		p.x = x;
		boundsCheck();
	}

	public void setY(double y) {
		p.y = y;
		boundsCheck();
	}

	public void onResize(Rectangle bounds) {
		this.bounds = bounds;
		boundsCheck();
	}

	private void boundsCheck() {
		if (bounds != null) {
			if (p.x < 0)
				p.x = 0;
			else if (p.x > bounds.getWidth())
				p.x = bounds.getWidth();
			if (p.y < 0)
				p.y = 0;
			else if (p.y > bounds.getHeight())
				p.y = bounds.getHeight();
		}
	}

	public Point toPoint() {
		return p.getCopy();
	}

}
