/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.policies;

import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.ClearHoverFocusSelectionOperation;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.zest.fx.operations.HideOperation;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;

import javafx.scene.Node;

/**
 * The {@link HideNodePolicy} can be installed on {@link NodeContentPart}. It
 * provides two methods:
 * <ul>
 * <li>{@link #hide()} to hide its host {@link NodeContentPart}
 * <li>{@link #show()} to show its host {@link NodeContentPart}
 * </ul>
 *
 * @author mwienand
 *
 */
// TODO: only applicable for NodeContentPart (override #getHost)
public class HideNodePolicy extends AbstractPolicy<Node> {

	/**
	 * Executes an operation on the history that hides the {@link #getHost()
	 * host} {@link NodeContentPart} (and clears the {@link FocusModel},
	 * {@link HoverModel}, and {@link SelectionModel}).
	 */
	public void hide() {
		ClearHoverFocusSelectionOperation<Node> revOp = new ClearHoverFocusSelectionOperation<Node>(
				getHost().getRoot().getViewer());
		revOp.add(HideOperation.hide((NodeContentPart) getHost()));
		getHost().getRoot().getViewer().getDomain().execute(revOp);
	}

	/**
	 * Executes an operation on the history that shows the {@link #getHost()
	 * host} {@link NodeContentPart} (and clears the {@link FocusModel},
	 * {@link HoverModel}, and {@link SelectionModel}).
	 */
	public void show() {
		ClearHoverFocusSelectionOperation<Node> revOp = new ClearHoverFocusSelectionOperation<Node>(
				getHost().getRoot().getViewer());
		revOp.add(HideOperation.show((NodeContentPart) getHost()));
		getHost().getRoot().getViewer().getDomain().execute(revOp);
	}

}
