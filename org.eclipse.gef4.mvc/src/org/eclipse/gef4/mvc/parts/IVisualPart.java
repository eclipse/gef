/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.gef4.common.activate.IActivatable;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.dispose.IDisposable;
import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.policies.IPolicy;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;

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
 * <code>[n:m]</code> anchorage-anchored relationship ( {@link #getAnchorages()}
 * , {@link #getAnchoreds()}) may be established between {@link IVisualPart}s
 * located at arbitrary places within the hierarchy.
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
 * thereby reacting to changes of the interactive state.
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
 * @param <V>
 *            The visual node used by this {@link IVisualPart}.
 */
public interface IVisualPart<VR, V extends VR>
		extends IAdaptable, IActivatable, IPropertyChangeNotifier, IDisposable {

	/**
	 * Name of the property storing this part's parent.
	 */
	public static final String PARENT_PROPERTY = "parent";
	/**
	 * Name of the property storing this part's children.
	 */
	public static final String CHILDREN_PROPERTY = "children";
	/**
	 * Name of the property storing this part's anchorages.
	 */
	public static final String ANCHORAGES_PROPERTY = "anchorages";
	/**
	 * Name of the property storing this part's anchoreds.
	 */
	public static final String ANCHOREDS_PROPERTY = "anchoreds";

	/**
	 * Adds the given {@link IVisualPart} to the anchorages of this
	 * {@link IVisualPart} under the "default" role.
	 *
	 * @param anchorage
	 *            The {@link IVisualPart} which is added to this part's
	 *            anchorages.
	 */
	public void addAnchorage(IVisualPart<VR, ? extends VR> anchorage);

	/**
	 * Adds the given {@link IVisualPart} to the anchorages of this
	 * {@link IVisualPart} under the given role.
	 *
	 * @param anchorage
	 *            The {@link IVisualPart} which is added to this part's
	 *            anchorages.
	 * @param role
	 *            The role under which the anchorage is added, or
	 *            <code>null</code>.
	 */
	public void addAnchorage(IVisualPart<VR, ? extends VR> anchorage,
			String role);

	/**
	 * Used by an anchored {@link IVisualPart} to establish an
	 * anchorage-anchored relationship with this anchorage {@link IVisualPart}.
	 * <P>
	 * Clients should never call this operation directly but instead add the
	 * anchorage to its anchored via the {@link #addAnchorage(IVisualPart)} and
	 * {@link #addAnchorage(IVisualPart, String)} operations, which will
	 * indirectly lead to a call here.
	 *
	 * @param anchored
	 *            An {@link IVisualPart} to attach to this anchorage
	 *            {@link IVisualPart} as anchored.
	 *
	 * @noreference Clients should call {@link #addAnchorage(IVisualPart)},
	 *              {@link #addAnchorage(IVisualPart, String)} instead to
	 *              establish an anchored-anchorage relationship.
	 */
	public void addAnchored(IVisualPart<VR, ? extends VR> anchored);

	/**
	 * Adds the given child to the list of this part's children.
	 *
	 * @param child
	 *            The {@link IVisualPart} which is added to the list of this
	 *            part's children.
	 */
	public void addChild(IVisualPart<VR, ? extends VR> child);

	/**
	 * Adds the given child to the list of this part's children at the specified
	 * index.
	 *
	 * @param child
	 *            The {@link IVisualPart} which is added to the list of this
	 *            part's children.
	 * @param index
	 *            The index at which the given {@link IVisualPart} is inserted
	 *            into this part's children list.
	 */
	public void addChild(IVisualPart<VR, ? extends VR> child, int index);

	/**
	 * Adds the given children to the list of this part's children.
	 *
	 * @param children
	 *            The {@link IVisualPart}s which are added to the list of this
	 *            part's children.
	 */
	public void addChildren(
			List<? extends IVisualPart<VR, ? extends VR>> children);

	/**
	 * Adds the given children to the list of this part's children at the
	 * specified index.
	 *
	 * @param children
	 *            The {@link IVisualPart}s which are added to the list of this
	 *            part's children.
	 * @param index
	 *            The index at which the given {@link IVisualPart}s are inserted
	 *            into this part's children list.
	 */
	public void addChildren(
			List<? extends IVisualPart<VR, ? extends VR>> children, int index);

	/**
	 * Returns a {@link SetMultimap} of this part's anchorages and their
	 * corresponding roles.
	 *
	 * @return A {@link SetMultimap} of this part's anchorages and their
	 *         corresponding roles.
	 */
	public SetMultimap<IVisualPart<VR, ? extends VR>, String> getAnchorages();

	/**
	 * Returns a {@link Multiset} of this part's anchoreds.
	 *
	 * @return A {@link Multiset} of this part's anchoreds.
	 */
	public Multiset<IVisualPart<VR, ? extends VR>> getAnchoreds();

	/**
	 * Returns a {@link Map} of this part's behaviors and their corresponding
	 * {@link AdapterKey}s.
	 *
	 * @return A {@link Map} of this part's behaviors and their corresponding
	 *         {@link AdapterKey}s.
	 */
	public Map<AdapterKey<? extends IBehavior<VR>>, IBehavior<VR>> getBehaviors();

	/**
	 * Returns a {@link List} of this part's children.
	 *
	 * @return A {@link List} of this part's children.
	 */
	public List<IVisualPart<VR, ? extends VR>> getChildren();

	/**
	 * Returns the parent of this part.
	 *
	 * @return The parent of this part.
	 */
	public IVisualPart<VR, ? extends VR> getParent();

	/**
	 * Returns a {@link Map} of this part's policies and their corresponding
	 * {@link AdapterKey}s.
	 *
	 * @return A {@link Map} of this part's policies and their corresponding
	 *         {@link AdapterKey}s.
	 */
	public Map<AdapterKey<? extends IPolicy<VR>>, IPolicy<VR>> getPolicies();

	/**
	 * Returns the {@link IRootPart}. This method should only be called
	 * internally or by helpers such as edit policies. The root can be used to
	 * get the viewer.
	 *
	 * @return <code>null</code> or the {@link IRootPart}
	 */
	public IRootPart<VR, ? extends VR> getRoot();

	/**
	 * Returns this part's visual.
	 *
	 * @return This part's visual.
	 */
	public abstract V getVisual();

	/**
	 * Returns <code>true</code> if this part is allowed to refresh its
	 * visualization based on its content. Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if this part is allowed to refresh its
	 *         visualization based on its content, otherwise <code>false</code>.
	 */
	public boolean isRefreshVisual();

	/**
	 * Refreshes this part's visualization based on this part's content.
	 */
	public void refreshVisual();

	/**
	 * Removes the given {@link IVisualPart} from the map of this part's
	 * anchorages.
	 *
	 * @param anchorage
	 *            The {@link IVisualPart} which is removed from this part's
	 *            anchorages.
	 */
	public void removeAnchorage(IVisualPart<VR, ? extends VR> anchorage);

	/**
	 * Removes the given {@link IVisualPart} and role from the map of this
	 * part's anchorages.
	 *
	 * @param anchorage
	 *            The {@link IVisualPart} which is removed from this part's
	 *            anchorages.
	 * @param role
	 *            The role under which the {@link IVisualPart} can be found in
	 *            this part's anchorages.
	 */
	// role may be null
	public void removeAnchorage(IVisualPart<VR, ? extends VR> anchorage,
			String role);

	/**
	 * Used by an anchored {@link IVisualPart} to unestablish an
	 * anchorage-anchored relationship with this anchorage {@link IVisualPart}.
	 * <P>
	 * Clients should never call this operation directly but instead remove the
	 * anchorage from its anchored via the {@link #removeAnchorage(IVisualPart)}
	 * or {@link #removeAnchorage(IVisualPart, String)} operations, which will
	 * indirectly lead to a call here.
	 *
	 * @param anchored
	 *            An {@link IVisualPart} (currently attached as anchored to this
	 *            anchorage {@link IVisualPart}) to detach from this anchorage
	 *            {@link IVisualPart} as anchored.
	 *
	 * @noreference Clients should call {@link #removeAnchorage(IVisualPart)} or
	 *              {@link #removeAnchorage(IVisualPart, String)} instead to
	 *              unestablish an anchored-anchorage relationship.
	 */
	public void removeAnchored(IVisualPart<VR, ? extends VR> anchored);

	/**
	 * Removes the given {@link IVisualPart} from the list of this part's
	 * children.
	 *
	 * @param child
	 *            The {@link IVisualPart} which is removed from the list of this
	 *            part's children.
	 */
	public void removeChild(IVisualPart<VR, ? extends VR> child);

	/**
	 * Removes the given {@link IVisualPart}s from the list of this part's
	 * children.
	 *
	 * @param children
	 *            The {@link IVisualPart}s which are removed from the list of
	 *            this part's children.
	 */
	public void removeChildren(
			List<? extends IVisualPart<VR, ? extends VR>> children);

	/**
	 * Swaps the given {@link IVisualPart} with the part at the given index
	 * position within this part's list of children.
	 *
	 * @param child
	 *            The {@link IVisualPart} which is reordered.
	 * @param index
	 *            The index to which the part is reordered.
	 */
	public void reorderChild(IVisualPart<VR, ? extends VR> child, int index);

	/**
	 * Used by a parent {@link IVisualPart} to establish/unestablish a
	 * parent-child relationship with this child {@link IVisualPart}.
	 * <P>
	 * Clients should never call this operation directly but instead add the
	 * children to its parent via the {@link #addChild(IVisualPart)},
	 * {@link #addChild(IVisualPart, int)}, {@link #addChildren(List)}, or
	 * {@link #addChildren(List, int)} or remove it via the
	 * {@link #removeChild(IVisualPart)} or {@link #removeChildren(List)}
	 * operations, which will indirectly lead to a call here.
	 *
	 * @param parent
	 *            The new parent {@link IVisualPart} or <code>null</code>.
	 *
	 * @noreference Clients should use {@link #addChild(IVisualPart)},
	 *              {@link #addChild(IVisualPart, int)},
	 *              {@link #addChildren(List)}, {@link #addChildren(List, int)},
	 *              {@link #removeChild(IVisualPart)}, or
	 *              {@link #removeChildren(List)} to establish/unestablish a
	 *              parent-child relationship instead.
	 */
	public void setParent(IVisualPart<VR, ? extends VR> parent);

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