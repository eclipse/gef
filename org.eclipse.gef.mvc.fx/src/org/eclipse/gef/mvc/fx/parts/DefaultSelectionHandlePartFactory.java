/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.planar.BezierCurve;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.IShape;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.behaviors.IBehavior;
import org.eclipse.gef.mvc.fx.providers.ResizableTransformableBoundsProvider;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 *
 * @author mwienand
 *
 */
public class DefaultSelectionHandlePartFactory implements IHandlePartFactory {

	/**
	 * A binding key for the fill color of intra segment handles.
	 */
	public static final String INSERT_HANDLE_COLOR_PROVIDER = "INSERT_HANDLE_COLOR_PROVIDER";

	/**
	 * Defines the default {@link Color} for insertion handles.
	 */
	public static final Color DEFAULT_INSERT_HANDLE_COLOR = Color.WHITE;

	/**
	 * An adapter role for the fill color of segment end handles.
	 */
	public static final String MOVE_HANDLE_COLOR_PROVIDER = "MOVE_HANDLE_COLOR_PROVIDER";

	/**
	 * Defines the default {@link Color} for movement handles.
	 */
	public static final Color DEFAULT_MOVE_HANDLE_COLOR = Color.web("#7986cb");

	/**
	 * An adapter role for the fill color of connected handles.
	 */
	public static final String CONNECTED_HANDLE_COLOR_PROVIDER = "CONNECTED_HANDLE_COLOR_PROVIDER";

	/**
	 * Defines the default {@link Color} for connected handles.
	 */
	public static final Color DEFAULT_CONNECTED_HANDLE_COLOR = Color.RED;

	/**
	 * The role name for the <code>Provider&lt;IGeometry&gt;</code> that will be
	 * used to generate selection handles.
	 */
	public static final String SELECTION_HANDLES_GEOMETRY_PROVIDER = "SELECTION_HANDLES_GEOMETRY_PROVIDER";

	/**
	 * The role name for the <code>Provider&lt;IGeometry&gt;</code> that will be
	 * used to generate selection handles for a multi selection.
	 */
	public static final String MULTI_SELECTION_HANDLES_GEOMETRY_PROVIDER = "MULTI_SELECTION_HANDLES_GEOMETRY_PROVIDER";

	/**
	 * The minimum segment length so that creation handles are shown.
	 */
	protected static final double BENDPOINT_CREATE_HANDLE_MINIMUM_SEGMENT_LENGTH = 30;
	/**
	 * The minimum segment length for the creation of segment bend handles.
	 */
	protected static final double SEGMENT_MOVE_HANDLE_MINIMUM_SEGMENT_LENGTH = 5;
	/**
	 * The minimum segment length for creation of segment create handles
	 */
	protected static final double SEGMENT_CREATE_HANDLE_MINIMUM_SEGMENT_LENGTH = 45;

