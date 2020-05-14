/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.examples.logo.parts;

import java.util.Set;

import org.eclipse.gef.common.collections.SetMultimapChangeListener;
import org.eclipse.gef.mvc.fx.parts.AbstractHandlePart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;

public abstract class AbstractLogoHoverHandlePart<T extends Node> extends AbstractHandlePart<T> {

	private boolean registered = false;
	private final SetMultimapChangeListener<IVisualPart<? extends Node>, String> parentAnchoragesChangeListener = new SetMultimapChangeListener<IVisualPart<? extends Node>, String>() {

		private IViewer getViewer(Set<? extends IVisualPart<? extends Node>> anchorages) {
			for (IVisualPart<? extends Node> anchorage : anchorages) {
				if (anchorage.getRoot() != null && anchorage.getRoot().getViewer() != null) {
					return anchorage.getRoot().getViewer();
				}
			}
			return null;
		}

		@Override
		public void onChanged(
				org.eclipse.gef.common.collections.SetMultimapChangeListener.Change<? extends IVisualPart<? extends Node>, ? extends String> change) {
			IViewer oldViewer = getViewer(change.getPreviousContents().keySet());
			IViewer newViewer = getViewer(change.getSetMultimap().keySet());
			if (registered && oldViewer != null && oldViewer != newViewer) {
				oldViewer.unsetAdapter(AbstractLogoHoverHandlePart.this);
			}
			if (!registered && newViewer != null && oldViewer != newViewer) {
				newViewer.setAdapter(AbstractLogoHoverHandlePart.this,
						String.valueOf(System.identityHashCode(AbstractLogoHoverHandlePart.this)));
			}
		}
	};

	@Override
	protected void doRefreshVisual(T visual) {
		// automatically layed out by its parent
	}

	@Override
	protected void register(IViewer viewer) {
		if (registered) {
			return;
		}
		super.register(viewer);
		registered = true;
	}

	@Override
	public void setParent(IVisualPart<? extends Node> newParent) {
		if (getParent() != null) {
			getParent().getAnchoragesUnmodifiable().removeListener(parentAnchoragesChangeListener);
		}
		if (newParent != null) {
			newParent.getAnchoragesUnmodifiable().addListener(parentAnchoragesChangeListener);
		}
		super.setParent(newParent);
	}

	@Override
	protected void unregister(IViewer viewer) {
		if (!registered) {
			return;
		}
		super.unregister(viewer);
		registered = false;
	}

}
