package org.eclipse.gef4.mvc.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.partviewer.IVisualPartViewer;

public abstract class AbstractNodeContentPart<V> extends
		AbstractContentEditPart<V> implements INodeContentPart<V> {

	/**
	 * List of <i>source</i> ConnectionEditParts
	 */
	protected List<IEdgeContentPart<V>> sourceConnections;

	/**
	 * List of <i>source</i> ConnectionEditParts
	 */
	protected List<IEdgeContentPart<V>> targetConnections;

	/**
	 * Adds a <i>source</i> ConnectionEditPart at the specified index. This
	 * method is called from {@link #synchronizeSourceConnections()}. There
	 * should be no reason to call or override this method. Source connection
	 * are created as a result of overriding
	 * {@link #getModelSourceConnections()}.
	 * <P>
	 * {@link #addSourceConnectionWithoutNotify(IEdgeContentPart, int)} is
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
	protected void addSourceConnection(IEdgeContentPart<V> connection,
			int index) {
		addSourceConnectionWithoutNotify(connection, index);

		INodeContentPart<V> source = connection.getSource();
		if (source != null)
			source.getSourceConnections().remove(connection);

		// TODO: for multi connections, this has to be an add
		connection.setSource(this);

		if (isActive())
			connection.activate();
	}

	@Override
	public void activate() {
		super.activate();

		// TODO: check how we should enable/disable connections properly
		List<IEdgeContentPart<V>> l = getSourceConnections();
		for (int i = 0; i < l.size(); i++)
			l.get(i).activate();
	}

	@Override
	public void deactivate() {
		// TODO: check how we should enable/disable connections properly
		List<IEdgeContentPart<V>> l = getSourceConnections();
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
	 * {@link #addTargetConnectionWithoutNotify(IEdgeContentPart, int)} is
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
	protected void addTargetConnection(IEdgeContentPart<V> connection,
			int index) {
		addTargetConnectionWithoutNotify(connection, index);

		INodeContentPart<V> target = connection.getTarget();
		if (target != null)
			target.getTargetConnections().remove(connection);

		connection.setTarget(this);
	}

	/**
	 * Searches for an existing <code>ConnectionEditPart</code> in the Viewer's
	 * {@link IVisualPartViewer#getContentPartMap() EditPart registry} and
	 * returns it if one is found.
	 * 
	 * @param model
	 *            the Connection's model
	 * @return the ConnectionEditPart
	 */
	protected IEdgeContentPart<V> findConnection(Object model) {
		IEdgeContentPart<V> connection = (IEdgeContentPart<V>) getViewer()
				.getContentPartMap().get(model);
		return connection;
	}

	/**
	 * Returns the <code>List</code> of the connection model objects for which
	 * this EditPart's model is the <b>source</b>.
	 * {@link #synchronizeSourceConnections()} calls this method. For each
	 * connection model object, {@link #createConnection(Object)} will be called
	 * automatically to obtain a corresponding {@link IEdgeContentPart}.
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
	 * automatically to obtain a corresponding {@link IEdgeContentPart}.
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
	public List<IEdgeContentPart<V>> getSourceConnections() {
		if (sourceConnections == null)
			return Collections.emptyList();
		return sourceConnections;
	}

	/**
	 * @see org.eclipse.gef.editparts.IGraphicalEditPart#getTargetConnections()
	 */
	public List<IEdgeContentPart<V>> getTargetConnections() {
		if (targetConnections == null)
			return Collections.emptyList();
		return targetConnections;
	}

	/**
	 * Removes the given connection for which this EditPart is the
	 * <B>source</b>. <BR>
	 * Fires notification. <BR>
	 * Inverse of {@link #addSourceConnection(IEdgeContentPart, int)}
	 * 
	 * @param connection
	 *            Connection being removed
	 */
	protected void removeSourceConnection(IEdgeContentPart<V> connection) {
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
	 * Inverse of {@link #addTargetConnection(IEdgeContentPart, int)}
	 * 
	 * @param connection
	 *            Connection being removed
	 */
	protected void removeTargetConnection(IEdgeContentPart<V> connection) {
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
	protected void reorderSourceConnection(IEdgeContentPart<V> connection,
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
	protected void reorderTargetConnection(IEdgeContentPart<V> connection,
			int index) {
		removeTargetConnectionWithoutNotify(connection);
		addTargetConnectionWithoutNotify(connection, index);
	}

	/**
	 * Adds the specified source <code>ConnectionEditPart</code> at an index.
	 * This method is used to update the {@link #sourceConnections} List. This
	 * method is called from
	 * {@link #addSourceConnection(IEdgeContentPart, int)}. Subclasses should
	 * not call or override this method.
	 * 
	 * @param connection
	 *            the ConnectionEditPart
	 * @param index
	 *            the index of the add
	 */
	private void addSourceConnectionWithoutNotify(
			IEdgeContentPart<V> connection, int index) {
		if (sourceConnections == null)
			sourceConnections = new ArrayList<IEdgeContentPart<V>>(2);
		sourceConnections.add(index, connection);
	}

	/**
	 * Adds the specified target <code>ConnectionEditPart</code> at an index.
	 * This method is used to update the {@link #targetConnections} List. This
	 * method is called from
	 * {@link #addTargetConnection(IEdgeContentPart, int)}. Subclasses should
	 * not call or override this method.
	 * 
	 * @param connection
	 *            the ConnectionEditPart
	 * @param index
	 *            the index of the add
	 */
	private void addTargetConnectionWithoutNotify(
			IEdgeContentPart<V> connection, int index) {
		if (targetConnections == null)
			targetConnections = new ArrayList<IEdgeContentPart<V>>(2);
		targetConnections.add(index, connection);
	}

	/**
	 * Removes the specified source <code>ConnectionEditPart</code> from the
	 * {@link #sourceConnections} List. This method is called from
	 * {@link #removeSourceConnection(IEdgeContentPart)}. Subclasses should
	 * not call or override this method.
	 * 
	 * @param connection
	 *            Connection to remove.
	 */
	private void removeSourceConnectionWithoutNotify(
			IEdgeContentPart<V> connection) {
		sourceConnections.remove(connection);
		if (sourceConnections.size() == 0) {
			sourceConnections = null;
		}
	}

	/**
	 * Removes the specified target <code>ConnectionEditPart</code> from the
	 * {@link #targetConnections} List. This method is called from
	 * {@link #removeTargetConnection(IEdgeContentPart)}. Subclasses should
	 * not call or override this method.
	 * 
	 * @param connection
	 *            Connection to remove.
	 */
	private void removeTargetConnectionWithoutNotify(
			IEdgeContentPart<V> connection) {
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
	 * longer exists are {@link #removeSourceConnection(IEdgeContentPart)
	 * removed}. New models have their ConnectionEditParts
	 * {@link #createConnection(Object) created}. Subclasses should override
	 * <code>getModelSourceChildren()</code>.
	 * <P>
	 * This method should <em>not</em> be overridden.
	 */
	@Override
	public void synchronizeSourceConnections() {
		int i;
		IEdgeContentPart<V> editPart;
		Object model;

		List<IEdgeContentPart<V>> sourceConnections = getSourceConnections();
		int size = sourceConnections.size();
		Map<Object, IEdgeContentPart<V>> modelToEditPart = Collections
				.emptyMap();
		if (size > 0) {
			modelToEditPart = new HashMap<Object, IEdgeContentPart<V>>(size);
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

			editPart = (IEdgeContentPart<V>) modelToEditPart.get(model);
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
			List<IEdgeContentPart<V>> trash = new ArrayList<IEdgeContentPart<V>>(
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
	 * longer exists are {@link #removeTargetConnection(IEdgeContentPart)
	 * removed}. New models have their ConnectionEditParts
	 * {@link #createConnection(Object) created}. Subclasses should override
	 * <code>getModelTargetChildren()</code>.
	 * <P>
	 * This method should <em>not</em> be overridden.
	 */
	@Override
	public void synchronizeTargetConnections() {
		int i;
		IEdgeContentPart<V> editPart;
		Object model;

		List<IEdgeContentPart<V>> targetConnections = getTargetConnections();
		int size = targetConnections.size();
		Map<Object, IEdgeContentPart<V>> modelToEditPart = Collections
				.emptyMap();
		if (size > 0) {
			modelToEditPart = new HashMap<Object, IEdgeContentPart<V>>(size);
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

			editPart = (IEdgeContentPart<V>) modelToEditPart.get(model);
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
			List<IEdgeContentPart<V>> trash = new ArrayList<IEdgeContentPart<V>>(
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
	public void setParent(IVisualPart<V> parent) {
		super.setParent(parent);
		if (parent != null) {
			refreshVisual();
			synchronize();
		}
	}
}
