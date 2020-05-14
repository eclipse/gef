/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import java.util.List;

import org.eclipse.gef.mvc.fx.models.FocusModel;
import org.eclipse.gef.mvc.fx.operations.ChangeFocusOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.reflect.TypeToken;

import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * The {@link FocusTraversalPolicy} can be used to assign focus to the next or
 * previous part in the focus traversal cycle.
 *
 * @author mwienand
 *
 */
public class FocusTraversalPolicy extends AbstractPolicy {

	private IViewer viewer;
	private FocusModel focusModel;

	@Override
	protected ITransactionalOperation createOperation() {
		return new ChangeFocusOperation(viewer, null);
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
	protected IContentPart<? extends Node> findInnerMostContentPart(
			IContentPart<? extends Node> part) {
		ObservableList<IVisualPart<? extends Node>> children = part
				.getChildrenUnmodifiable();
		while (!children.isEmpty()) {
			for (int i = children.size() - 1; i >= 0; i--) {
				IVisualPart<? extends Node> child = children.get(i);
				if (child instanceof IContentPart) {
					// continue searching for content part children within this
					// child's part hierarchy
					part = (IContentPart<? extends Node>) child;
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected IContentPart<? extends Node> findNextContentPart(
			IContentPart<? extends Node> current) {
		// search children for content parts
		List<IVisualPart<? extends Node>> children = current
				.getChildrenUnmodifiable();
		if (!children.isEmpty()) {
			for (IVisualPart<? extends Node> child : children) {
				if (child instanceof IContentPart) {
					return (IContentPart<? extends Node>) child;
				}
			}
		}
		// no content part children available, therefore, we have to search our
		// siblings and move up the hierarchy
		IVisualPart<? extends Node> parent = current.getParent();
		while (parent instanceof IContentPart || parent instanceof IRootPart) {
			children = parent instanceof IContentPart
					? parent.getChildrenUnmodifiable()
					: ((IRootPart) parent).getContentPartChildren();
			int index = children.indexOf(current) + 1;
			while (index < children.size()) {
				IVisualPart<? extends Node> part = children.get(index);
				if (part instanceof IContentPart) {
					return (IContentPart<? extends Node>) part;
				}
				index++;
			}
			if (parent instanceof IContentPart) {
				current = (IContentPart<? extends Node>) parent;
				parent = current.getParent();
			} else {
				return null;
			}
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
	protected IContentPart<? extends Node> findPreviousContentPart(
			IContentPart<? extends Node> current) {
		// find previous content part sibling
		IVisualPart<? extends Node> parent = current.getParent();
		if (parent instanceof IContentPart || parent instanceof IRootPart) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			List<IVisualPart<? extends Node>> children = parent instanceof IContentPart
					? parent.getChildrenUnmodifiable()
					: ((IRootPart) parent).getContentPartChildren();
			int index = children.indexOf(current) - 1;
			while (index >= 0) {
				IVisualPart<? extends Node> part = children.get(index);
				if (part instanceof IContentPart) {
					return findInnerMostContentPart(
							(IContentPart<? extends Node>) part);
				}
				index--;
			}
			if (parent instanceof IContentPart) {
				return (IContentPart<? extends Node>) parent;
			}
		}
		// could not find a previous content part
		return null;
	}

	/**
	 * Assigns focus to the next part in the traversal cycle. Returns the
	 * {@link IContentPart} to which focus is assigned by the operation of this
	 * policy, or <code>null</code> if focus is assigned to the viewer.
	 *
	 * @return The {@link IContentPart} to which focus is assigned by the
	 *         operation of this policy, or <code>null</code> if focus is
	 *         assigned to the viewer.
	 */
	public IContentPart<? extends Node> focusNext() {
		return traverse(false);
	}

	/**
	 * Assigns focus to the previous part in the traversal cycle. Returns the
	 * {@link IContentPart} to which focus is assigned by the operation of this
	 * policy, or <code>null</code> if focus is assigned to the viewer.
	 *
	 * @return The {@link IContentPart} to which focus is assigned by the
	 *         operation of this policy, or <code>null</code> if focus is
	 *         assigned to the viewer.
	 */
	public IContentPart<? extends Node> focusPrevious() {
		return traverse(true);
	}

	/**
	 * Returns the {@link ChangeFocusOperation} that is used to change the focus
	 * part.
	 *
	 * @return The {@link ChangeFocusOperation} that is used to change the focus
	 *         part.
	 */
	protected ChangeFocusOperation getChangeFocusOperation() {
		return (ChangeFocusOperation) getOperation();
	}

	@SuppressWarnings("serial")
	@Override
	public void init() {
		viewer = getHost().getRoot().getViewer();
		focusModel = viewer.getAdapter(new TypeToken<FocusModel>() {
		});
		super.init();
	}

	/**
	 * Traverses the focus forwards or backwards depending on the given flag.
	 * Returns the {@link IContentPart} to which focus is assigned by the
	 * operation of this policy, or <code>null</code> if focus is assigned to
	 * the viewer.
	 *
	 * @param backwards
	 *            <code>true</code> if the focus should be traversed backwards,
	 *            otherwise <code>false</code>.
	 * @return The {@link IContentPart} to which focus is assigned by the
	 *         operation of this policy, or <code>null</code> if focus is
	 *         assigned to the viewer.
	 */
	protected IContentPart<? extends Node> traverse(boolean backwards) {
		// get current focus part
		IContentPart<? extends Node> current = focusModel.getFocus();
		IContentPart<? extends Node> next = null;

		// determine the first focus part if no part currently has focus
		if (current == null) {
			List<IContentPart<? extends Node>> children = viewer.getRootPart()
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
		return next;
	}

}
