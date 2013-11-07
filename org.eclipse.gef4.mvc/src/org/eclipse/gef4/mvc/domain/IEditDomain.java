package org.eclipse.gef4.mvc.domain;

import java.util.List;

import org.eclipse.gef4.mvc.commands.CommandStack;
import org.eclipse.gef4.mvc.partviewer.IEditPartViewer;
import org.eclipse.gef4.mvc.tools.ITool;

public interface IEditDomain<V> {

	<P extends IEditDomainProperty<V>> void setProperty(Class<P> key, P property);
	
	<P extends IEditDomainProperty<V>> P getProperty(Class<P> key);
	
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
	 * Returns the CommandStack. Command stacks could potentially be shared
	 * across domains depending on the application.
	 * 
	 * @return The command stack
	 */
	public abstract CommandStack getCommandStack();

	public IEditPartViewer<V> getViewer();

	/**
	 * Sets the <code>CommandStack</code>.
	 * 
	 * @param stack
	 *            the CommandStack
	 */
	public abstract void setCommandStack(CommandStack stack);

	/**
	 * Sets the active Tool for this EditDomain. If a current Tool is active, it
	 * is deactivated. The new Tool is told its EditDomain, and is activated.
	 * 
	 * @param tool
	 *            the Tool
	 */
	public abstract void setActiveTools(List<ITool<V>> tool);

}