package org.eclipse.gef4.mvc.anchors;

public abstract class AbstractAnchor<V> implements IAnchor<V> {

	private V anchorage;

	@Override
	public V getAnchorage() {
		return anchorage;
	}

	@Override
	public void setAnchorage(V anchorage) {
		this.anchorage = anchorage;
	}

}
