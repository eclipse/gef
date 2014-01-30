package org.eclipse.gef4.mvc.policies;

public interface ISelectionPolicy<V> extends IEditPolicy<V> {

	public class Impl<V> extends AbstractEditPolicy<V> implements ISelectionPolicy<V> {

		@Override
		public boolean isSelectable() {
			return true;
		}
	}
	
	public boolean isSelectable();
	
}
