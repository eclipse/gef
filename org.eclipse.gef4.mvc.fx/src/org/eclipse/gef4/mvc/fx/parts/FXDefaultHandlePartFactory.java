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

import javafx.geometry.Pos;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.IProvider;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.policies.AbstractResizeRelocateSelectedOnHandleDragPolicy.ReferencePoint;
import org.eclipse.gef4.mvc.policies.IDragPolicy;

public class FXDefaultHandlePartFactory implements IHandlePartFactory<Node> {

	@Override
	public List<IHandlePart<Node>> createHandleParts(
			List<IContentPart<Node>> targets, IBehavior<Node> contextBehavior) {
		// no targets
		if (targets == null || targets.isEmpty())
			return Collections.emptyList();

		// differentiate creation context
		if (contextBehavior instanceof FXSelectionBehavior) {
			return createSelectionHandleParts(targets,
					(FXSelectionBehavior) contextBehavior);
		}

		// unknown creation context, do not create handles
		return Collections.emptyList();
	}

	public List<IHandlePart<Node>> createSelectionHandleParts(
			List<IContentPart<Node>> targets,
			FXSelectionBehavior selectionBehavior) {
		// multiple selection
		if (targets.size() > 1) {
			return createMultiSelectionHandleParts(targets, selectionBehavior);
		}

		// single selection
		final IContentPart<Node> targetPart = targets.get(0);
		IProvider<IGeometry> handleGeometryProvider = selectionBehavior
				.getHandleGeometryProvider();

		// generate handles from handle geometry
		IGeometry geom = handleGeometryProvider.get();
		List<IHandlePart<Node>> handleParts = new ArrayList<IHandlePart<Node>>();

		if (geom instanceof ICurve) {
			// generate handles for the end/join points of the individual
			// beziers
			BezierCurve[] beziers = ((ICurve) geom).toBezier();
			for (int i = 0; i < beziers.length; i++) {
				IHandlePart<Node> hp = createCurveSelectionHandlePart(
						targetPart, handleGeometryProvider, i, false);
				handleParts.add(hp);
				// create handlepart for the curve's end point, too
				if (i == beziers.length - 1) {
					hp = createCurveSelectionHandlePart(targetPart,
							handleGeometryProvider, i, true);
					handleParts.add(hp);
				}
			}
		} else {
			// everything else is expected to be an IShape, even though the user
			// could supply a Path
			if (geom instanceof IShape) {
				IShape shape = (IShape) geom;
				ICurve[] edges = shape.getOutlineSegments();
				// create a handle for each vertex
				for (int i = 0; i < edges.length; i++) {
					IHandlePart<Node> hp = createShapeSelectionHandlePart(
							targetPart, handleGeometryProvider, i);
					handleParts.add(hp);
				}
			} else {
				throw new IllegalStateException(
						"Unable to generate handles for this handle geometry. Expected ICurve or IShape, but got: "
								+ geom);
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
	 * @return {@link IHandlePart} for the specified vertex of the
	 *         {@link IGeometry} provided by the <i>handleGeometryProvider</i>
	 */
	public IHandlePart<Node> createShapeSelectionHandlePart(
			IContentPart<Node> targetPart,
			IProvider<IGeometry> handleGeometryProvider, int vertexIndex) {
		return new FXSelectionHandlePart(targetPart, handleGeometryProvider,
				vertexIndex);
	}

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
			IProvider<IGeometry> handleGeometryProvider, int segmentIndex,
			boolean isEndPoint) {
		return new FXSelectionHandlePart(targetPart, handleGeometryProvider, segmentIndex,
				isEndPoint);
	}

	public List<IHandlePart<Node>> createMultiSelectionHandleParts(
			List<IContentPart<Node>> targets,
			FXSelectionBehavior selectionBehavior) {
		List<IHandlePart<Node>> handleParts = new ArrayList<IHandlePart<Node>>();

		// per default, handle parts are created for the 4 corners of the multi
		// selection bounds
		IHandlePart<Node> handlePart = createMultiSelectionCornerHandlePart(
				targets, Pos.TOP_LEFT);
		handlePart.installBound(IDragPolicy.class,
				new FXResizeRelocateSelectedOnHandleDragPolicy(
						ReferencePoint.TOP_LEFT));
		handleParts.add(handlePart);

		handlePart = createMultiSelectionCornerHandlePart(targets,
				Pos.TOP_RIGHT);
		handlePart.installBound(IDragPolicy.class,
				new FXResizeRelocateSelectedOnHandleDragPolicy(
						ReferencePoint.TOP_RIGHT));
		handleParts.add(handlePart);

		handlePart = createMultiSelectionCornerHandlePart(targets,
				Pos.BOTTOM_RIGHT);
		handlePart.installBound(IDragPolicy.class,
				new FXResizeRelocateSelectedOnHandleDragPolicy(
						ReferencePoint.BOTTOM_RIGHT));
		handleParts.add(handlePart);

		handlePart = createMultiSelectionCornerHandlePart(targets,
				Pos.BOTTOM_LEFT);
		handlePart.installBound(IDragPolicy.class,
				new FXResizeRelocateSelectedOnHandleDragPolicy(
						ReferencePoint.BOTTOM_LEFT));
		handleParts.add(handlePart);

		return handleParts;
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
	 * @return an {@link IHandlePart} for the specified corner of the bounds of
	 *         the multi selection
	 */
	public IHandlePart<Node> createMultiSelectionCornerHandlePart(
			List<IContentPart<Node>> targets, Pos position) {
		return new FXBoxHandlePart(targets, position);
	}

}
