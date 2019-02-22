/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef.editparts.AbstractEditPart.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.gef.common.collections.SetMultimapChangeListener;
import org.eclipse.gef.common.dispose.IDisposable;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;

/**
 * A behavior that can be adapted to an {@link IRootPart} or an
 * {@link IContentPart} to synchronize the list of {@link IContentPart} children
 * and (only in case of an {@link IContentPart}) anchorages with the list of
 * content children and anchored.
 *
 * @author anyssen
 *
 */
public class ContentBehavior extends AbstractBehavior implements IDisposable {

	private ListChangeListener<Object> contentObserver = new ListChangeListener<Object>() {
		@Override
		public void onChanged(
				ListChangeListener.Change<? extends Object> change) {
			// System.out.println("Content changed " + change);
			// XXX: An atomic operation (including setAll()) on the
			// ObservableList will lead to an atomic change here; we do not have
			// to iterate through the individual changes but may simply
			// synchronize with the list as it emerges after the changes have
			// been applied.
			// while (change.next()) {
			// if (change.wasRemoved()) {
			// removeContentPartChildren(getHost(),
			// ImmutableList.copyOf(change.getRemoved()));
			// } else if (change.wasAdded()) {
			// addContentPartChildren(getHost(),
			// ImmutableList.copyOf(change.getAddedSubList()),
			// change.getFrom());
			// } else if (change.wasPermutated()) {
			// throw new UnsupportedOperationException(
			// "Reorder not yet implemented");
			// }
			// }
			synchronizeContentPartChildren(getHost(), change.getList());
		}
	};

	private ListChangeListener<Object> contentChildrenObserver = new ListChangeListener<Object>() {
		@SuppressWarnings("unchecked")
		@Override
		public void onChanged(
				final ListChangeListener.Change<? extends Object> change) {
			// System.out.println("Content children changed " + change);
			// XXX: An atomic operation (including setAll()) on the
			// ObservableList will lead to an atomic change here; we do not have
			// to iterate through the individual changes but may simply
			// synchronize with the list as it emerges after the changes have
			// been applied.
			IContentPart<? extends Node> parent = (IContentPart<? extends Node>) ((ReadOnlyProperty<?>) change
					.getList()).getBean();
			// while (change.next()) {
			// if (change.wasRemoved()) {
			// removeContentPartChildren(parent,
			// ImmutableList.copyOf(change.getRemoved()));
			// } else if (change.wasAdded()) {
			// addContentPartChildren(parent,
			// ImmutableList.copyOf(change.getAddedSubList()),
			// change.getFrom());
			// } else if (change.wasPermutated()) {
			// throw new UnsupportedOperationException(
			// "Reorder not yet implemented");
			// }
			// }
			synchronizeContentPartChildren(parent, change.getList());
		}
	};

	private SetMultimapChangeListener<Object, String> contentAnchoragesObserver = new SetMultimapChangeListener<Object, String>() {
		@SuppressWarnings("unchecked")
		@Override
		public void onChanged(
				final SetMultimapChangeListener.Change<? extends Object, ? extends String> change) {
			// System.out.println("Content anchorages changed " + change);
			// XXX: An atomic operation (including replaceAll()) on the
			// ObservableSetMultimap will lead to an atomic change here; we do
			// not have to iterate through the individual changes but may simply
			// synchronize with the list as it emerges after the changes have
			// been applied.
			// TODO: detach or attach directly
			IContentPart<? extends Node> anchored = (IContentPart<? extends Node>) ((ReadOnlyProperty<?>) change
					.getSetMultimap()).getBean();
			synchronizeContentPartAnchorages(anchored,
					HashMultimap.create(change.getSetMultimap()));
		}
	};

	private MapChangeListener<Object, IContentPart<? extends Node>> contentPartMapObserver = new MapChangeListener<Object, IContentPart<? extends Node>>() {
		@Override
		public void onChanged(
				MapChangeListener.Change<? extends Object, ? extends IContentPart<? extends Node>> change) {
			if (change.wasRemoved()) {
				IContentPart<? extends Node> contentPart = change
						.getValueRemoved();
				contentPart.contentChildrenUnmodifiableProperty()
						.removeListener(contentChildrenObserver);
				contentPart.contentAnchoragesUnmodifiableProperty()
						.removeListener(contentAnchoragesObserver);
			}
			if (change.wasAdded()) {
				IContentPart<? extends Node> contentPart = change
						.getValueAdded();
				contentPart.contentChildrenUnmodifiableProperty()
						.addListener(contentChildrenObserver);
				contentPart.contentAnchoragesUnmodifiableProperty()
						.addListener(contentAnchoragesObserver);
			}
		}
	};

