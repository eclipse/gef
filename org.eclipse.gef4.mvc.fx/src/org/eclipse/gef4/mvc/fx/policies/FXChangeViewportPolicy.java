/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.fx.operations.FXChangeViewportOperation;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public class FXChangeViewportPolicy extends AbstractPolicy<Node> implements
		ITransactional {

	private FXChangeViewportOperation zoomOperation;

	@Override
	public IUndoableOperation commit() {
		IUndoableOperation commit = zoomOperation;
		zoomOperation = null;
		return commit;
	}

	@Override
	public void init() {
		ViewportModel viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		zoomOperation = new FXChangeViewportOperation(viewportModel,
				viewportModel.getContentsTransform().getCopy());

	}

	public void zoomRelative(double relativeZoom, double sceneX, double sceneY) {
		ViewportModel viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		// compute transformation
		Point2D contentGroupPivot = ((FXViewer) getHost().getRoot().getViewer())
				.getScrollPane().getContentGroup().sceneToLocal(sceneX, sceneY);
		zoomOperation
				.concatenateToNewTransform(new AffineTransform()
						.translate(contentGroupPivot.getX(),
								contentGroupPivot.getY())
						.scale(relativeZoom, relativeZoom)
						.translate(-contentGroupPivot.getX(),
								-contentGroupPivot.getY()));

		// locally execute operation
		try {
			zoomOperation.execute(null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
