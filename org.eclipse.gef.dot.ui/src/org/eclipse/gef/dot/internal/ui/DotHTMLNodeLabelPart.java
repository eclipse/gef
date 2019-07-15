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
