package org.eclipse.gef4.mvc.policies;

public interface ISelectionPolicy<V> extends IPolicy<V> {

	public class Impl<V> extends AbstractPolicy<V> implements ISelectionPolicy<V> {

		@Override
		public boolean isSelectable() {
			return true;
		}
	}
	
	public boolean isSelectable();
	
}
