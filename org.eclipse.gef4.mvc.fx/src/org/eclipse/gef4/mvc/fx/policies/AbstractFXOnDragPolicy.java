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

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * An {@link AbstractFXOnDragPolicy} is called upon mouse drag events by the
 * {@link FXClickDragTool}. You can use it as an adapter on any
 * {@link IVisualPart} for which mouse drag interaction is desired, and you can
 * also register multiple instances of {@link AbstractFXOnDragPolicy} on the
 * same {@link IVisualPart} (with different adapter roles).
 *
 * @author anyssen
 *
 */
public abstract class AbstractFXOnDragPolicy extends AbstractPolicy<Node> {

	/**
	 * This callback method is invoked when the mouse is moved while a button is
	 * pressed.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 * @param delta
	 *            The mouse offset since {@link #press(MouseEvent)} (in pixel).
	 */
	public abstract void drag(MouseEvent e, Dimension delta);

	/**
	 * This callback method is invoked when a mouse button is pressed on the
	 * {@link #getHost() host}.
	 *
	 * @param e
	 *            The original {@link MouseEvent}
	 */
	public abstract void press(MouseEvent e);

	/**
	 * This callback method is invoked when the previously pressed mouse button
	 * is released.
	 *
	 * @param e
	 *            The original {@link MouseEvent}.
	 * @param delta
	 *            The mouse offset since {@link #press(MouseEvent)} (in pixel).
	 */
	public abstract void release(MouseEvent e, Dimension delta);

}
