package org.eclipse.gef4.mvc.anchors;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.IPropertyChangeSupport;

public interface IAnchor<V> extends IPropertyChangeSupport {

	// TODO: create dedicated interface to notify that position is invalidated and has to be re-retrieved via the getPosition callback. 
	public final static String REPRESH = "anchorageReferenceShape";

	V getAnchorage();

	void setAnchorage(V anchorage);

	Point getPosition(V anchored, Point referencePosition);
	
}
