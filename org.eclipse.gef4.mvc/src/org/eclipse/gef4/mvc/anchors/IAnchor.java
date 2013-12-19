package org.eclipse.gef4.mvc.anchors;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.IPropertyChangeSupport;

public interface IAnchor<V> extends IPropertyChangeSupport {

	public final static String POSITION_PROPERTY = "position";

	V getAnchorage();

	void setAnchorage(V anchorage);

	Point getPosition();
}
