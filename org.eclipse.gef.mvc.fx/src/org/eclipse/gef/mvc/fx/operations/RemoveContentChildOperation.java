/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.fx.operations;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.mvc.fx.parts.IContentPart;

import com.google.common.collect.ImmutableList;

import javafx.scene.Node;

/**
 * The {@link RemoveContentChildOperation} uses the {@link IContentPart} API to
 * remove a content object from an {@link IContentPart}.
 *
 */
public class RemoveContentChildOperation extends AbstractOperation
		implements ITransactionalOperation {

	private final IContentPart<? extends Node> parent;
	private final Object contentChild;

	// capture initial content children (for no-op test)
	private List<Object> initialContentChildren;
	private int initialIndex;

	/**
	 * Creates a new {@link RemoveContentChildOperation} for removing the given
	 * <i>contentChild</i> {@link Object} from the content children of the given
	 * <i>parent</i> {@link IContentPart}.
	 *
	 * @param parent
	 *            The {@link IContentPart} from which a content child is to be
	 *            removed.
	 * @param contentChild
	 *            The content {@link Object} which is to be removed from the
	 *            content children of the <i>parent</i>.
	 */
	public RemoveContentChildOperation(IContentPart<? extends Node> parent,
			Object contentChild) {
		super("Remove Content Child");
		this.parent = parent;
		this.contentChild = contentChild;
		initialIndex = parent.getContentChildrenUnmodifiable()
				.indexOf(contentChild);
		this.initialContentChildren = ImmutableList
				.copyOf(parent.getContentChildrenUnmodifiable());
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// System.out.println("EXEC remove content " + contentChild + " from "
		// + parent + ".");
		if (parent.getContent() != null && parent
				.getContentChildrenUnmodifiable().contains(contentChild)) {
			parent.removeContentChild(contentChild);
		}
		return Status.OK_STATUS;
	}

	@Override
	public boolean isContentRelevant() {
		return true;
	}

	@Override
	public boolean isNoOp() {
		return !initialContentChildren.contains(contentChild);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return execute(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// System.out.println("UNDO remove content " + contentChild + " from "
		// + parent + ".");
		if (!parent.getContentChildrenUnmodifiable().contains(contentChild)) {
			parent.addContentChild(contentChild, initialIndex);
		}
		return Status.OK_STATUS;
	}

}