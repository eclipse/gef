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

/**
 * @author mwienand
 * 
 */
public abstract class AbstractGraphicsProperties implements IGraphicsProperties {

	/**
	 * <p>
	 * Flag, that indicates if this {@link AbstractGraphicsProperties} is
	 * currently active on an {@link IGraphics}.
	 * </p>
	 * 
	 * <p>
	 * Note that an {@link AbstractGraphicsProperties} is active on creation.
	 * </p>
	 * 
	 * @see IGraphicsProperties#activate()
	 * @see IGraphicsProperties#deactivate()
	 * @see IGraphicsProperties#isActive()
	 * @see IGraphics#pushState()
	 * @see IGraphics#popState()
	 */
	protected boolean active = true;

	public void activate() {
		if (active) {
			throw new IllegalStateException(
					"Cannot activate already active IGraphicsProperties.");
		}
		active = true;
	}

	public void deactivate() {
		if (!active) {
			throw new IllegalStateException(
					"Cannot deactivate already inactive IGraphicsProperties.");
		}
		active = false;
	}

	public boolean isActive() {
		return active;
	}

}
