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
package org.eclipse.gef4.graphics;

/**
 * <p>
 * The {@link IGraphicsProperties} interface provides methods to control the
 * modification of underlying graphics systems.
 * </p>
 * 
 * <p>
 * The {@link #init(IGraphics)} method is called when building the initial set
 * of {@link IGraphicsProperties} or when calling {@link IGraphics#pushState()}.
 * It should read out all values from the underlying graphics system to be able
 * to reset those values later.
 * </p>
 * 
 * <p>
 * The individual IGraphicsProperties implementations do all provide a method to
 * apply their set of managed attributes when performing a respective drawing
 * operation. This method is called from an {@link IGraphics} before performing
 * the individual
 * {@link IGraphics#draw(org.eclipse.gef4.geometry.planar.ICurve) draw},
 * {@link IGraphics#fill(org.eclipse.gef4.geometry.planar.IMultiShape) fill},
 * {@link IGraphics#blit(Image) blit}, and {@link IGraphics#write(String) write}
 * operations.
 * </p>
 * 
 * <p>
 * The {@link #cleanUp(IGraphics)} method is called when an
 * {@link IGraphicsProperties} is no longer used. For example after calling
 * {@link IGraphics#popState()} or when finishing drawing with an
 * {@link IGraphics}.
 * </p>
 * 
 * @author mwienand
 * 
 */
public interface IGraphicsProperties {

	/**
	 * Marks this {@link IGraphicsProperties} as currently being active on an
	 * {@link IGraphics}. Any {@link IGraphicsProperties} may only be
	 * manipulated when being active. If the user attempts to manipulate an
	 * {@link IGraphicsProperties} that is currently deactivated (see
	 * {@link #deactivate()}) an {@link IllegalStateException} should be thrown.
	 */
	void activate();

	/**
	 * <p>
	 * Resets the properties of the underlying graphics system of the passed-in
	 * {@link IGraphics} to the values read out during {@link #init(IGraphics)}.
	 * </p>
	 * 
	 * <p>
	 * Disposes any system resources allocated by this
	 * {@link IGraphicsProperties}.
	 * </p>
	 * 
	 * @param g
	 *            the {@link IGraphics} for which the properties are reset
	 */
	void cleanUp(IGraphics g);

	/**
	 * Marks this {@link IGraphicsProperties} as currently being inactive on an
	 * {@link IGraphics}. Any {@link IGraphicsProperties} may only be
	 * manipulated when being active (see {@link #activate()}). If the user
	 * attempts to manipulate an {@link IGraphicsProperties} that is currently
	 * deactivated an {@link IllegalStateException} should be thrown.
	 */
	void deactivate();

	/**
	 * Returns a deep copy of this {@link IGraphicsProperties} object.
	 * 
	 * @return a deep copy of this {@link IGraphicsProperties}
	 */
	IGraphicsProperties getCopy();

	/**
	 * Reads out any properties that may be modified by this
	 * {@link IGraphicsProperties} during the appliance of its managed
	 * attributes to be able to reset them later when
	 * {@link #cleanUp(IGraphics)} is called.
	 * 
	 * @param g
	 *            the {@link IGraphics} for which the properties are read out
	 */
	void init(IGraphics g);

	/**
	 * Returns <code>true</code> if this {@link IGraphicsProperties} is
	 * currently active on an {@link IGraphics} (see {@link #activate()}).
	 * Returns <code>false</code> if this {@link IGraphicsProperties} is
	 * currently inactive on an {@link IGraphics}. If the user attempts to
	 * manipulate an {@link IGraphicsProperties} that is currently deactivated
	 * an {@link IllegalStateException} should be thrown.
	 * 
	 * @return <code>true</code> if this {@link IGraphicsProperties} is
	 *         currently active on an {@link IGraphics}, otherwise
	 *         <code>false</code>
	 */
	boolean isActive();

}
