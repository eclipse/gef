/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Zoey Gerrit Prigge (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.conversion;

import org.eclipse.gef.dot.internal.language.style.NodeStyle;

public interface DotNodeStyleUtil {

	/**
	 * Computes Zest node shape style
	 *
	 * @return StringBuilder containing node shape style
	 */
	public StringBuilder computeZestStyle();

	/**
	 * Checks if node has given style
	 *
	 * @param nodeStyle
	 * @return true, if node has nodeStyle
	 */
	public boolean hasStyle(NodeStyle nodeStyle);
}
