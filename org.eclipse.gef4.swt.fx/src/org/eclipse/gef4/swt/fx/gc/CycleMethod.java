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
package org.eclipse.gef4.swt.fx.gc;

import org.eclipse.gef4.geometry.planar.Rectangle;

/**
 * <p>
 * The CycleMethod determines the behavior of a {@link Gradient} when applying
 * it to an area which it does not fully fill in one iteration. For example, you
 * can fill a {@link Rectangle} of width <code>100</code> with a
 * {@link LinearGradient} gradient of width <code>50</code>. Then, the
 * CycleMethod decides which {@link Color} to use for the other <code>50%</code>
 * .
 * </p>
 * 
 * <p>
 * There are three different CycleModes available:
 * <ul>
 * <li>{@link CycleMethod#NO_CYCLE}</li>
 * <li>{@link CycleMethod#REFLECT}</li>
 * <li>{@link CycleMethod#REPEAT}</li>
 * </ul>
 * </p>
 */
public enum CycleMethod {

	/**
	 * The NO_CYCLE {@link CycleMethod} determines that the last specified
	 * {@link Color} is used to fill the area in surplus.
	 * 
	 * @see {@link CycleMethod#REFLECT}
	 * @see {@link CycleMethod#REPEAT}
	 */
	NO_CYCLE,

	/**
	 * The REFLECT {@link CycleMethod} determines that the gradient
	 * {@link Color}s are mirrored at the border. For the prior
	 * {@link Rectangle} example, the result visually equals putting a mirror in
	 * the middle.
	 * 
	 * @see {@link CycleMethod#NO_CYCLE}
	 * @see {@link CycleMethod#REPEAT}
	 */
	REFLECT,

	/**
	 * The REPEAT {@link CycleMethod} determines that the gradient {@link Color}
	 * s are repeated.
	 * 
	 * @see {@link CycleMethod#NO_CYCLE}
	 * @see {@link CycleMethod#REFLECT}
	 */
	REPEAT;

	/**
	 * The default {@link CycleMethod} for {@link Gradient}s is
	 * {@link CycleMethod#NO_CYCLE}
	 */
	public static final CycleMethod DEFAULT = NO_CYCLE;

}