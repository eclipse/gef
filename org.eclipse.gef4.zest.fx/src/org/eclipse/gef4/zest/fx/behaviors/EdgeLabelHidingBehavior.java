/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.behaviors;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.zest.fx.parts.EdgeLabelPart;

// only applicable for EdgeLabelPart (see #getHost())
public class EdgeLabelHidingBehavior extends AbstractHidingBehavior {

	@Override
	protected boolean determineHiddenStatus() {
		Edge edge = getHost().getHost().getContent();
		return getHidingModel().isHidden(edge.getSource())
				|| getHidingModel().isHidden(edge.getTarget());
	}

	@Override
	public EdgeLabelPart getHost() {
		return (EdgeLabelPart) super.getHost();
	}

}
