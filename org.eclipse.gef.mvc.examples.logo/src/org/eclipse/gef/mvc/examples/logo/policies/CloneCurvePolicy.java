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
package org.eclipse.gef.mvc.examples.logo.policies;

import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.examples.logo.model.AbstractGeometricElement;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricCurvePart;

public class CloneCurvePolicy extends AbstractCloneContentPolicy {

	@Override
	public Object cloneContent() {
		GeometricCurve original = getHost().getContent();
		GeometricCurve clone = new GeometricCurve(
				original.getWayPointsCopy().toArray(new Point[] {}),
				original.getStroke(), original.getStrokeWidth(),
				original.getDashes(), original.getEffect());
		clone.setGeometry((ICurve) original.getGeometry().getCopy());
		clone.setSourceDecoration(original.getSourceDecoration());
		clone.setTargetDecoration(original.getTargetDecoration());
		clone.setTransform(original.getTransform());

		// anchorages
		for (AbstractGeometricElement<?> srcAnchorage : original
				.getSourceAnchorages()) {
			clone.addSourceAnchorage(srcAnchorage);
		}
		for (AbstractGeometricElement<?> dstAnchorage : original
				.getTargetAnchorages()) {
			clone.addTargetAnchorage(dstAnchorage);
		}

		return clone;
	}

	@Override
	public GeometricCurvePart getHost() {
		return (GeometricCurvePart) super.getHost();
	}

}
