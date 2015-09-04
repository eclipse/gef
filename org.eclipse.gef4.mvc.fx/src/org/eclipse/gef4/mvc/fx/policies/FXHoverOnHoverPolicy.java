/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.gef4.mvc.models.HoverModel;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FXHoverOnHoverPolicy} is an {@link AbstractFXOnHoverPolicy} that
 * hovers its {@link #getHost() host} by altering the {@link HoverModel} when
 * the {@link #getHost() host} is hovered by the mouse.
 *
 * @author anyssen
 *
 */
public class FXHoverOnHoverPolicy extends AbstractFXOnHoverPolicy {

	@Override
	public void hover(MouseEvent e) {
		getHost().getRoot().getViewer()
				.<HoverModel<Node>> getAdapter(HoverModel.class)
				.setHover(getHost());
	}

}
