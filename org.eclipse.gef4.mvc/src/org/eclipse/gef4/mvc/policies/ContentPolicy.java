/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.AddContentChildOperation;
import org.eclipse.gef4.mvc.operations.AttachToContentAnchorageOperation;
import org.eclipse.gef4.mvc.operations.DetachFromContentAnchorageOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.RemoveContentChildOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * A (transaction) policy to handle content changes, i.e. adding/removing of
 * content children as well as attaching/detaching to/from content anchorages.
 * All changes are wrapped into {@link ITransactionalOperation}s that delegate
 * to respective operations of the host {@link IContentPart} upon execution.
 * <p>
 * In detail, the following delegations are performed to operations of the host
 * {@link IContentPart}:
 * <ul>
 * <li>{@link #addContentChild(Object, int)} will delegate through a
 * {@link AddContentChildOperation} to
 * {@link IContentPart#addContentChild(Object, int)}</li>
 * <li>{@link #removeContentChild(Object)} will delegate through a
 * {@link RemoveContentChildOperation} to
 * {@link IContentPart#removeContentChild(Object, int)}</li>
 * <li>{@link #attachToContentAnchorage(Object, String)} will delegate through a
 * {@link AttachToContentAnchorageOperation} to
 * {@link IContentPart#attachToContentAnchorage(Object, String)}</li>
 * <li>{@link #detachFromContentAnchorage(Object, String)} will delegate through
 * a {@link DetachFromContentAnchorageOperation} to
 * IContentPart#detachFromContentAnchorage(Object, String)}</li>
 * </ul>
 * <p>
 * This policy should be registered on each {@link IContentPart}.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
// TODO: unwrap composite operations no longer needed
public class ContentPolicy<VR> extends AbstractPolicy<VR>
		implements ITransactional {

	/**
	 * Stores the <i>initialized</i> flag for this policy, i.e.
	 * <code>true</code> after {@link #init()} was called, and
	 * <code>false</code> after {@link #commit()} was called, respectively.
	 */
	protected boolean initialized;

	private AbstractCompositeOperation commitOperation;

	/**
	 * Creates and records operations to add the given <i>contentChild</i> to
	 * the {@link #getHost() host} of this {@link ContentPolicy} at the
	 * specified <i>index</i>.
	 *
	 * @param contentChild
	 *            The content {@link Object} that is to be added to the
	 *            {@link #getHost() host} of this {@link ContentPolicy}.
	 * @param index
	 *            The index of the new content child.
	 */
	public void addContentChild(Object contentChild, int index) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		// IMPORTANT: Assemble content operations in a
		// ForwardUndoCompositeOperation, so content synchronization is
		// performed after having changed the content during undo as well.
		ForwardUndoCompositeOperation addOperation = new ForwardUndoCompositeOperation(
				"Add Content Child");
		addOperation.add(new AddContentChildOperation<VR>(getHost(),
				contentChild, index));
		commitOperation.add(addOperation);
	}

	/**
	 * Creates and records operations to attach the {@link #getHost() host} of
	 * this {@link ContentPolicy} to the specified <i>contentAnchorage</i> under
	 * the specified <i>role</i>.
	 *
	 * @param contentAnchorage
	 *            The content {@link Object} to which the {@link #getHost()
	 *            host} of this {@link ContentPolicy} is to be attached.
	 * @param role
	 *            The role for the attachment.
	 *
	 */
	public void attachToContentAnchorage(Object contentAnchorage, String role) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		// IMPORTANT: Assemble content operations in a
		// ForwardUndoCompositeOperation, so content synchronization is
		// performed after having changed the content during undo as well.
		ForwardUndoCompositeOperation attachOperation = new ForwardUndoCompositeOperation(
				"Attach To Content Anchorage");
		attachOperation.add(new AttachToContentAnchorageOperation<VR>(getHost(),
				contentAnchorage, role));
		commitOperation.add(attachOperation);
	}

	@Override
	public ITransactionalOperation commit() {
		// after commit, we need to be re-initialized
		initialized = false;

		if (commitOperation != null && !commitOperation.isNoOp()) {
			ITransactionalOperation commit = commitOperation.unwrap(true);
			commitOperation = null;
			return commit;
		}
		return null;
	}

	/**
	 * Creates and records operations to detach the {@link #getHost() host} of
	 * this {@link ContentPolicy} from the specified <i>contentAnchorage</i>
	 * under the specified <i>role</i>.
	 *
	 * @param contentAnchorage
	 *            The content {@link Object} from which the {@link #getHost()}
	 *            of this {@link ContentPolicy} is detached.
	 * @param role
	 *            The role under which the anchorage is detached.
	 */
	public void detachFromContentAnchorage(Object contentAnchorage,
			String role) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		// IMPORTANT: Assemble content operations in a
		// ForwardUndoCompositeOperation, so content synchronization is
		// performed after having changed the content during undo as well.
		ForwardUndoCompositeOperation detachOperation = new ForwardUndoCompositeOperation(
				"Detach From Content Anchorage");
		detachOperation.add(new DetachFromContentAnchorageOperation<VR>(
				getHost(), contentAnchorage, role));
		commitOperation.add(detachOperation);
	}

	@Override
	public IContentPart<VR, ? extends VR> getHost() {
		return (IContentPart<VR, ? extends VR>) super.getHost();
	}

	@Override
	public void init() {
		commitOperation = new ReverseUndoCompositeOperation("Content Change");
		initialized = true;
	}

	/**
	 * Creates and records operations to remove the given <i>contentChild</i>
	 * from the content children of the {@link #getHost() host} of this
	 * {@link ContentPolicy}.
	 *
	 * @param contentChild
	 *            The content {@link Object} that is removed from content
	 *            children of the {@link #getHost() host} of this
	 *            {@link ContentPolicy}.
	 */
	public void removeContentChild(Object contentChild) {
		// ensure we have been properly initialized
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		// IMPORTANT: Assemble content operations in a
		// ForwardUndoCompositeOperation, so content synchronization is
		// performed after having changed the content during undo as well.
		ForwardUndoCompositeOperation removeOperation = new ForwardUndoCompositeOperation(
				"Remove Content Child");
		removeOperation.add(
				new RemoveContentChildOperation<VR>(getHost(), contentChild));
		commitOperation.add(removeOperation);
	}

	@Override
	public void setAdaptable(IVisualPart<VR, ? extends VR> adaptable) {
		if (!(adaptable instanceof IContentPart)) {
			throw new IllegalStateException(
					"A ContentPolicy may only be attached to an IContentPart.");
		}
		super.setAdaptable(adaptable);
	}

}
