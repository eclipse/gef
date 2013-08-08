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

public class BorderPaneConstraints {

	private Pos align = Pos.TOP_LEFT;

	// private Insets margin;

	public BorderPaneConstraints() {
	}

	public BorderPaneConstraints(Pos alignment) {
		align = alignment;
	}

	public Pos getAlignment() {
		return align;
	}

	public void setAlignment(Pos alignment) {
		if (alignment == null) {
			throw new IllegalArgumentException("Alignment may not be null.");
		}
		align = alignment;
	}

}
