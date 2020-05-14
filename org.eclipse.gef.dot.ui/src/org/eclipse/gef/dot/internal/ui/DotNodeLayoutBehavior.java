/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import org.eclipse.gef.dot.internal.ui.preferences.GraphvizPreferencePage;
import org.eclipse.gef.zest.fx.behaviors.NodeLayoutBehavior;

public class DotNodeLayoutBehavior extends NodeLayoutBehavior {

	@Override
	protected void layoutLabels() {
		// labels only have to be positioned in emulated mode; in native mode,
		// label positions are calculated by dot already
		if (!GraphvizPreferencePage.isGraphvizConfigured()) {
			super.layoutLabels();
		}
	}
}
