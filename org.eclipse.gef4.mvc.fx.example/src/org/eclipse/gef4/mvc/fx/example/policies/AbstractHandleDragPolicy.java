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
package org.eclipse.gef4.mvc.fx.example.policies;

import javafx.scene.Node;
import javafx.scene.input.MouseButton;

import org.eclipse.gef4.mvc.policies.AbstractEditPolicy;

public abstract class AbstractHandleDragPolicy extends AbstractEditPolicy<Node> {
	
	public abstract void init(MouseButton mouseButton);
	
	public abstract void perform(double dx, double dy);
	
	public abstract void commit(double dx, double dy);

}
