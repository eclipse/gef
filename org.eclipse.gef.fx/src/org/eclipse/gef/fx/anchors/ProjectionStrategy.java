/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.anchors;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchorageReferenceGeometry;
import org.eclipse.gef.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.IShape;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Point;

import javafx.scene.Node;

/**
 * An {@link IComputationStrategy} that computes anchor position by projecting
 * the respective anchored reference point to the outline of the anchorage
 * reference geometry so that the respective point has minimal distance to the
 * anchored reference point.
 *
 * In detail, the computation is done as follows:
 * <ol>
 * <li>Compute the anchorage outlines (in scene) based on the anchorage
 * reference geometry,using {@link #getOutlineSegments(IGeometry)}.</li>
 * <li>Transform the given anchored reference point to scene coordinates.</li>
 * <li>Project the anchored reference point (in scene) onto the anchorage
 * outlines.</li>
 * <li>Return the nearest projection to the anchored reference point.</li>
 * </ol>
 *
 * @author anyssen
 * @author mwienand
 */
public class ProjectionStrategy implements IComputationStrategy {

	@Override
	public Point computePositionInScene(Node anchorage, Node anchored,
			Set<Parameter<?>> parameters) {
		// retrieve required computation parameters
		IGeometry anchorageReferenceGeometryInLocal = Parameter
				.get(parameters, AnchorageReferenceGeometry.class).get();
		Point anchoredReferencePointInLocal = Parameter
				.get(parameters, AnchoredReferencePoint.class).get();

		// determine anchorage geometry in scene
		IGeometry anchorageGeometryInScene = NodeUtils.localToScene(anchorage,
				anchorageReferenceGeometryInLocal);

		// determine anchorage outlines in scene
		List<ICurve> anchorageOutlinesInScene = getOutlineSegments(
				anchorageGeometryInScene);

		// transform anchored reference point to scene
		Point anchoredReferencePointInScene = NodeUtils.localToScene(anchored,
				anchoredReferencePointInLocal);

		// compute nearest projection of the anchored reference point on the
		// anchorage outlines
		return computeProjectionInScene(anchorageOutlinesInScene,
				anchoredReferencePointInScene, parameters);
	}

	/**
	 * Computes the anchorage reference position in scene coordinates, based on
	 * the given anchorage outlines and the given anchored reference point.
	 *
	 * @param anchorageOutlinesInScene
	 *            A list of {@link ICurve}s that describe the outline of the
	 *            anchorage.
	 * @param anchoredReferencePointInScene
	 *            The reference {@link Point} of the anchored for which the
	 *            anchorage reference {@link Point} is to be determined.
	 * @param parameters
	 *            The parameters available for the computation.
	 * @return The anchorage reference position.
	 */
	protected Point computeProjectionInScene(
			List<ICurve> anchorageOutlinesInScene,
			Point anchoredReferencePointInScene, Set<Parameter<?>> parameters) {
		Point[] projections = new Point[anchorageOutlinesInScene.size()];
		for (int i = 0; i < anchorageOutlinesInScene.size(); i++) {
			ICurve c = anchorageOutlinesInScene.get(i);
			projections[i] = c.getProjection(anchoredReferencePointInScene);
		}
		return Point.nearest(anchoredReferencePointInScene, projections);
	}

	/**
	 * Determines the outline of the given {@link IGeometry}, represented as a
	 * list of {@link ICurve}s.
	 *
	 * @param geometry
	 *            The anchorage geometry.
	 * @return A list of {@link ICurve}s representing the outline of the given
	 *         {@link IGeometry}.
	 */
	// TODO: Move to utility within GEF Geometry?
	protected List<ICurve> getOutlineSegments(IGeometry geometry) {
		if (geometry instanceof IShape) {
			return Collections.singletonList(((IShape) geometry).getOutline());
		} else if (geometry instanceof ICurve) {
			return Collections.singletonList((ICurve) geometry);
		} else if (geometry instanceof Path) {
			return ((Path) geometry).getOutlines();
		} else {
			throw new IllegalStateException(
					"The transformed geometry is neither an ICurve nor an IShape.");
		}
	}

	@Override
	public Set<Class<? extends Parameter<?>>> getRequiredParameters() {
		Set<Class<? extends Parameter<?>>> parameters = new HashSet<>();
		parameters.add(AnchorageReferenceGeometry.class);
		parameters.add(AnchoredReferencePoint.class);
		return parameters;
	}
}