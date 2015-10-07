package org.eclipse.gef4.mvc.examples.logo.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.models.ViewportModel;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.Node;

public class FXClickableAreaBehavior extends AbstractBehavior<Node> {

	private static final double ABSOLUTE_CLICKABLE_WIDTH = 5;

	private final PropertyChangeListener zoomListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ViewportModel.VIEWPORT_CONTENTS_TRANSFORM_PROPERTY
					.equals(evt.getPropertyName())) {
				clickableAreaBinding.invalidate();
			}
		}
	};
	private DoubleBinding clickableAreaBinding;

	@Override
	public void activate() {
		clickableAreaBinding = new DoubleBinding() {
			@Override
			protected double computeValue() {
				double localClickableWidth = ABSOLUTE_CLICKABLE_WIDTH
						/ getHost().getRoot().getViewer()
								.getAdapter(ViewportModel.class)
								.getContentsTransform().getScaleX();
				return Math.min(localClickableWidth, ABSOLUTE_CLICKABLE_WIDTH);
			}
		};
		getHost().getVisual().clickableAreaWidthProperty()
				.bind(clickableAreaBinding);
		getHost().getRoot().getViewer().getAdapter(ViewportModel.class)
				.addPropertyChangeListener(zoomListener);
	}

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getAdapter(ViewportModel.class)
				.removePropertyChangeListener(zoomListener);
		clickableAreaBinding.dispose();
		super.deactivate();
	}

	@Override
	public FXGeometricCurvePart getHost() {
		return (FXGeometricCurvePart) super.getHost();
	}
}