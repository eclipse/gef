package org.eclipse.gef4.mvc.tools;

import java.util.List;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IDragPolicy;

public abstract class AbstractDragTool<V> extends AbstractTool<V> {

	@SuppressWarnings("unchecked")
	protected IDragPolicy<V> getToolPolicy(IVisualPart<V> targetPart) {
		return targetPart.getPolicy(IDragPolicy.class);
	}

	protected void press(List<IVisualPart<V>> targetParts,
			Point mouseLocation) {
		
		for (IVisualPart<V> targetPart : targetParts) {
			IDragPolicy<V> policy = getToolPolicy(targetPart);
			if (policy != null && policy.isDraggable())
				policy.press(mouseLocation);
		}
	}

	protected void drag(List<IVisualPart<V>> targetParts,
			Point mouseLocation, Dimension delta) {
		for (IVisualPart<V> targetPart : targetParts) {
			IDragPolicy<V> policy = getToolPolicy(targetPart);
			if (policy != null && policy.isDraggable())
				policy.drag(mouseLocation, delta);
		}
	}

	protected void release(List<IVisualPart<V>> targetParts,
			Point mouseLocation, Dimension delta) {
		for (IVisualPart<V> targetPart : targetParts) {
			IDragPolicy<V> policy = getToolPolicy(targetPart);
			if (policy != null && policy.isDraggable())
				policy.release(mouseLocation,
						delta);
		}
	}
}
