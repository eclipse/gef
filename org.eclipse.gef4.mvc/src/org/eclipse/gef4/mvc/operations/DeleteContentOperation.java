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
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

public class DeleteContentOperation<VR> extends ReverseUndoCompositeOperation {

	protected final IContentPart<VR> toDelete;
	protected final IViewer<VR> viewer;

	public DeleteContentOperation(IViewer<VR> viewer, IContentPart<VR> toDelete) {
		super("Delete()");
		this.viewer = viewer;
		this.toDelete = toDelete;
		add(new ClearHoverFocusSelectionOperation<VR>(viewer));
		add(getContentOperations(toDelete));
	}

	protected IUndoableOperation getContentOperations(
			final IContentPart<VR> toDelete) {
		// first detach then remove, and first add then attach => reverse ops
		ReverseUndoCompositeOperation revOp = new ReverseUndoCompositeOperation(
				"DeleteContent");

		// assemble content operations in forward-undo-operations, so that
		// synchronization is always performed after changing the content
		// model (in execute() and undo())
		revOp.add(new ForwardUndoCompositeOperation("detachAnchorages()") {
			private void buildDetachOperations() {
				for (IVisualPart<VR> anchored : toDelete.getAnchoreds()) {
					if (!(anchored instanceof IContentPart)) {
						continue;
					}
					IContentPart<VR> cp = (IContentPart<VR>) anchored;
					for (String role : cp.getAnchorages().get(toDelete)) {
						add(new DetachFromContentAnchorageOperation<VR>(cp,
								toDelete.getContent(), role));
					}
					// synchronize content anchorages once per anchored
					add(new SynchronizeContentAnchoragesOperation<VR>(
							"SynchronizeAnchorages",
							(IContentPart<VR>) anchored));
				}
			}

			@Override
			public IStatus execute(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				operations.clear();
				buildDetachOperations();
				return super.execute(monitor, info);
			}

			@Override
			public IStatus undo(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException {
				operations.clear();
				buildDetachOperations();
				return super.undo(monitor, info);
			}
		});

		// delete from content children
		IContentPart<VR> parent = (IContentPart<VR>) toDelete.getParent();
		ForwardUndoCompositeOperation fwd = new ForwardUndoCompositeOperation(
				"deleteContent()");
		fwd.add(new RemoveContentChildOperation<VR>(parent, toDelete
				.getContent()));
		fwd.add(new SynchronizeContentChildrenOperation<VR>(
				"SynchronizeChildren", parent));
		revOp.add(fwd);

		return revOp;
	}

}
