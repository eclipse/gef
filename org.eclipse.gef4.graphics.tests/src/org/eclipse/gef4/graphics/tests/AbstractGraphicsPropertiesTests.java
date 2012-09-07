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
package org.eclipse.gef4.graphics.tests;

import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.IGraphicsProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The AbstractGraphicsPropertiesTests class defines two attributes
 * {@link #properties} and {@link #propertiesCopy} which store copies of the
 * specific {@link IGraphicsProperties} returned by the
 * {@link #getProperties(IGraphics)} method for the igraphics implementation
 * returned by the {@link #createGraphics()} method.
 * 
 * @author mwienand
 * 
 */
public abstract class AbstractGraphicsPropertiesTests<T extends IGraphicsProperties> {

	protected IGraphics graphics;

	/**
	 * Stores a copy of the {@link IGraphicsProperties} returned by the
	 * {@link #getProperties(IGraphics)} method on the {@link IGraphics}
	 * implementation returned by the {@link #createGraphics()} method.
	 */
	protected T properties;

	/**
	 * Stores a copy of the {@link #properties} attribute.
	 */
	protected T propertiesCopy;

	@After
	public void cleanUp() {
		graphics.cleanUp();
	}

	/**
	 * Returns a specific {@link IGraphics} implementation to be tested.
	 * 
	 * @return a specific {@link IGraphics} implementation
	 */
	public abstract IGraphics createGraphics();

	/**
	 * Returns the specific {@link IGraphicsProperties} for the passed-in
	 * {@link IGraphics} implementation.
	 * 
	 * @param g
	 *            the {@link IGraphics} which is tested
	 * @return the specific {@link IGraphicsProperties} for the passed-in
	 *         {@link IGraphics}
	 */
	public abstract T getProperties(IGraphics g);

	@Test
	public void instance_changes() {
		assertTrue(properties != propertiesCopy);
	}

	/**
	 * Copies the {@link IGraphicsProperties} returned by the
	 * {@link #getProperties(IGraphics)} method for the {@link IGraphics}
	 * created by the {@link #createGraphics()} into the {@link #properties}
	 * attribute. Additionally, a copy of the {@link #properties} attribute is
	 * assigned to the {@link #propertiesCopy} attribute.
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void localSetUp() {
		graphics = createGraphics();
		properties = (T) getProperties(graphics).getCopy();
		propertiesCopy = (T) properties.getCopy();
	}

}
