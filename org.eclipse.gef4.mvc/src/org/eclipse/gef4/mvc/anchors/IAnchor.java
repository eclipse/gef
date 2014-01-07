package org.eclipse.gef4.mvc.anchors;

import java.util.List;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.IPropertyChangeSupport;

public interface IAnchor<V> extends IPropertyChangeSupport {

	// fires indexed property change
	public final static String POSITIONS_PROPERTY = "positions";

	V getAnchorage();

	void setAnchorage(V anchorage);

	List<V> getAnchoreds();

	void addAnchored(V anchored);

	void removeAnchored(V anchored);
	
	Point getPosition(V anchored);
}
