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

public class Insets {

	public double top, left, right, bottom;

	public Insets() {
	}

	public Insets(double top) {
		this(top, 0, 0, 0);
	}

	public Insets(double top, double right) {
		this(top, right, 0, 0);
	}

	public Insets(double top, double right, double bottom) {
		this(top, right, bottom, 0);
	}

	public Insets(double top, double right, double bottom, double left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

}
