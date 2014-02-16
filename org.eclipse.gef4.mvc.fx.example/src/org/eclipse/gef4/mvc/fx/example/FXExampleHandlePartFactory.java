/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.fx.parts.FXBoxHandlePart;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.policies.AbstractResizeRelocateSelectedOnHandleDragPolicy.ReferencePoint;
import org.eclipse.gef4.mvc.policies.IDragPolicy;

public class FXExampleHandlePartFactory implements IHandlePartFactory<Node> {

	@Override
	public List<IHandlePart<Node>> createSelectionHandleParts(
			List<IContentPart<Node>> selection) {
		if (selection.isEmpty())
			return Collections.emptyList();

		List<IHandlePart<Node>> handleParts = new ArrayList<IHandlePart<Node>>();

		IContentPart<Node> contentPart = selection.get(0);
		if (contentPart instanceof FXGeometricCurvePart) {
		} else {
			FXBoxHandlePart handlePart = new FXBoxHandlePart(selection,
					Pos.TOP_LEFT);
			handlePart.installBound(IDragPolicy.class,
					new FXResizeRelocateSelectedOnHandleDragPolicy(
							ReferencePoint.TOP_LEFT));
			handleParts.add(handlePart);

			handlePart = new FXBoxHandlePart(selection, Pos.TOP_RIGHT);
			handlePart.installBound(IDragPolicy.class,
					new FXResizeRelocateSelectedOnHandleDragPolicy(
							ReferencePoint.TOP_RIGHT));
			handleParts.add(handlePart);

			handlePart = new FXBoxHandlePart(selection, Pos.BOTTOM_RIGHT);
			handlePart.installBound(IDragPolicy.class,
					new FXResizeRelocateSelectedOnHandleDragPolicy(
							ReferencePoint.BOTTOM_RIGHT));
			handleParts.add(handlePart);

			handlePart = new FXBoxHandlePart(selection, Pos.BOTTOM_LEFT);
			handlePart.installBound(IDragPolicy.class,
					new FXResizeRelocateSelectedOnHandleDragPolicy(
							ReferencePoint.BOTTOM_LEFT));
			handleParts.add(handlePart);
		}

		return handleParts;
	}

	@Override
	public List<IHandlePart<Node>> createFocusHandleParts(
			IContentPart<Node> focused) {
		return Collections.emptyList();
	}

	@Override
	public List<IHandlePart<Node>> createHoverHandleParts(
			IContentPart<Node> hovered) {
		return Collections.emptyList();
	}

}
