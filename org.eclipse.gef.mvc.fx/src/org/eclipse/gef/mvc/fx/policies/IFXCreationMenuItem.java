/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IRootPart;

import javafx.scene.Node;

/**
 * An {@link IFXCreationMenuItem} can be displayed by an
 * {@link FXCreationMenuOnClickPolicy}.
 *
 * @author wienand
 *
 */
public interface IFXCreationMenuItem {

	/**
	 * Returns a newly created content element that is added to the viewer when
	 * this menu item is selected.
	 *
	 * @return The content element that is created when this menu item is
	 *         selected.
	 */
	public Object createContent();

	/**
	 * Returns the visual for this menu item.
	 *
	 * @return The visual for this menu item.
	 */
	public Node createVisual();

	/**
	 * Returns the {@link IContentPart} that will serve as the parent for the
	 * newly created content.
	 *
	 * @param rootPart
	 *            The {@link IRootPart} in which to find a suitable content
	 *            parent.
	 * @return The {@link IContentPart} that will serve as the parent for the
	 *         newly created content.
	 */
	public IContentPart<Node, ? extends Node> findContentParent(
			IRootPart<Node, ? extends Node> rootPart);

}
