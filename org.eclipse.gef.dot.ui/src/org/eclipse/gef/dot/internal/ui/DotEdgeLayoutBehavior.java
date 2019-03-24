/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import org.eclipse.gef.dot.internal.ui.preferences.GraphvizPreferencePage;
import org.eclipse.gef.zest.fx.behaviors.EdgeLayoutBehavior;

public class DotEdgeLayoutBehavior extends EdgeLayoutBehavior {

	@Override
	protected void layoutLabels() {
		// labels only have to be positioned in emulated mode; in native mode,
		// label positions are calculated by dot already
		if (!GraphvizPreferencePage.isGraphvizConfigured()) {
			super.layoutLabels();
		}
	}
}
