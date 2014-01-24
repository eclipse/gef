package org.eclipse.gef4.mvc.policies;


public abstract class AbstractResizeRelocatePolicy<V> extends AbstractEditPolicy<V> {

	public abstract void initResizeRelocate();

	public abstract void performResizeRelocate(double dx, double dy, double dw, double dh);

	public abstract void commitResizeRelocate(double dx, double dy, double dw, double dh);
	
}
