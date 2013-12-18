package org.eclipse.gef4.mvc.parts;

import java.util.List;

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
		// handles do not support children
		throw new UnsupportedOperationException(
				"IHandleParts do not support children");
	}

	@Override
	protected void removeChildVisual(IVisualPart<V> child) {
		// handles do not support children
		throw new UnsupportedOperationException(
				"IHandleParts do not support children");
	}

	@Override
	protected void linkAnchoredVisual(IVisualPart<V> anchored) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support anchoreds");
	}

	@Override
	protected void unlinkAnchoredVisual(IVisualPart<V> anchored) {
		throw new UnsupportedOperationException(
				"IHandleParts do not support anchoreds");
	}

}
