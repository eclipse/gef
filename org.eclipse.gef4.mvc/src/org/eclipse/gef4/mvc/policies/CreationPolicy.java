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

import java.util.Collections;
import java.util.Map.Entry;

import org.eclipse.gef4.common.reflect.Types;
import org.eclipse.gef4.mvc.behaviors.ContentPartPool;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.ChangeFocusOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.SelectOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

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
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public class CreationPolicy<VR> extends AbstractTransactionPolicy<VR> {

	@Inject
	private ContentPartPool<VR> contentPartPool;

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
	@SuppressWarnings("serial")
	public IContentPart<VR, ? extends VR> create(Object content,
			IContentPart<VR, ? extends VR> parent, int index,
			SetMultimap<IContentPart<VR, ? extends VR>, String> anchoreds) {
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
		IContentPart<VR, ? extends VR> contentPart = getContentPartFactory()
				.createContentPart(content, null, null);
		// establish relationships to parent and anchored parts
		contentPart.setContent(content);
		parent.addChild(contentPart, index);
		for (Entry<IContentPart<VR, ? extends VR>, String> anchored : anchoreds
				.entries()) {
			anchored.getKey().attachToAnchorage(contentPart,
					anchored.getValue());
		}
		// register the content part, so that the ContentBehavior
		// synchronization reuses it (when committing the create operation)
		contentPartPool.add(contentPart);

		// add to parent via content policy
		ContentPolicy<VR> parentContentPolicy = parent
				.getAdapter(new TypeToken<ContentPolicy<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(
						getHost().getRoot().getViewer().getClass())));
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
		for (IContentPart<VR, ? extends VR> anchored : anchoreds.keys()) {
			ContentPolicy<VR> anchoredPolicy = anchored
					.getAdapter(new TypeToken<ContentPolicy<VR>>() {
					}.where(new TypeParameter<VR>() {
					}, Types.<VR> argumentOf(
							getHost().getRoot().getViewer().getClass())));
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
	public IContentPart<VR, ? extends VR> create(Object content,
			IContentPart<VR, ? extends VR> parent,
			SetMultimap<IContentPart<VR, ? extends VR>, String> anchoreds) {
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
			IContentPart<VR, ? extends VR> part) {
		// remove from focus model
		return new ChangeFocusOperation<>(part.getRoot().getViewer(), part);
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
			IContentPart<VR, ? extends VR> part) {
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
	protected IContentPartFactory<VR> getContentPartFactory() {
		IViewer<VR> viewer = getHost().getRoot().getViewer();
		@SuppressWarnings("serial")
		IContentPartFactory<VR> cpFactory = viewer
				.getAdapter(new TypeToken<IContentPartFactory<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(viewer.getClass())));
		return cpFactory;
	}

}