	@SuppressWarnings("unchecked")
	private void addAll(IVisualPart<? extends Node> parent,
			List<? extends Object> contentChildren,
			List<IContentPart<? extends Node>> added,
			LinkedHashMap<IVisualPart<? extends Node>, List<IContentPart<? extends Node>>> addsPerParent) {

		List<IContentPart<? extends Node>> childContentParts = PartUtils
				.filterParts(parent.getChildrenUnmodifiable(),
						IContentPart.class);
		// store the existing content parts in a map using the contents as keys
		Map<Object, IContentPart<? extends Node>> contentPartMap = new HashMap<>();
		// find all content parts for which no content element exists in
		// contentChildren, and therefore have to be removed
		for (IContentPart<? extends Node> contentPart : childContentParts) {
			// store content part in map
			contentPartMap.put(contentPart.getContent(), contentPart);
		}
		int contentChildrenSize = contentChildren.size();
		int childContentPartsSize = childContentParts.size();
		List<IContentPart<? extends Node>> childrenToAdd = new ArrayList<>();
		for (int i = 0; i < contentChildrenSize; i++) {
			Object content = contentChildren.get(i);
			// Do a quick check to see if the existing content part is at
			// the correct location in the children list.
			if (i < childContentPartsSize
					&& childContentParts.get(i).getContent() == content) {
				continue;
			}
			// Look to see if the ContentPart is already around but in the
			// wrong location.
			IContentPart<? extends Node> contentPart = findOrCreatePartFor(
					content);
			if (contentPartMap.containsKey(content)) {
				// Re-order the existing content part to its designated
				// location in the children list.
				// TODO: this is wrong, it has to take into consideration
				// the visual parts in between
				parent.reorderChild(contentPart, i);
			} else {
				// A ContentPart for this model does not exist yet. Create
				// and insert one.
				if (contentPart.getParent() != null) {
					// TODO: Up to now a model element may only be
					// controlled by a single content part; unless we
					// differentiate content elements by context (which is not
					// covered by the current content part map implementation)
					// it is an illegal state if we locate a content part, which
					// is already bound to a parent and whose content is equal
					// to the one we are processing here.
					throw new IllegalStateException(
							"Located a ContentPart which controls the same (or an equal) content element but is already bound to a parent. A content element may only be controlled by a single ContentPart.");
				}
				added.add(contentPart);
				childrenToAdd.add(contentPart);
				addAll(contentPart,
						contentPart.getContentChildrenUnmodifiable(), added,
						addsPerParent);
			}
		}
		if (!childrenToAdd.isEmpty()) {
			addsPerParent.put(parent, childrenToAdd);
		}
	}

	@SuppressWarnings("unchecked")
	private void detachAll(IVisualPart<? extends Node> parent,
			final Set<? extends Object> contentChildren,
			List<IContentPart<? extends Node>> toRemove,
			Map<IVisualPart<? extends Node>, List<IContentPart<? extends Node>>> removalsPerParent) {
		// only synchronize IContentPart children
		// find all content parts for which no content element exists in
		// contentChildren, and therefore have to be removed

		List<IContentPart<? extends Node>> childrenToRemove = new ArrayList<>();
		for (IContentPart<? extends Node> contentPart : (List<IContentPart<? extends Node>>) PartUtils
				.filterParts(parent.getChildrenUnmodifiable(),
						IContentPart.class)) {
			// mark for removal
			if (!contentChildren.contains(contentPart.getContent())) {
				detachAll(contentPart, Collections.emptySet(), toRemove,
						removalsPerParent);
				childrenToRemove.add(contentPart);
				toRemove.add(contentPart);
				synchronizeContentPartAnchorages(contentPart,
						HashMultimap.create());
			}
		}
		if (!childrenToRemove.isEmpty()) {
			removalsPerParent.put(parent, childrenToRemove);
		}
	}

	@Override
	public void dispose() {
		// the content part pool is shared by all content behaviors of a viewer,
		// so the viewer disposes it.
		contentObserver = null;
		contentChildrenObserver = null;
		contentAnchoragesObserver = null;
	}

	/**
	 * If the given {@link IContentPart} does neither have a parent nor any
	 * anchoreds, then it's content is set to <code>null</code> and the part is
	 * added to the {@link ContentPartPool}.
	 *
	 * @param contentPart
	 *            The {@link IContentPart} that is eventually disposed.
	 */
	protected void disposeIfObsolete(IContentPart<? extends Node> contentPart) {
		if (contentPart.getParent() == null
				&& contentPart.getAnchoredsUnmodifiable().isEmpty()) {
			// System.out.println("DISPOSE " + contentPart.getContent());
			getContentPartPool().add(contentPart);
			contentPart.setContent(null);
		} // else {
			// System.out.println("CANNOT DISPOSE " + contentPart.getContent());
			// }
	}

