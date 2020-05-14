/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.behaviors;

import org.eclipse.gef.common.collections.MultisetChangeListener;
import org.eclipse.gef.layout.LayoutContext;
import org.eclipse.gef.mvc.fx.behaviors.AbstractBehavior;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
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

	private MultisetChangeListener<IVisualPart<? extends Node>> anchoredsChangeListener = new MultisetChangeListener<IVisualPart<? extends Node>>() {

		@Override
		public void onChanged(
				org.eclipse.gef.common.collections.MultisetChangeListener.Change<? extends IVisualPart<? extends Node>> change) {
			boolean isRelevantChange = false;
			while (change.next()) {
				if (change.getElement() instanceof IContentPart) {
					isRelevantChange = true;
					break;
				}
			}
			if (isRelevantChange) {
				layoutLabels();
			}
		}
	};

	@Override
	protected void doActivate() {
		super.doActivate();
		IContentPart<? extends Node> cp = (IContentPart<? extends Node>) getHost();
		getHost().getAnchoredsUnmodifiable().addListener(anchoredsChangeListener);
	}

	@Override
	protected void doDeactivate() {
		getHost().getAnchoredsUnmodifiable().removeListener(anchoredsChangeListener);
		super.doDeactivate();
	}

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
	 * Called after a layout pass to adjust label positions.
	 */
	protected void layoutLabels() {
		// iterate anchoreds
		for (IVisualPart<? extends Node> anchored : getHost().getAnchoredsUnmodifiable().elementSet()) {
			// FIXME: Layout should only be triggered when content-part-map
			// is changed, not when the children are changed.
			if (anchored.getViewer() == null) {
				continue;
			}

			// filter for label parts
			if (anchored instanceof AbstractLabelPart) {
				AbstractLabelPart labelPart = (AbstractLabelPart) anchored;
				// compute label position
				labelPart.recomputeLabelPosition();
			}
		}
	}

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

}
