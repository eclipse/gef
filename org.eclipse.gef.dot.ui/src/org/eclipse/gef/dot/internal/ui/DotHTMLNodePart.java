/************************************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoey Prigge (itemis AG) - initial API and implementation (bug #321775)
 *
 ***********************************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

public class DotHTMLNodePart extends DotNodePart {

	private Pane htmlLabelParentVisual;

	@Override
	protected Group doCreateVisual() {
		Group visual = super.doCreateVisual();
		Parent labelParent = findFxPaneWithText(visual, getLabelText());
		if (labelParent instanceof Pane) {
			htmlLabelParentVisual = ((Pane) labelParent);
			htmlLabelParentVisual.getChildren().remove(getLabelText());
		} else {
			new IllegalStateException("HTML label could not be visualized"); //$NON-NLS-1$
		}
		return visual;
	}

	private Parent findFxPaneWithText(Parent group, javafx.scene.Node text) {
		for (javafx.scene.Node node : group.getChildrenUnmodifiable()) {
			if (node == text) {
				return group;
			} else if (node instanceof Parent) {
				Parent possibleParent = findFxPaneWithText((Parent) node, text);
				if (possibleParent != null) {
					return possibleParent;
				}
			}
		}
		return null;
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		super.doRefreshVisual(visual);
		refreshHtmlLabelNode();
	}

	protected void refreshHtmlLabelNode() {
		javafx.scene.Node fx = DotProperties.getHtmlLikeLabel(getContent());
		if (fx != null && !htmlLabelParentVisual.getChildren().contains(fx)) {
			htmlLabelParentVisual.getChildren().clear();
			htmlLabelParentVisual.getChildren().add(fx);
		}
	}
}
