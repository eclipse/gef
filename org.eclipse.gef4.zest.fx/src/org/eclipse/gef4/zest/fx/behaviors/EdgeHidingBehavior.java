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

import org.eclipse.gef4.zest.fx.parts.EdgeContentPart;

/**
 * The {@link EdgeHidingBehavior} is an {@link EdgeContentPart}-specific
 * {@link AbstractHidingBehavior} implementation.
 *
 * @author mwienand
 *
 */
// only applicable for EdgeContentPart (see #getHost())
public class EdgeHidingBehavior extends AbstractHidingBehavior {

	@Override
	protected boolean determineHiddenStatus() {
		return getHidingModel().isHidden(getHost().getContent().getSource())
				|| getHidingModel().isHidden(getHost().getContent().getTarget());
	}

	@Override
	public EdgeContentPart getHost() {
		return (EdgeContentPart) super.getHost();
	}

}
