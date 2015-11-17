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
package org.eclipse.gef4.mvc.examples.logo.policies;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.policies.FXTransformPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXTranslateSelectedOnDragPolicy;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.policies.CreationPolicy;

import com.google.common.collect.HashMultimap;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class FXCloneRelocateOnDragPolicy
		extends FXTranslateSelectedOnDragPolicy {

	class CopyTransformOperation extends AbstractOperation
			implements ITransactionalOperation {
		private final Object targetContent;
		private ITransactionalOperation operation; // generated on first execute

		public CopyTransformOperation(Object targetContent) {
			super("CopyTransform");
			this.targetContent = targetContent;
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException {
			// dynamically generate copy operation
			if (operation == null) {
				IContentPart<Node, ? extends Node> cloneContentPart = getHost()
						.getRoot().getViewer().getContentPartMap()
						.get(targetContent);
				// copy original transform over to new content part
				FXTransformPolicy cloneTransformPolicy = cloneContentPart
						.getAdapter(FXTransformPolicy.class);
				cloneTransformPolicy.init();
				cloneTransformPolicy.setTransform(originalTransform);
				operation = cloneTransformPolicy.commit();
			}
			// maybe no clone/transformation has to be performed
			if (operation == null) {
				return Status.OK_STATUS;
			}
			return operation.execute(null, null);
		}

		@Override
		public boolean isNoOp() {
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
			if (operation == null) {
				// in case no clone/transformation was performed
				return Status.OK_STATUS;
			}
			return operation.undo(monitor, info);
		}
	}

	private boolean isClone = true;
	private AffineTransform originalTransform;
	private ReverseUndoCompositeOperation cloneOperations;

	private void createClone() {
		cloneOperations = new ReverseUndoCompositeOperation("Clone");

		// clone content
		Object cloneContent = getHost()
				.getAdapter(AbstractCloneContentPolicy.class).cloneContent();

		// build create operation
		IRootPart<Node, ? extends Node> root = getHost().getRoot();
		CreationPolicy<Node> creationPolicy = root
				.<CreationPolicy<Node>> getAdapter(CreationPolicy.class);
		creationPolicy.init();
		creationPolicy.create(cloneContent,
				(IContentPart<Node, ? extends Node>) getHost().getParent(),
				HashMultimap
						.<IContentPart<Node, ? extends Node>, String> create());
		cloneOperations.add(creationPolicy.commit());

		// build operation to copy the transformation
		cloneOperations.add(new CopyTransformOperation(cloneContent));
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		boolean wasClone = isClone;
		isClone = isCloneModifierDown(e);
		if (!wasClone && isClone) {
			createClone();
			// execute locally
			try {
				cloneOperations.execute(null, null);
			} catch (ExecutionException x) {
				x.printStackTrace();
			}
		} else if (wasClone && !isClone) {
			removeClone();
		}
		super.drag(e, delta);
	}

	protected boolean isCloneModifierDown(MouseEvent e) {
		return e.isAltDown() || e.isShiftDown();
	}

	@Override
	public void press(MouseEvent e) {
		originalTransform = getHost()
				.<FXTransformPolicy> getAdapter(FXTransformPolicy.class)
				.getCurrentNodeTransform();
		isClone = false;
		super.press(e);
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		if (isCloneModifierDown(e)) {
			removeClone();
			createClone();
			// execute on stack
			getHost().getRoot().getViewer().getDomain()
					.execute(cloneOperations);
		}
		super.release(e, delta);
	}

	private void removeClone() {
		if (cloneOperations == null) {
			return;
		}
		try {
			cloneOperations.undo(null, null);
			cloneOperations = null;
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

}
