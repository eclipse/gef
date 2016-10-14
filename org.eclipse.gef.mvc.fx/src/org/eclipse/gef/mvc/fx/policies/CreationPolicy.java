/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.fx.policies;

import java.util.Collections;
import java.util.Map.Entry;

import org.eclipse.gef.mvc.fx.behaviors.ContentPartPool;
import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.operations.AbstractCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.ChangeFocusOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.SelectOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;

import javafx.scene.Node;

/**
 * The {@link CreationPolicy} is an {@link AbstractTransactionPolicy} that
 * handles the creation of content.
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
 */
public class CreationPolicy extends AbstractTransactionPolicy {

	@Inject
	private ContentPartPool contentPartPool;

	/**
	 * Creates an {@link IContentPart} for the given content {@link Object} and
	 * establishes parent and anchored relationships for the newly created part.
	 * Besides, operations are created for the establishment of the parent and
	 * anchored relationships within the content model. These operations are
	 * part of the operation returned by {@link #commit()}.
	 *
	 * @param content
	 *            The content {@link Object} to be created.
	 * @param parent
	 *            The {@link IContentPart} where the <i>content</i> is added as
	 *            a child.
	 * @param index
	 *            The index for the new element.
	 * @param anchoreds
	 *            The {@link IContentPart} whose content should be attached to
	 *            the new content under the given roles.
	 * @return The {@link IContentPart} controlling the newly created content.
	 */
	public IContentPart<? extends Node> create(Object content,
			IContentPart<? extends Node> parent, int index,
			SetMultimap<IContentPart<? extends Node>, String> anchoreds) {
		checkInitialized();
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

		// create content part beforehand
		IContentPart<? extends Node> contentPart = getContentPartFactory()
				.createContentPart(content, null, null);
		// establish relationships to parent and anchored parts
		contentPart.setContent(content);
		parent.addChild(contentPart, index);
		for (Entry<IContentPart<? extends Node>, String> anchored : anchoreds
				.entries()) {
			anchored.getKey().attachToAnchorage(contentPart,
					anchored.getValue());
		}
		// register the content part, so that the ContentBehavior
		// synchronization reuses it (when committing the create operation)
		contentPartPool.add(contentPart);

		// add to parent via content policy
		ContentPolicy parentContentPolicy = parent
				.getAdapter(ContentPolicy.class);
		if (parentContentPolicy == null) {
			throw new IllegalStateException(
					"No ContentPolicy registered for <" + parent + ">.");
		}
		parentContentPolicy.init();
		parentContentPolicy.addContentChild(content, index);
		ITransactionalOperation addToParentOperation = parentContentPolicy
				.commit();
		if (addToParentOperation != null) {
			getCompositeOperation().add(addToParentOperation);
		}

		// add anchoreds via content policy
		for (IContentPart<? extends Node> anchored : anchoreds.keys()) {
			ContentPolicy anchoredPolicy = anchored
					.getAdapter(ContentPolicy.class);
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
			if (attachToAnchorageOperation != null) {
				getCompositeOperation().add(attachToAnchorageOperation);
			}
		}

		// set as focus part
		ITransactionalOperation focusOperation = createFocusOperation(
				contentPart);
		if (focusOperation != null) {
			getCompositeOperation().add(focusOperation);
		}

		// select the newly created part
		ITransactionalOperation selectOperation = createSelectOperation(
				contentPart);
		if (selectOperation != null) {
			getCompositeOperation().add(selectOperation);
		}

		locallyExecuteOperation();
		return contentPart;
	}

	/**
	 * Creates an {@link IContentPart} for the given content {@link Object} and
	 * establishes parent and anchored relationships for the newly created part.
	 * Besides, operations are created for the establishment of the parent and
	 * anchored relationships within the content model. These operations are
	 * part of the operation returned by {@link #commit()}.
	 *
	 * @param content
	 *            The content {@link Object} to be created.
	 * @param parent
	 *            The {@link IContentPart} where the <i>content</i> is added as
	 *            a child.
	 * @param anchoreds
	 *            The {@link IContentPart} whose content should be attached to
	 *            the new content under the given roles.
	 * @return The {@link IContentPart} controlling the newly created content.
	 */
	public IContentPart<? extends Node> create(Object content,
			IContentPart<? extends Node> parent,
			SetMultimap<IContentPart<? extends Node>, String> anchoreds) {
		return create(content, parent, parent.getChildrenUnmodifiable().size(),
				anchoreds);
	}

	/**
	 * Returns an {@link ITransactionalOperation} that adds the given
	 * {@link IContentPart} to the {@link FocusModel} of the corresponding
	 * {@link IViewer}.
	 *
	 * @param part
	 *            The {@link IContentPart} that is added to the viewer models.
	 * @return An {@link ITransactionalOperation} that changes the viewer
	 *         models.
	 */
	protected ITransactionalOperation createFocusOperation(
			IContentPart<? extends Node> part) {
		// remove from focus model
		return new ChangeFocusOperation(part.getRoot().getViewer(), part);
	}

	@Override
	protected ITransactionalOperation createOperation() {
		return new ReverseUndoCompositeOperation("Create Content");
	}

	/**
	 * Returns an {@link ITransactionalOperation} that adds the given
	 * {@link IContentPart} to the {@link SelectionModel} of the corresponding
	 * {@link IViewer}.
	 *
	 * @param part
	 *            The {@link IContentPart} that is added to the viewer models.
	 * @return An {@link ITransactionalOperation} that changes the viewer
	 *         models.
	 */
	protected ITransactionalOperation createSelectOperation(
			IContentPart<? extends Node> part) {
		return new SelectOperation<>(part.getRoot().getViewer(),
				Collections.singletonList(part));
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

	/**
	 * Returns the {@link IContentPartFactory} of the current viewer.
	 *
	 * @return the {@link IContentPartFactory} of the current viewer.
	 */
	protected IContentPartFactory getContentPartFactory() {
		IViewer viewer = getHost().getRoot().getViewer();
		IContentPartFactory cpFactory = viewer
				.getAdapter(IContentPartFactory.class);
		return cpFactory;
	}

}
