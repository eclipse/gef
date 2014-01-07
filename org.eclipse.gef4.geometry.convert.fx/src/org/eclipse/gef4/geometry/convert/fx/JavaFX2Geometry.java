/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *    
 *******************************************************************************/
package org.eclipse.gef4.geometry.convert.fx;

import javafx.geometry.Bounds;

import org.eclipse.gef4.geometry.planar.Rectangle;

public class JavaFX2Geometry {

	private JavaFX2Geometry() {
		// this class should not be instantiated by clients
	}

	public static final Rectangle toRectangle(Bounds b) {
		return new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(),
				b.getHeight());
	}
}
