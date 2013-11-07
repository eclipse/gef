package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IEditPart;

public interface IDragPolicy<V> extends IEditPolicy<V> {

	public void initDrag(IEditPart<V> editPart, Point initialAbsolutePosition);

	public void commitDrag(Point newAbsolutePosition);
}
