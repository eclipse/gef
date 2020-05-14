/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPart
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.activate.IActivatable;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.beans.property.ReadOnlyMultisetProperty;
import org.eclipse.gef.common.beans.property.ReadOnlySetMultimapProperty;
import org.eclipse.gef.common.collections.ObservableMultiset;
import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.common.dispose.IDisposable;
import org.eclipse.gef.mvc.fx.behaviors.IBehavior;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.gestures.IGesture;
import org.eclipse.gef.mvc.fx.handlers.IHandler;
import org.eclipse.gef.mvc.fx.policies.IPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * An {@link IVisualPart} plays the controller role in the model-view-controller
 * architecture. While it does not have to be bound to a model (actually only
 * {@link IContentPart}s are bound to model elements, {@link IFeedbackPart}s and
 * {@link IHandlePart}s do not refer to model elements), an {@link IVisualPart}
 * always controls a visual.
 * <p>
 * Within an {@link IViewer}, {@link IVisualPart} are organized in a hierarchy
 * via a <code>[1:n]</code> parent-children relationship ({@link #getParent()},
 * {@link #getChildrenUnmodifiable()}), which roots in an {@link IRootPart}.
 * Furthermore a <code>[n:m]</code> anchorage-anchored relationship (
 * {@link #getAnchoragesUnmodifiable()} , {@link #getAnchoredsUnmodifiable()})
 * may be established between {@link IVisualPart}s located at arbitrary places
 * within the hierarchy.
 * <p>
 * An {@link IVisualPart} is adaptable ({@link IAdaptable}). Usually,
 * {@link IPolicy}s and {@link IBehavior}s are adapted to it (but arbitrary
 * adapters may indeed be registered as needed). {@link IPolicy}s are usually
 * required in case the {@link IVisualPart} is directly involved in user
 * interaction (e.g. the user clicks on its controlled visual). They may be
 * accessed type-safe by {@link IGesture}s or other {@link IPolicy}s (
 * {@link IPolicy}s may delegate to other {@link IPolicy}s)). {@link IBehavior}s
 * are used to react to changes of the attached model (in case of an
 * {@link IContentPart}s), the viewer models, or others sources (e.g. adapters
 * of the {@link IViewer} or {@link IDomain}), thereby reacting to changes of
 * the interactive state.
 * <p>
 * {@link IVisualPart}s are {@link IActivatable} activatable, and an
 * activation/deactivation of an {@link IVisualPart} will result in the
 * activation/deactivation of all registered adapters (i.e. {@link IPolicy}s and
 * {@link IBehavior}s).
 * <p>
 * An {@link IVisualPart} is responsible for registering itself for its
 * visualization at the {@link IViewer#getVisualPartMap()} when it obtains a
 * link to the {@link IViewer}. Equally, an {@link IVisualPart} is responsible
 * for unregistering itself for its visualization from the
 * {@link IViewer#getVisualPartMap()} when it loses a link to the
 * {@link IViewer}.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 *              Instead, {@link AbstractVisualPart} should be subclassed.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual node used by this {@link IVisualPart}.
 */
public interface IVisualPart<V extends Node> extends IAdaptable,
		IAdaptable.Bound<IViewer>, IActivatable, IDisposable {

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
	 * Name of the property storing the refresh visual boolean property.
	 */
	public static final String REFRESH_VISUAL_PROPERTY = "refreshVisual";

	/**
	 * Adds the given child to the list of this part's children.
	 *
	 * @param child
	 *            The {@link IVisualPart} which is added to the list of this
	 *            part's children.
	 */
	public void addChild(IVisualPart<? extends Node> child);

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
	public void addChild(IVisualPart<? extends Node> child, int index);

	/**
	 * Adds the given children to the list of this part's children.
	 *
	 * @param children
	 *            The {@link IVisualPart}s which are added to the list of this
	 *            part's children.
	 */
	public void addChildren(
			List<? extends IVisualPart<? extends Node>> children);

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
			List<? extends IVisualPart<? extends Node>> children, int index);

	/**
	 * Returns a read-only set-multimap property containing this part's
	 * anchorages and their corresponding roles.
	 *
	 * @return A read-only set-multimap property named
	 *         {@link #ANCHORAGES_PROPERTY}.
	 */
	public ReadOnlySetMultimapProperty<IVisualPart<? extends Node>, String> anchoragesUnmodifiableProperty();

	/**
	 * Returns an unmodifiable read-only multiset property representing the
	 * anchoreds of this {@link IVisualPart}.
	 *
	 * @return An unmodifiable read-only multiset property named
	 *         {@link #ANCHOREDS_PROPERTY}.
	 */
	public ReadOnlyMultisetProperty<IVisualPart<? extends Node>> anchoredsUnmodifiableProperty();

	/**
	 * Used by an anchored {@link IVisualPart} to establish an
	 * anchorage-anchored relationship with this anchorage {@link IVisualPart}.
	 * <P>
	 * Clients should never call this operation directly but instead add the
	 * anchorage to its anchored via the {@link #attachToAnchorage(IVisualPart)}
	 * and {@link #attachToAnchorage(IVisualPart, String)} operations, which
	 * will indirectly lead to a call here.
	 *
	 * @param anchored
	 *            An {@link IVisualPart} to attach to this anchorage
	 *            {@link IVisualPart} as anchored.
	 *
	 * @noreference Clients should call {@link #attachToAnchorage(IVisualPart)},
	 *              {@link #attachToAnchorage(IVisualPart, String)} instead to
	 *              establish an anchored-anchorage relationship.
	 */
	public void attachAnchored(IVisualPart<? extends Node> anchored);

	/**
	 * Attaches the given {@link IVisualPart} to the given anchorage under the
	 * "default" role.
	 *
	 * @param anchorage
	 *            The anchorage {@link IVisualPart} to attach this part to.
	 */
	public void attachToAnchorage(IVisualPart<? extends Node> anchorage);

	/**
	 * Attaches the given {@link IVisualPart} to the given anchorage under the
	 * given role.
	 *
	 * @param anchorage
	 *            The anchorage {@link IVisualPart} to attach this part to.
	 * @param role
	 *            The role under which this {@link IVisualPart} is attached to
	 *            the given anchorage. <code>null</code>.
	 */
	public void attachToAnchorage(IVisualPart<? extends Node> anchorage,
			String role);

	/**
	 * Returns an unmodifiable read-only property containing the children of
	 * this {@link IVisualPart}.
	 *
	 * @see #getChildrenUnmodifiable()
	 * @see #addChild(IVisualPart)
	 * @see #addChild(IVisualPart, int)
	 * @see #addChildren(List)
	 * @see #addChildren(List, int)
	 * @see #removeChild(IVisualPart)
	 * @see #removeChildren(List)
	 * @see #reorderChild(IVisualPart, int)
	 *
	 * @return An unmodifiable read-only property named
	 *         {@link #CHILDREN_PROPERTY}.
	 */
	public ReadOnlyListProperty<IVisualPart<? extends Node>> childrenUnmodifiableProperty();

	/**
	 * Used by an anchored {@link IVisualPart} to unestablish an
	 * anchorage-anchored relationship with this anchorage {@link IVisualPart}.
	 * <P>
	 * Clients should never call this operation directly but instead remove the
	 * anchorage from its anchored via the
	 * {@link #detachFromAnchorage(IVisualPart)} or
	 * {@link #detachFromAnchorage(IVisualPart, String)} operations, which will
	 * indirectly lead to a call here.
	 *
	 * @param anchored
	 *            An {@link IVisualPart} (currently attached as anchored to this
	 *            anchorage {@link IVisualPart}) to detach from this anchorage
	 *            {@link IVisualPart} as anchored.
	 *
	 * @noreference Clients should call
	 *              {@link #detachFromAnchorage(IVisualPart)} or
	 *              {@link #detachFromAnchorage(IVisualPart, String)} instead to
	 *              unestablish an anchored-anchorage relationship.
	 */
	public void detachAnchored(IVisualPart<? extends Node> anchored);

	/**
	 * Detaches this {@link IVisualPart} from the given anchorage
	 * {@link IVisualPart} under the 'default' role.
	 *
	 * @param anchorage
	 *            The anchorage {@link IVisualPart} to detach this part from.
	 */
	public void detachFromAnchorage(IVisualPart<? extends Node> anchorage);

	/**
	 * Detaches this {@link IVisualPart} from the given anchorage
	 * {@link IVisualPart} under the given role.
	 *
	 * @param anchorage
	 *            The anchorage {@link IVisualPart} to detach this part from.
	 * @param role
	 *            The role under which the {@link IVisualPart} can be found in
	 *            this part's anchorages.
	 */
	public void detachFromAnchorage(IVisualPart<? extends Node> anchorage,
			String role);

	/**
	 * Returns an unmodifiable {@link ObservableSetMultimap} of this part's
	 * anchorages and their corresponding roles.
	 *
	 * @return A {@link ObservableSetMultimap} of this part's anchorages and
	 *         their corresponding roles.
	 */
	public ObservableSetMultimap<IVisualPart<? extends Node>, String> getAnchoragesUnmodifiable();

	/**
	 * Returns an unmodifiable {@link ObservableMultiset} of this part's
	 * anchoreds.
	 *
	 * @return An unmodifiable {@link ObservableMultiset} of this part's
	 *         anchoreds.
	 */
	public ObservableMultiset<IVisualPart<? extends Node>> getAnchoredsUnmodifiable();

	/**
	 * Returns a {@link Map} of this part's behaviors and their corresponding
	 * {@link AdapterKey}s.
	 *
	 * @return A {@link Map} of this part's behaviors and their corresponding
	 *         {@link AdapterKey}s.
	 */
	public Map<AdapterKey<? extends IBehavior>, IBehavior> getBehaviors();

	/**
	 * Returns an unmodifiable {@link ObservableList} of this part's children.
	 *
	 * @return A {@link ObservableList} of this part's children.
	 */
	public ObservableList<IVisualPart<? extends Node>> getChildrenUnmodifiable();

	/**
	 * Returns a {@link Map} of this part's handlers and their corresponding
	 * {@link AdapterKey}s.
	 *
	 * @return A {@link Map} of this part's handlers and their corresponding
	 *         {@link AdapterKey}s.
	 */
	public Map<AdapterKey<? extends IHandler>, IHandler> getHandlers();

	/**
	 * Returns the parent of this part.
	 *
	 * @return The parent of this part.
	 */
	public IVisualPart<? extends Node> getParent();

	/**
	 * Returns a {@link Map} of this part's policies and their corresponding
	 * {@link AdapterKey}s.
	 *
	 * @return A {@link Map} of this part's policies and their corresponding
	 *         {@link AdapterKey}s.
	 */
	public Map<AdapterKey<? extends IPolicy>, IPolicy> getPolicies();

	/**
	 * Returns the {@link IRootPart}. This method should only be called
	 * internally or by helpers such as edit policies. The root can be used to
	 * get the viewer.
	 *
	 * @return <code>null</code> or the {@link IRootPart}
	 */
	public IRootPart<? extends Node> getRoot();

	/**
	 * Returns the {@link IViewer} this {@link IVisualPart} is bound to.
	 *
	 * @return The {@link IViewer} this {@link IVisualPart} is attached to.
	 */
	public default IViewer getViewer() {
		return getAdaptable();
	}

	/**
	 * Returns this part's visual.
	 *
	 * @return This part's visual.
	 */
	public V getVisual();

	/**
	 * Returns <code>true</code> if this part is allowed to refresh its
	 * visualization based on its content. Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if this part is allowed to refresh its
	 *         visualization based on its content, otherwise <code>false</code>.
	 */
	public boolean isRefreshVisual();

	/**
	 * Returns a read-only property that refers to the parent of this
	 * {@link IVisualPart}.
	 *
	 * @see #getParent()
	 * @see #setParent(IVisualPart)
	 *
	 * @return A read-only property named {@link #PARENT_PROPERTY}.
	 */
	public ReadOnlyObjectProperty<IVisualPart<? extends Node>> parentProperty();

	/**
	 * Refreshes this part's visualization based on this part's content.
	 */
	public void refreshVisual();

	/**
	 * A boolean property indicating whether this {@link IVisualPart} should
	 * refresh its visuals or not.
	 *
	 * @see #isRefreshVisual()
	 * @see #setRefreshVisual(boolean)
	 *
	 * @return A boolean property named {@link #REFRESH_VISUAL_PROPERTY}.
	 */
	public BooleanProperty refreshVisualProperty();

	/**
	 * Removes the given {@link IVisualPart} from the list of this part's
	 * children.
	 *
	 * @param child
	 *            The {@link IVisualPart} which is removed from the list of this
	 *            part's children.
	 */
	public void removeChild(IVisualPart<? extends Node> child);

	/**
	 * Removes the given {@link IVisualPart}s from the list of this part's
	 * children.
	 *
	 * @param children
	 *            The {@link IVisualPart}s which are removed from the list of
	 *            this part's children.
	 */
	public void removeChildren(
			List<? extends IVisualPart<? extends Node>> children);

	/**
	 * Swaps the given {@link IVisualPart} with the part at the given index
	 * position within this part's list of children.
	 *
	 * @param child
	 *            The {@link IVisualPart} which is reordered.
	 * @param index
	 *            The index to which the part is reordered.
	 */
	public void reorderChild(IVisualPart<? extends Node> child, int index);

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
	public void setParent(IVisualPart<? extends Node> parent);

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