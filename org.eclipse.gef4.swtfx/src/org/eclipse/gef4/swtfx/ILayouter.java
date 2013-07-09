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

import org.eclipse.gef4.geometry.planar.Rectangle;

/**
 * The {@link ILayouter} interface is implemented by any external layout
 * manager. It does not know much about the layoutables, other than
 * 
 * @author mwienand
 * 
 */
public interface ILayouter {

	/**
	 * Returns the computed layout-bounds for the given <i>layoutable</i>.
	 * 
	 * @param layoutable
	 */
	public Rectangle getComputedLayoutBounds(Object layoutable);

	/**
	 * All preferred layout-bounds are set up. Do the layout pass.
	 */
	public void layout();

	/**
	 * Reset this {@link ILayouter}, i.e. discard all preferred/computed
	 * layout-bounds.
	 */
	public void reset();

	/**
	 * Sets the preferred layout-bounds for the specified <i>layoutable</i>.
	 * 
	 * @param layoutable
	 * @param layoutBounds
	 */
	public void setPreferredLayoutBounds(Object layoutable,
			Rectangle layoutBounds);

}
