/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.providers;

import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.mvc.fx.parts.IResizableContentPart;
import org.eclipse.gef.mvc.fx.parts.ITransformableContentPart;

/**
 * The {@link ResizableTransformableOutlineProvider} returns the outline
 * according to the size and transformation as returned by the part API, i.e.
 * {@link IResizableContentPart} and {@link ITransformableContentPart}.
 *
 * @author wienand
 *
 */
public class ResizableTransformableOutlineProvider
		extends ResizableTransformableBoundsProvider {

	@Override
	public IGeometry get() {
		return super.get().getBounds().getOutline();
	}
}
