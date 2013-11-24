package org.eclipse.gef4.mvc.aspects.resize;

import java.util.List;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public abstract class AbstractResizeTool<V> extends AbstractTool<V> {

	@SuppressWarnings("unchecked")
	protected AbstractResizePolicy<V> getResizePolicy(IContentPart<V> editPart) {
		return editPart.getEditPolicy(AbstractResizePolicy.class);
	}

	public List<IContentPart<V>> getTargetParts() {
		return getDomain().getViewer().getContentPartSelection().getSelected();
	}

	public void initResize(Point position) {
		for (IContentPart<V> targetPart : getTargetParts()) {
			getResizePolicy(targetPart).initResize(position);
		}
	}

	public void performResize(Point position) {
		for (IContentPart<V> targetPart : getTargetParts()) {
			getResizePolicy(targetPart).performResize(position);
		}
	}

	public void commitResize(Point position) {
		for (IContentPart<V> targetPart : getTargetParts()) {
			getResizePolicy(targetPart).commitResize(position);
		}
	}

}
