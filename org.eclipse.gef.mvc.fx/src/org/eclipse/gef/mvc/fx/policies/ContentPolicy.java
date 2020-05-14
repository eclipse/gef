/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.mvc.fx.operations.AbstractCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.AddContentChildOperation;
import org.eclipse.gef.mvc.fx.operations.AttachToContentAnchorageOperation;
import org.eclipse.gef.mvc.fx.operations.DetachFromContentAnchorageOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.operations.RemoveContentChildOperation;
import org.eclipse.gef.mvc.fx.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import javafx.scene.Node;

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
 * {@link IContentPart#removeContentChild(Object)}</li>
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
 */
public class ContentPolicy extends AbstractPolicy {

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
		checkInitialized();
		getCompositeOperation().add(
				new AddContentChildOperation(getHost(), contentChild, index));
		locallyExecuteOperation();
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
		checkInitialized();
		getCompositeOperation().add(new AttachToContentAnchorageOperation(
				getHost(), contentAnchorage, role));
		locallyExecuteOperation();
	}

	@Override
	protected ITransactionalOperation createOperation() {
		return new ReverseUndoCompositeOperation("Content Change");
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
		checkInitialized();
		getCompositeOperation().add(new DetachFromContentAnchorageOperation(
				getHost(), contentAnchorage, role));
		locallyExecuteOperation();
	}

	/**
	 * Extracts a {@link AbstractCompositeOperation} from the operation created
	 * by {@link #createOperation()}. The composite operation is used to combine
	 * individual content change operations.
	 *
	 * @return The {@link AbstractCompositeOperation} that is used to combine
	 *         the individual content change operations.
	 */
	protected AbstractCompositeOperation getCompositeOperation() {
		return (AbstractCompositeOperation) getOperation();
	}

	@Override
	public IContentPart<? extends Node> getHost() {
		return (IContentPart<? extends Node>) super.getHost();
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
		checkInitialized();
		getCompositeOperation()
				.add(new RemoveContentChildOperation(getHost(), contentChild));
		locallyExecuteOperation();
	}

	@Override
	public void setAdaptable(IVisualPart<? extends Node> adaptable) {
		if (adaptable != null && !(adaptable instanceof IContentPart)) {
			throw new IllegalStateException(
					"A ContentPolicy may only be attached to an IContentPart.");
		}
		super.setAdaptable(adaptable);
	}
}
