/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
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
package org.eclipse.gef.mvc.examples.logo.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnClickHandler;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.operations.DeselectOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.ITransformableContentPart;
import org.eclipse.gef.mvc.fx.policies.CreationPolicy;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.HashMultimap;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

public class CloneOnClickHandler extends AbstractHandler implements IOnClickHandler {

	@Override
	public void click(MouseEvent event) {
		if (!isCloneModifierDown(event)) {
			return;
		}

		// clone content
		Object cloneContent = getHost().getAdapter(AbstractCloneContentSupport.class).cloneContent();

		// create the clone content part
		IRootPart<? extends Node> root = getHost().getRoot();
		CreationPolicy creationPolicy = root.getAdapter(CreationPolicy.class);
		init(creationPolicy);
		IContentPart<? extends Node> clonedContentPart = creationPolicy.create(cloneContent, getHost().getParent(),
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
		if (getHost() instanceof ITransformableContentPart) {
			@SuppressWarnings("unchecked")
			ITransformableContentPart<Node> transformableContentPart = (ITransformableContentPart<Node>) getHost();
			Affine originalTransform = transformableContentPart.getVisualTransform();
			TransformPolicy transformPolicy = clonedContentPart.getAdapter(TransformPolicy.class);
			init(transformPolicy);
			transformPolicy.setTransform(FX2Geometry.toAffineTransform(originalTransform));
			commit(transformPolicy);
		}
	}

	protected boolean isCloneModifierDown(MouseEvent e) {
		return e.isAltDown() || e.isShiftDown();
	}
}
