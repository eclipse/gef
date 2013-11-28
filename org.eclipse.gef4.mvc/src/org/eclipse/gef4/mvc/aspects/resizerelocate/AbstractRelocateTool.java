package org.eclipse.gef4.mvc.aspects.resizerelocate;

import java.util.List;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public abstract class AbstractRelocateTool<V> extends AbstractTool<V> {

	private Point initialMouseLocation = null;
	
	@SuppressWarnings("unchecked")
	protected AbstractResizeRelocatePolicy<V> getResizeRelocatePolicy(
			IContentPart<V> editPart) {
		return editPart.getEditPolicy(AbstractResizeRelocatePolicy.class);
	}

	public List<IContentPart<V>> getTargetParts() {
		return getDomain().getViewer().getContentPartSelection().getSelected();
	}

	public void initRelocate(Point mouseLocation) {
		for (IContentPart<V> targetPart : getTargetParts()) {
			getResizeRelocatePolicy(targetPart).initResizeRelocate();
		}
	}

	public void performRelocate(Point mouseLocation) {
		Point delta = initialMouseLocation.getTranslated(mouseLocation.getNegated());
		for (IContentPart<V> targetPart : getTargetParts()) {
			getResizeRelocatePolicy(targetPart).performResizeRelocate(delta.x, delta.y, 0, 0);
		}
	}

	public void commitRelocate(Point mouseLocation) {
		Point delta = initialMouseLocation.getTranslated(mouseLocation.getNegated());
		for (IContentPart<V> targetPart : getTargetParts()) {
			getResizeRelocatePolicy(targetPart).commitResizeRelocate(delta.x, delta.y, 0, 0);
		}
	}

}
