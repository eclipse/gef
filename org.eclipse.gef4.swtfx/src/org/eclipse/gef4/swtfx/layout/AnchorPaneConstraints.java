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
package org.eclipse.gef4.swtfx.layout;

public class AnchorPaneConstraints {

	/*
	 * Attributes declared as Double to allow <code>null</code> which represents
	 * the absence of a constraint.
	 */

	private Double left;
	private Double right;
	private Double top;
	private Double bottom;

	/**
	 * Constructs a new {@link AnchorPaneConstraints} without constraints for
	 * the top, left, bottom, and right sides.
	 */
	public AnchorPaneConstraints() {
	}

	/**
	 * Constructs a new {@link AnchorPaneConstraints} with the specified
	 * constraints for the top, left, bottom, and right sides. <code>null</code>
	 * represents the absence of a constraint.
	 * 
	 * @param left
	 *            distance to left side
	 * @param bottom
	 *            distance to bottom
	 * @param right
	 *            distance to right side
	 * @param top
	 *            distance to top
	 */
	public AnchorPaneConstraints(Double left, Double bottom, Double right,
			Double top) {
		setLeft(left);
		setBottom(bottom);
		setRight(right);
		setTop(top);
	}

	public Double getBottom() {
		return bottom;
	}

	public Double getLeft() {
		return left;
	}

	public Double getRight() {
		return right;
	}

	public Double getTop() {
		return top;
	}

	public void setBottom(Double bottom) {
		this.bottom = bottom;
	}

	public void setLeft(Double left) {
		this.left = left;
	}

	public void setRight(Double right) {
		this.right = right;
	}

	public void setTop(Double top) {
		this.top = top;
	}

}
