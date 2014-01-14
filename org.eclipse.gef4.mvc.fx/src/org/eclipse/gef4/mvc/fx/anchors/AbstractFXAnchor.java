package org.eclipse.gef4.mvc.fx.anchors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.anchors.AbstractAnchor;
import org.eclipse.gef4.mvc.anchors.IAnchor;

public abstract class AbstractFXAnchor extends AbstractAnchor<Node> {

	private ChangeListener<Bounds> boundsListener = new ChangeListener<Bounds>() {
		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			propertyChangeSupport.firePropertyChange(IAnchor.REPRESH, null,
					null);
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

	private void registerLayoutListeners(Node anchorageOrAnchored) {
		// add bounds-in-parent listeners to the whole hierarchy to witness node
		// resizing and container movement
		Node current = anchorageOrAnchored;
		current.boundsInParentProperty().addListener(boundsListener);
//		while (current != null && current.getParent() != null) {
//			current.boundsInParentProperty().addListener(boundsListener);
//			current = current.getParent();
//		}
	}

	private void unregisterLayoutListener(Node anchorageOrAnchored) {
		// remove the previously added listeners
		Node current = anchorageOrAnchored;
		current.boundsInParentProperty().removeListener(boundsListener);
//		while (current != null && current.getParent() != null) {
//			current.boundsInParentProperty().removeListener(boundsListener);
//			current = current.getParent();
//		}
	}

}
