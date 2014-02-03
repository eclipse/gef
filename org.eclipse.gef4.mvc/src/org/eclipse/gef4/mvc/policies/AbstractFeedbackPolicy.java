package org.eclipse.gef4.mvc.policies;

import java.util.List;

import org.eclipse.gef4.mvc.parts.IHandlePart;

public abstract class AbstractFeedbackPolicy<V> extends AbstractPolicy<V> {
	
	private List<IHandlePart<V>> handles;
	
	/**
	 * Adds the given handles to the root visual part.
	 * 
	 * @param handles
	 */
	protected void addHandles() {
		handles = createHandles();
		if (handles != null && !handles.isEmpty()) {
			getHost().getRoot().addChildren(handles);
			getHost().addAnchoreds(handles);
		}
	}
	
	protected abstract List<IHandlePart<V>> createHandles();

	protected void removeHandles() {
		if (handles != null && !handles.isEmpty()) {
			getHost().removeAnchoreds(handles);
			getHost().getRoot().removeChildren(handles);
			handles.clear();
		}
	}
	
}
