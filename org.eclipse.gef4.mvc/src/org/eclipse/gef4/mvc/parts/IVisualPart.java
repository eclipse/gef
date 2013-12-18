package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef4.mvc.IActivateable;
import org.eclipse.gef4.mvc.policies.IEditPolicy;

public interface IVisualPart<V> extends IActivateable, IAdaptable {

	/**
	 * Returns the {@link IRootVisualPart}. This method should only be called
	 * internally or by helpers such as edit policies. The root can be used to
	 * get the viewer.
	 * 
	 * @return <code>null</code> or the {@link IRootVisualPart}
	 */
	public IRootVisualPart<V> getRoot();

	public abstract V getVisual();

	public void refreshVisual();

	public void setParent(IVisualPart<V> parent);

	public IVisualPart<V> getParent();

	public List<IVisualPart<V>> getChildren();

	public void removeChild(IVisualPart<V> child);

	public void addChild(IVisualPart<V> child, int index);

	public void addAnchored(IVisualPart<V> anchored);

	public void removeAnchored(IVisualPart<V> anchored);

	public List<IVisualPart<V>> getAnchoreds();

	public void addAnchorage(IVisualPart<V> anchorage);

	public void removeAnchorage(IVisualPart<V> anchorage);

	public List<IVisualPart<V>> getAnchorages();

	// TODO: add something similar to @Named, i.e. some additional name key to
	// allow an instance binding?
	// TODO: maybe we can replace this with juice (so no need to register that
	// externally)
	public <P extends IEditPolicy<V>> P getEditPolicy(Class<P> key);

	public <P extends IEditPolicy<V>> void installEditPolicy(Class<P> key,
			P editPolicy);

	public <P extends IEditPolicy<V>> void uninstallEditPolicy(Class<P> key);

}