/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.behaviors;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.layout.LayoutContext;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.parts.AbstractLabelPart;

import javafx.scene.Node;

/**
 * The {@link AbstractLayoutBehavior} is an abstract behavior that schedules
 * {@link #provideToLayout()} and {@link #adaptFromLayout()} to be called before
 * or after a layout pass, respectively. The {@link #provideToLayout()} method
 * can be used to write layout information into the layout model. Similarly, the
 * {@link #adaptFromLayout()} method can be used to read layout information from
 * the layout model.
 *
 * @author mwienand
 *
 */
public abstract class AbstractLayoutBehavior extends AbstractBehavior<Node> {

	private Runnable adaptFromLayout = new Runnable() {
		@Override
		public void run() {
			adaptFromLayout();
		}
	};

	private Runnable provideToLayout = new Runnable() {
		@Override
		public void run() {
			provideToLayout();
		}
	};

	/**
	 * Called after a layout pass. Should be used to transfer layout information
	 * from the layout model over to the visualization.
	 */
	protected abstract void adaptFromLayout();

	@Override
	protected void doActivate() {
		LayoutContext layoutContext = getLayoutContext();
		layoutContext.schedulePreLayoutPass(provideToLayout);
		layoutContext.schedulePostLayoutPass(adaptFromLayout);
	}

	@Override
	protected void doDeactivate() {
		LayoutContext layoutContext = getLayoutContext();
		layoutContext.unschedulePreLayoutPass(provideToLayout);
		layoutContext.unschedulePostLayoutPass(adaptFromLayout);
	}

	/**
	 * Returns the {@link LayoutContext} for which {@link #provideToLayout()}
	 * and {@link #adaptFromLayout()} shall be called before or after a layout
	 * pass, respectively.
	 *
	 * @return The {@link LayoutContext} for which {@link #provideToLayout()}
	 *         and {@link #adaptFromLayout()} shall be called before or after a
	 *         layout pass, respectively.
	 */
	protected abstract LayoutContext getLayoutContext();

	/**
	 * Called before a layout pass. Should be used to transfer layout
	 * information from the visualization over to the layout model.
	 */
	protected abstract void provideToLayout();

	/**
	 * Called after all layout behaviors had the chance to adapt to the layout.
	 * Should be used to update the label positions for the new layout.
	 */
	protected void updateLabels() {
		// iterate anchoreds
		for (IVisualPart<Node, ? extends Node> anchored : getHost().getAnchoredsUnmodifiable().elementSet()) {
			// filter for label parts
			if (anchored instanceof AbstractLabelPart) {
				AbstractLabelPart labelPart = (AbstractLabelPart) anchored;
				// compute label position
				Point computedPosition = labelPart.computeLabelPosition();
				// store it as an attribute
				labelPart.setStoredLabelPosition(computedPosition);
				labelPart.refreshVisual();
			}
		}
	}

}
