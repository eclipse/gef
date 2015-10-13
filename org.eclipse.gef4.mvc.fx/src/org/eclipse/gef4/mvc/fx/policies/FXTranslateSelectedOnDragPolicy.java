/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FXTranslateSelectedOnDragPolicy} is an
 * {@link AbstractFXOnDragPolicy} that relocates its {@link #getHost() host}
 * when it is dragged with the mouse.
 *
 * @author anyssen
 *
 */
public class FXTranslateSelectedOnDragPolicy extends AbstractFXOnDragPolicy {

	private Point initialMouseLocationInScene = null;
	private Map<IContentPart<Node, ? extends Node>, Integer> translationIndices = new HashMap<IContentPart<Node, ? extends Node>, Integer>();

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		for (IContentPart<Node, ? extends Node> part : getTargetParts()) {
			FXTransformPolicy policy = getTransformPolicy(part);
			if (policy != null) {
				policy.setPostTranslateInScene(translationIndices.get(part),
						delta.width, delta.height);
			}
		}
	}

	/**
	 * Returns the initial mouse location in scene coordinates.
	 *
	 * @return The initial mouse location in scene coordinates.
	 */
	protected Point getInitialMouseLocationInScene() {
		return initialMouseLocationInScene;
	}

	/**
	 * Returns a {@link List} containing all {@link IContentPart}s that should
	 * be relocated by this policy.
	 *
	 * @return A {@link List} containing all {@link IContentPart}s that should
	 *         be relocated by this policy.
	 */
	public List<IContentPart<Node, ? extends Node>> getTargetParts() {
		return getHost().getRoot().getViewer()
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelected();
	}

	/**
	 * Returns the {@link FXTransformPolicy} that is installed on the given
	 * {@link IContentPart}.
	 *
	 * @param part
	 *            The {@link IContentPart} for which to return the installed
	 *            {@link FXTransformPolicy}.
	 * @return The {@link FXTransformPolicy} that is installed on the given
	 *         {@link IContentPart}.
	 */
	protected FXTransformPolicy getTransformPolicy(
			IContentPart<Node, ? extends Node> part) {
		return part.getAdapter(FXTransformPolicy.class);
	}

	@Override
	public void press(MouseEvent e) {
		translationIndices.clear();
		setInitialMouseLocationInScene(new Point(e.getSceneX(), e.getSceneY()));
		for (IContentPart<Node, ? extends Node> part : getTargetParts()) {
			storeAndDisableRefreshVisuals(part);
			// init transaction policy
			init(getTransformPolicy(part));
			translationIndices.put(part,
					getTransformPolicy(part).createPostTransform());
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		for (IContentPart<Node, ? extends Node> part : getTargetParts()) {
			FXTransformPolicy policy = getTransformPolicy(part);
			if (policy != null) {
				restoreRefreshVisuals(part);
				// TODO: we need to ensure this can be done before
				// enableRefreshVisuals(), because visuals should already be up
				// to date
				// (and we thus save a potential refresh)
				commit(policy);
			}
		}
		setInitialMouseLocationInScene(null);
		translationIndices.clear();
	}

	/**
	 * Sets the initial mouse location to the given value.
	 *
	 * @param point
	 *            The initial mouse location.
	 */
	protected void setInitialMouseLocationInScene(Point point) {
		initialMouseLocationInScene = point;
	}

}
