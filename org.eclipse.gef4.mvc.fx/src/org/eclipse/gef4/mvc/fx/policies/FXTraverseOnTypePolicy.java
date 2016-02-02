/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.operations.ChangeFocusOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The {@link FXTraverseOnTypePolicy} implements focus traversal via keyboard
 * input.
 *
 * @author mwienand
 *
 */
public class FXTraverseOnTypePolicy extends AbstractFXInteractionPolicy
		implements IFXOnTypePolicy {

	/**
	 * Determines the next {@link IContentPart} to which keyboard focus is
	 * assigned, depending on the currently focused {@link IContentPart}.
	 * <p>
	 * The first content part child of the given focus part is returned as the
	 * next part if a content part child is available.
	 * <p>
	 * The next content part sibling of the given focus part is returned as the
	 * next part if a content part sibling is available. When one sibling list
	 * ends, the search continues with the parent's siblings until it reaches
	 * the root of the hierarchy.
	 * <p>
	 * If the next content part cannot be determined, <code>null</code> is
	 * returned.
	 *
	 * @param current
	 *            The currently focused {@link IContentPart}.
	 * @return The next {@link IContentPart} to which keyboard focus is
	 *         assigned, or <code>null</code> if no subsequent
	 *         {@link IContentPart} could be determined.
	 */
	protected IContentPart<Node, ? extends Node> findNextContentPart(
			IContentPart<Node, ? extends Node> current) {
		// search children for content parts
		ObservableList<IVisualPart<Node, ? extends Node>> children = current
				.getChildrenUnmodifiable();
		if (!children.isEmpty()) {
			for (IVisualPart<Node, ? extends Node> child : children) {
				if (child instanceof IContentPart) {
					return (IContentPart<Node, ? extends Node>) child;
				}
			}
		}
		// no content part children available, therefore, we have to search our
		// siblings and move up the hierarchy
		IVisualPart<Node, ? extends Node> parent = current.getParent();
		while (parent instanceof IContentPart) {
			children = parent.getChildrenUnmodifiable();
			int index = children.indexOf(current) + 1;
			while (index < children.size()) {
				IVisualPart<Node, ? extends Node> part = children.get(index);
				if (part instanceof IContentPart) {
					return (IContentPart<Node, ? extends Node>) part;
				}
				index++;
			}
			current = (IContentPart<Node, ? extends Node>) parent;
			parent = current.getParent();
		}
		// could not find another content part
		return null;
	}

	@SuppressWarnings("serial")
	@Override
	public void pressed(KeyEvent event) {
		if (KeyCode.TAB.equals(event.getCode())) {
			// find focus model
			IViewer<Node> viewer = getHost().getRoot().getViewer();
			FocusModel<Node> focusModel = viewer
					.getAdapter(new TypeToken<FocusModel<Node>>() {
					});
			if (focusModel == null) {
				throw new IllegalStateException(
						"Cannot find <FocusModel<Node>> on viewer within <"
								+ getClass() + "> that is bound to host <"
								+ getHost() + ">.");
			}

			// get current focus part
			IContentPart<Node, ? extends Node> current = focusModel.getFocus();

			// determine next focus part
			IContentPart<Node, ? extends Node> next = null;
			if (current == null) {
				// focus first content leaf
				List<IContentPart<Node, ? extends Node>> children = viewer
						.getRootPart().getContentPartChildren();
				if (children != null && !children.isEmpty()) {
					next = children.get(0);
				}
			} else {
				next = findNextContentPart(current);
			}

			// give focus to the next part or to the viewer (if next is null)
			try {
				viewer.getDomain()
						.execute(new ChangeFocusOperation<>(viewer, next));
			} catch (ExecutionException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	@Override
	public void released(KeyEvent event) {
	}

	@Override
	public void typed(KeyEvent event) {
	}

	@Override
	public void unfocus() {
	}

}
