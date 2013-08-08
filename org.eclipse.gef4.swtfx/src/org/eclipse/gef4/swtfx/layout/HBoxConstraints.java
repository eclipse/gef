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

/**
 * For every child-node of an {@link HBox}, an instance of HBoxConstraints is
 * used to specify {@link #margin}, {@link #growPriority},
 * {@link #shrinkPriority}, and {@link #vfill} of the node.
 * 
 * @author mwienand
 * 
 */
public class HBoxConstraints {

	/**
	 * Specifies top, bottom, left, and right margins.
	 */
	private Insets margin;

	/**
	 * The node with highest shrink priority is resized below its preferred size
	 * first, when the {@link HBox} does not have enough space for all children.
	 */
	private double shrinkPriority;

	/**
	 * The node with highest grow priority is resized above its preferred size
	 * first, when the {@link HBox} has excess space available.
	 */
	private double growPriority;

	/**
	 * If vfill is set to <code>true</code>, the node will occupy the full
	 * height of the HBox, otherwise it will resize to its preferred height at
	 * maximum.
	 */
	private boolean vfill;

	public HBoxConstraints() {
		margin = new Insets();
		shrinkPriority = 0;
		growPriority = 0;
		vfill = false;
	}

	/**
	 * Returns the {@link #growPriority} associated with this HBoxConstraints.
	 * 
	 * @return the {@link #growPriority}
	 */
	public double getGrowPriority() {
		return growPriority;
	}

	/**
	 * Returns the {@link #margin} associated with this HBoxConstraints.
	 * 
	 * @return the {@link #margin}
	 */
	public Insets getMargin() {
		return margin;
	}

	/**
	 * Returns the {@link #shrinkPriority} associated with this HBoxConstraints.
	 * 
	 * @return the {@link #shrinkPriority}
	 */
	public double getShrinkPriority() {
		return shrinkPriority;
	}

	/**
	 * @return <code>true</code> if the node should fill the full available
	 *         height, otherwise <code>false</code>
	 */
	public boolean isVFill() {
		return vfill;
	}

	/**
	 * Sets the {@link #growPriority}.
	 * 
	 * @param growPriority
	 */
	public void setGrowPriority(double growPriority) {
		this.growPriority = growPriority;
	}

	/**
	 * Sets the {@link #margin}.
	 * 
	 * @param margin
	 */
	public void setMargin(Insets margin) {
		this.margin = margin;
	}

	/**
	 * Sets the {@link #shrinkPriority}.
	 * 
	 * @param shrinkPriority
	 */
	public void setShrinkPriority(double shrinkPriority) {
		this.shrinkPriority = shrinkPriority;
	}

}
