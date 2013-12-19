package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.gef4.mvc.anchors.IAnchor;

public abstract class AbstractHandlePart<V> extends AbstractVisualPart<V>
		implements IHandlePart<V> {

	private List<IContentPart<V>> targetContentParts;

	@Override
	public List<IContentPart<V>> getTargetContentParts() {
		return targetContentParts;
	}

	@Override
	public void setTargetContentParts(List<IContentPart<V>> targetContentParts) {
		this.targetContentParts = targetContentParts;
	}

	@Override
	protected void addChildVisual(IVisualPart<V> child, int index) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support children");
	}

	@Override
	protected void removeChildVisual(IVisualPart<V> child) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support this");
	}

	@Override
	public void attachVisualToAnchorageVisual(IAnchor<V> anchor) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support this");
	}

	@Override
	public void detachVisualFromAnchorageVisual(IAnchor<V> anchor) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support this");
	}

	@Override
	protected IAnchor<V> getAnchor(IVisualPart<V> anchored) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support this");
	}

}
