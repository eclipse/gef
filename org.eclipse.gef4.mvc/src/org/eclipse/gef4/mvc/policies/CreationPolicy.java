/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG)  - refactorings
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IRootPart;

import com.google.common.collect.SetMultimap;

/**
 * The {@link CreationPolicy} is an {@link ITransactional}
 * {@link AbstractPolicy} that handles the creation of content.
 * <p>
 * It handles the creation by initiating the adding of a content child to the
 * content parent via the {@link ContentPolicy} of the parent
 * {@link IContentPart}, as well as the attachment of anchored content elements
 * via the {@link ContentPolicy}s of anchored {@link IContentPart}s.
 * <p>
 * This policy should be registered at an {@link IRootPart}. It depends on
 * {@link ContentPolicy}s being registered on all {@link IContentPart}s that are
 * affected by the creation.
 *
 * @author mwienand
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public class CreationPolicy<VR> extends AbstractPolicy<VR>
		implements ITransactional {

	/**
	 * Stores the <i>initialized</i> flag for this policy, i.e.
	 * <code>true</code> after {@link #init()} was called, and
	 * <code>false</code> after {@link #commit()} was called, respectively.
	 */
	protected boolean initialized;
	private ReverseUndoCompositeOperation createOperation;

	@Override
	public ITransactionalOperation commit() {
		if (!initialized) {
			return null;
		}

		// after commit, we need to be re-initialized
		initialized = false;
		ITransactionalOperation commit = null;
		if (createOperation != null && !createOperation.isNoOp()) {
			commit = createOperation.unwrap(true);
		}
		return commit;
	}

	/**
	 * Adds the given <i>content</i> to the collection of to-be-created contents
	 * in the specified <i>parent</i>.
	 *
	 * @param content
	 *            The content {@link Object} to be created.
	 * @param parent
	 *            The {@link IContentPart} where the <i>content</i> is added as
	 *            a child.
	 *
	 * @param anchoreds
	 *            The {@link IContentPart} whose content should be attached to
	 *            the new content under the given roles.
	 */
	public void create(Object content, IContentPart<VR, ? extends VR> parent,
			SetMultimap<IContentPart<VR, ? extends VR>, String> anchoreds) {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		if (content == null) {
			throw new IllegalArgumentException(
					"The given content may not be null.");
		}
		if (parent == null) {
			throw new IllegalArgumentException(
					"The given parent may not be null.");
		}
		if (anchoreds == null) {
			throw new IllegalArgumentException(
					"The given anchored parts may not be null");
		}

		// add to parent via content policy
		ContentPolicy<VR> parentContentPolicy = parent
				.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
		if (parentContentPolicy == null) {
			throw new IllegalStateException(
					"No ContentPolicy registered for <" + parent + ">.");
		}
		parentContentPolicy.init();
		parentContentPolicy.addContentChild(content,
				parent.getContentChildren().size());
		ITransactionalOperation addToParentOperation = parentContentPolicy
				.commit();
		if (addToParentOperation != null && !addToParentOperation.isNoOp()) {
			createOperation.add(addToParentOperation);
		}

		// add anchoreds via content policy
		for (IContentPart<VR, ? extends VR> anchored : anchoreds.keys()) {
			ContentPolicy<VR> anchoredPolicy = anchored
					.<ContentPolicy<VR>> getAdapter(ContentPolicy.class);
			if (anchoredPolicy == null) {
				throw new IllegalStateException(
						"No ContentPolicy registered for <" + anchored + ">.");
			}
			anchoredPolicy.init();
			for (String role : anchoreds.get(anchored)) {
				anchoredPolicy.attachToContentAnchorage(content, role);
			}
			ITransactionalOperation attachToAnchorageOperation = anchoredPolicy
					.commit();
			if (attachToAnchorageOperation != null
					&& !attachToAnchorageOperation.isNoOp()) {
				createOperation.add(attachToAnchorageOperation);
			}
		}
	}

	@Override
	public void init() {
		createOperation = new ReverseUndoCompositeOperation("Create Content");
		initialized = true;
	}

}
