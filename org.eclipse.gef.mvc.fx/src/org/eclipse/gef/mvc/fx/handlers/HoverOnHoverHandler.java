/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contributions for Bugzilla #481810
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IHandlePart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link HoverOnHoverHandler} is an {@link IOnHoverHandler} that hovers its
 * {@link #getHost() host} by altering the {@link HoverModel} when the
 * {@link #getHost() host} is hovered by the mouse.
 *
 * @author anyssen
 *
 */
public class HoverOnHoverHandler extends AbstractHandler
		implements IOnHoverHandler {

	/**
	 * Returns the {@link HoverModel} that is manipulated by this
	 * {@link HoverOnHoverHandler}.
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

	@Override
	public void hoverIntent(Node hoverIntent) {
		HoverModel hoverModel = getHoverModel();
		if (!isRegistered(hoverIntent) && getHost() instanceof IRootPart) {
			hoverModel.setHoverIntent(null);
		} else if (isRegisteredForHost(hoverIntent)) {
			if (getHost() instanceof IHandlePart) {
				if (!getHost().getAnchoragesUnmodifiable()
						.containsKey(hoverModel.getHoverIntent())) {
					hoverModel.setHoverIntent(null);
				}
			} else if (getHost() instanceof IContentPart) {
				hoverModel.setHoverIntent(
						(IContentPart<? extends Node>) getHost());
			} else if (getHost() instanceof IRootPart) {
				hoverModel.setHoverIntent(null);
			}
		}
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * hover. Otherwise returns <code>false</code>. Per default, returns
	 * <code>true</code> if the mouse target is not registered in the visual
	 * part map or it is registered for this {@link HoverOnHoverHandler}'s host.
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
}
