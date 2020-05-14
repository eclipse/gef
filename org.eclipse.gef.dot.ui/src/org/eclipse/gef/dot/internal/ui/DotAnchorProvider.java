/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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
