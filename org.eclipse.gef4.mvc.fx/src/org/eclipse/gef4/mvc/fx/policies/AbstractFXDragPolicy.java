/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public abstract class AbstractFXDragPolicy extends AbstractPolicy<Node> {
	
	public abstract void press(Point mouseLocation);
	
	public abstract void drag(Point mouseLocation, Dimension delta);
	
	public abstract void release(Point mouseLocation, Dimension delta);
}