	private static Provider<BezierCurve[]> createSegmentsProvider(
			final Provider<? extends IGeometry> geometryProvider) {
		// TODO: get() call is very expensive, maybe cache the result and/or use
		// a binding mechanism instead?
		return new Provider<BezierCurve[]>() {
			@Override
			public BezierCurve[] get() {
				IGeometry geometry = geometryProvider.get();

				if (geometry == null) {
					return new BezierCurve[] {};
				}

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
	public List<IHandlePart<? extends Node>> createHandleParts(
			List<? extends IVisualPart<? extends Node>> targets,
			Map<Object, Object> contextMap) {
		// check that we have targets
		if (targets == null || targets.isEmpty()) {
			throw new IllegalArgumentException(
					"Part factory is called without targets.");
		}

		if (targets.size() == 1) {
			return createSingleSelectionHandleParts(targets.get(0), contextMap);
		} else {
			return createMultiSelectionHandleParts(targets, contextMap);
		}
	}

	/**
	 * Creates handle parts for a multi selection.
	 *
	 * @param targets
	 *            The target {@link IVisualPart}s for which handles are to be
	 *            created.
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
	protected List<IHandlePart<? extends Node>> createMultiSelectionHandleParts(
			final List<? extends IVisualPart<? extends Node>> targets,
			Map<Object, Object> contextMap) {
		// determine handle geometry provider
		@SuppressWarnings("serial")
		Provider<? extends IGeometry> multiSelectionHandlesGeometryInSceneProvider = targets
				.get(0).getRoot().getAdapter(AdapterKey
						.get(new TypeToken<Provider<? extends IGeometry>>() {
						}, MULTI_SELECTION_HANDLES_GEOMETRY_PROVIDER));

		if (multiSelectionHandlesGeometryInSceneProvider == null) {
			// generate default handle geometry provider that unions the
			// ResizableTransformable bounds of all targets
			multiSelectionHandlesGeometryInSceneProvider = new Provider<IGeometry>() {
				@Override
				public IGeometry get() {
					Rectangle bounds = null;
					for (IVisualPart<? extends Node> part : targets) {
						ResizableTransformableBoundsProvider boundsProvider = new ResizableTransformableBoundsProvider();
						boundsProvider.setAdaptable(part);
						IGeometry boundsInLocal = boundsProvider.get();
						// transform to scene
						if (boundsInLocal != null) {
							Rectangle boundsInScene = FX2Geometry
									.toRectangle(part.getVisual()
											.localToScene(Geometry2FX
													.toFXBounds(boundsInLocal
															.getBounds())));
							if (bounds == null) {
								bounds = boundsInScene;
							} else {
								bounds.union(boundsInScene);
							}
						}
					}
					return bounds;
				}
			};
		}

		// per default, handle parts are created for the 4 corners of the
		// multi selection bounds
		Provider<BezierCurve[]> segmentsProvider = createSegmentsProvider(
				multiSelectionHandlesGeometryInSceneProvider);

		// check if provider is OK
		int segments = segmentsProvider.get().length;
		if (segments != 0 && segments != 4) {
			throw new IllegalStateException(
					"The multi selection handle geometry provider is expected to return bounds around the selection. However, instead of 4 segments, the provider provides "
							+ segments + " segments.");
		}

		// create a handle for each start point of the segments
		List<IHandlePart<? extends Node>> handleParts = new ArrayList<>();
		for (int i = 0; i < segments; i++) {
			SquareSegmentHandlePart part = injector
					.getInstance(SquareSegmentHandlePart.class);
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
	protected List<IHandlePart<? extends Node>> createSingleSelectionHandleParts(
			final IVisualPart<? extends Node> target,
			Map<Object, Object> contextMap) {
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
			return createSingleSelectionHandlePartsForCurve(target, contextMap,
					selectionHandlesSegmentsInSceneProvider);
		} else if (selectionHandlesGeometry instanceof IShape) {
			if (selectionHandlesGeometry instanceof Rectangle) {
				// create box handles
				return createSingleSelectionHandlePartsForRectangularOutline(
						target, contextMap,
						selectionHandlesSegmentsInSceneProvider);
			} else {
				// create segment handles (based on outline)
				return createSingleSelectionHandlePartsForPolygonalOutline(
						target, contextMap,
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
	protected List<IHandlePart<? extends Node>> createSingleSelectionHandlePartsForCurve(
			final IVisualPart<? extends Node> target,
			Map<Object, Object> contextMap,
			Provider<BezierCurve[]> segmentsProvider) {
		List<IHandlePart<? extends Node>> hps = new ArrayList<>();
		BezierCurve[] segments = segmentsProvider.get();

		if (target.getVisual() instanceof Connection
				&& ((Connection) target.getVisual())
						.getRouter() instanceof OrthogonalRouter) {
			// generate segment based handles
			for (int i = 0; i < segments.length; i++) {
				// create handle for the start point of the curve
				if (i == 0) {
					CircleSegmentHandlePart part = injector
							.getInstance(CircleSegmentHandlePart.class);
					part.setSegmentsProvider(segmentsProvider);
					part.setSegmentIndex(i);
					part.setSegmentParameter(0.0);
					hps.add(part);
				}

				// create quarter handle for the creation of a new segment
				double segmentLength = new Polyline(segments[i].getPoints())
						.getLength();
				if (segmentLength > SEGMENT_CREATE_HANDLE_MINIMUM_SEGMENT_LENGTH) {
					RectangleSegmentHandlePart part = injector
							.getInstance(RectangleSegmentHandlePart.class);
					part.setSegmentsProvider(segmentsProvider);
					part.setSegmentIndex(i);
					part.setSegmentParameter(0.25);
					hps.add(part);
				}

				// mid handle for segment drag
				if (segmentLength > SEGMENT_MOVE_HANDLE_MINIMUM_SEGMENT_LENGTH) {
					RectangleSegmentHandlePart midPart = injector
							.getInstance(RectangleSegmentHandlePart.class);
					midPart.setSegmentsProvider(segmentsProvider);
					midPart.setSegmentIndex(i);
					midPart.setSegmentParameter(0.5);
					hps.add(midPart);
				}

				// create quarter handle for the creation of a new segment
				if (segmentLength > SEGMENT_CREATE_HANDLE_MINIMUM_SEGMENT_LENGTH) {
					RectangleSegmentHandlePart part = injector
							.getInstance(RectangleSegmentHandlePart.class);
					part.setSegmentsProvider(segmentsProvider);
					part.setSegmentIndex(i);
					part.setSegmentParameter(0.75);
					hps.add(part);
				}

				// create handle for the end point of the curve
				if (i == segments.length - 1) {
					CircleSegmentHandlePart part = injector
							.getInstance(CircleSegmentHandlePart.class);
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
				CircleSegmentHandlePart part = injector
						.getInstance(CircleSegmentHandlePart.class);
				part.setSegmentsProvider(segmentsProvider);
				part.setSegmentIndex(i);
				part.setSegmentParameter(0.0);
				hps.add(part);

				double segmentLength = new Polyline(segments[i].getPoints())
						.getLength();
				if (segmentLength >= BENDPOINT_CREATE_HANDLE_MINIMUM_SEGMENT_LENGTH) {
					// create handle for the middle of a segment
					part = injector.getInstance(CircleSegmentHandlePart.class);
					part.setSegmentsProvider(segmentsProvider);
					part.setSegmentIndex(i);
					part.setSegmentParameter(0.5);
					hps.add(part);
				}

				// create handle for the end point of the curve
				if (i == segments.length - 1) {
					part = injector.getInstance(CircleSegmentHandlePart.class);
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
	protected List<IHandlePart<? extends Node>> createSingleSelectionHandlePartsForPolygonalOutline(
			IVisualPart<? extends Node> target, Map<Object, Object> contextMap,
			Provider<BezierCurve[]> segmentsProvider) {
		List<IHandlePart<? extends Node>> handleParts = new ArrayList<>();
		BezierCurve[] segments = segmentsProvider.get();
		for (int i = 0; i < segments.length; i++) {
			// create handle for the start point of the segment
			CircleSegmentHandlePart part = injector
					.getInstance(CircleSegmentHandlePart.class);
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
	protected List<IHandlePart<? extends Node>> createSingleSelectionHandlePartsForRectangularOutline(
			IVisualPart<? extends Node> target, Map<Object, Object> contextMap,
			Provider<BezierCurve[]> segmentsProvider) {
		List<IHandlePart<? extends Node>> hps = new ArrayList<>();
		BezierCurve[] segments = segmentsProvider.get();
		for (int i = 0; i < segments.length; i++) {
			SquareSegmentHandlePart part = injector
					.getInstance(SquareSegmentHandlePart.class);
			part.setSegmentsProvider(segmentsProvider);
			part.setSegmentIndex(i);
			part.setSegmentParameter(0);
			hps.add(part);
		}
		return hps;
	}

}
