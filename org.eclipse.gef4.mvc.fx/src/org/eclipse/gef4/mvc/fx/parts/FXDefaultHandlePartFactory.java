/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class FXDefaultHandlePartFactory implements IHandlePartFactory<Node> {

	public static final String SELECTION_HANDLES_GEOMETRY_PROVIDER = "SELECTION_HANDLES_GEOMETRY_PROVIDER";

	// TODO: add hover handles -> can be useful for connection creation
	// public static final String HOVER_HANDLES_GEOMETRY_PROVIDER =
	// "HOVER_HANDLES_GEOMETRY_PROVIDER";

	@Inject
	private Injector injector;

	// TODO: maybe inline this method
	protected List<IHandlePart<Node>> createBoundsSelectionHandleParts(
			Provider<IGeometry> handleGeometryProvider,
			Map<Object, Object> contextMap) {
		List<IHandlePart<Node>> handleParts = new ArrayList<IHandlePart<Node>>();

		// per default, handle parts are created for the 4 corners of the
		// multi selection bounds
		for (Pos pos : new Pos[] { Pos.TOP_LEFT, Pos.TOP_RIGHT,
				Pos.BOTTOM_LEFT, Pos.BOTTOM_RIGHT }) {
			handleParts.add(createCornerHandlePart(handleGeometryProvider, pos,
					contextMap));
		}
		return handleParts;
	}

	/**
	 * Creates an {@link IHandlePart} for one corner of the bounds of a multi
	 * selection. The corner is specified via the <i>position</i> parameter.
	 *
	 * @param handleGeometryProvider
	 *            Provides an {@link IGeometry} from which the handle positions
	 *            are derived.
	 * @param position
	 *            Relative position of the {@link IHandlePart} on the collective
	 *            bounds of the multi selection.
	 * @param contextMap
	 *            Stores context information as an {@link IBehavior} is
	 *            stateless.
	 * @return an {@link IHandlePart} for the specified corner of the bounds of
	 *         the multi selection
	 */
	protected IHandlePart<Node> createCornerHandlePart(
			Provider<IGeometry> handleGeometryProvider, Pos position,
			Map<Object, Object> contextMap) {
		FXCornerHandlePart part = new FXCornerHandlePart(
				handleGeometryProvider, position);
		injector.injectMembers(part);
		return part;
	}

	/**
	 * Creates an {@link IHandlePart} for the specified segment vertex of the
	 * {@link IGeometry} provided by the given <i>handleGeometryProvider</i>.
	 *
	 * @param targetPart
	 *            The {@link IContentPart} which is selected.
	 * @param segmentsProvider
	 *            Provides an {@link IGeometry} for which an {@link IHandlePart}
	 *            is to be created.
	 * @param segmentIndex
	 *            Index of the segment of the provided {@link IGeometry} for
	 *            which an {@link IHandlePart} is to be created.
	 * @param segmentParameter
	 *            Parameter between 0 and 1 that specifies the location on the
	 *            segment
	 * @return {@link IHandlePart} for the specified segment vertex of the
	 *         {@link IGeometry} provided by the <i>handleGeometryProvider</i>
	 */
	protected IHandlePart<Node> createCurveSelectionHandlePart(
			final IContentPart<Node> targetPart,
			Provider<BezierCurve[]> segmentsProvider, int segmentCount,
			int segmentIndex, double segmentParameter) {
		FXSegmentHandlePart part = new FXSegmentHandlePart(segmentsProvider,
				segmentIndex, segmentParameter);
		injector.injectMembers(part);
		return part;
	}

	/**
	 * Generate handles for the end/join points of the individual beziers.
	 *
	 * @param targetPart
	 *            The {@link IContentPart} which is selected.
	 * @param segmentsProvider
	 *            Provides an {@link IGeometry} for which {@link IHandlePart}s
	 *            are to be created.
	 * @param contextMap
	 *            Stores context information as an {@link IBehavior} is
	 *            stateless.
	 * @return {@link IHandlePart}s for the given target part.
	 */
	protected List<IHandlePart<Node>> createCurveSelectionHandleParts(
			final IContentPart<Node> targetPart,
			Provider<BezierCurve[]> segmentsProvider,
			Map<Object, Object> contextMap) {
		List<IHandlePart<Node>> hps = new ArrayList<IHandlePart<Node>>();
		BezierCurve[] segments = segmentsProvider.get();
		for (int i = 0; i < segments.length; i++) {
			hps.add(createCurveSelectionHandlePart(targetPart,
					segmentsProvider, segments.length, i, 0.0));
			hps.add(createCurveSelectionHandlePart(targetPart,
					segmentsProvider, segments.length, i, 0.5));

			// create handle part for the curve's end point, too
			if (i == segments.length - 1) {
				hps.add(createCurveSelectionHandlePart(targetPart,
						segmentsProvider, segments.length, i, 1.0));
			}
		}
		return hps;
	}

	@Override
	public List<IHandlePart<Node>> createHandleParts(
			List<IContentPart<Node>> targets, IBehavior<Node> contextBehavior,
			Map<Object, Object> contextMap) {
		// no targets
		if (targets == null || targets.isEmpty()) {
			return Collections.emptyList();
		}

		// differentiate creation context
		if (contextBehavior instanceof SelectionBehavior) {
			return createSelectionHandleParts(targets,
					(SelectionBehavior<Node>) contextBehavior, contextMap);
		}

		// unknown creation context, do not create handles
		return Collections.emptyList();
	}

	protected List<IHandlePart<Node>> createMultiSelectionHandleParts(
			final List<IContentPart<Node>> targets,
			Map<Object, Object> contextMap) {
		Provider<IGeometry> handleGeometryProvider = new Provider<IGeometry>() {
			@Override
			public IGeometry get() {
				// TODO: move code out of FXPartUtils into a geometry provider
				// (move to FX)
				final Bounds unionedBoundsInScene = FXPartUtils
						.getUnionedVisualBoundsInScene(targets);
				return JavaFX2Geometry.toRectangle(unionedBoundsInScene);
			}
		};
		return createBoundsSelectionHandleParts(handleGeometryProvider,
				contextMap);
	}

	/**
	 * Creates an {@link IHandlePart} for the specified vertex of the
	 * {@link IGeometry} provided by the given <i>handleGeometryProvider</i>.
	 *
	 * @param segmentIndex
	 *            Index of the vertex of the provided {@link IGeometry} for
	 *            which an {@link IHandlePart} is to be created.
	 * @param contextMap
	 *            Stores context information as an {@link IBehavior} is
	 *            stateless.
	 * @return {@link IHandlePart} for the specified vertex of the
	 *         {@link IGeometry} provided by the <i>handleGeometryProvider</i>
	 */
	protected IHandlePart<Node> createSegmentHandlePart(
			Provider<BezierCurve[]> segmentsProvider, int segmentCount,
			int segmentIndex, Map<Object, Object> contextMap) {
		FXSegmentHandlePart part = new FXSegmentHandlePart(segmentsProvider,
				segmentIndex);
		injector.injectMembers(part);
		return part;
	}

	protected List<IHandlePart<Node>> createSelectionHandleParts(
			List<IContentPart<Node>> targets,
			SelectionBehavior<Node> selectionBehavior,
			Map<Object, Object> contextMap) {
		if (targets.isEmpty()) {
			return Collections.emptyList();
		} else if (targets.size() == 1) {
			return createSingleSelectionHandleParts(targets.get(0), contextMap);
		} else {
			// multiple selection uses bounds
			return createMultiSelectionHandleParts(targets, contextMap);
		}

	}

	protected List<IHandlePart<Node>> createSingleSelectionHandleParts(
			final IContentPart<Node> target, Map<Object, Object> contextMap) {

		List<IHandlePart<Node>> handleParts = new ArrayList<IHandlePart<Node>>();

		// handle geometry is in target visual local coordinate space.
		final Provider<IGeometry> selectionHandlesGeometryInTargetLocalProvider = target
				.<Provider<IGeometry>> getAdapter(AdapterKey.get(
						Provider.class, SELECTION_HANDLES_GEOMETRY_PROVIDER));

		// generate handles from selection handles geometry
		IGeometry selectionHandlesGeometry = (selectionHandlesGeometryInTargetLocalProvider != null) ? selectionHandlesGeometryInTargetLocalProvider
				.get() : null;

		if (selectionHandlesGeometry == null) {
			return Collections.emptyList();
		}

		// we will need a provider that returns the geometry in scene
		// coordinates
		final Provider<IGeometry> selectionHandlesGeometryInSceneProvider = new Provider<IGeometry>() {

			@Override
			public IGeometry get() {
				return FXUtils.localToScene(target.getVisual(),
						selectionHandlesGeometryInTargetLocalProvider.get());
			}
		};
		Provider<BezierCurve[]> selectionHandlesSegmentsInSceneProvider = new Provider<BezierCurve[]>() {
			@Override
			public BezierCurve[] get() {
				IGeometry handleGeometry = selectionHandlesGeometryInSceneProvider
						.get();
				if (handleGeometry instanceof IShape) {
					List<BezierCurve> segments = new ArrayList<>();
					for (ICurve os : ((IShape) handleGeometry)
							.getOutlineSegments()) {
						segments.addAll(Arrays.asList(os.toBezier()));
					}
					return segments.toArray(new BezierCurve[] {});
				} else if (handleGeometry instanceof ICurve) {
					return ((ICurve) handleGeometry).toBezier();
				} else {
					throw new IllegalStateException(
							"Unable to determine handle position: Expected IShape or ICurve but got: "
									+ handleGeometry);
				}
			}
		};

		if (selectionHandlesGeometry instanceof ICurve) {
			// assure the geometry provider that is handed over returns the
			// geometry in scene coordinates
			handleParts.addAll(createCurveSelectionHandleParts(target,
					selectionHandlesSegmentsInSceneProvider, contextMap));
		} else if (selectionHandlesGeometry instanceof IShape) {
			if (selectionHandlesGeometry instanceof Rectangle) {
				// create corner handles
				handleParts.addAll(createBoundsSelectionHandleParts(
						selectionHandlesGeometryInSceneProvider, contextMap));
			} else {
				// create segment handles (based on outline)
				BezierCurve[] segments = selectionHandlesSegmentsInSceneProvider
						.get();
				for (int i = 0; i < segments.length; i++) {
					IHandlePart<Node> hp = createSegmentHandlePart(
							selectionHandlesSegmentsInSceneProvider,
							segments.length, i, contextMap);
					handleParts.add(hp);
				}
			}
		} else {
			throw new IllegalStateException(
					"Unable to generate handles for this handle geometry. Expected ICurve or IShape, but got: "
							+ selectionHandlesGeometry);
		}
		return handleParts;
	}
}
