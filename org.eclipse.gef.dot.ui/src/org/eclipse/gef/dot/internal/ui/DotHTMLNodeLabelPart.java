/************************************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Prigge (itemis AG) - initial API and implementation (bug #321775)
 *
 ***********************************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import javafx.scene.Group;

public class DotHTMLNodeLabelPart extends DotNodeLabelPart {

	@Override
	protected Group doCreateVisual() {
		createText(); // to avoid NPE
		return new Group();
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		super.doRefreshVisual(visual);
		refreshHtmlLabelNode();
	}

	protected void refreshHtmlLabelNode() {
		javafx.scene.Node fx = DotProperties
				.getHtmlLikeExternalLabel(getContent().getKey());
		if (fx != null && !getVisual().getChildren().contains(fx)) {
			getVisual().getChildren().clear();
			getVisual().getChildren().add(fx);
		}
	}
}
