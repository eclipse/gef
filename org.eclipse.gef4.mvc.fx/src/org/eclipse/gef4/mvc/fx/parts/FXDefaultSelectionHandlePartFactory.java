/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Polyline;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import javafx.scene.Node;

/**
 *
 * @author mwienand
 *
 */
public class FXDefaultSelectionHandlePartFactory
		implements IHandlePartFactory<Node> {

	/**
	 * The role name for the <code>Provider&lt;IGeometry&gt;</code> that will be
	 * used to generate selection handles.
	 */
	public static final String SELECTION_HANDLES_GEOMETRY_PROVIDER = "SELECTION_HANDLES_GEOMETRY_PROVIDER";
	/**
	 * The minimum segment length so that creation handles are shown.
	 */
	public static final double CREATION_HANDLE_MINIMUM_SEGMENT_LENGTH = 0;

	// TODO
	private static Provider<BezierCurve[]> createSegmentsProvider(
			final Provider<? extends IGeometry> geometryProvider) {
		return new Provider<BezierCurve[]>() {
			@Override
			public BezierCurve[] get() {
				IGeometry geometry = geometryProvider.get();
				if (geometry instanceof IShape) {
					List<BezierCurve> segments = new ArrayList<>();
					for (ICurve os : ((IShape) geometry).getOutlineSegments()) {
						segments.addAll(Arrays.asList(os.toBezier()));
					}
					return segments.toArray(new BezierCurve[] {});
				} else if (geometry instanceof ICurve) {
					return ((ICurve) geometry).toBezier();
				} else {
					throw new IllegalStateException(
							"Unable to deduce segments from geometry: Expected IShape or ICurve but got: "
									+ geometry);
				}
			}
		};
	}

	@Inject
	private Injector injector;

	// entry point
	@Override
	public List<IHandlePart<Node, ? extends Node>> createHandleParts(
			List<? extends IVisualPart<Node, ? extends Node>> targets,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
		// check creation context
		if (!(contextBehavior instanceof SelectionBehavior)) {
			throw new IllegalArgumentException(
					"The FXDefaultSelectionHandlePartFactory can only generate handle parts in the context of a SelectionBehavior, but the context behavior is a <"
							+ contextBehavior + ">.");
		}
		// check that we have targets
		if (targets == null || targets.isEmpty()) {
			throw new IllegalArgumentException(
					"Part factory is called without targets.");
		}

		if (targets.size() == 1) {
			return createSingleSelectionHandleParts(targets.get(0),
					contextBehavior, contextMap);
		} else {
			return createMultiSelectionHandleParts(targets, contextBehavior,
					contextMap);
		}
	}

	/**
	 * Creates handle parts for a multi selection.
	 *
	 * @param targets
	 *            The target {@link IVisualPart}s for which handles are to be
	 *            created.
	 * @param contextBehavior
	 *            The context {@link IBehavior} which initiates the creation of
	 *            feedback.
	 * @param contextMap
	 *            A map in which the state-less context {@link IBehavior}) may
	 *            place additional context information for the creation process.
	 *            It may either directly contain additional information needed
	 *            by the {@link IHandlePartFactory}, or may be passed back by
	 *            the {@link IHandlePartFactory} to the calling context
	 *            {@link IBehavior} to query such kind of information (in which
	 *            case it will allow the context {@link IBehavior} to identify
	 *            the creation context).
	 * @return A list of {@link IHandlePart}s that can be used to manipulate the
	 *         given targets.
	 */
	protected List<IHandlePart<Node, ? extends Node>> createMultiSelectionHandleParts(
			final List<? extends IVisualPart<Node, ? extends Node>> targets,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
		Provider<? extends IGeometry> handleGeometryProvider = new Provider<IGeometry>() {
			@Override
			public IGeometry get() {
				// TODO: move code out of FXPartUtils into a geometry provider
				// (move to FX)
				return FXPartUtils.getUnionedVisualBoundsInScene(targets);
			}
		};
		List<IHandlePart<Node, ? extends Node>> handleParts = new ArrayList<>();
		// per default, handle parts are created for the 4 corners of the
		// multi selection bounds
		Provider<BezierCurve[]> segmentsProvider = createSegmentsProvider(
				handleGeometryProvider);
		for (int i = 0; i < 4; i++) {
			// create handle for the start point of the segment
			FXSquareSegmentHandlePart part = injector
					.getInstance(FXSquareSegmentHandlePart.class);
			part.setSegmentsProvider(segmentsProvider);
			part.setSegmentIndex(i);
			part.setSegmentParameter(0);
			handleParts.add(part);
		}
		return handleParts;
	}

	/**
	 * Creates handle parts for a single selection.
	 *
	 * @param target
	 *            The target {@link IVisualPart} for which handles are to be
	 *            created.
	 * @param contextBehavior
	 *            The context {@link IBehavior} which initiates the creation of
	 *            feedback.
	 * @param contextMap
	 *            A map in which the state-less context {@link IBehavior}) may
	 *            place additional context information for the creation process.
	 *            It may either directly contain additional information needed
	 *            by the {@link IHandlePartFactory}, or may be passed back by
	 *            the {@link IHandlePartFactory} to the calling context
	 *            {@link IBehavior} to query such kind of information (in which
	 *            case it will allow the context {@link IBehavior} to identify
	 *            the creation context).
	 * @return A list of {@link IHandlePart}s that can be used to manipulate the
	 *         given targets.
	 */
	@SuppressWarnings("serial")
	protected List<IHandlePart<Node, ? extends Node>> createSingleSelectionHandleParts(
			final IVisualPart<Node, ? extends Node> target,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
		// determine handle geometry (in target visual local coordinates)
		final Provider<? extends IGeometry> selectionHandlesGeometryInTargetLocalProvider = target
				.getAdapter(AdapterKey
						.get(new TypeToken<Provider<? extends IGeometry>>() {
						}, SELECTION_HANDLES_GEOMETRY_PROVIDER));
		IGeometry selectionHandlesGeometry = (selectionHandlesGeometryInTargetLocalProvider != null)
				? selectionHandlesGeometryInTargetLocalProvider.get() : null;
		if (selectionHandlesGeometry == null) {
			return Collections.emptyList();
		}

		// create provider that returns the geometry in scene coordinates
		final Provider<IGeometry> selectionHandlesGeometryInSceneProvider = new Provider<IGeometry>() {
			@Override
			public IGeometry get() {
				return NodeUtils.localToScene(target.getVisual(),
						selectionHandlesGeometryInTargetLocalProvider.get());
			}
		};
		Provider<BezierCurve[]> selectionHandlesSegmentsInSceneProvider = createSegmentsProvider(
				selectionHandlesGeometryInSceneProvider);

		if (selectionHandlesGeometry instanceof ICurve) {
			// create curve handles
			return createSingleSelectionHandlePartsForCurve(target,
					contextBehavior, contextMap,
					selectionHandlesSegmentsInSceneProvider);
		} else if (selectionHandlesGeometry instanceof IShape) {
			if (selectionHandlesGeometry instanceof Rectangle) {
				// create box handles
				return createSingleSelectionHandlePartsForRectangularOutline(
						target, contextBehavior, contextMap,
						selectionHandlesSegmentsInSceneProvider);
			} else {
				// create segment handles (based on outline)
				return createSingleSelectionHandlePartsForPolygonalOutline(
						target, contextBehavior, contextMap,
						selectionHandlesSegmentsInSceneProvider);
			}
		} else {
			throw new IllegalStateException(
					"Unable to generate handles for this handle geometry. Expected ICurve or IShape, but got: "
							+ selectionHandlesGeometry);
		}
	}

	/**
	 * Creates handle parts for a single selection of which the handle geometry
	 * is an {@link ICurve}.
	 *
	 * @param target
	 *            The target {@link IVisualPart} for which handles are to be
	 *            created.
	 * @param contextBehavior
	 *            The context {@link IBehavior} which initiates the creation of
	 *            feedback.
	 * @param contextMap
	 *            A map in which the state-less context {@link IBehavior}) may
	 *            place additional context information for the creation process.
	 *            It may either directly contain additional information needed
	 *            by the {@link IHandlePartFactory}, or may be passed back by
	 *            the {@link IHandlePartFactory} to the calling context
	 *            {@link IBehavior} to query such kind of information (in which
	 *            case it will allow the context {@link IBehavior} to identify
	 *            the creation context).
	 * @param segmentsProvider
	 *            A provider for the segments of the handle geometry for which
	 *            handles are to be created.
	 * @return A list of {@link IHandlePart}s that can be used to manipulate the
	 *         given targets.
	 */
	protected List<IHandlePart<Node, ? extends Node>> createSingleSelectionHandlePartsForCurve(
			final IVisualPart<Node, ? extends Node> target,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap,
			Provider<BezierCurve[]> segmentsProvider) {
		List<IHandlePart<Node, ? extends Node>> hps = new ArrayList<>();
		BezierCurve[] segments = segmentsProvider.get();

		if (target.getVisual() instanceof Connection
				&& ((Connection) target.getVisual())
						.getRouter() instanceof OrthogonalRouter) {
			// generate segment based handles
			for (int i = 0; i < segments.length; i++) {
				// create handle for the start point of the curve
				if (i == 0) {
					FXCircleSegmentHandlePart part = injector
							.getInstance(FXCircleSegmentHandlePart.class);
					part.setSegmentsProvider(segmentsProvider);
					part.setSegmentIndex(i);
					part.setSegmentParameter(0.0);
					hps.add(part);
				}

				double segmentLength = new Polyline(segments[i].getPoints())
						.getLength();
				if (segmentLength >= CREATION_HANDLE_MINIMUM_SEGMENT_LENGTH) {
					// create quarter handle for the creation of a new segment
					FXRectangleSegmentHandlePart part = injector
							.getInstance(FXRectangleSegmentHandlePart.class);
					part.setSegmentsProvider(segmentsProvider);
					part.setSegmentIndex(i);
					part.setSegmentParameter(0.25);
					hps.add(part);

					// create quarter handle for the creation of a new segment
					part = injector
							.getInstance(FXRectangleSegmentHandlePart.class);
					part.setSegmentsProvider(segmentsProvider);
					part.setSegmentIndex(i);
					part.setSegmentParameter(0.75);
					hps.add(part);
				}

				// create handle for the end point of the curve
				if (i == segments.length - 1) {
					FXCircleSegmentHandlePart part = injector
							.getInstance(FXCircleSegmentHandlePart.class);
					part.setSegmentsProvider(segmentsProvider);
					part.setSegmentIndex(i);
					part.setSegmentParameter(1.0);
					hps.add(part);
				}
			}
		} else {
			// generate vertex based handles
			for (int i = 0; i < segments.length; i++) {
				// create handle for the start point of a segment
				FXCircleSegmentHandlePart part = injector
						.getInstance(FXCircleSegmentHandlePart.class);
				part.setSegmentsProvider(segmentsProvider);
				part.setSegmentIndex(i);
				part.setSegmentParameter(0.0);
				hps.add(part);

				double segmentLength = new Polyline(segments[i].getPoints())
						.getLength();
				if (segmentLength >= CREATION_HANDLE_MINIMUM_SEGMENT_LENGTH) {
					// create handle for the middle of a segment
					part = injector
							.getInstance(FXCircleSegmentHandlePart.class);
					part.setSegmentsProvider(segmentsProvider);
					part.setSegmentIndex(i);
					part.setSegmentParameter(0.5);
					hps.add(part);
				}

				// create handle for the end point of the curve
				if (i == segments.length - 1) {
					part = injector
							.getInstance(FXCircleSegmentHandlePart.class);
					part.setSegmentsProvider(segmentsProvider);
					part.setSegmentIndex(i);
					part.setSegmentParameter(1.0);
					hps.add(part);
				}
			}
		}

		return hps;
	}

	/**
	 * Creates handle parts for a single selection of which the handle geometry
	 * is an {@link IShape} but not a {@link Rectangle}.
	 *
	 * @param target
	 *            The target {@link IVisualPart} for which handles are to be
	 *            created.
	 * @param contextBehavior
	 *            The context {@link IBehavior} which initiates the creation of
	 *            feedback.
	 * @param contextMap
	 *            A map in which the state-less context {@link IBehavior}) may
	 *            place additional context information for the creation process.
	 *            It may either directly contain additional information needed
	 *            by the {@link IHandlePartFactory}, or may be passed back by
	 *            the {@link IHandlePartFactory} to the calling context
	 *            {@link IBehavior} to query such kind of information (in which
	 *            case it will allow the context {@link IBehavior} to identify
	 *            the creation context).
	 * @param segmentsProvider
	 *            A provider for the segments of the handle geometry for which
	 *            handles are to be created.
	 * @return A list of {@link IHandlePart}s that can be used to manipulate the
	 *         given targets.
	 */
	protected List<IHandlePart<Node, ? extends Node>> createSingleSelectionHandlePartsForPolygonalOutline(
			IVisualPart<Node, ? extends Node> target,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap,
			Provider<BezierCurve[]> segmentsProvider) {
		List<IHandlePart<Node, ? extends Node>> handleParts = new ArrayList<>();
		BezierCurve[] segments = segmentsProvider.get();
		for (int i = 0; i < segments.length; i++) {
			// create handle for the start point of the segment
			FXCircleSegmentHandlePart part = injector
					.getInstance(FXCircleSegmentHandlePart.class);
			part.setSegmentsProvider(segmentsProvider);
			part.setSegmentIndex(i);
			part.setSegmentParameter(0);
			handleParts.add(part);
		}
		return handleParts;
	}

	/**
	 * Creates handle parts for a single selection of which the handle geometry
	 * is a {@link Rectangle}.
	 *
	 * @param target
	 *            The target {@link IVisualPart} for which handles are to be
	 *            created.
	 * @param contextBehavior
	 *            The context {@link IBehavior} which initiates the creation of
	 *            feedback.
	 * @param contextMap
	 *            A map in which the state-less context {@link IBehavior}) may
	 *            place additional context information for the creation process.
	 *            It may either directly contain additional information needed
	 *            by the {@link IHandlePartFactory}, or may be passed back by
	 *            the {@link IHandlePartFactory} to the calling context
	 *            {@link IBehavior} to query such kind of information (in which
	 *            case it will allow the context {@link IBehavior} to identify
	 *            the creation context).
	 * @param segmentsProvider
	 *            A provider for the segments of the handle geometry for which
	 *            handles are to be created.
	 * @return A list of {@link IHandlePart}s that can be used to manipulate the
	 *         given targets.
	 */
	protected List<IHandlePart<Node, ? extends Node>> createSingleSelectionHandlePartsForRectangularOutline(
			IVisualPart<Node, ? extends Node> target,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap,
			Provider<BezierCurve[]> segmentsProvider) {
		List<IHandlePart<Node, ? extends Node>> hps = new ArrayList<>();
		BezierCurve[] segments = segmentsProvider.get();
		for (int i = 0; i < segments.length; i++) {
			FXSquareSegmentHandlePart part = injector
					.getInstance(FXSquareSegmentHandlePart.class);
			part.setSegmentsProvider(segmentsProvider);
			part.setSegmentIndex(i);
			part.setSegmentParameter(0);
			hps.add(part);
		}
		return hps;
	}

}
