/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import org.eclipse.gef.zest.fx.parts.EdgeLabelPart;

public class DotEdgeLabelPart extends EdgeLabelPart {

	@Override
	public void recomputeLabelPosition() {
		// only compute label positions in emulated mode
		// TODO: make native mode available within viewer
		if (!GraphvizPreferencePage.isGraphvizConfigured()) {
			super.recomputeLabelPosition();
			;
		}
	}

}