	@Override
	protected void doActivate() {
		IVisualPart<? extends Node> host = getHost();
		if (host != host.getRoot()) {
			throw new IllegalArgumentException();
		}
		IViewer viewer = host.getRoot().getViewer();
		viewer.contentPartMapProperty().addListener(contentPartMapObserver);
		synchronizeContentPartChildren(getHost(), viewer.getContents());
		viewer.getContents().addListener(contentObserver);
	}

	@Override
	protected void doDeactivate() {
		IVisualPart<? extends Node> host = getHost();
		IViewer viewer = host.getRoot().getViewer();
		viewer.getContents().removeListener(contentObserver);
		synchronizeContentPartChildren(getHost(), Collections.emptyList());
		viewer.contentPartMapProperty().removeListener(contentPartMapObserver);
	}

	/**
	 * Finds/Revives/Creates an {@link IContentPart} for the given
	 * <i>content</i> {@link Object}. If an {@link IContentPart} for the given
	 * content {@link Object} can be found in the viewer's content-part-map,
	 * then this part is returned. If an {@link IContentPart} for the given
	 * content {@link Object} is stored in the {@link ContentPartPool}, then
	 * this part is returned. Otherwise, the injected
	 * {@link IContentPartFactory} is used to create a new {@link IContentPart}
	 * for the given content {@link Object}.
	 *
	 * @param content
	 *            The content {@link Object} for which the corresponding
	 *            {@link IContentPart} is to be returned.
	 * @return The {@link IContentPart} corresponding to the given
	 *         <i>content</i> {@link Object}.
	 */
	protected IContentPart<? extends Node> findOrCreatePartFor(Object content) {
		Map<Object, IContentPart<? extends Node>> contentPartMap = getHost()
				.getRoot().getViewer().getContentPartMap();
		if (contentPartMap.containsKey(content)) {
			// System.out.println("FOUND " + content);
			return contentPartMap.get(content);
		} else {
			// 'Revive' a content part, if it was removed before
			IContentPart<? extends Node> contentPart = getContentPartPool()
					.remove(content);
			// If the part could not be revived, a new one is created
			if (contentPart == null) {
				// create part using the factory
				// System.out.println("CREATE " + content);
				IContentPartFactory contentPartFactory = getContentPartFactory();
				contentPart = contentPartFactory.createContentPart(content,
						Collections.emptyMap());
				if (contentPart == null) {
					throw new IllegalStateException("IContentPartFactory '"
							+ contentPartFactory.getClass().getSimpleName()
							+ "' did not create part for " + content + ".");
				}
			} // else {
				// System.out.println("REVIVE " + content);
				// }

			// initialize part
			contentPart.setContent(content);
			return contentPart;
		}
	}

	/**
	 * Returns the {@link IContentPartFactory} of the current viewer.
	 *
	 * @return the {@link IContentPartFactory} of the current viewer.
	 */
	protected IContentPartFactory getContentPartFactory() {
		return getHost().getRoot().getViewer()
				.getAdapter(IContentPartFactory.class);
	}

	/**
	 * Returns the {@link ContentPartPool} that is used to recycle content parts
	 * in the context of an {@link IViewer}.
	 *
	 * @return The {@link ContentPartPool} of the {@link IViewer}.
	 */
	protected ContentPartPool getContentPartPool() {
		return getHost().getRoot().getViewer()
				.getAdapter(ContentPartPool.class);
	}

