/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPart
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gef4.mvc.IActivatable;
import org.eclipse.gef4.mvc.anchors.IAnchor;
import org.eclipse.gef4.mvc.policies.IPolicy;

public interface IVisualPart<V> extends IActivatable, IAdaptable {

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

	public void reorderChild(IVisualPart<V> child, int index);

	// public void addVisualToParentVisual(V parentVisual);
	//
	// public void removeVisualFromParentVisual(/*V parentVisual*/);

	public void addAnchored(IVisualPart<V> anchored);

	public void removeAnchored(IVisualPart<V> anchored);

	public List<IVisualPart<V>> getAnchoreds();

	public void addAnchorage(IVisualPart<V> anchorage);

	public void removeAnchorage(IVisualPart<V> anchorage);

	public List<IVisualPart<V>> getAnchorages();

	public void attachVisualToAnchorageVisual(IAnchor<V> anchor);

	public void detachVisualFromAnchorageVisual(IAnchor<V> anchor);

	// TODO: add something similar to @Named, i.e. some additional name key to
	// allow an instance binding?
	// TODO: maybe we can replace this with juice (so no need to register that
	// externally)
	public <P extends IPolicy<V>> P getEditPolicy(Class<? super P> key);

	public <P extends IPolicy<V>> void installEditPolicy(Class<? super P> key, P editPolicy);

	public <P extends IPolicy<V>> void uninstallEditPolicy(Class<P> key);

}