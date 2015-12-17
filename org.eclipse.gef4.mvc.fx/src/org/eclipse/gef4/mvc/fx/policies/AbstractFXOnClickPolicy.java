/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.scene.input.MouseEvent;

/**
 * An {@link AbstractFXOnClickPolicy} is called upon mouse click events by the
 * {@link FXClickDragTool}. You can use it as an adapter on any
 * {@link IVisualPart} for which mouse click interaction is desired, and you can
 * also register multiple instances of {@link AbstractFXOnClickPolicy} on the
 * same {@link IVisualPart} (with different adapter roles).
 *
 * @author anyssen
 *
 */
public abstract class AbstractFXOnClickPolicy
		extends AbstractFXInteractionPolicy {

	/**
	 * This callback method is invoked when the user performs a mouse click on
	 * the {@link #getHost() host}.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 */
	public abstract void click(MouseEvent e);

}
