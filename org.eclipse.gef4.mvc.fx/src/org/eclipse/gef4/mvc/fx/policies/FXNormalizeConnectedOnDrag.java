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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Triggers a normalization of the control points of all content parts that
 * support {@link FXBendConnectionPolicy} and are anchored to the host of this
 * policy.
 *
 * @author mwienand
 *
 */
public class FXNormalizeConnectedOnDrag extends AbstractFXInteractionPolicy
		implements IFXOnDragPolicy {

	private List<IVisualPart<Node, ? extends Node>> connected;

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		if (connected == null) {
			return;
		}
		for (IVisualPart<Node, ? extends Node> part : connected) {
			part.getAdapter(FXBendConnectionPolicy.class).normalize();
		}
	}

	@Override
	public void dragAborted() {
		if (connected == null) {
			return;
		}
		for (IVisualPart<Node, ? extends Node> part : connected) {
			rollback(part.getAdapter(FXBendConnectionPolicy.class));
			restoreRefreshVisuals(part);
		}
		connected = null;
	}

	@Override
	public void hideIndicationCursor() {
	}

	@Override
	public void press(MouseEvent e) {
		connected = new ArrayList<>();
		for (IVisualPart<Node, ? extends Node> anchored : getHost()
				.getAnchoredsUnmodifiable()) {
			if (anchored instanceof IContentPart) {
				FXBendConnectionPolicy bendConnectionPolicy = anchored
						.getAdapter(FXBendConnectionPolicy.class);
				if (bendConnectionPolicy != null) {
					connected.add(anchored);
				}
			}
		}

		// filter out selected
		@SuppressWarnings("serial")
		SelectionModel<Node> selectionModel = getHost().getRoot().getViewer()
				.getAdapter(new TypeToken<SelectionModel<Node>>() {
				});
		Iterator<IVisualPart<Node, ? extends Node>> it = connected.iterator();
		while (it.hasNext()) {
			IVisualPart<Node, ? extends Node> part = it.next();
			if (part instanceof IContentPart && selectionModel
					.isSelected((IContentPart<Node, ? extends Node>) part)) {
				it.remove();
			}
		}

		if (connected.isEmpty()) {
			connected = null;
		} else {
			for (IVisualPart<Node, ? extends Node> part : connected) {
				storeAndDisableRefreshVisuals(part);
				init(part.getAdapter(FXBendConnectionPolicy.class));
			}
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		if (connected == null) {
			return;
		}
		for (IVisualPart<Node, ? extends Node> part : connected) {
			commit(part.getAdapter(FXBendConnectionPolicy.class));
			restoreRefreshVisuals(part);
		}
		connected = null;
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

}
