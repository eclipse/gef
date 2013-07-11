/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx;

import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.GraphicsContextState;

public interface IFigure extends INode {

	/**
	 * Returns the clipping {@link Path} associated with this {@link INode}.
	 * 
	 * @return the clipping {@link Path} associated with this {@link INode}
	 */
	public Path getClipPath();

	/**
	 * Returns the {@link GraphicsContextState} associated with this IFigure.
	 * 
	 * @deprecated The paint state will not be part of the public API.
	 * @return the {@link GraphicsContextState} associated with this IFigure
	 */
	@Deprecated
	GraphicsContextState getPaintStateByReference();

	/**
	 * Draws this {@link IFigure} using the passed-in {@link GraphicsContext}.
	 * 
	 * @param g
	 */
	void paint(GraphicsContext g);

	/**
	 * Sets the clipping {@link Path} of this {@link IFigure} to the given
	 * {@link Path}.
	 * 
	 * @param clipPath
	 */
	public void setClipPath(Path clipPath);

	/**
	 * Requests the container to redraw.
	 */
	void update();

}
