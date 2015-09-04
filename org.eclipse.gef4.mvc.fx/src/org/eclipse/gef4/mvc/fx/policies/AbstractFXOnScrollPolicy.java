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

import org.eclipse.gef4.mvc.fx.tools.FXScrollTool;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

import javafx.scene.Node;
import javafx.scene.input.ScrollEvent;

/**
 * An {@link AbstractFXOnScrollPolicy} is called upon mouse scroll events by the
 * {@link FXScrollTool}. You can use it as an adapter on any {@link IVisualPart}
 * for which mouse scroll interaction is desired, and you can also register
 * multiple instances of {@link AbstractFXOnScrollPolicy} on the same
 * {@link IVisualPart} (with different adapter roles).
 *
 * @author anyssen
 *
 */
public abstract class AbstractFXOnScrollPolicy extends AbstractPolicy<Node> {

	/**
	 * This callback method is invoked when the user performs mouse scrolling
	 * over the {@link #getHost() host}.
	 *
	 * @param event
	 *            The original {@link ScrollEvent}.
	 */
	public abstract void scroll(ScrollEvent event);
}
