package org.eclipse.gef4.mvc.domain;

import java.util.List;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.gef4.mvc.partviewer.IEditPartViewer;
import org.eclipse.gef4.mvc.tools.ITool;

public interface IEditDomain<V> {

	<P extends Object> void setProperty(Class<P> key, P property);

	<P extends Object> P getProperty(Class<P> key);

	/**
	 * Adds an EditPartViewer into the EditDomain. A viewer is most likely
	 * placed in a {@link org.eclipse.ui.IWorkbenchPart WorkbenchPart} of some
	 * form, such as the IEditorPart or an IViewPart.
	 * 
	 * @param viewer
	 *            The EditPartViewer
	 */
	public abstract void setViewer(IEditPartViewer<V> viewer);

	/**
	 * Returns the active Tool
	 * 
	 * @return the active Tool
	 */
	public abstract List<ITool<V>> getActiveTools();

	/**
	 * Returns the {@link IOperationHistory} that is used by this domain.
	 * 
	 * @return The {@link IOperationHistory}.
	 */
	public abstract IOperationHistory getOperationHistory();

	public IEditPartViewer<V> getViewer();

	/**
	 * Sets the {@link IOperationHistory}, which can later be requested via {@link #getOperationHistory()}.
	 * 
	 * @param operationHistory
	 *            The new {@link IOperationHistory} to be used.
	 */
	public abstract void setOperationHistory(IOperationHistory operationHistory);

	/**
	 * Sets the active Tool for this EditDomain. If a current Tool is active, it
	 * is deactivated. The new Tool is told its EditDomain, and is activated.
	 * 
	 * @param tool
	 *            the Tool
	 */
	public abstract void setActiveTools(List<ITool<V>> tool);

}