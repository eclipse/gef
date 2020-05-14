/*******************************************************************************
 * Copyright (c) 2009, 2016 Mateusz Matela and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull (The Chisel Group)
 *               Matthias Wienand (itemis AG) - refactorings
 ******************************************************************************/
package org.eclipse.gef.layout.algorithms;

/**
 * Layout algorithm that places all elements in one column or one row, depending
 * on set orientation.
 * 
 * @author Mateusz Matela
 * @author Ian Bull
 * @author mwienand
 */
public class BoxLayoutAlgorithm extends GridLayoutAlgorithm {

	/**
	 * Constant representing a horizontal orientation.
	 */
	public static final int HORIZONTAL = 1;

	/**
	 * Constant representing a vertical orientation.
	 */
	public static final int VERTICAL = 2;

	private int orientation = HORIZONTAL;

	/**
	 * Constructs a new {@link BoxLayoutAlgorithm} with horizontal orientation.
	 */
	public BoxLayoutAlgorithm() {
	}

	/**
	 * Constructs a new {@link BoxLayoutAlgorithm} with the given orientation.
	 * 
	 * @param orientation
	 *            Either {@link #HORIZONTAL} or {@link #VERTICAL}.
	 * @throws RuntimeException
	 *             when the given <i>orientation</i> is neither
	 *             {@link #HORIZONTAL} nor {@link #VERTICAL}.
	 */
	public BoxLayoutAlgorithm(int orientation) {
		setOrientation(orientation);
	}

	/**
	 * Returns the orientation of this {@link BoxLayoutAlgorithm}, either
	 * {@link #HORIZONTAL} or {@link #VERTICAL}.
	 * 
	 * @return The orientation of this {@link BoxLayoutAlgorithm}.
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * Changes the orientation of this {@link BoxLayoutAlgorithm} to the given
	 * value, which may either be {@link #HORIZONTAL} or {@link #VERTICAL}.
	 * 
	 * @param orientation
	 *            The new orientation for this {@link BoxLayoutAlgorithm}.
	 * @throws RuntimeException
	 *             when the given <i>orientation</i> is neither
	 *             {@link #HORIZONTAL} nor {@link #VERTICAL}.
	 */
	public void setOrientation(int orientation) {
		if (orientation == HORIZONTAL || orientation == VERTICAL)
			this.orientation = orientation;
		else
			throw new RuntimeException("Invalid orientation: " + orientation);
	}

	protected int[] calculateNumberOfRowsAndCols(int numChildren, double boundX,
			double boundY, double boundWidth, double boundHeight) {
		if (orientation == HORIZONTAL)
			return new int[] { numChildren, 1 };
		else
			return new int[] { 1, numChildren };
	}
}
