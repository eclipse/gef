package org.eclipse.gef4.mvc.anchors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractAnchor<V> implements IAnchor<V> {

	private V anchorage;
	private List<V> anchoreds;

	@Override
	public V getAnchorage() {
		return anchorage;
	}

	@Override
	public void setAnchorage(V anchorage) {
		this.anchorage = anchorage;
	}

	@Override
	public List<V> getAnchored() {
		if (anchoreds == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(anchoreds);
	}

	@Override
	public void addAnchored(V anchored) {
		if (anchoreds == null) {
			anchoreds = new ArrayList<V>();
		}
		anchoreds.add(anchored);
	}

	@Override
	public void removeAnchored(V anchored) {
		anchoreds.remove(anchored);
		if (anchoreds.size() == 0) {
			anchoreds = null;
		}
	}

}
