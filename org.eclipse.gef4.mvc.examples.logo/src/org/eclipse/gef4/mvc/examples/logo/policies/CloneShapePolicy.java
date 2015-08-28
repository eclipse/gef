/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.policies;

import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricShape;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricShapePart;

// only applicable for FXGeometricShapePart
public class CloneShapePolicy extends AbstractCloneContentPolicy {

	@Override
	public Object cloneContent() {
		FXGeometricShape originalShape = getHost().getContent();
		FXGeometricShape shape = new FXGeometricShape(
				(IShape) originalShape.getGeometry().getCopy(),
				originalShape.getTransform(), originalShape.getFill(),
				originalShape.getEffect());
		shape.setStroke(originalShape.getStroke());
		shape.setStrokeWidth(originalShape.getStrokeWidth());
		return shape;
	}

	@Override
	public FXGeometricShapePart getHost() {
		return (FXGeometricShapePart) super.getHost();
	}

}
