package org.eclipse.gef4.mvc.policies;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;

public interface IDragPolicy<V> extends IPolicy<V>{
	
	public class Impl<V> extends AbstractPolicy<V> implements IDragPolicy<V> {

		@Override
		public boolean isDraggable() {
			return true;
		}

		@Override
		public void press(Point mouseLocation) {
			// do nothing by default
		}

		@Override
		public void drag(Point mouseLocation, Dimension delta) {
			// do nothing by default
		}

		@Override
		public void release(Point mouseLocation, Dimension delta) {
			// do nothing by default
		}
	}
	
	public boolean isDraggable();
	
	public void press(Point mouseLocation);
	
	public void drag(Point mouseLocation, Dimension delta);
	
	public void release(Point mouseLocation, Dimension delta);
}
