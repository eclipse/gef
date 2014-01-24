package org.eclipse.gef4.mvc.policies;


public abstract class AbstractSelectionPolicy<V> extends AbstractEditPolicy<V> {

	public abstract void selectPrimary();

	public abstract void selectSecondary();

	public abstract void deselect();
	
}
