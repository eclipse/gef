package org.eclipse.gef4.mvc.anchors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.geometry.planar.Point;

public abstract class AbstractAnchor<V> implements IAnchor<V> {

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
	
	private Map<V, Point> positions = new HashMap<V, Point>();
	
	private V anchorage;
	private List<V> anchoreds;

	@Override
	public V getAnchorage() {
		return anchorage;
	}

	@Override
	public void setAnchorage(V anchorage) {
		this.anchorage = anchorage;
		updatePositons();
	}
	
	@Override
	public void addAnchored(V anchored) {
		if(anchoreds == null){
			anchoreds = new ArrayList<V>();
		}
		anchoreds.add(anchored);
		updatePositons();
	}
	
	@Override
	public void removeAnchored(V anchored) {
		positions.remove(anchored);
		anchoreds.remove(anchored);
		if(anchoreds.isEmpty()){
			anchoreds = null;
		}
	}
	
	@Override
	public List<V> getAnchoreds() {
		if(anchoreds == null){
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(anchoreds);
	}

	protected void updatePositons(){
		for(V anchored : getAnchoreds()){
			Point oldPosition = positions.get(anchored);
			Point newPosition = calculatePosition(anchored); // initiate a re-calculation
			if (oldPosition == null ? newPosition != null : !oldPosition
					.equals(newPosition)) {
				positions.put(anchored, newPosition);
				propertyChangeSupport.fireIndexedPropertyChange(POSITIONS_PROPERTY, anchoreds.indexOf(anchored),
						oldPosition, newPosition);
			}
		}
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	@Override
	public Point getPosition(V anchored) {
		return positions.get(anchored);
	}
	
	protected abstract Point calculatePosition(V anchored);
}