	/**
	 * Updates the host {@link IVisualPart}'s {@link IContentPart} anchorages
	 * (see {@link IVisualPart#getAnchoragesUnmodifiable()}) so that it is in
	 * sync with the set of content anchorages that is passed in.
	 *
	 * @param anchored
	 *            The anchored {@link IVisualPart} whose content part anchorages
	 *            to synchronize with the given content anchorages.
	 *
	 * @param contentAnchorages
	 *            * The map of content anchorages with roles to be synchronized
	 *            with the list of {@link IContentPart} anchorages (
	 *            {@link IContentPart#getAnchoragesUnmodifiable()}).
	 *
	 * @see IContentPart#getContentAnchoragesUnmodifiable()
	 * @see IContentPart#getAnchoragesUnmodifiable()
	 */
	public void synchronizeContentPartAnchorages(
			IVisualPart<? extends Node> anchored,
			SetMultimap<? extends Object, ? extends String> contentAnchorages) {
		if (contentAnchorages == null) {
			throw new IllegalArgumentException(
					"contentAnchorages may not be null");
		}
		SetMultimap<IVisualPart<? extends Node>, String> anchorages = anchored
				.getAnchoragesUnmodifiable();

		// find anchorages whose content vanished
		List<Entry<IVisualPart<? extends Node>, String>> toRemove = new ArrayList<>();
		Set<Entry<IVisualPart<? extends Node>, String>> entries = anchorages
				.entries();
		for (Entry<IVisualPart<? extends Node>, String> e : entries) {
			if (!(e.getKey() instanceof IContentPart)) {
				continue;
			}
			Object content = ((IContentPart<? extends Node>) e.getKey())
					.getContent();
			if (!contentAnchorages.containsEntry(content, e.getValue())) {
				toRemove.add(e);
			}
		}

		// Correspondingly remove the anchorages. This is done in a separate
		// step to prevent ConcurrentModificationException.
		for (Entry<IVisualPart<? extends Node>, String> contentPart : toRemove) {
			anchored.detachFromAnchorage(contentPart.getKey(),
					contentPart.getValue());
			disposeIfObsolete(
					(IContentPart<? extends Node>) contentPart.getKey());
		}

		// find content for which no anchorages exist
		List<Entry<IVisualPart<? extends Node>, String>> toAdd = new ArrayList<>();
		for (Entry<? extends Object, ? extends String> e : contentAnchorages
				.entries()) {
			IContentPart<? extends Node> anchorage = findOrCreatePartFor(
					e.getKey());
			if (!anchorages.containsEntry(anchorage, e.getValue())) {
				toAdd.add(Maps
						.<IVisualPart<? extends Node>, String> immutableEntry(
								anchorage, e.getValue()));
			}
		}

		// Correspondingly add the anchorages. This is done in a separate
		// step to prevent ConcurrentModificationException.
		for (Entry<IVisualPart<? extends Node>, String> e : toAdd) {
			anchored.attachToAnchorage(e.getKey(), e.getValue());
		}
	}

	/**
	 * Updates the host {@link IVisualPart}'s {@link IContentPart} children (see
	 * {@link IVisualPart#getChildrenUnmodifiable()}) so that it is in sync with
	 * the set of content children that is passed in.
	 *
	 * @param parent
	 *            The parent {@link IVisualPart} whose content part children to
	 *            synchronize against the given content children.
	 *
	 * @param contentChildren
	 *            The list of content part children to be synchronized with the
	 *            list of {@link IContentPart} children (
	 *            {@link IContentPart#getChildrenUnmodifiable()}).
	 *
	 * @see IContentPart#getContentChildrenUnmodifiable()
	 * @see IContentPart#getChildrenUnmodifiable()
	 */
	public void synchronizeContentPartChildren(
			IVisualPart<? extends Node> parent,
			final List<? extends Object> contentChildren) {
		if (contentChildren == null) {
			throw new IllegalArgumentException(
					"contentChildren may not be null");
		}

		List<IContentPart<? extends Node>> toRemove = new ArrayList<>();
		Map<IVisualPart<? extends Node>, List<IContentPart<? extends Node>>> removalsPerParent = new LinkedHashMap<>();
		detachAll(parent, Sets.newHashSet(contentChildren), toRemove,
				removalsPerParent);
		removalsPerParent.forEach((removeFrom, removeUs) -> {
			removeFrom.removeChildren(removeUs);
		});
		for (IContentPart<? extends Node> cp : toRemove) {
			disposeIfObsolete(cp);
		}

		LinkedHashMap<IVisualPart<? extends Node>, List<IContentPart<? extends Node>>> addsPerParent = new LinkedHashMap<IVisualPart<? extends Node>, List<IContentPart<? extends Node>>>();
		ArrayList<IContentPart<? extends Node>> added = Lists.newArrayList();
		addAll(parent, contentChildren, added, addsPerParent);

		ArrayList<IVisualPart<? extends Node>> keyList = new ArrayList<IVisualPart<? extends Node>>(
				addsPerParent.keySet());

		for (int i = keyList.size() - 1; i >= 0; i--) {
			IVisualPart<? extends Node> key = keyList.get(i);
			List<IContentPart<? extends Node>> value = addsPerParent.get(key);
			key.addChildren(value);

		}
		added.forEach(cp -> {
			synchronizeContentPartAnchorages(cp,
					cp.getContentAnchoragesUnmodifiable());
		});
	}
}
