package org.eclipse.gef4.mvc.fx.parts;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.parts.AbstractHandlePart;

abstract public class AbstractFXHandlePart extends AbstractHandlePart<Node> {

	private ChangeListener<Number> positionChangeListener = new ChangeListener<Number>() {
		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			refreshVisual();
		}
	};

	private ChangeListener<Bounds> boundsChangeListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			refreshVisual();
		}
	};

	public void attachVisualToAnchorageVisual(Node anchorageVisual,
			org.eclipse.gef4.mvc.anchors.IAnchor<Node> anchor) {
		anchorageVisual.layoutXProperty().addListener(positionChangeListener);
		anchorageVisual.layoutYProperty().addListener(positionChangeListener);
		anchorageVisual.layoutBoundsProperty()
				.addListener(boundsChangeListener);
	};

	@Override
	public void detachVisualFromAnchorageVisual(Node anchorageVisual,
			IAnchor<Node> anchor) {
		anchorageVisual.layoutXProperty()
				.removeListener(positionChangeListener);
		anchorageVisual.layoutYProperty()
				.removeListener(positionChangeListener);
		anchorageVisual.layoutBoundsProperty().removeListener(
				boundsChangeListener);
	}

}
