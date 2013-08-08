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
 * For every child-node of a {@link VBox}, an instance of VBoxConstraints is
 * used to specify {@link #margin}, {@link #growPriority},
 * {@link #shrinkPriority}, and {@link #hfill} of the node.
 * 
 * @author mwienand
 * 
 */
public class VBoxConstraints {

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
	 * If hfill is set to <code>true</code>, the node will occupy the full width
	 * of the VBox, otherwise it will resize to its preferred width at maximum.
	 */
	private boolean hfill;

	public VBoxConstraints() {
		margin = new Insets();
		shrinkPriority = 0;
		growPriority = 0;
		hfill = false;
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
	 *         width, otherwise <code>false</code>
	 */
	public boolean isHFill() {
		return hfill;
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
