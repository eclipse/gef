package org.eclipse.gef4.mvc.policies;

public interface ISelectionPolicy<V> extends IEditPolicy<V> {

	public void selectPrimary();
	
	public void becomeSecondary();
	
	public void deselect();
	
}
