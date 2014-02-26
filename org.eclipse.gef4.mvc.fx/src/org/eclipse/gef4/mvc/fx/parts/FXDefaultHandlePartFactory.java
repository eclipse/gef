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
	public List<IHandlePart<Node>> createHandleParts(List<IContentPart<Node>> targets, IBehavior<Node> contextBehavior) {
		// no targets
		if (targets == null || targets.isEmpty())
			return Collections.emptyList();
		
		// differentiate creation context
		if (contextBehavior instanceof FXSelectionBehavior) {
			return createSelectionHandleParts(targets, (FXSelectionBehavior) contextBehavior);
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
		
		IProvider<IGeometry> handleGeometryProvider = selectionBehavior.getHandleGeometryProvider();

		// generate handles from handle geometry
		IGeometry geom = handleGeometryProvider.get();
		List<IHandlePart<Node>> handleParts = new ArrayList<IHandlePart<Node>>();

		if (geom instanceof ICurve) {
			// generate handles for the end/join points of the individual
			// beziers
			BezierCurve[] beziers = ((ICurve) geom).toBezier();
			for (int i = 0; i < beziers.length; i++) {
				IHandlePart<Node> hp = new FXSelectionHandlePart(targetPart,
						handleGeometryProvider, i);
				handleParts.add(hp);
				// create handlepart for the curve's end point, too
				if (i == beziers.length - 1) {
					hp = new FXSelectionHandlePart(targetPart,
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
					IHandlePart<Node> hp = new FXSelectionHandlePart(
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

	public List<IHandlePart<Node>> createMultiSelectionHandleParts(
			List<IContentPart<Node>> targets,
			FXSelectionBehavior selectionBehavior) {
		List<IHandlePart<Node>> handleParts = new ArrayList<IHandlePart<Node>>();
		
		FXBoxHandlePart handlePart = new FXBoxHandlePart(targets,
				Pos.TOP_LEFT);
		handlePart.installBound(IDragPolicy.class,
				new FXResizeRelocateSelectedOnHandleDragPolicy(
						ReferencePoint.TOP_LEFT));
		handleParts.add(handlePart);

		handlePart = new FXBoxHandlePart(targets, Pos.TOP_RIGHT);
		handlePart.installBound(IDragPolicy.class,
				new FXResizeRelocateSelectedOnHandleDragPolicy(
						ReferencePoint.TOP_RIGHT));
		handleParts.add(handlePart);

		handlePart = new FXBoxHandlePart(targets, Pos.BOTTOM_RIGHT);
		handlePart.installBound(IDragPolicy.class,
				new FXResizeRelocateSelectedOnHandleDragPolicy(
						ReferencePoint.BOTTOM_RIGHT));
		handleParts.add(handlePart);

		handlePart = new FXBoxHandlePart(targets, Pos.BOTTOM_LEFT);
		handlePart.installBound(IDragPolicy.class,
				new FXResizeRelocateSelectedOnHandleDragPolicy(
						ReferencePoint.BOTTOM_LEFT));
		handleParts.add(handlePart);

		return handleParts;
	}

}
