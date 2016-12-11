/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.behaviors;

import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.mvc.fx.behaviors.AbstractBehavior;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.zest.fx.parts.AbstractLabelPart;

import javafx.scene.Node;

/**
 * The {@link AbstractLayoutBehavior} is an abstract behavior that schedules
 * {@link #preLayout()} and {@link #postLayout()} to be called before or after a
 * layout pass, respectively. The {@link #preLayout()} method can be used to
 * write layout information into the layout model. Similarly, the
 * {@link #postLayout()} method can be used to read layout information from the
 * layout model.
 *
 * @author mwienand
 *
 */
public abstract class AbstractLayoutBehavior extends AbstractBehavior {

	/**
	 * Returns the {@link LayoutContext} for which {@link #preLayout()} and
	 * {@link #postLayout()} shall be called before or after a layout pass,
	 * respectively.
	 *
	 * @return The {@link LayoutContext} for which {@link #preLayout()} and
	 *         {@link #postLayout()} shall be called before or after a layout
	 *         pass, respectively.
	 */
	protected abstract LayoutContext getLayoutContext();

	/**
	 * Called after a layout pass. Should be used to transfer layout information
	 * from the layout model.
	 */
	protected abstract void postLayout();

	/**
	 * Called before a layout pass. Should be used to transfer layout
	 * information to the layout model.
	 */
	protected abstract void preLayout();

	/**
	 * Called before a layout pass. Should be used to transfer layout
	 * information from the visualization over to the layout model.
	 */
	protected void updateLabels() {
		// iterate anchoreds
		for (IVisualPart<? extends Node> anchored : getHost().getAnchoredsUnmodifiable().elementSet()) {
			// filter for label parts
			if (anchored instanceof AbstractLabelPart) {
				AbstractLabelPart labelPart = (AbstractLabelPart) anchored;
				// compute label position
				labelPart.setLabelPosition(labelPart.computeLabelPosition());
			}
		}
	}

}
