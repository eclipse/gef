package org.eclipse.gef4.mvc.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.partviewer.IEditPartViewer;

public abstract class AbstractNodeEditPart<V> extends
		AbstractContentsEditPart<V> implements INodeEditPart<V> {

	/**
	 * List of <i>source</i> ConnectionEditParts
	 */
	protected List<IConnectionEditPart<V>> sourceConnections;

	/**
	 * List of <i>source</i> ConnectionEditParts
	 */
	protected List<IConnectionEditPart<V>> targetConnections;

	/**
	 * @see org.eclipse.gef.editparts.IGraphicalEditPart#addNodeListener(org.eclipse.gef4.mvc.parts.INodeEditPartListener)
	 */
	public void addNodeListener(INodeEditPartListener listener) {
		eventListeners.addListener(INodeEditPartListener.class, listener);
	}

	/**
	 * @see org.eclipse.gef.editparts.IGraphicalEditPart#removeNodeListener(org.eclipse.gef4.mvc.parts.INodeEditPartListener)
	 */
	public void removeNodeListener(INodeEditPartListener listener) {
		eventListeners.removeListener(INodeEditPartListener.class, listener);
	}

	/**
	 * Notifies listeners that a target connection has been removed. Called from
	 * {@link #removeTargetConnection(IConnectionEditPart)}. There is no reason
	 * for subclasses to call or override this method.
	 * 
	 * @param connection
	 *            <code>ConnectionEditPart</code> being added as child.
	 * @param index
	 *            Position child is being added into.
	 */
	private void fireRemovingTargetConnection(
			IConnectionEditPart<V> connection, int index) {
		if (eventListeners == null)
			return;
		Iterator listeners = eventListeners
				.getListeners(INodeEditPartListener.class);
		INodeEditPartListener listener = null;
		while (listeners.hasNext()) {
			listener = (INodeEditPartListener) listeners.next();
			listener.removingTargetConnection(connection, index);
		}
	}

	/**
	 * Notifies listeners that a source connection has been removed. Called from
	 * {@link #removeSourceConnection(IConnectionEditPart)}. There is no reason
	 * for subclasses to call or override this method.
	 * 
	 * @param connection
	 *            <code>ConnectionEditPart</code> being added as child.
	 * @param index
	 *            Position child is being added into.
	 */
	private void fireRemovingSourceConnection(
			IConnectionEditPart<V> connection, int index) {
		if (eventListeners == null)
			return;
		Iterator listeners = eventListeners
				.getListeners(INodeEditPartListener.class);
		INodeEditPartListener listener = null;
		while (listeners.hasNext()) {
			listener = (INodeEditPartListener) listeners.next();
			listener.removingSourceConnection(connection, index);
		}
	}

	/**
	 * Notifies listeners that a source connection has been added. Called from
	 * {@link #addSourceConnection(IConnectionEditPart, int)}. There is no
	 * reason for subclasses to call or override this method.
	 * 
	 * @param connection
	 *            <code>ConnectionEditPart</code> being added as child.
	 * @param index
	 *            Position child is being added into.
	 */
	private void fireSourceConnectionAdded(IConnectionEditPart<V> connection,
			int index) {
		if (eventListeners == null)
			return;
		Iterator listeners = eventListeners
				.getListeners(INodeEditPartListener.class);
		INodeEditPartListener listener = null;
		while (listeners.hasNext()) {
			listener = (INodeEditPartListener) listeners.next();
			listener.sourceConnectionAdded(connection, index);
		}
	}

	/**
	 * Notifies listeners that a target connection has been added. Called from
	 * {@link #addTargetConnection(IConnectionEditPart, int)}. There is no
	 * reason for subclasses to call or override this method.
	 * 
	 * @param connection
	 *            <code>ConnectionEditPart</code> being added as child.
	 * @param index
	 *            Position child is being added into.
	 */
	private void fireTargetConnectionAdded(IConnectionEditPart<V> connection,
			int index) {
		if (eventListeners == null)
			return;
		Iterator listeners = eventListeners
				.getListeners(INodeEditPartListener.class);
		INodeEditPartListener listener = null;
		while (listeners.hasNext()) {
			listener = (INodeEditPartListener) listeners.next();
			listener.targetConnectionAdded(connection, index);
		}
	}

	/**
	 * Adds a <i>source</i> ConnectionEditPart at the specified index. This
	 * method is called from {@link #synchronizeSourceConnections()}. There
	 * should be no reason to call or override this method. Source connection
	 * are created as a result of overriding
	 * {@link #getModelSourceConnections()}.
	 * <P>
	 * {@link #addSourceConnectionWithoutNotify(IConnectionEditPart, int)} is
	 * called to perform the actual update of the {@link #sourceConnections}
	 * <code>List</code>. The connection will have its source set to
	 * <code>this</code>.
	 * <P>
	 * If active, this EditPart will activate the ConnectionEditPart.
	 * <P>
	 * Finally, all {@link INodeEditPartListener}s are notified of the new
	 * connection.
	 * 
	 * @param connection
	 *            Connection being added
	 * @param index
	 *            Index where it is being added
	 */
	protected void addSourceConnection(IConnectionEditPart<V> connection,
			int index) {
		addSourceConnectionWithoutNotify(connection, index);

		INodeEditPart<V> source = connection.getSource();
		if (source != null)
			source.getSourceConnections().remove(connection);

		connection.setSource(this);

		if (isActive())
			connection.activate();
		fireSourceConnectionAdded(connection, index);
	}

	@Override
	public void activate() {
		super.activate();

		// TODO: check how we should enable/disable connections properly
		List<IConnectionEditPart<V>> l = getSourceConnections();
		for (int i = 0; i < l.size(); i++)
			l.get(i).activate();
	}

	@Override
	public void deactivate() {
		// TODO: check how we should enable/disable connections properly
		List<IConnectionEditPart<V>> l = getSourceConnections();
		for (int i = 0; i < l.size(); i++)
			l.get(i).deactivate();

		super.deactivate();
	}

	/**
	 * Adds a <i>target</i> ConnectionEditPart at the specified index. This
	 * method is called from {@link #synchronizeTargetConnections()}. There
	 * should be no reason to call or override this method. Target connection
	 * are created as a result of overriding
	 * {@link #getModelTargetConnections()}.
	 * <P>
	 * {@link #addTargetConnectionWithoutNotify(IConnectionEditPart, int)} is
	 * called to perform the actual update of the {@link #targetConnections}
	 * <code>List</code>. The connection will have its target set to
	 * <code>this</code>.
	 * <P>
	 * Finally, all {@link INodeEditPartListener}s are notified of the new
	 * connection.
	 * 
	 * @param connection
	 *            Connection being added
	 * @param index
	 *            Index where it is being added
	 */
	protected void addTargetConnection(IConnectionEditPart<V> connection,
			int index) {
		addTargetConnectionWithoutNotify(connection, index);

		INodeEditPart<V> target = connection.getTarget();
		if (target != null)
			target.getTargetConnections().remove(connection);

		connection.setTarget(this);
		fireTargetConnectionAdded(connection, index);
	}

	/**
	 * Searches for an existing <code>ConnectionEditPart</code> in the Viewer's
	 * {@link IEditPartViewer#getEditPartRegistry() EditPart registry} and
	 * returns it if one is found.
	 * 
	 * @param model
	 *            the Connection's model
	 * @return the ConnectionEditPart
	 */
	protected IConnectionEditPart<V> findConnection(Object model) {
		IConnectionEditPart<V> connection = (IConnectionEditPart<V>) getViewer()
				.getEditPartRegistry().get(model);
		return connection;
	}

	/**
	 * Returns the <code>List</code> of the connection model objects for which
	 * this EditPart's model is the <b>source</b>.
	 * {@link #synchronizeSourceConnections()} calls this method. For each
	 * connection model object, {@link #createConnection(Object)} will be called
	 * automatically to obtain a corresponding {@link IConnectionEditPart}.
	 * <P>
	 * Callers must not modify the returned List.
	 * 
	 * @return the List of model source connections
	 */
	protected List<Object> getModelSourceConnections() {
		return Collections.emptyList();
	}

	/**
	 * Returns the <code>List</code> of the connection model objects for which
	 * this EditPart's model is the <b>target</b>.
	 * {@link #synchronizeTargetConnections()} calls this method. For each
	 * connection model object, {@link #createConnection(Object)} will be called
	 * automatically to obtain a corresponding {@link IConnectionEditPart}.
	 * <P>
	 * Callers must not modify the returned List.
	 * 
	 * @return the List of model target connections
	 */
	protected List<Object> getModelTargetConnections() {
		return Collections.emptyList();
	}

	/**
	 * @see org.eclipse.gef.editparts.IGraphicalEditPart#getSourceConnections()
	 */
	public List<IConnectionEditPart<V>> getSourceConnections() {
		if (sourceConnections == null)
			return Collections.emptyList();
		return sourceConnections;
	}

	/**
	 * @see org.eclipse.gef.editparts.IGraphicalEditPart#getTargetConnections()
	 */
	public List<IConnectionEditPart<V>> getTargetConnections() {
		if (targetConnections == null)
			return Collections.emptyList();
		return targetConnections;
	}

	/**
	 * Removes the given connection for which this EditPart is the
	 * <B>source</b>. <BR>
	 * Fires notification. <BR>
	 * Inverse of {@link #addSourceConnection(IConnectionEditPart, int)}
	 * 
	 * @param connection
	 *            Connection being removed
	 */
	protected void removeSourceConnection(IConnectionEditPart<V> connection) {
		fireRemovingSourceConnection(connection, getSourceConnections()
				.indexOf(connection));
		if (connection.getSource() == this) {
			connection.deactivate();
			connection.setSource(null);
		}
		removeSourceConnectionWithoutNotify(connection);
	}

	/**
	 * Removes the given connection for which this EditPart is the
	 * <B>target</b>. <BR>
	 * Fires notification. <BR>
	 * Inverse of {@link #addTargetConnection(IConnectionEditPart, int)}
	 * 
	 * @param connection
	 *            Connection being removed
	 */
	protected void removeTargetConnection(IConnectionEditPart<V> connection) {
		fireRemovingTargetConnection(connection, getTargetConnections()
				.indexOf(connection));
		if (connection.getTarget() == this)
			connection.setTarget(null);
		removeTargetConnectionWithoutNotify(connection);
	}

	/**
	 * Moves a source <code>ConnectionEditPart</code> into a lower index than it
	 * currently occupies. This method is called from
	 * {@link #synchronizeSourceConnections()}.
	 * 
	 * @param connection
	 *            the ConnectionEditPart
	 * @param index
	 *            the new index
	 */
	protected void reorderSourceConnection(IConnectionEditPart<V> connection,
			int index) {
		removeSourceConnectionWithoutNotify(connection);
		addSourceConnectionWithoutNotify(connection, index);
	}

	/**
	 * Moves a target <code>ConnectionEditPart</code> into a lower index than it
	 * currently occupies. This method is called from
	 * {@link #synchronizeTargetConnections()}.
	 * 
	 * @param connection
	 *            the ConnectionEditPart
	 * @param index
	 *            the new index
	 */
	protected void reorderTargetConnection(IConnectionEditPart<V> connection,
			int index) {
		removeTargetConnectionWithoutNotify(connection);
		addTargetConnectionWithoutNotify(connection, index);
	}

	/**
	 * Adds the specified source <code>ConnectionEditPart</code> at an index.
	 * This method is used to update the {@link #sourceConnections} List. This
	 * method is called from
	 * {@link #addSourceConnection(IConnectionEditPart, int)}. Subclasses should
	 * not call or override this method.
	 * 
	 * @param connection
	 *            the ConnectionEditPart
	 * @param index
	 *            the index of the add
	 */
	private void addSourceConnectionWithoutNotify(
			IConnectionEditPart<V> connection, int index) {
		if (sourceConnections == null)
			sourceConnections = new ArrayList<IConnectionEditPart<V>>(2);
		sourceConnections.add(index, connection);
	}

	/**
	 * Adds the specified target <code>ConnectionEditPart</code> at an index.
	 * This method is used to update the {@link #targetConnections} List. This
	 * method is called from
	 * {@link #addTargetConnection(IConnectionEditPart, int)}. Subclasses should
	 * not call or override this method.
	 * 
	 * @param connection
	 *            the ConnectionEditPart
	 * @param index
	 *            the index of the add
	 */
	private void addTargetConnectionWithoutNotify(
			IConnectionEditPart<V> connection, int index) {
		if (targetConnections == null)
			targetConnections = new ArrayList<IConnectionEditPart<V>>(2);
		targetConnections.add(index, connection);
	}

	/**
	 * Removes the specified source <code>ConnectionEditPart</code> from the
	 * {@link #sourceConnections} List. This method is called from
	 * {@link #removeSourceConnection(IConnectionEditPart)}. Subclasses should
	 * not call or override this method.
	 * 
	 * @param connection
	 *            Connection to remove.
	 */
	private void removeSourceConnectionWithoutNotify(
			IConnectionEditPart<V> connection) {
		sourceConnections.remove(connection);
		if (sourceConnections.size() == 0) {
			sourceConnections = null;
		}
	}

	/**
	 * Removes the specified target <code>ConnectionEditPart</code> from the
	 * {@link #targetConnections} List. This method is called from
	 * {@link #removeTargetConnection(IConnectionEditPart)}. Subclasses should
	 * not call or override this method.
	 * 
	 * @param connection
	 *            Connection to remove.
	 */
	private void removeTargetConnectionWithoutNotify(
			IConnectionEditPart<V> connection) {
		targetConnections.remove(connection);
		if (targetConnections.size() == 0) {
			targetConnections = null;
		}
	}

	@Override
	protected void synchronize() {
		super.synchronize();
		synchronizeSourceConnections();
		synchronizeTargetConnections();
	}

	/**
	 * Updates the set of <i>source</i> ConnectionEditParts so that it is in
	 * sync with the model source connections. This method is called from
	 * {@link #refresh()}, and may also be called in response to notification
	 * from the model.
	 * <P>
	 * The update is performed by comparing the existing source
	 * ConnectionEditParts with the set of model source connections returned
	 * from {@link #getModelSourceConnections()}. EditParts whose model no
	 * longer exists are {@link #removeSourceConnection(IConnectionEditPart)
	 * removed}. New models have their ConnectionEditParts
	 * {@link #createConnection(Object) created}. Subclasses should override
	 * <code>getModelSourceChildren()</code>.
	 * <P>
	 * This method should <em>not</em> be overridden.
	 */
	@Override
	public void synchronizeSourceConnections() {
		int i;
		IConnectionEditPart<V> editPart;
		Object model;

		List<IConnectionEditPart<V>> sourceConnections = getSourceConnections();
		int size = sourceConnections.size();
		Map<Object, IConnectionEditPart<V>> modelToEditPart = Collections
				.emptyMap();
		if (size > 0) {
			modelToEditPart = new HashMap<Object, IConnectionEditPart<V>>(size);
			for (i = 0; i < size; i++) {
				editPart = sourceConnections.get(i);
				modelToEditPart.put(editPart.getModel(), editPart);
			}
		}

		List<Object> modelObjects = getModelSourceConnections();
		if (modelObjects == null) {
			modelObjects = Collections.emptyList();
		}
		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			if (i < sourceConnections.size()
					&& sourceConnections.get(i).getModel() == model)
				continue;

			editPart = (IConnectionEditPart<V>) modelToEditPart.get(model);
			if (editPart != null)
				reorderSourceConnection(editPart, i);
			else {
				editPart = findConnection(model);
				if (editPart != null) {
					addSourceConnection(editPart, i);
				}
			}
		}

		// Remove the remaining EditParts
		size = sourceConnections.size();
		if (i < size) {
			List<IConnectionEditPart<V>> trash = new ArrayList<IConnectionEditPart<V>>(
					size - i);
			for (; i < size; i++)
				trash.add(sourceConnections.get(i));
			for (i = 0; i < trash.size(); i++)
				removeSourceConnection(trash.get(i));
		}
	}

	/**
	 * Updates the set of <i>target</i> ConnectionEditParts so that it is in
	 * sync with the model target connections. This method is called from
	 * {@link #refresh()}, and may also be called in response to notification
	 * from the model.
	 * <P>
	 * The update is performed by comparing the existing source
	 * ConnectionEditParts with the set of model source connections returned
	 * from {@link #getModelTargetConnections()}. EditParts whose model no
	 * longer exists are {@link #removeTargetConnection(IConnectionEditPart)
	 * removed}. New models have their ConnectionEditParts
	 * {@link #createConnection(Object) created}. Subclasses should override
	 * <code>getModelTargetChildren()</code>.
	 * <P>
	 * This method should <em>not</em> be overridden.
	 */
	@Override
	public void synchronizeTargetConnections() {
		int i;
		IConnectionEditPart<V> editPart;
		Object model;

		List<IConnectionEditPart<V>> targetConnections = getTargetConnections();
		int size = targetConnections.size();
		Map<Object, IConnectionEditPart<V>> modelToEditPart = Collections
				.emptyMap();
		if (size > 0) {
			modelToEditPart = new HashMap<Object, IConnectionEditPart<V>>(size);
			for (i = 0; i < size; i++) {
				editPart = targetConnections.get(i);
				modelToEditPart.put(editPart.getModel(), editPart);
			}
		}

		List<Object> modelObjects = getModelTargetConnections();
		if (modelObjects == null) {
			modelObjects = Collections.emptyList();
		}
		for (i = 0; i < modelObjects.size(); i++) {
			model = modelObjects.get(i);

			if (i < targetConnections.size()
					&& targetConnections.get(i).getModel() == model)
				continue;

			editPart = (IConnectionEditPart<V>) modelToEditPart.get(model);
			if (editPart != null)
				reorderTargetConnection(editPart, i);
			else {
				editPart = findConnection(model);
				if (editPart != null) {
					addTargetConnection(editPart, i);
				}
			}
		}

		// Remove the remaining EditParts
		size = targetConnections.size();
		if (i < size) {
			List<IConnectionEditPart<V>> trash = new ArrayList<IConnectionEditPart<V>>(
					size - i);
			for (; i < size; i++)
				trash.add(targetConnections.get(i));
			for (i = 0; i < trash.size(); i++)
				removeTargetConnection(trash.get(i));
		}
	}

	/**
	 * Sets the parent EditPart. There is no reason to override this method.
	 * 
	 * @see IEditPart#setParent(IEditPart)
	 */
	@Override
	public void setParent(IEditPart<V> parent) {
		super.setParent(parent);
		if (parent != null) {
			refreshVisual();
			synchronize();
		}
	}
}
