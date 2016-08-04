/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.behaviors;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.parts.IVisualPart;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * A behavior that regulates the clickable area width of an {@link IVisualPart}
 * 's connection visual dependent on the zoom level.
 *
 * @author anyssen
 *
 */
public class FXConnectionClickableAreaBehavior extends AbstractBehavior<Node> {

	private static final double ABSOLUTE_CLICKABLE_WIDTH = 8;
	private DoubleBinding clickableAreaBinding;

	private final ChangeListener<? super Number> scaleXListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			clickableAreaBinding.invalidate();
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void doActivate() {
		clickableAreaBinding = new DoubleBinding() {
			@Override
			protected double computeValue() {
				double localClickableWidth = ABSOLUTE_CLICKABLE_WIDTH
						/ ((FXViewer) getHost().getRoot().getViewer())
								.getCanvas().getContentTransform().getMxx();
				return Math.min(localClickableWidth, ABSOLUTE_CLICKABLE_WIDTH);
			}
		};
		// TODO: bind to the curve property of the connection and update the
		// binding in case the curve node is changed
		((GeometryNode<ICurve>) getHost().getVisual().getCurve())
				.clickableAreaWidthProperty().bind(clickableAreaBinding);
		((FXViewer) getHost().getRoot().getViewer()).getCanvas()
				.getContentTransform().mxxProperty()
				.addListener(scaleXListener);
	}

	@Override
	protected void doDeactivate() {
		clickableAreaBinding.dispose();
		((FXViewer) getHost().getRoot().getViewer()).getCanvas()
				.getContentTransform().mxxProperty()
				.removeListener(scaleXListener);
	}

	@Override
	protected String getFeedbackPartFactoryRole() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected String getHandlePartFactoryRole() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public IVisualPart<Node, Connection> getHost() {
		return (IVisualPart<Node, Connection>) super.getHost();
	}

}