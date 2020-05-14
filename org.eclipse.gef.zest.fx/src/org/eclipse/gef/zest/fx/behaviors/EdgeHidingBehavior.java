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

import org.eclipse.gef.zest.fx.parts.EdgePart;

/**
 * The {@link EdgeHidingBehavior} is an {@link EdgePart}-specific
 * {@link AbstractHidingBehavior} implementation.
 *
 * @author mwienand
 *
 */
// only applicable for EdgePart (see #getHost())
public class EdgeHidingBehavior extends AbstractHidingBehavior {

	@Override
	protected boolean determineHiddenStatus() {
		return getHidingModel().isHidden(getHost().getContent().getSource())
				|| getHidingModel().isHidden(getHost().getContent().getTarget());
	}

	@Override
	public EdgePart getHost() {
		return (EdgePart) super.getHost();
	}

}
