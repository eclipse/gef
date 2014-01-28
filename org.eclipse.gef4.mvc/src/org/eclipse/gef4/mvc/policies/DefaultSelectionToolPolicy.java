package org.eclipse.gef4.mvc.policies;

/**
 * Default {@link ISelectionToolPolicy} implementation.
 * 
 * @author mwienand
 *
 * @param <V>
 */
public class DefaultSelectionToolPolicy<V> extends
		AbstractEditPolicy<V> implements ISelectionToolPolicy<V> {
	
	@Override
	public boolean isSelectable() {
		return true;
	}
	
}
