package org.eclipse.gef4.mvc.parts;

import java.util.List;

public interface INodeEditPart<V> extends IContentsEditPart<V> {
	
	/**
	 * Adds a NodeListener to the EditPart. Duplicate calls result in duplicate
	 * notification.
	 * 
	 * @param listener
	 *            the Listener
	 */
	// TODO: use obversable lists instead
	void addNodeListener(INodeEditPartListener listener);
	
	/**
	 * Removes the first occurance of the specified listener from the list of
	 * listeners. Does nothing if the listener was not present.
	 * 
	 * @param listener
	 *            the listener being removed
	 */
	// TODO: use obversable lists instead
	void removeNodeListener(INodeEditPartListener listener);
	
	/**
	 * Returns the <i>source</i> connections for this GraphicalEditPart. This
	 * method should only be called by the EditPart itself, and its helpers such
	 * as EditPolicies.
	 * 
	 * @return the source connections
	 */
	List<IConnectionEditPart<V>> getSourceConnections();

	/**
	 * Returns the <i>target</i> connections for this GraphicalEditPart. This
	 * method should only be called by the EditPart itself, and its helpers such
	 * as EditPolicies.
	 * 
	 * @return the target connections
	 */
	List<IConnectionEditPart<V>> getTargetConnections();

	public abstract void synchronizeTargetConnections();

	public abstract void synchronizeSourceConnections();

}
