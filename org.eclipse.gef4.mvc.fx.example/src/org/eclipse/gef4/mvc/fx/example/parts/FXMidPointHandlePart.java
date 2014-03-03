/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example.parts;

import javafx.scene.Node;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.IProvider;
import org.eclipse.gef4.mvc.fx.parts.FXSelectionHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;

/**
 * A HandlePart to insert new way points into a curve.
 * 
 * @author mwienand
 *
 */
public class FXMidPointHandlePart extends FXSelectionHandlePart {

	/**
	 * Creates a new {@link IHandlePart} for the mid-point of the specified segment of the provided geometry.
	 * 
	 * @param targetPart
	 * @param handleGeometryProvider
	 * @param segmentIndex
	 */
	public FXMidPointHandlePart(IContentPart<Node> targetPart,
			IProvider<IGeometry> handleGeometryProvider, int segmentIndex) {
		super(targetPart, handleGeometryProvider, segmentIndex);
		
		// adjust fill (strong white: #F3F0E1, decent green: #ADFF2F)
		visual.setFill(Color.web("#F3F0E1"));
	}

	@Override
	protected Point getPosition(IGeometry handleGeometry) {
		Point position = null;
		if (handleGeometry instanceof ICurve) {
			ICurve curve = (ICurve) handleGeometry;
			BezierCurve[] beziers = curve.toBezier();
			if (beziers == null) {
				// TODO: Choose meaningful position (maybe center of bounds) or throw exception
				position = new Point();
			} else if (vertexIndex >= beziers.length) {
				// TODO: Choose meaningful position (maybe center of bounds) or throw exception
				position = new Point();
			} else {
				BezierCurve bc = beziers[vertexIndex];
				position = bc.get(0.5);
			}
		} else {
			throw new IllegalStateException(
					"Unable to determine handle position: Expected ICurve but got: "
							+ handleGeometry);
		}
		return position;
	}
	
}
