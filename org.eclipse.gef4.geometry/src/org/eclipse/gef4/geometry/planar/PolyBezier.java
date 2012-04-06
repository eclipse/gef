/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.utils.CurveUtils;

/**
 * A {@link PolyBezier} is an {@link IPolyCurve} which consists of one or more
 * connected {@link BezierCurve}s.
 */
public class PolyBezier extends AbstractGeometry implements IPolyCurve {

	private static final long serialVersionUID = 1L;
	private BezierCurve[] beziers;

	/**
	 * Constructs a new {@link PolyBezier} of the given {@link BezierCurve}s.
	 * The {@link BezierCurve}s are expected to be connected with each other.
	 * 
	 * @param beziers
	 *            the {@link BezierCurve}s which will constitute this
	 *            {@link PolyBezier}
	 */
	public PolyBezier(BezierCurve... beziers) {
		this.beziers = copy(beziers);
	}

	public boolean contains(Point p) {
		for (BezierCurve c : beziers) {
			if (c.contains(p)) {
				return true;
			}
		}
		return false;
	}

	public Rectangle getBounds() {
		Rectangle bounds = new Rectangle();

		for (BezierCurve c : beziers) {
			bounds.union(c.getBounds());
		}

		return bounds;
	}

	public Path toPath() {
		// TODO: need a Path.append(Path)
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public IGeometry getCopy() {
		return new PolyBezier(beziers);
	}

	public double getY2() {
		return getP2().y;
	}

	public double getY1() {
		return getP1().y;
	}

	public double getX2() {
		return getP2().x;
	}

	public double getX1() {
		return getP1().x;
	}

	public Point getP2() {
		return beziers[beziers.length - 1].getP2();
	}

	public Point getP1() {
		return beziers[0].getP1();
	}

	public BezierCurve[] toBezier() {
		return copy(beziers);
	}

	public Point[] getIntersections(ICurve g) {
		return CurveUtils.getIntersections(g, this);
	}

	public boolean intersects(ICurve c) {
		return CurveUtils.intersects(c, this);
	}

	public boolean overlaps(ICurve c) {
		return CurveUtils.overlaps(c, this);
	}

	public BezierCurve[] getCurves() {
		return copy(beziers);
	}

	private static BezierCurve[] copy(BezierCurve... beziers) {
		BezierCurve[] copy = new BezierCurve[beziers.length];

		for (int i = 0; i < beziers.length; i++) {
			copy[i] = beziers[i].getCopy();
		}

		return copy;
	}

}
