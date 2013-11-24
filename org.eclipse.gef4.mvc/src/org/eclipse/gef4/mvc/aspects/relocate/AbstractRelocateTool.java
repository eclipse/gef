package org.eclipse.gef4.mvc.aspects.relocate;

import java.util.List;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.tools.AbstractTool;

public abstract class AbstractRelocateTool<V> extends AbstractTool<V> {

	@SuppressWarnings("unchecked")
	protected AbstractRelocatePolicy<V> getRelocatePolicy(
			IContentPart<V> editPart) {
		return editPart.getEditPolicy(AbstractRelocatePolicy.class);
	}

	public List<IContentPart<V>> getTargetParts() {
		return getDomain().getViewer().getContentPartSelection().getSelected();
	}

	public void initRelocate(Point position) {
		for (IContentPart<V> targetPart : getTargetParts()) {
			getRelocatePolicy(targetPart).initRelocate(position);
		}
	}

	public void performRelocate(Point position) {
		for (IContentPart<V> targetPart : getTargetParts()) {
			getRelocatePolicy(targetPart).performRelocate(position);
		}
	}

	public void commitRelocate(Point position) {
		for (IContentPart<V> targetPart : getTargetParts()) {
			getRelocatePolicy(targetPart).commitRelocate(position);
		}
	}

}
