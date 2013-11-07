package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.geometry.planar.Point;

public interface IDragPolicy<V> extends IEditPolicy<V> {

	public void initDrag(Point initialAbsolutePosition);

	public void commitDrag(Point newAbsolutePosition);
}
