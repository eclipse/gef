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
package org.eclipse.gef.mvc.examples.logo.parts;

import java.util.Set;

import org.eclipse.gef.common.collections.SetMultimapChangeListener;
import org.eclipse.gef.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.viewer.IViewer;

import javafx.scene.Node;

public abstract class AbstractLogoHoverHandlePart<T extends Node> extends AbstractFXHandlePart<T> {

	private boolean registered = false;
	private final SetMultimapChangeListener<IVisualPart<Node, ? extends Node>, String> parentAnchoragesChangeListener = new SetMultimapChangeListener<IVisualPart<Node, ? extends Node>, String>() {

		private IViewer<Node> getViewer(Set<? extends IVisualPart<Node, ? extends Node>> anchorages) {
			for (IVisualPart<Node, ? extends Node> anchorage : anchorages) {
				if (anchorage.getRoot() != null && anchorage.getRoot().getViewer() != null) {
					return anchorage.getRoot().getViewer();
				}
			}
			return null;
		}

		@Override
		public void onChanged(
				org.eclipse.gef.common.collections.SetMultimapChangeListener.Change<? extends IVisualPart<Node, ? extends Node>, ? extends String> change) {
			IViewer<Node> oldViewer = getViewer(change.getPreviousContents().keySet());
			IViewer<Node> newViewer = getViewer(change.getSetMultimap().keySet());
			if (registered && oldViewer != null && oldViewer != newViewer) {
				unregister(oldViewer);
			}
			if (!registered && newViewer != null && oldViewer != newViewer) {
				register(newViewer);
			}
		}
	};

	@Override
	protected void doRefreshVisual(T visual) {
		// automatically layed out by its parent
	}

	@Override
	protected void register(IViewer<Node> viewer) {
		if (registered) {
			return;
		}
		super.register(viewer);
		registered = true;
	}

	@Override
	public void setParent(IVisualPart<Node, ? extends Node> newParent) {
		if (getParent() != null) {
			getParent().getAnchoragesUnmodifiable().removeListener(parentAnchoragesChangeListener);
		}
		if (newParent != null) {
			newParent.getAnchoragesUnmodifiable().addListener(parentAnchoragesChangeListener);
		}
		super.setParent(newParent);
	}

	@Override
	protected void unregister(IViewer<Node> viewer) {
		if (!registered) {
			return;
		}
		super.unregister(viewer);
		registered = false;
	}

}
