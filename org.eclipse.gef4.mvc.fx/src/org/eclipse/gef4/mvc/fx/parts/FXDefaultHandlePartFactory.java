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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class FXDefaultHandlePartFactory implements IHandlePartFactory<Node> {

	@Inject
	private Injector injector;

	/**
	 * Creates an {@link IHandlePart} for the specified segment vertex of the
	 * {@link IGeometry} provided by the given <i>handleGeometryProvider</i>.
	 *
	 * @param targetPart
	 *            The {@link IContentPart} which is selected.
	 * @param handleGeometryProvider
	 *            Provides an {@link IGeometry} for which an {@link IHandlePart}
	 *            is to be created.
	 * @param segmentIndex
	 *            Index of the segment of the provided {@link IGeometry} for
	 *            which an {@link IHandlePart} is to be created.
	 * @param isEndPoint
	 *            Signifies if the handle is to be created for the end point of
	 *            the segment.
	 * @return {@link IHandlePart} for the specified segment vertex of the
	 *         {@link IGeometry} provided by the <i>handleGeometryProvider</i>
	 */
	public IHandlePart<Node> createCurveSelectionHandlePart(
			final IContentPart<Node> targetPart,
			Provider<IGeometry> handleGeometryProvider, int segmentIndex,
			boolean isEndPoint) {
		FXSegmentHandlePart part = new FXSegmentHandlePart(
				handleGeometryProvider, segmentIndex, isEndPoint ? 1 : 0);
		injector.injectMembers(part);
		return part;
	}

	/**
	 * Generate handles for the end/join points of the individual beziers.
	 *
	 * @param targetPart
	 * @param handleGeometryProvider
	 * @param contextMap
	 *            TODO
	 * @return {@link IHandlePart}s for the given target part.
	 */
	protected List<IHandlePart<Node>> createCurveSelectionHandleParts(
			final IContentPart<Node> targetPart,
			Provider<IGeometry> handleGeometryProvider,
			Map<Object, Object> contextMap) {
		List<IHandlePart<Node>> hps = new ArrayList<IHandlePart<Node>>();
		BezierCurve[] beziers = ((ICurve) handleGeometryProvider.get())
				.toBezier();
		for (int i = 0; i < beziers.length; i++) {
			IHandlePart<Node> hp = createCurveSelectionHandlePart(targetPart,
					handleGeometryProvider, i, false);
			hps.add(hp);
			// create handlepart for the curve's end point, too
			if (i == beziers.length - 1) {
				hp = createCurveSelectionHandlePart(targetPart,
						handleGeometryProvider, i, true);
				hps.add(hp);
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
					(SelectionBehavior<Node>) contextBehavior,
					contextMap);
		}

		// unknown creation context, do not create handles
		return Collections.emptyList();
	}

	/**
	 * Creates an {@link IHandlePart} for one corner of the bounds of a multi
	 * selection. The corner is specified via the <i>position</i> parameter.
	 *
	 * @param targets
	 *            All selected {@link IContentPart}s.
	 * @param position
	 *            Relative position of the {@link IHandlePart} on the collective
	 *            bounds of the multi selection.
	 * @param contextMap
	 *            TODO
	 * @return an {@link IHandlePart} for the specified corner of the bounds of
	 *         the multi selection
	 */
	public IHandlePart<Node> createMultiSelectionCornerHandlePart(
			List<IContentPart<Node>> targets, Pos position,
			Map<Object, Object> contextMap) {
		FXBoxHandlePart part = new FXBoxHandlePart(position);
		injector.injectMembers(part);
		return part;
	}

	public List<IHandlePart<Node>> createMultiSelectionHandleParts(
			List<IContentPart<Node>> targets,
			SelectionBehavior<Node> selectionBehavior,
			Map<Object, Object> contextMap) {
		List<IHandlePart<Node>> handleParts = new ArrayList<IHandlePart<Node>>();

		// per default, handle parts are created for the 4 corners of the multi
		// selection bounds
		for (Pos pos : new Pos[] { Pos.TOP_LEFT, Pos.TOP_RIGHT,
				Pos.BOTTOM_LEFT, Pos.BOTTOM_RIGHT }) {
			handleParts.add(createMultiSelectionCornerHandlePart(targets, pos,
					contextMap));
		}

		return handleParts;
	}

	public List<IHandlePart<Node>> createSelectionHandleParts(
			List<IContentPart<Node>> targets,
			SelectionBehavior<Node> selectionBehavior,
			Map<Object, Object> contextMap) {
		// multiple selection
		if (targets.size() > 1) {
			return createMultiSelectionHandleParts(targets, selectionBehavior,
					contextMap);
		}

		// single selection
		final IContentPart<Node> targetPart = targets.get(0);
		Provider<IGeometry> handleGeometryProvider = selectionBehavior
				.getHandleGeometryProvider(contextMap);

		// generate handles from handle geometry
		IGeometry handleGeometry = null;
		if (handleGeometryProvider != null) {
			handleGeometry = handleGeometryProvider.get();
		}

		if (handleGeometry == null) {
			return Collections.emptyList();
		}

		List<IHandlePart<Node>> handleParts = new ArrayList<IHandlePart<Node>>();
		if (handleGeometry instanceof ICurve) {
			handleParts.addAll(createCurveSelectionHandleParts(targetPart,
					handleGeometryProvider, contextMap));
		} else {
			// everything else is expected to be an IShape, even though the user
			// could supply a Path
			if (handleGeometry instanceof IShape) {
				IShape shape = (IShape) handleGeometry;
				ICurve[] edges = shape.getOutlineSegments();
				// create a handle for each vertex
				for (int i = 0; i < edges.length; i++) {
					IHandlePart<Node> hp = createShapeSelectionHandlePart(
							targetPart, handleGeometryProvider, i, contextMap);
					handleParts.add(hp);
				}
			} else {
				throw new IllegalStateException(
						"Unable to generate handles for this handle geometry. Expected ICurve or IShape, but got: "
								+ handleGeometry);
			}
		}

		return handleParts;
	}

	/**
	 * Creates an {@link IHandlePart} for the specified vertex of the
	 * {@link IGeometry} provided by the given <i>handleGeometryProvider</i>.
	 *
	 * @param targetPart
	 *            The {@link IContentPart} which is selected.
	 * @param handleGeometryProvider
	 *            Provides an {@link IGeometry} for which an {@link IHandlePart}
	 *            is to be created.
	 * @param vertexIndex
	 *            Index of the vertex of the provided {@link IGeometry} for
	 *            which an {@link IHandlePart} is to be created.
	 * @param contextMap
	 *            TODO
	 * @return {@link IHandlePart} for the specified vertex of the
	 *         {@link IGeometry} provided by the <i>handleGeometryProvider</i>
	 */
	public IHandlePart<Node> createShapeSelectionHandlePart(
			IContentPart<Node> targetPart,
			Provider<IGeometry> handleGeometryProvider, int vertexIndex,
			Map<Object, Object> contextMap) {
		FXSegmentHandlePart part = new FXSegmentHandlePart(
				handleGeometryProvider, vertexIndex);
		injector.injectMembers(part);
		return part;
	}

}
