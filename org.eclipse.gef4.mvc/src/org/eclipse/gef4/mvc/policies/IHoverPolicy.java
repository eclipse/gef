package org.eclipse.gef4.mvc.policies;

public interface IHoverPolicy<V> extends IPolicy<V> {

	public class Impl<V> extends AbstractPolicy<V> implements IHoverPolicy<V>{

		@Override
		public boolean isHoverable() {
			return true;
		}
	}
	
	public boolean isHoverable();
	
}
