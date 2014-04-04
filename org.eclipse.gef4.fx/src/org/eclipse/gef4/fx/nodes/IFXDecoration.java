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
package org.eclipse.gef4.fx.nodes;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;

public interface IFXDecoration {

	/**
	 * Returns the start point of this decoration in the local coordinate space
	 * of its {@link #getVisual() visual}.
	 * 
	 * @return the (local) start point of this decoration
	 */
	public Point getLocalStartPoint();

	/**
	 * Returns the end point of this decoration in the local coordinate space of
	 * its {@link #getVisual() visual}.
	 * 
	 * @return the (local) end point of this decoration
	 */
	public Point getLocalEndPoint();

	/**
	 * Returns the decoration's visual, for example, an arrow shape.
	 * 
	 * @return the decoration's visual
	 */
	public Node getVisual();

}
