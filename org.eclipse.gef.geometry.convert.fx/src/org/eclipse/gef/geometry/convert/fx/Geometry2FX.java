/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *    
 *******************************************************************************/
package org.eclipse.gef.geometry.convert.fx;

import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;

/**
 * Utility class to support conversions between GEF's geometry API and
 * corresponding JavaFX classes.
 * 
 * @author anyssen
 *
 */
public class Geometry2FX {

	private Geometry2FX() {
		// this class should not be instantiated by clients
	}

	/**
	 * Converts the given {@link Rectangle} to a JavaFX {@link Bounds}. The new
	 * {@link Bounds}'s <code>min-x</code> and <code>min-y</code> values are set
	 * to the {@link Rectangle}'s x- and y-coordinates and the {@link Bounds}'s
	 * <code>width</code> and <code>height</code> values are set to the
	 * {@link Rectangle}'s width and height, respectively.
	 * 
	 * @param r
	 *            The {@link Rectangle} to convert.
	 * @return The new {@link Bounds}.
	 */
	public static final Bounds toFXBounds(Rectangle r) {
		return new BoundingBox(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Converts the given {@link AffineTransform} to a JavaFX {@link Affine}.
	 * 
	 * @param transform
	 *            The {@link AffineTransform} to convert.
	 * @return The new {@link Affine}.
	 */
	public static final Affine toFXAffine(AffineTransform transform) {
		Affine affine = new Affine();
		affine.setMxx(transform.getM00());
		affine.setMxy(transform.getM01());
		affine.setMyx(transform.getM10());
		affine.setMyy(transform.getM11());
		affine.setTx(transform.getTranslateX());
		affine.setTy(transform.getTranslateY());
		return affine;
	}

	/**
	 * Converts the given {@link Point} to a JavaFX {@link Point2D}.
	 * 
	 * @param p
	 *            The {@link Point} to convert.
	 * @return The new {@link Point2D}.
	 */
	public static final Point2D toFXPoint(Point p) {
		return new Point2D(p.x, p.y);
	}

}
