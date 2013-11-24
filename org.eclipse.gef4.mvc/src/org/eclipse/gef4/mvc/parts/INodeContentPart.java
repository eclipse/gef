package org.eclipse.gef4.mvc.parts;

import java.util.List;

public interface INodeContentPart<V> extends IContentPart<V> {
	
	// TODO: do we need to differentiate between nodes and connections?? 
	// anchor node<->edge edge<->edge ; node<->node??
	// TODO: synchronization should be related to anchors rather than to connections
	/**
	 * Returns the <i>source</i> connections for this GraphicalEditPart. This
	 * method should only be called by the EditPart itself, and its helpers such
	 * as EditPolicies.
	 * 
	 * @return the source connections
	 */
	List<IEdgeContentPart<V>> getSourceConnections();

	/**
	 * Returns the <i>target</i> connections for this GraphicalEditPart. This
	 * method should only be called by the EditPart itself, and its helpers such
	 * as EditPolicies.
	 * 
	 * @return the target connections
	 */
	List<IEdgeContentPart<V>> getTargetConnections();

	public abstract void synchronizeTargetConnections();

	public abstract void synchronizeSourceConnections();

}
