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

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;

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
public abstract class AbstractLayoutBehavior extends AbstractBehavior<Node> {

	private Runnable postLayout = new Runnable() {
		@Override
		public void run() {
			postLayout();
		}
	};

	private Runnable preLayout = new Runnable() {
		@Override
		public void run() {
			preLayout();
		}
	};

	@Override
	protected void doActivate() {
		GraphLayoutContext layoutContext = getGraphLayoutContext();
		layoutContext.schedulePreLayoutPass(preLayout);
		layoutContext.schedulePostLayoutPass(postLayout);
	}

	@Override
	protected void doDeactivate() {
		GraphLayoutContext layoutContext = getGraphLayoutContext();
		layoutContext.unschedulePreLayoutPass(preLayout);
		layoutContext.unschedulePostLayoutPass(postLayout);
	}

	/**
	 * Returns the {@link GraphLayoutContext} for which {@link #preLayout()} and
	 * {@link #postLayout()} shall be called before or after a layout pass,
	 * respectively.
	 *
	 * @return The {@link GraphLayoutContext} for which {@link #preLayout()} and
	 *         {@link #postLayout()} shall be called before or after a layout
	 *         pass, respectively.
	 */
	protected abstract GraphLayoutContext getGraphLayoutContext();

	/**
	 * Called after a layout pass. Should be used to transfer layout information
	 * from the layout model over to the visualization.
	 */
	protected abstract void postLayout();

	/**
	 * Called before a layout pass. Should be used to transfer layout
	 * information from the visualization over to the layout model.
	 */
	protected abstract void preLayout();

}
