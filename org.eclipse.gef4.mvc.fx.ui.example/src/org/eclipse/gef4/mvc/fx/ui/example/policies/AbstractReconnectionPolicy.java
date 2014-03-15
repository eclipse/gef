/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.example.policies;

import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXSelectionHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public abstract class AbstractReconnectionPolicy extends AbstractPolicy<Node> {

	public abstract void loosen(int anchorIndex, Point startPointInScene, FXSelectionHandlePart handle);

	public abstract void dragTo(Point pointInScene,
			List<IContentPart<Node>> partsUnderMouse);

	public abstract void releaseAt(Point pointInScene,
			List<IContentPart<Node>> partsUnderMouse);

}
