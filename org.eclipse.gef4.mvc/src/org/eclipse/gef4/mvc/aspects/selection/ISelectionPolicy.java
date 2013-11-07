package org.eclipse.gef4.mvc.aspects.selection;

import org.eclipse.gef4.mvc.policies.IEditPolicy;

public interface ISelectionPolicy<V> extends IEditPolicy<V> {

	public void selectPrimary();
	
	public void becomeSecondary();
	
	public void deselect();
	
}
