package org.eclipse.gef4.mvc.fx;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.anchors.AbstractAnchor;

public abstract class AbstractFXAnchor extends AbstractAnchor<Node> {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private ChangeListener<Bounds> boundsListener = new ChangeListener<Bounds>() {

		@Override
		public void changed(ObservableValue<? extends Bounds> observable,
				Bounds oldValue, Bounds newValue) {
			// compute new position and notify listeners
			Point oldPosition = positionInScene;
			positionInScene = null; // clear cached value
			Point newPosition = getPosition(); // initiate a re-calculation
			propertyChangeSupport.firePropertyChange(POSITION_PROPERTY, oldPosition, newPosition);
		}
	};
	
	private Point positionInScene;
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);		
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);		
	}
	
	@Override
	public Point getPosition() {
		if(positionInScene == null){
			positionInScene = calculatePositionInScene();
		}
		return positionInScene;
	}
	
	protected abstract Point calculatePositionInScene();

	@Override
	public void setAnchorage(Node anchorage) {
		Node oldAnchorage = getAnchorage();
		if(oldAnchorage != null){
			// unregister listeners
			unregisterLayoutListener(oldAnchorage);
			
		}
		super.setAnchorage(anchorage);
		if(anchorage != null){
			// register listeners
			registerLayoutListeners(anchorage);
		}
	}

	private void registerLayoutListeners(Node anchorage) {
		// add listeners all the way up the hierarchy (TODO: stop at root visual??)
		Node current = anchorage;
		current.layoutBoundsProperty().addListener(boundsListener);
		while(current != null) {
			current.boundsInParentProperty().addListener(boundsListener);
			current = current.getParent();
		}		
	}

	private void unregisterLayoutListener(Node oldAnchorage) {
		// remove listeners all the way up the hierarchy (TODO: stop at root visual??)
		Node current = oldAnchorage;
		current.layoutBoundsProperty().removeListener(boundsListener);
		while(current != null && current.getParent() != null) {
			current.boundsInParentProperty().removeListener(boundsListener);
			current = current.getParent();
		}		
	}

	
}
