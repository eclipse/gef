/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.gef4.mvc.fx.tools.FXTypeTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;

/**
 * An {@link AbstractFXOnTypePolicy} is called upon keyboard events by the
 * {@link FXTypeTool}. You can use it as an adapter on any {@link IVisualPart}
 * for which keyboard interaction is desired, and you can also register multiple
 * instances of {@link AbstractFXOnTypePolicy} on the same {@link IVisualPart}
 * (with different adapter roles).
 *
 * @author anyssen
 *
 */
public abstract class AbstractFXOnTypePolicy extends AbstractPolicy<Node> {

	/**
	 * This callback method is invoked when the user presses a key while the
	 * {@link #getHost() host} has keyboard focus.
	 *
	 * @param event
	 *            The original {@link KeyEvent}.
	 */
	public abstract void pressed(KeyEvent event);

	/**
	 * This callback method is invoked when the user releases a key while the
	 * {@link #getHost() host} has keyboard focus.
	 *
	 * @param event
	 *            The original {@link KeyEvent}.
	 */
	public abstract void released(KeyEvent event);

}
