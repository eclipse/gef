package org.eclipse.gef4.mvc.tools;

import org.eclipse.gef4.mvc.domain.IEditDomain;

public interface ICompositeTool<V> extends ITool<V> {

	/**
	 * Appends the given {@link ITool} to the list of sub-tools managed by this
	 * {@link ICompositeTool}. If this {@link ICompositeTool} is already
	 * registered on an {@link IEditDomain}, the added tool will be registered
	 * on the same {@link IEditDomain}.
	 * 
	 * @param tool
	 */
	public void add(ITool<V> tool);

	/**
	 * Inserts the given {@link ITool} into the list of sub-tools managed by
	 * this {@link ICompositeTool} at the given index. If this
	 * {@link ICompositeTool} is already registered on an {@link IEditDomain},
	 * the added tool will be registered on the same {@link IEditDomain}.
	 * 
	 * @param index
	 * @param tool
	 */
	public void add(int index, ITool<V> tool);

	/**
	 * Removes the given {@link ITool} from the list of sub-tools.
	 * 
	 * @param tool
	 */
	public void remove(ITool<V> tool);

	/**
	 * Removes the {@link ITool} at the given index from the list of sub-tools.
	 * 
	 * @param index
	 */
	public void remove(int index);

	/**
	 * Registers all sub-tools on the supplied {@link IEditDomain}. If the
	 * supplied {@link IEditDomain} is <code>null</code> all sub-tools are
	 * unregistered. (Un-)registration is done via the designated
	 * {@link #registerListeners()} and {@link #unregisterListeners()} methods.
	 * 
	 * @param domain
	 */
	@Override
	public void setDomain(IEditDomain<V> domain);

	/**
	 * This method is called when a valid {@link IEditDomain} is attached to
	 * this tool so that you can register event listeners for various inputs
	 * (keyboard, mouse) or model changes (selection, scroll offset / viewport).
	 */
	public void registerListeners();

	/**
	 * This method is called when the attached {@link IEditDomain} is reset to
	 * <code>null</code> so that you can unregister previously registered event
	 * listeners.
	 */
	public void unregisterListeners();

}
