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

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.policies.IDragPolicy;
import org.eclipse.gef4.mvc.policies.AbstractResizeRelocateSelectedOnHandleDragPolicy.ReferencePoint;

public class FXExampleHandlePartFactory extends FXDefaultHandlePartFactory {

	@Override
	public IHandlePart<Node> createMultiSelectionCornerHandlePart(
			List<IContentPart<Node>> targets, Pos position) {
		IHandlePart<Node> part = super.createMultiSelectionCornerHandlePart(
				targets, position);
		part.installBound(IDragPolicy.class,
				new FXResizeRelocateSelectedOnHandleDragPolicy(
						toReferencePoint(position)));
		return part;
	}

	private ReferencePoint toReferencePoint(Pos position) {
		switch (position) {
		case TOP_LEFT:
			return ReferencePoint.TOP_LEFT;
		case TOP_RIGHT:
			return ReferencePoint.TOP_RIGHT;
		case BOTTOM_LEFT:
			return ReferencePoint.BOTTOM_LEFT;
		case BOTTOM_RIGHT:
			return ReferencePoint.BOTTOM_RIGHT;
		default:
			throw new IllegalStateException(
					"Unknown Pos: <"
							+ position
							+ ">. Expected any of: TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT.");
		}
	}

}
