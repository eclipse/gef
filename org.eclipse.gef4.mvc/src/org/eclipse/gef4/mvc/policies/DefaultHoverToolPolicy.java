package org.eclipse.gef4.mvc.policies;

public class DefaultHoverToolPolicy<V> extends AbstractEditPolicy<V> implements IHoverToolPolicy<V> {
	
	@Override
	public boolean isHoverable() {
		return true;
	}

}
