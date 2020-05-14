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

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.transform.Transform;

/**
 * Utility class to support the conversion between JavaFX objects and
 * corresponding classes of the GEF geometry API.
 * 
 * @author anyssen
 *
 */
public class FX2Geometry {

	private FX2Geometry() {
		// this class should not be instantiated by clients
	}

	/**
	 * Converts the given JavaFX {@link Transform} to an {@link AffineTransform}
	 * .
	 * 
	 * @param t
	 *            The JavaFX {@link Transform} to convert.
	 * @return The new {@link AffineTransform}.
	 */
	public static final AffineTransform toAffineTransform(Transform t) {
		return new AffineTransform(t.getMxx(), t.getMyx(), t.getMxy(), t.getMyy(), t.getTx(), t.getTy());
	}

	/**
	 * Converts the given JavaFX {@link Bounds} to a {@link Rectangle}.
	 * 
	 * @param b
	 *            The JavaFX {@link Bounds} to convert.
	 * @return The new {@link Rectangle}.
	 */
	public static final Rectangle toRectangle(Bounds b) {
		return new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
	}

	/**
	 * Converts the given JavaFX {@link Point2D} to a {@link Point}.
	 * 
	 * @param point
	 *            The {@link Point2D} to convert.
	 * @return The new {@link Point}.
	 */
	public static final Point toPoint(Point2D point) {
		return new Point(point.getX(), point.getY());
	}

}
