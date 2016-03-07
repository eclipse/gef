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
import java.util.List;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

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

	private List<IContentPart<Node, ? extends Node>> connected;

	@Override
	public void drag(MouseEvent e, Dimension delta) {
	}

	@Override
	public void dragAborted() {
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
				if (anchored.getAdapter(FXBendConnectionPolicy.class) != null) {
					connected
							.add((IContentPart<Node, ? extends Node>) anchored);
				}
			}
		}

		// filter out selected
		// @SuppressWarnings("serial")
		// SelectionModel<Node> selectionModel = getHost().getRoot().getViewer()
		// .getAdapter(new TypeToken<SelectionModel<Node>>() {
		// });
		// Iterator<IContentPart<Node, ? extends Node>> it =
		// connected.iterator();
		// while (it.hasNext()) {
		// IContentPart<Node, ? extends Node> part = it.next();
		// if (selectionModel.isSelected(part)) {
		// it.remove();
		// }
		// }

		if (connected.isEmpty()) {
			connected = null;
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		if (connected == null) {
			return;
		}
		// normalize connected
		for (IContentPart<Node, ? extends Node> part : connected) {
			FXBendConnectionPolicy bendPolicy = part
					.getAdapter(FXBendConnectionPolicy.class);
			if (bendPolicy != null) {
				init(bendPolicy);
				// FIXME: Normalization always occurs when committing a bend
				// policy. However, it should only happen when interacting.
				// bendPolicy.normalizeControlPoints();
				commit(bendPolicy);
			}
		}
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
