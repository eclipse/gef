/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * An {@link IDetachContentAnchoragesPolicy} controls the deletion of a specific
 * content anchorage from the adaptee's content anchorages.
 *
 * @author mwienand
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 *
 */
public interface IDetachContentAnchoragesPolicy<VR> extends IPolicy<VR> {

	/**
	 * Returns an {@link IUndoableOperation} for the deletion of the specified
	 * anchorage (with role) from the adaptee's content anchorages, or
	 * <code>null</code> if no actions have to be performed.
	 *
	 * @param anchorage
	 *            The anchorage {@link IContentPart} to remove.
	 * @param role
	 *            The corresponding role for the anchorage to remove.
	 * @return An {@link IUndoableOperation} for the deletion of the specified
	 *         anchorage (with role) from the adaptee's content anchorages, or
	 *         <code>null</code> if no actions have to be performed.
	 */
	public abstract IUndoableOperation getDeleteOperation(
			IContentPart<VR> anchorage, String role);

	// TODO: deleting an anchorage for all roles could be an extra operation
	// public abstract IUndoableOperation
	// getDeleteAllOperation(IContentPart<Node>
	// anchorage);

}
