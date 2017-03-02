/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzilla #481810
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IHandlePart;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link HoverOnHoverPolicy} is an {@link IOnHoverPolicy} that hovers its
 * {@link #getHost() host} by altering the {@link HoverModel} when the
 * {@link #getHost() host} is hovered by the mouse.
 *
 * @author anyssen
 *
 */
public class HoverOnHoverPolicy extends AbstractInteractionPolicy
		implements IOnHoverPolicy {

	/**
	 * Returns the {@link HoverModel} that is manipulated by this
	 * {@link HoverOnHoverPolicy}.
	 *
	 * @return The {@link HoverModel} of the host's viewer.
	 */
	protected HoverModel getHoverModel() {
		return getHost().getViewer().getAdapter(HoverModel.class);
	}

	@Override
	public void hover(MouseEvent e) {
		// do nothing in case there is an explicit event target
		if (!isHover(e)) {
			return;
		}

		getHoverModel().setHover(getHost());
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * hover. Otherwise returns <code>false</code>. Per default, returns
	 * <code>true</code> if the mouse target is not registered in the visual
	 * part map or it is registered for this {@link HoverOnHoverPolicy}'s host.
	 *
	 * @param event
	 *            The {@link MouseEvent} in question.
	 * @return <code>true</code> to indicate that the given {@link MouseEvent}
	 *         should trigger hover, otherwise <code>false</code>.
	 */
	protected boolean isHover(MouseEvent event) {
		return !isRegistered(event.getTarget())
				|| isRegisteredForHost(event.getTarget());
	}

	@Override
	public void lingeringHover(Node lingeringHover) {
		if (lingeringHover != null && isRegistered(lingeringHover)
				&& !isRegisteredForHost(lingeringHover)) {
			return;
		}

		HoverModel hoverModel = getHoverModel();
		if (lingeringHover == null) {
			hoverModel.setLingeringHover(null);
		} else {
			if (getHost() instanceof IHandlePart) {
				if (!getHost().getAnchoragesUnmodifiable()
						.containsKey(hoverModel.getLingeringHover())) {
					hoverModel.setLingeringHover(null);
				}
			} else if (getHost() instanceof IContentPart) {
				hoverModel.setLingeringHover(
						(IContentPart<? extends Node>) getHost());
			} else {
				hoverModel.setLingeringHover(null);
			}
		}
	}
}
