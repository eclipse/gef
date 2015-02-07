/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.models.ViewportModel;
import org.eclipse.gef4.mvc.operations.SynchronizeContentChildrenOperation;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

// only applicable for NodeContentPart (see #getHost())
public class SynchronizeChildrenOnZoomBehavior extends AbstractBehavior<Node> {

	private PropertyChangeListener viewportPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ViewportModel.VIEWPORT_CONTENTS_TRANSFORM_PROPERTY.equals(evt.getPropertyName())) {
				AffineTransform oldTransform = (AffineTransform) evt.getOldValue();
				AffineTransform newTransform = (AffineTransform) evt.getNewValue();
				double oldScale = oldTransform.getScaleX();
				double newScale = newTransform.getScaleX();
				if (oldScale != newScale) {
					onZoomLevelChange(oldScale, newScale);
				}
			}
		}
	};

	@Override
	public void activate() {
		super.activate();
		ViewportModel viewportModel = getHost().getRoot().getViewer().getAdapter(ViewportModel.class);
		viewportModel.addPropertyChangeListener(viewportPropertyChangeListener);
	}

	@Override
	public void deactivate() {
		ViewportModel viewportModel = getHost().getRoot().getViewer().getAdapter(ViewportModel.class);
		viewportModel.removePropertyChangeListener(viewportPropertyChangeListener);
		super.deactivate();
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	protected void onZoomLevelChange(double oldScale, double newScale) {
		/*
		 * The PropertyChangeEvent could be processed already by another
		 * SynchronizeChildrenOnZoomBehavior which could have deactivated our
		 * host, in which case we should not do anything.
		 */
		if (!isActive()) {
			// TODO: Enhance property change mechanism to allow for easier
			// decoupling of behaviors. For example, an event could be consumed
			// to not notify any more listeners.
			return;
		}
		// execute synchronization locally (so it does not affect the undo
		// history)
		try {
			new SynchronizeContentChildrenOperation<Node>("SyncOnZoom", getHost()).execute(new NullProgressMonitor(),
					null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
