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
package org.eclipse.gef4.swtfx.gc;

/**
 * The {@link TextAlignment} determines the justification of text:
 * <ul>
 * <li>{@link #LEFT}</li>
 * <li>{@link #RIGHT}</li>
 * <li>{@link #CENTER}</li>
 * <li>{@link #JUSTIFY}</li>
 * </ul>
 * 
 * @author mwienand
 * 
 */
public enum TextAlignment {

	/**
	 * Text is left-justified.
	 * 
	 * @see #RIGHT
	 * @see #CENTER
	 * @see #JUSTIFY
	 */
	LEFT,

	/**
	 * Text is centered.
	 * 
	 * @see #LEFT
	 * @see #RIGHT
	 * @see #JUSTIFY
	 */
	CENTER,

	/**
	 * Text is right-justified.
	 * 
	 * @see #LEFT
	 * @see #CENTER
	 * @see #JUSTIFY
	 */
	RIGHT,

	/**
	 * Text is full-justified.
	 * 
	 * @see #LEFT
	 * @see #RIGHT
	 * @see #CENTER
	 */
	JUSTIFY

}
