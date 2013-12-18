package org.eclipse.gef4.mvc.anchors;

import java.util.List;

public interface IAnchor<V> {

	V getAnchorage();

	void setAnchorage(V anchorage);

	List<V> getAnchored();

	void addAnchored(V anchored);

	void removeAnchored(V anchored);
}
