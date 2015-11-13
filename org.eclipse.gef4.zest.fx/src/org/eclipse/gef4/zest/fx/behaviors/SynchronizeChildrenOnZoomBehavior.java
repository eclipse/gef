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

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * The {@link SynchronizeChildrenOnZoomBehavior} starts a content
 * synchronization for the {@link NodeContentPart} on which it is installed when
 * the zoom level is changed. This enables the {@link NodeContentPart} to report
 * a nested {@link Graph} as a child depending on the zoom level (see
 * {@link NodeContentPart#getContentChildren()}).
 *
 * @author mwienand
 *
 */
// only applicable for NodeContentPart (see #getHost())
public class SynchronizeChildrenOnZoomBehavior extends AbstractBehavior<Node> {

	private ChangeListener<? super Number> scaleXListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			onZoomLevelChange(oldValue.doubleValue(), newValue.doubleValue());
		}
	};

	@Override
	public void activate() {
		super.activate();
		((FXViewer) getHost().getRoot().getViewer()).getCanvas().getContentTransform().mxxProperty()
				.addListener(scaleXListener);
	}

	@Override
	public void deactivate() {
		((FXViewer) getHost().getRoot().getViewer()).getCanvas().getContentTransform().mxxProperty()
				.removeListener(scaleXListener);
		super.deactivate();
	}

	@Override
	public NodeContentPart getHost() {
		return (NodeContentPart) super.getHost();
	}

	/**
	 * Called upon zoom level changes and synchronizes content children for its
	 * host.
	 *
	 * @param oldScale
	 *            The old zoom level.
	 * @param newScale
	 *            The new zoom level.
	 */
	@SuppressWarnings("unchecked")
	protected void onZoomLevelChange(double oldScale, double newScale) {
		/*
		 * Another behavior could have deactivated our host in response to the
		 * scale change, in which case we should not do anything.
		 */
		if (isActive()) {
			getHost().getAdapter(ContentBehavior.class).synchronizeContentChildren(getHost().getContentChildren());
		}
	}

}
