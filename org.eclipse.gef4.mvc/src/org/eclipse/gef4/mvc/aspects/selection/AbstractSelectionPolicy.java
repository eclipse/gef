package org.eclipse.gef4.mvc.aspects.selection;

import org.eclipse.gef4.mvc.policies.AbstractEditPolicy;

public abstract class AbstractSelectionPolicy<V> extends AbstractEditPolicy<V> {

	public abstract void selectPrimary();

	public abstract void selectSecondary();

	public abstract void deselect();
	
}
