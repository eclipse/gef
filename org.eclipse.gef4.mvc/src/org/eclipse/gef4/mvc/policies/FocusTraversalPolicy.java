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
package org.eclipse.gef4.mvc.policies;

import java.util.List;

import org.eclipse.gef4.common.reflect.Types;
import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.operations.ChangeFocusOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

import javafx.collections.ObservableList;

/**
 * The {@link FocusTraversalPolicy} can be used to assign focus to the next or
 * previous part in the focus traversal cycle.
 *
 * @author mwienand
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public class FocusTraversalPolicy<VR> extends AbstractTransactionPolicy<VR> {

	private IViewer<VR> viewer;
	private FocusModel<VR> focusModel;

	@Override
	protected ITransactionalOperation createOperation() {
		return new ChangeFocusOperation<>(viewer, null);
	}

	/**
	 * Returns the inner most {@link IContentPart} child within the part
	 * hierarchy of the given {@link IContentPart}. If the given
	 * {@link IContentPart} does not have any {@link IContentPart} children,
	 * then the given {@link IContentPart} is returned.
	 *
	 * @param part
	 *            The {@link IContentPart} for which to determine the inner most
	 *            {@link IContentPart} child.
	 * @return The inner most {@link IContentPart} child within the part
	 *         hierarchy of the given {@link IContentPart}.
	 */
	protected IContentPart<VR, ? extends VR> findInnerMostContentPart(
			IContentPart<VR, ? extends VR> part) {
		ObservableList<IVisualPart<VR, ? extends VR>> children = part
				.getChildrenUnmodifiable();
		while (!children.isEmpty()) {
			for (int i = children.size() - 1; i >= 0; i--) {
				IVisualPart<VR, ? extends VR> child = children.get(i);
				if (child instanceof IContentPart) {
					// continue searching for content part children within this
					// child's part hierarchy
					part = (IContentPart<VR, ? extends VR>) child;
					children = part.getChildrenUnmodifiable();
					break;
				}
			}
		}
		// did not find a content part child => return the given content part
		return part;
	}

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
	protected IContentPart<VR, ? extends VR> findNextContentPart(
			IContentPart<VR, ? extends VR> current) {
		// search children for content parts
		ObservableList<IVisualPart<VR, ? extends VR>> children = current
				.getChildrenUnmodifiable();
		if (!children.isEmpty()) {
			for (IVisualPart<VR, ? extends VR> child : children) {
				if (child instanceof IContentPart) {
					return (IContentPart<VR, ? extends VR>) child;
				}
			}
		}
		// no content part children available, therefore, we have to search our
		// siblings and move up the hierarchy
		IVisualPart<VR, ? extends VR> parent = current.getParent();
		while (parent instanceof IContentPart) {
			children = parent.getChildrenUnmodifiable();
			int index = children.indexOf(current) + 1;
			while (index < children.size()) {
				IVisualPart<VR, ? extends VR> part = children.get(index);
				if (part instanceof IContentPart) {
					return (IContentPart<VR, ? extends VR>) part;
				}
				index++;
			}
			current = (IContentPart<VR, ? extends VR>) parent;
			parent = current.getParent();
		}
		// could not find another content part
		return null;
	}

	/**
	 * Determines the previous {@link IContentPart} to which keyboard focus is
	 * assigned, depending on the currently focused {@link IContentPart}.
	 * <p>
	 * At first, the previous content part sibling of the given focus part is
	 * determined. If a siblings list ends, the search continues with the
	 * parent's siblings.
	 * <p>
	 * The inner most content part child of the previous content part sibling is
	 * returned as the previous content part, or <code>null</code> if no
	 * previous content part sibling could be found.
	 *
	 * @param current
	 *            The currently focused {@link IContentPart}.
	 * @return The previous {@link IContentPart} to which keyboard focus is
	 *         assigned, or <code>null</code> if no previous
	 *         {@link IContentPart} could be determined.
	 */
	protected IContentPart<VR, ? extends VR> findPreviousContentPart(
			IContentPart<VR, ? extends VR> current) {
		// find previous content part sibling
		IVisualPart<VR, ? extends VR> parent = current.getParent();
		if (parent instanceof IContentPart) {
			ObservableList<IVisualPart<VR, ? extends VR>> children = parent
					.getChildrenUnmodifiable();
			int index = children.indexOf(current) - 1;
			while (index >= 0) {
				IVisualPart<VR, ? extends VR> part = children.get(index);
				if (part instanceof IContentPart) {
					return findInnerMostContentPart(
							(IContentPart<VR, ? extends VR>) part);
				}
				index--;
			}
			return (IContentPart<VR, ? extends VR>) parent;
		}
		// could not find a previous content part
		return null;
	}

	/**
	 * Assigns focus to the next part in the traversal cycle.
	 */
	public void focusNext() {
		traverse(false);
	}

	/**
	 * Assigns focus to the previous part in the traversal cycle.
	 */
	public void focusPrevious() {
		traverse(true);
	}

	/**
	 * Returns the {@link ChangeFocusOperation} that is used to change the focus
	 * part.
	 *
	 * @return The {@link ChangeFocusOperation} that is used to change the focus
	 *         part.
	 */
	@SuppressWarnings("unchecked")
	protected ChangeFocusOperation<VR> getChangeFocusOperation() {
		return (ChangeFocusOperation<VR>) getOperation();
	}

	@SuppressWarnings("serial")
	@Override
	public void init() {
		viewer = getHost().getRoot().getViewer();
		focusModel = viewer.getAdapter(new TypeToken<FocusModel<VR>>() {
		}.where(new TypeParameter<VR>() {
		}, Types.<VR> argumentOf(viewer.getClass())));
		super.init();
	}

	/**
	 * Traverses the focus forwards or backwards depending on the given flag.
	 *
	 * @param backwards
	 *            <code>true</code> if the focus should be traversed backwards,
	 *            otherwise <code>false</code>.
	 */
	protected void traverse(boolean backwards) {
		// get current focus part
		IContentPart<VR, ? extends VR> current = focusModel.getFocus();
		IContentPart<VR, ? extends VR> next = null;

		// determine the first focus part if no part currently has focus
		if (current == null) {
			List<IContentPart<VR, ? extends VR>> children = viewer.getRootPart()
					.getContentPartChildren();
			if (children != null && !children.isEmpty()) {
				if (backwards) {
					// focus last content leaf
					next = findInnerMostContentPart(
							children.get(children.size() - 1));
				} else {
					// focus first content part
					next = children.get(0);
				}
			}
		}

		// find the next/previous part that is focusable
		if (current != null || next != null && !next.isFocusable()) {
			// in case we did not select a next part yet, start with the
			// currently focused part
			if (next == null) {
				next = current;
			}
			// search until a focusable part is found
			if (backwards) {
				do {
					next = findPreviousContentPart(next);
				} while (next != null && !next.isFocusable());
			} else {
				do {
					next = findNextContentPart(next);
				} while (next != null && !next.isFocusable());
			}
		}

		// give focus to the next part or to the viewer (if next is null)
		if (next == null || next.isFocusable()) {
			getChangeFocusOperation().setNewFocused(next);
		}
	}

}
