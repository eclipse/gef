package org.eclipse.gef4.mvc.fx.parts;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.parts.AbstractHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;

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
	
	@Override
	public void activate() {
		super.activate();
		for (IContentPart<Node> target : getTargetContentParts()) {
			target.getVisual().layoutXProperty()
					.addListener(positionChangeListener);
			target.getVisual().layoutYProperty()
					.addListener(positionChangeListener);
			target.getVisual().layoutBoundsProperty()
					.addListener(boundsChangeListener);
		}
	}

	@Override
	public void deactivate() {
		for (IContentPart<Node> target : getTargetContentParts()) {
			target.getVisual().layoutXProperty()
					.removeListener(positionChangeListener);
			target.getVisual().layoutYProperty()
					.removeListener(positionChangeListener);
			target.getVisual().layoutBoundsProperty()
					.removeListener(boundsChangeListener);
		}
		super.deactivate();
	}
	
}
