/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.transform.Affine;

import org.eclipse.gef4.fx.nodes.ScrollPaneEx;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.ViewportModel;

public class FXViewportBehavior extends AbstractBehavior<Node> implements
		PropertyChangeListener {

	protected final Affine contentsTx = new Affine();
	private ViewportModel viewportModel;
	private final ChangeListener<Number> translateXListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			// IMPORTANT: When setting the tx, the ScrollPane will convert the
			// tx into a scrollbar position and convert that scrollbar position
			// back into a new tx. If those tx values differ, then we could
			// possibly run into a StackOverflow error, reciprocative updating
			// the viewport model and the ScrollPane.
			double stx = getScrollPane().computeTx(
					getScrollPane().computeHv(viewportModel.getTranslateX()));
			if (stx != newValue.doubleValue()) {
				viewportModel.setTranslateX(newValue.doubleValue());
			}
		}
	};
	private final ChangeListener<Number> translateYListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			// IMPORTANT: When setting the ty, the ScrollPane will convert the
			// ty into a scrollbar position and convert that scrollbar position
			// back into a new ty. If those ty values differ, then we could
			// possibly run into a StackOverflow error, reciprocative updating
			// the viewport model and the ScrollPane.
			double sty = getScrollPane().computeTy(
					getScrollPane().computeVv(viewportModel.getTranslateY()));
			if (sty != newValue.doubleValue()) {
				viewportModel.setTranslateY(newValue.doubleValue());
			}
		}
	};
	private final ChangeListener<Number> widthListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldWidth, Number newWidth) {
			viewportModel.setWidth(newWidth.doubleValue());
		}
	};
	private final ChangeListener<Number> heightListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldHeight, Number newHeight) {
			viewportModel.setHeight(newHeight.doubleValue());
		}
	};

	@Override
	public void activate() {
		super.activate();
		viewportModel = getHost().getRoot().getViewer()
				.getAdapter(ViewportModel.class);
		viewportModel.addPropertyChangeListener(this);
		getScrollPane().widthProperty().addListener(widthListener);
		getScrollPane().heightProperty().addListener(heightListener);
		getScrollPane().getCanvas().translateXProperty()
				.addListener(translateXListener);
		getScrollPane().getCanvas().translateYProperty()
				.addListener(translateYListener);
	}

	protected void applyViewport(double translateX, double translateY,
			double width, double height, AffineTransform contentsTransform) {
		getScrollPane().setScrollOffsetX(translateX);
		getScrollPane().setScrollOffsetY(translateY);
		getScrollPane().setPrefWidth(width);
		getScrollPane().setPrefHeight(height);
		setTx(contentsTx, contentsTransform);
		getScrollPane().setViewportTransform(contentsTx);
	}

	@Override
	public void deactivate() {
		viewportModel.removePropertyChangeListener(this);
		getScrollPane().widthProperty().removeListener(widthListener);
		getScrollPane().heightProperty().removeListener(heightListener);
		getScrollPane().getCanvas().translateXProperty()
				.removeListener(translateXListener);
		getScrollPane().getCanvas().translateYProperty()
				.removeListener(translateYListener);
		super.deactivate();
	}

	protected ScrollPaneEx getScrollPane() {
		return ((FXViewer) getHost().getRoot().getViewer()).getScrollPane();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (ViewportModel.VIEWPORT_TRANSLATE_X_PROPERTY.equals(evt
				.getPropertyName())
				|| ViewportModel.VIEWPORT_TRANSLATE_Y_PROPERTY.equals(evt
						.getPropertyName())
				|| ViewportModel.VIEWPORT_WIDTH_PROPERTY.equals(evt
						.getPropertyName())
				|| ViewportModel.VIEWPORT_HEIGHT_PROPERTY.equals(evt
						.getPropertyName())
				|| ViewportModel.VIEWPORT_CONTENTS_TRANSFORM_PROPERTY
						.equals(evt.getPropertyName())) {
			applyViewport(viewportModel.getTranslateX(),
					viewportModel.getTranslateY(), viewportModel.getWidth(),
					viewportModel.getHeight(),
					viewportModel.getContentsTransform());
		}
	}

	protected void setTx(Affine tx, AffineTransform at) {
		double[] m = at.getMatrix();
		tx.setMxx(m[0]);
		tx.setMxy(m[1]);
		tx.setMyx(m[2]);
		tx.setMyy(m[3]);
		tx.setTx(m[4]);
		tx.setTy(m[5]);
	}

}
