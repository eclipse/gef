package org.eclipse.gef4.mvc.aspects.dragging;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.policies.IEditPolicy;

public interface IDragPolicy<V> extends IEditPolicy<V> {

	public void initDrag(Point initialAbsolutePosition);

	public void commitDrag(Point newAbsolutePosition);
}
