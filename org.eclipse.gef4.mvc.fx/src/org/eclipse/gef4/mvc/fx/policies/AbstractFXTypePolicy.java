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
package org.eclipse.gef4.mvc.fx.policies;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;

import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public abstract class AbstractFXTypePolicy extends AbstractPolicy<Node> {

	public abstract void pressed(KeyEvent event);

	public abstract void released(KeyEvent event);

}
