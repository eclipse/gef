/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.operations.DeselectOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.AbstractInteractionPolicy;
import org.eclipse.gef.mvc.fx.policies.CreationPolicy;
import org.eclipse.gef.mvc.fx.policies.IOnClickPolicy;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.HashMultimap;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class FXCloneOnClickPolicy extends AbstractInteractionPolicy implements IOnClickPolicy {

	@Override
	public void click(MouseEvent event) {
		if (!isCloneModifierDown(event)) {
			return;
		}

		// clone content
		Object cloneContent = getHost().getAdapter(AbstractCloneContentPolicy.class).cloneContent();

		// create the clone content part
		IRootPart<? extends Node> root = getHost().getRoot();
		CreationPolicy creationPolicy = root.getAdapter(CreationPolicy.class);
		init(creationPolicy);
		IContentPart<? extends Node> clonedContentPart = creationPolicy.create(cloneContent,
				(IContentPart<? extends Node>) getHost().getParent(),
				HashMultimap.<IContentPart<? extends Node>, String> create());
		commit(creationPolicy);

		// XXX: Ensure start and end anchor are set for connections, so that
		// they can be interacted with (transform/bend).
		if (clonedContentPart.getVisual() instanceof Connection) {
			Connection connection = (Connection) clonedContentPart.getVisual();
			if (connection.getStartAnchor() == null) {
				connection.setStartPoint(((Connection) getHost().getVisual()).getStartPoint());
			}
			if (connection.getEndAnchor() == null) {
				connection.setEndPoint(((Connection) getHost().getVisual()).getEndPoint());
			}
		}

		// deselect all but the clone
		IViewer viewer = getHost().getRoot().getViewer();
		List<? extends IContentPart<? extends Node>> toBeDeselected = new ArrayList<>(
				viewer.getAdapter(SelectionModel.class).getSelectionUnmodifiable());
		toBeDeselected.remove(clonedContentPart);
		try {
			viewer.getDomain().execute(new DeselectOperation(getHost().getRoot().getViewer(), toBeDeselected),
					new NullProgressMonitor());
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}

		// copy the transformation
		AffineTransform originalTransform = FX2Geometry
				.toAffineTransform(getHost().getAdapter(IVisualPart.TRANSFORM_PROVIDER_KEY).get());
		TransformPolicy transformPolicy = clonedContentPart.getAdapter(TransformPolicy.class);
		init(transformPolicy);
		transformPolicy.setTransform(originalTransform);
		commit(transformPolicy);
	}

	protected boolean isCloneModifierDown(MouseEvent e) {
		return e.isAltDown() || e.isShiftDown();
	}

}
