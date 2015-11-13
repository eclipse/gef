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
package org.eclipse.gef4.mvc.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * The {@link RemoveContentChildOperation} uses the {@link IContentPart} API to
 * remove a content object from an {@link IContentPart}.
 *
 * @param <VR>
 *            The visual root node of the UI toolkit, e.g. javafx.scene.Node in
 *            case of JavaFX.
 */
public class RemoveContentChildOperation<VR> extends AbstractOperation
		implements ITransactionalOperation {

	private final IContentPart<VR, ? extends VR> parent;
	private final Object contentChild;
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
	public RemoveContentChildOperation(IContentPart<VR, ? extends VR> parent,
			Object contentChild) {
		// TODO: expect initialIndex as in AddContentChildOperation
		super("Remove Content Child");
		this.parent = parent;
		this.contentChild = contentChild;
		initialIndex = parent.getContentChildren().indexOf(contentChild);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		// System.out.println("EXEC remove content " + contentChild + " from "
		// + parent + ".");
		parent.removeContentChild(contentChild);
		return Status.OK_STATUS;
	}

	@Override
	public boolean isNoOp() {
		// TODO: noop if child is not present (at that initialIndex)
		return false;
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
		parent.addContentChild(contentChild, initialIndex);
		return Status.OK_STATUS;
	}

}