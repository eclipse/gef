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
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
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
public class FXDefaultHoverHandlePartFactory
		implements IHandlePartFactory<Node> {

	/**
	 * The role name for the <code>Provider&lt;IGeometry&gt;</code> that will be
	 * used to generate hover handles.
	 */
	public static final String HOVER_HANDLES_GEOMETRY_PROVIDER = "HOVER_HANDLES_GEOMETRY_PROVIDER";

	@Inject
	private Injector injector;

	@Override
	public List<IHandlePart<Node, ? extends Node>> createHandleParts(
			List<? extends IVisualPart<Node, ? extends Node>> targets,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
		// check creation context
		if (!(contextBehavior instanceof HoverBehavior)) {
			throw new IllegalStateException(
					"The FXDefaultHoverHandlePartFactory can only generate handle parts in the context of a HoverBehavior, but the context behavior is a <"
							+ contextBehavior + ">.");
		}

		// no targets
		if (targets == null || targets.isEmpty()) {
			return Collections.emptyList();
		}

		// check that only one part is hovered at a time
		if (targets.size() > 1) {
			throw new IllegalStateException(
					"Cannot create hover handles for more than one target.");
		}

		return createHoverHandleParts(targets.get(0),
				(HoverBehavior<Node>) contextBehavior, contextMap);
	}

	/**
	 * Creates hover handle parts for the given (hovered) <i>target</i>
	 * {@link IVisualPart}.
	 *
	 * @param target
	 *            The (hovered) target {@link IVisualPart} for which hover
	 *            handles are created.
	 * @param contextBehavior
	 *            The {@link HoverBehavior} that initiated the creation process.
	 * @param contextMap
	 *            A map in which the state-less {@link HoverBehavior} may place
	 *            additional context information for the creation process. It
	 *            may either directly contain additional information needed by
	 *            this factory, or may be passed back by the factory to the
	 *            calling {@link HoverBehavior} to query such kind of
	 *            information (in which case it will allow the
	 *            {@link HoverBehavior} to identify the creation context).
	 * @return A list containing the created hover handle parts.
	 */
	@SuppressWarnings("serial")
	protected List<IHandlePart<Node, ? extends Node>> createHoverHandleParts(
			final IVisualPart<Node, ? extends Node> target,
			final HoverBehavior<Node> contextBehavior,
			final Map<Object, Object> contextMap) {
		List<IHandlePart<Node, ? extends Node>> handleParts = new ArrayList<>();

		// handle geometry is in target visual local coordinate space.
		final Provider<? extends IGeometry> hoverHandlesGeometryInTargetLocalProvider = target
				.getAdapter(AdapterKey
						.get(new TypeToken<Provider<? extends IGeometry>>() {
						}, HOVER_HANDLES_GEOMETRY_PROVIDER));

		// generate handles from selection handles geometry
		IGeometry hoverHandlesGeometry = (hoverHandlesGeometryInTargetLocalProvider != null)
				? hoverHandlesGeometryInTargetLocalProvider.get() : null;
		if (hoverHandlesGeometry == null) {
			return handleParts; // empty
		}

		// we will need a provider that returns the geometry in scene
		// coordinates
		final Provider<? extends IGeometry> hoverHandlesGeometryInSceneProvider = new Provider<IGeometry>() {
			@Override
			public IGeometry get() {
				return NodeUtils.localToScene(target.getVisual(),
						hoverHandlesGeometryInTargetLocalProvider.get());
			}
		};

		// the handle parts are located based on the segments of the handle
		// geometry
		Provider<BezierCurve[]> hoverHandlesSegmentsInSceneProvider = new Provider<BezierCurve[]>() {
			@Override
			public BezierCurve[] get() {
				IGeometry handleGeometry = hoverHandlesGeometryInSceneProvider
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

		// create segment handles (based on outline)
		BezierCurve[] segments = hoverHandlesSegmentsInSceneProvider.get();
		for (int i = 0; i < segments.length; i++) {
			IHandlePart<Node, ? extends Node> hp = createHoverSegmentHandlePart(
					target, hoverHandlesSegmentsInSceneProvider,
					segments.length, i, contextMap);
			if (hp != null) {
				handleParts.add(hp);
			}
		}

		return handleParts;
	}

	/**
	 * Creates an {@link FXCircleSegmentHandlePart} for the given (hovered)
	 * <i>target</i> {@link IVisualPart}. The segments provider and segment
	 * index determine the position of the hover handle.
	 *
	 * @param target
	 *            The (hovered) target {@link IVisualPart}.
	 * @param hoverHandlesSegmentsInSceneProvider
	 *            The <code>Provider&lt;BezierCurve[]&gt;</code> that is used to
	 *            determine the handle's position.
	 * @param segmentCount
	 *            The number of segments returned by the segments provider.
	 * @param segmentIndex
	 *            The segment index on which the created handle part is located.
	 * @param contextMap
	 *            A map in which the state-less {@link HoverBehavior} may place
	 *            additional context information for the creation process. It
	 *            may either directly contain additional information needed by
	 *            this factory, or may be passed back by the factory to the
	 *            calling {@link HoverBehavior} to query such kind of
	 *            information (in which case it will allow the
	 *            {@link HoverBehavior} to identify the creation context).
	 * @return An {@link FXCircleSegmentHandlePart} for the given target at the
	 *         specified position.
	 */
	protected IHandlePart<Node, ? extends Node> createHoverSegmentHandlePart(
			final IVisualPart<Node, ? extends Node> target,
			Provider<BezierCurve[]> hoverHandlesSegmentsInSceneProvider,
			int segmentCount, int segmentIndex,
			Map<Object, Object> contextMap) {
		FXCircleSegmentHandlePart part = injector
				.getInstance(FXCircleSegmentHandlePart.class);
		part.setSegmentsProvider(hoverHandlesSegmentsInSceneProvider);
		part.setSegmentIndex(segmentIndex);
		part.setSegmentParameter(0);
		return part;
	}

}
