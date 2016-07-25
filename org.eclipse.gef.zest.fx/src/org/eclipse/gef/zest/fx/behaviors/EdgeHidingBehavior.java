/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
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
