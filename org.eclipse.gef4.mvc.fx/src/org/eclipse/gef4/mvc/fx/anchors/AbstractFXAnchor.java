package org.eclipse.gef4.mvc.fx.anchors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.anchors.AbstractAnchor;

public abstract class AbstractFXAnchor extends AbstractAnchor<Node> {

	
	private ChangeListener<Bounds> boundsListener = new ChangeListener<Bounds>() {

		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			updatePositons();
		}
	};
	
	@Override
	public void setAnchorage(Node anchorage) {
		Node oldAnchorage = getAnchorage();
		if (oldAnchorage != null) {
			// unregister listeners
			unregisterLayoutListener(oldAnchorage);

		}
		super.setAnchorage(anchorage);
		if (anchorage != null) {
			// register listeners
			registerLayoutListeners(anchorage);
		}
	}

	@Override
	public void addAnchored(Node anchored) {
		registerLayoutListeners(anchored);
		super.addAnchored(anchored);
	}

	@Override
	public void removeAnchored(Node anchored) {
		super.removeAnchored(anchored);
		unregisterLayoutListener(anchored);
	}

	private void registerLayoutListeners(Node anchorageOrAnchored) {
		// add listeners all the way up the hierarchy (TODO: stop at root
		// visual??)
		Node current = anchorageOrAnchored;
		current.layoutBoundsProperty().addListener(boundsListener);
		while (current != null) {
			current.boundsInParentProperty().addListener(boundsListener);
			current = current.getParent();
		}
	}

	private void unregisterLayoutListener(Node anchorageOrAnchored) {
		// remove listeners all the way up the hierarchy (TODO: stop at root
		// visual??)
		Node current = anchorageOrAnchored;
		current.layoutBoundsProperty().removeListener(boundsListener);
		while (current != null && current.getParent() != null) {
			current.boundsInParentProperty().removeListener(boundsListener);
			current = current.getParent();
		}
	}

}
