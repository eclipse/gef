package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef4.mvc.IActivateable;
import org.eclipse.gef4.mvc.policies.IEditPolicy;

public interface IVisualPart<V> extends IActivateable, IAdaptable {

	// TODO do we need an internal interface for createVisuals, registerVisuals,
	// internal interface with register visuals, etc?

	// allow multiple visuals so something like a resizeHandles object b
	public abstract V getVisual();

	// public void showVisual();
	//
	// public void hideVisual();

	public void refreshVisual();

	/**
	 * Returns the {@link IRootVisualPart}. This method should only be called
	 * internally or by helpers such as edit policies. The root can be used to
	 * get the viewer.
	 * 
	 * @return <code>null</code> or the {@link IRootVisualPart}
	 */
	public IRootVisualPart<V> getRoot();

	/**
	 * <img src="doc-files/dblack.gif"/>Sets the parent. This should only be
	 * called by the parent EditPart.
	 * 
	 * @param parent
	 *            the parent EditPart
	 */
	public void setParent(IVisualPart<V> parent);

	/**
	 * Returns the parent <code>EditPart</code>. This method should only be
	 * called internally or by helpers such as EditPolicies.
	 * 
	 * @return <code>null</code> or the parent {@link IEditPart<V>}
	 */
	public IVisualPart<V> getParent();

	public List<IVisualPart<V>> getChildren();

	public void removeChild(IVisualPart<V> child);

	public void addChild(IVisualPart<V> child, int index);

	// TODO: handle handles separately or as edit policies??
	// TODO: add something similar to @Named, i.e. some additional name key to
	// allow an instance binding?
	// TODO: maybe we can replace this with juice (so no need to register that
	// externally)
	public <P extends IEditPolicy<V>> P getEditPolicy(Class<P> key);

	public <P extends IEditPolicy<V>> void installEditPolicy(Class<P> key,
			P editPolicy);

	public <P extends IEditPolicy<V>> void uninstallEditPolicy(Class<P> key);

}