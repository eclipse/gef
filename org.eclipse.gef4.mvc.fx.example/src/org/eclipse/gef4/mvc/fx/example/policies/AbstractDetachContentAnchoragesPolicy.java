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
package org.eclipse.gef4.mvc.fx.example.policies;

import javafx.scene.Node;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

/**
 * An {@link AbstractDetachContentAnchoragesPolicy} controls the deletion of a
 * specific content anchorage from the adaptee's content anchorages.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractDetachContentAnchoragesPolicy extends
		AbstractPolicy<Node> {

	/**
	 * Returns an {@link IUndoableOperation} for the deletion of the specified
	 * anchorage (with role) from the adaptee's content anchorages, or
	 * <code>null</code> if no actions have to be performed.
	 * 
	 * @return An {@link IUndoableOperation} for the deletion of the specified
	 *         anchorage (with role) from the adaptee's content anchorages, or
	 *         <code>null</code> if no actions have to be performed.
	 */
	public abstract IUndoableOperation getDeleteOperation(
			IContentPart<Node> anchorage, String role);

	// TODO: deleting an anchorage for all roles could be an extra operation
	// public abstract IUndoableOperation
	// getDeleteAllOperation(IContentPart<Node>
	// anchorage);

}
