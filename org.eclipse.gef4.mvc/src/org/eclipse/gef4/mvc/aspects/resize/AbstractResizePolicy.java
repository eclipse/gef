package org.eclipse.gef4.mvc.aspects.resize;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.policies.AbstractEditPolicy;

public abstract class AbstractResizePolicy<V> extends AbstractEditPolicy<V> {

	public abstract void initResize(Point position);

	public abstract void performResize(Point position);

	public abstract void commitResize(Point position);
}
