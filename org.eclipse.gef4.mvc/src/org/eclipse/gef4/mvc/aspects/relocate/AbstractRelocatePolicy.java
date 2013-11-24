package org.eclipse.gef4.mvc.aspects.relocate;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.policies.AbstractEditPolicy;

public abstract class AbstractRelocatePolicy<V> extends AbstractEditPolicy<V> {

	public abstract void initRelocate(Point initialAbsolutePosition);

	public abstract void performRelocate(Point initialAbsolutePosition);

	public abstract void commitRelocate(Point newAbsolutePosition);
}
