/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics.render;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.graphics.Font;

/**
 * Provides utility methods for {@link Font}s.
 * 
 * @author mwienand
 * 
 */
public interface IFontUtils {

	/**
	 * Returns the width and height required to display the given {@link String}
	 * with the {@link Font} that is currently set in the
	 * {@link IWriteProperties} of the given {@link IGraphics} as a
	 * {@link Dimension}.
	 * 
	 * @param graphics
	 * @param text
	 * @return a {@link Dimension} representing the width and height required to
	 *         display the given {@link String} with the {@link Font} currently
	 *         set in the {@link IWriteProperties} of the given
	 *         {@link IGraphics} {@link IWriteProperties#getFont() font}
	 */
	Dimension getTextDimension(String text);

}
