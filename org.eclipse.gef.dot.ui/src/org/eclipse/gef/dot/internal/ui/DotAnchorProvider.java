/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.anchors.OrthogonalProjectionStrategy;
import org.eclipse.gef.fx.anchors.ProjectionStrategy;
import org.eclipse.gef.zest.fx.providers.NodePartAnchorProvider;

public class DotAnchorProvider extends NodePartAnchorProvider {

	private IAnchor defaultAnchor;
	private IAnchor orthoAnchor;

	@Override
	protected IAnchor getOrthogonalAnchor() {
		if (orthoAnchor == null) {
			orthoAnchor = createDynamicAnchor(
					new OrthogonalProjectionStrategy());
		}
		return orthoAnchor;
	}

	@Override
	protected IAnchor getDefaultAnchor() {
		if (defaultAnchor == null) {
			defaultAnchor = createDynamicAnchor(new ProjectionStrategy());
		}
		return defaultAnchor;
	}

}
