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

/**
 * An {@link AbstractDeleteContentChildrenPolicy} provides an
 * {@link IUndoableOperation} for the deletion of the content of one of its
 * adaptee's children.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractDeleteContentChildrenPolicy<VR> extends
		AbstractPolicy<VR> {

	/**
	 * Constructs an {@link IUndoableOperation} for the deletion of the child's
	 * content from the adaptee's content children, or <code>null</code> if no
	 * actions have to be performed.
	 * 
	 * @param child
	 *            {@link IContentPart} child of the adaptee.
	 * @return An {@link IUndoableOperation} for the deletion of the child's
	 *         content from the adaptee's content children, or <code>null</code>
	 *         if no actions have to be performed.
	 * 
	 * @see IContentPart#getContentChildren()
	 */
	public abstract IUndoableOperation getDeleteOperation(IContentPart<VR> child);

	// TODO: deleting multiple parts at one go could be an extra operation
	// public abstract IUndoableOperation getDeleteOperation(
	// Set<IContentPart<Node>> children);

}
