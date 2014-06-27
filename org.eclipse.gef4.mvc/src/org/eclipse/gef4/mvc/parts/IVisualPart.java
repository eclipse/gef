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
import java.util.Map;

import org.eclipse.gef4.mvc.IActivatable;
import org.eclipse.gef4.mvc.IPropertyChangeSupport;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.bindings.IAdaptable;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

/**
 * An {@link IVisualPart} plays the controller role in the model-view-controller
 * architecture. While it does not have to bound to a model (actually only
 * {@link IContentPart}s are bound to model elements, {@link IFeedbackPart}s and
 * {@link IHandlePart}s do not refer to model elements), an {@link IVisualPart}
 * controls a visual and is responsible of handling user interaction.
 * <p>
 * Within an {@link IViewer}, {@link IVisualPart} are organized in a hierarchy
 * via a <code>[1:n]</code> parent-children relationship ({@link #getParent()},
 * {@link #getChildren()}), which roots in an {@link IRootPart}. Furthermore a
 * <code>[n:m]</code> anchorage-anchored relationship ({@link #getAnchorages(),
 * #getAnchoreds()}) may be established via {@link IVisualPart} at arbitrary
 * places in the hierarchy.
 * <p>
 * An {@link IVisualPart} is adaptable ({@link IAdaptable}). Usually,
 * {@link IPolicy}s and {@link IBehavior}s are adapted to it (but arbitrary
 * adapters may indeed be registered as needed). {@link IPolicy}s are usually
 * required in case the {@link IVisualPart} is directly involved in user
 * interaction (e.g. the user clicks on its controlled visual). They may be
 * accessed type-safe by {@link ITool}s or other {@link IPolicy}s (
 * {@link IPolicy}s may delegate to other {@link IPolicy}s) via their class key
 * (see {@link IAdaptable}). {@link IBehavior}s are used to react to changes of
 * the attached model (in case of an {@link IContentPart}s), the viewer models,
 * or others sources (e.g. adapters of the {@link IViewer} or {@link IDomain}),
 * thereby reacting to changes of the interactive state (e.g. the
 * {@link ISelectionModel} reporting a selection change).
 * <p>
 * {@link IVisualPart}s are activatable ({@link IActivatable}), and an
 * activation/deactivation of an {@link IVisualPart} will result in the
 * activation/deactivation of all registered adapters (i.e. {@link IPolicy}s and
 * {@link IBehavior}s).
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *              Instead, {@link AbstractVisualPart} should be subclassed.
 * 
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public interface IVisualPart<VR> extends IActivatable, IAdaptable,
		IPropertyChangeSupport {

	// TODO: add others
	public static final String PARENT_PROPERTY = "parent";
	public static final String ANCHORAGES_PROPERTY = "anchorage";

	public void addAnchorage(IVisualPart<VR> anchorage);

	public void addAnchored(IVisualPart<VR> anchored);

	public void addAnchoreds(List<? extends IVisualPart<VR>> anchoreds);

	public void addChild(IVisualPart<VR> child);

	public void addChild(IVisualPart<VR> child, int index);

	public void addChildren(List<? extends IVisualPart<VR>> children);

	// anchorage visual may not be the visual of the anchorage itself!
	public void attachVisualToAnchorageVisual(IVisualPart<VR> anchorage,
			VR anchorageVisual);

	public void detachVisualFromAnchorageVisual(IVisualPart<VR> anchorage,
			VR anchorageVisual);

	public List<IVisualPart<VR>> getAnchorages();

	public List<IVisualPart<VR>> getAnchoreds();

	public Map<Class<? extends IBehavior<VR>>, IBehavior<VR>> getBehaviors();

	public List<IVisualPart<VR>> getChildren();

	public IVisualPart<VR> getParent();

	public Map<Class<? extends IPolicy<VR>>, IPolicy<VR>> getPolicies();

	/**
	 * Returns the {@link IRootPart}. This method should only be called
	 * internally or by helpers such as edit policies. The root can be used to
	 * get the viewer.
	 * 
	 * @return <code>null</code> or the {@link IRootPart}
	 */
	public IRootPart<VR> getRoot();

	public abstract VR getVisual();

	// public void addVisualToParentVisual(IVisualPart<VR> parent, V
	// parentVisual);
	//
	// public void removeVisualFromParentVisual(IVisualPart<VR> parent, V
	// parentVisual);

	public boolean isRefreshVisual();

	// TODO: add by index and reordering of anchored

	public void refreshVisual();

	public void removeAnchorage(IVisualPart<VR> anchorage);

	public void removeAnchored(IVisualPart<VR> anchored);

	public void removeAnchoreds(List<? extends IVisualPart<VR>> anchoreds);

	public void removeChild(IVisualPart<VR> child);

	public void removeChildren(List<? extends IVisualPart<VR>> children);

	public void reorderChild(IVisualPart<VR> child, int index);

	public void setParent(IVisualPart<VR> parent);

	/**
	 * Allows to temporarily turn {@link #refreshVisual()} into a no-op
	 * operation. This may for instance be used to disable visual updates that
	 * are initiated by the model (in case of {@link IContentPart}s) while
	 * interacting with the {@link IVisualPart}.
	 * 
	 * @param refreshVisual
	 *            Whether {@link #refreshVisual()} should perform updates of the
	 *            visual (<code>true</code>) or behave like a no-op operation (
	 *            <code>false</code>).
	 */
	public void setRefreshVisual(boolean refreshVisual);
}