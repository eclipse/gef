/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG)  - refactorings
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.operations.AbstractCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.ChangeContentsOperation;
import org.eclipse.gef.mvc.fx.operations.ChangeFocusOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.SelectOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.SetMultimap;

import javafx.scene.Node;

/**
 * The {@link CreationPolicy} is an {@link AbstractPolicy} that
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
public class CreationPolicy extends AbstractPolicy {

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
	 *            a child or the {@link IRootPart} for 'root' content.
	 * @param index
	 *            The index for the new element.
	 * @param anchoreds
	 *            The {@link IContentPart} whose content should be attached to
	 *            the new content under the given roles.
	 * @param doFocus
	 *            <code>true</code> if the newly created part should be focused.
	 * @param doSelect
	 *            <code>true</code> if the newly created part should be
	 *            selected.
	 * @return The {@link IContentPart} controlling the newly created content.
	 */
	public IContentPart<? extends Node> create(Object content,
			IVisualPart<? extends Node> parent, int index,
			SetMultimap<IContentPart<? extends Node>, String> anchoreds,
			boolean doFocus, boolean doSelect) {
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

		IViewer viewer = getHost().getRoot().getViewer();

		// add content to parent
		if (parent instanceof IRootPart) {
			// add content to viewer content
			ChangeContentsOperation changeContentsOperation = new ChangeContentsOperation(
					viewer);
			List<Object> newContents = new ArrayList<>(viewer.getContents());
			newContents.add(index, content);
			changeContentsOperation.setNewContents(newContents);
			getCompositeOperation().add(changeContentsOperation);
		} else {
			// add content to parent
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
		}

		// attach anchoreds to content
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

		locallyExecuteOperation();
		IContentPart<? extends Node> contentPart = viewer.getContentPartMap()
				.get(content);

		if (doFocus) {
			// set as focus part
			ITransactionalOperation focusOperation = createFocusOperation(
					contentPart);
			if (focusOperation != null) {
				getCompositeOperation().add(focusOperation);
			}
		}

		if (doSelect) {
			// select the newly created part
			ITransactionalOperation selectOperation = createSelectOperation(
					contentPart);
			if (selectOperation != null) {
				getCompositeOperation().add(selectOperation);
			}
		}

		return contentPart;
	}

	/**
	 * Creates an {@link IContentPart} for the given content {@link Object} and
	 * establishes parent and anchored relationships for the newly created part.
	 * The respective content operations are also created.
	 *
	 * In case the given part is to be created for root contents, the root part
	 * is expected to be passed in as parent. The content will then be added to
	 * the viewer contents.
	 *
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
			IVisualPart<? extends Node> parent,
			SetMultimap<IContentPart<? extends Node>, String> anchoreds) {
		int index = parent instanceof IRootPart
				? getHost().getRoot().getViewer().getContents().size()
				: ((IContentPart<? extends Node>) parent)
						.getContentChildrenUnmodifiable().size();
		return create(content, parent, index, anchoreds, true, true);
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
		return new SelectOperation(part.getRoot().getViewer(),
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
