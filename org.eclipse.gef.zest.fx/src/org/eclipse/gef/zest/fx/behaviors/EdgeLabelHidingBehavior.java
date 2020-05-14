/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.behaviors;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.zest.fx.parts.EdgeLabelPart;

/**
 * The {@link EdgeLabelHidingBehavior} is an {@link EdgeLabelPart}-specific
 * {@link AbstractHidingBehavior} implementation.
 *
 * @author mwienand
 *
 */
// only applicable for EdgeLabelPart (see #getHost())
public class EdgeLabelHidingBehavior extends AbstractHidingBehavior {

	@Override
	protected boolean determineHiddenStatus() {
		Edge edge = getHost().getContent().getKey();
		return getHidingModel().isHidden(edge.getSource()) || getHidingModel().isHidden(edge.getTarget());
	}

	@Override
	public EdgeLabelPart getHost() {
		return (EdgeLabelPart) super.getHost();
	}

}
