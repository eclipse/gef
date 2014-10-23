/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.layout.GraphNodeLayout;
import org.eclipse.gef4.zest.fx.policies.NodeLayoutPolicy;

public class NodeLayoutBehavior extends AbstractLayoutBehavior {

	public static Class<NodeLayoutPolicy> LAYOUT_POLICY_KEY = NodeLayoutPolicy.class;

	protected GraphNodeLayout nodeLayout;
	private ChangeListener<Boolean> visibilityChangeListener = new ChangeListener<Boolean>() {
		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			onVisibilityChanged(oldValue, newValue);
		}
	};

	public NodeLayoutBehavior() {
	}

	@Override
	public void activate() {
		super.activate();
		getHost().getVisual().visibleProperty()
				.addListener(visibilityChangeListener);
	}

	@Override
	public void deactivate() {
		super.deactivate();
		getHost().getVisual().visibleProperty()
				.removeListener(visibilityChangeListener);
	}

	@Override
	protected void initializeLayout(GraphLayoutContext glc) {
		// find node layout
		nodeLayout = glc
				.getNodeLayout((org.eclipse.gef4.graph.Node) ((IContentPart<Node>) getHost())
						.getContent());
		getHost().refreshVisual();
		// initialize layout information
		getHost().getAdapter(LAYOUT_POLICY_KEY).provideLayoutInformation(
				nodeLayout);
	}

	@Override
	protected void onBoundsChange(Bounds oldBounds, Bounds newBounds) {
		if (nodeLayout != null) {
			// refresh layout information
			getHost().getAdapter(LAYOUT_POLICY_KEY).provideLayoutInformation(
					nodeLayout);
		}
	}

	@Override
	protected void onFlushChanges() {
		if (nodeLayout != null) {
			getHost().getAdapter(LAYOUT_POLICY_KEY).adaptLayoutInformation(
					nodeLayout);
			getHost().refreshVisual();
		}
	}

	protected void onVisibilityChanged(Boolean oldValue, Boolean newValue) {
		if (nodeLayout != null) {
			getHost().getAdapter(LAYOUT_POLICY_KEY).provideLayoutInformation(
					nodeLayout);
		}
	}

}
