/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.gef.common.collections.SetMultimapChangeListener;
import org.eclipse.gef.common.dispose.IDisposable;
import org.eclipse.gef.common.reflect.Types;
import org.eclipse.gef.mvc.models.ContentModel;
import org.eclipse.gef.mvc.models.HoverModel;
import org.eclipse.gef.mvc.models.SelectionModel;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IContentPartFactory;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.parts.PartUtils;
import org.eclipse.gef.mvc.viewer.IViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

/**
 * A behavior that can be adapted to an {@link IRootPart} or an
 * {@link IContentPart} to synchronize the list of {@link IContentPart} children
 * and (only in case of an {@link IContentPart}) anchorages with the list of
 * content children and anchored.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public class ContentBehavior<VR> extends AbstractBehavior<VR>
		implements IDisposable {

	private ListChangeListener<Object> contentModelObserver = new ListChangeListener<Object>() {
		@Override
		public void onChanged(
				ListChangeListener.Change<? extends Object> change) {
			// XXX: An atomic operation (including setAll()) on the
			// ObservableList will lead to an atomic change here; we do not have
			// to iterate through the individual changes but may simply
			// synchronize with the list as it emerges after the changes have
			// been applied.
			synchronizeContentChildren(change.getList());

			// TODO: Check if the flushing of the viewer models can be done in a
			// more appropriate place.

			// clear selection
			IViewer<VR> viewer = getHost().getRoot().getViewer();
			@SuppressWarnings("serial")
			SelectionModel<VR> selectionModel = viewer
					.getAdapter(new TypeToken<SelectionModel<VR>>() {
					}.where(new TypeParameter<VR>() {
					}, Types.<VR> argumentOf(viewer.getClass())));
			if (selectionModel != null) {
				selectionModel.clearSelection();
			}
			// clear hover
			@SuppressWarnings("serial")
			HoverModel<VR> hoverModel = viewer
					.getAdapter(new TypeToken<HoverModel<VR>>() {
					}.where(new TypeParameter<VR>() {
					}, Types.<VR> argumentOf(viewer.getClass())));
			if (hoverModel != null) {
				hoverModel.clearHover();
			}
		}
	};

	private ChangeListener<Object> contentObserver = new ChangeListener<Object>() {
		@Override
		public void changed(ObservableValue<? extends Object> observable,
				Object oldValue, Object newValue) {
			synchronizeContentChildren(ImmutableList
					.copyOf(((IContentPart<VR, ? extends VR>) getHost())
							.getContentChildrenUnmodifiable()));
			synchronizeContentAnchorages(ImmutableSetMultimap
					.copyOf(((IContentPart<VR, ? extends VR>) getHost())
							.getContentAnchoragesUnmodifiable()));
		}
	};

	private ListChangeListener<Object> contentChildrenObserver = new ListChangeListener<Object>() {
		@Override
		public void onChanged(
				final ListChangeListener.Change<? extends Object> change) {
			// XXX: An atomic operation (including setAll()) on the
			// ObservableList will lead to an atomic change here; we do not have
			// to iterate through the individual changes but may simply
			// synchronize with the list as it emerges after the changes have
			// been applied.
			synchronizeContentChildren(new ArrayList<>(change.getList()));
		}
	};

	private SetMultimapChangeListener<Object, String> contentAnchoragesObserver = new SetMultimapChangeListener<Object, String>() {
		@Override
		public void onChanged(
				final SetMultimapChangeListener.Change<? extends Object, ? extends String> change) {
			// XXX: An atomic operation (including replaceAll()) on the
			// ObservableSetMultimap will lead to an atomic change here; we do
			// not have to iterate through the individual changes but may simply
			// synchronize with the list as it emerges after the changes have
			// been applied.
			synchronizeContentAnchorages(
					HashMultimap.create(change.getSetMultimap()));
		}
	};

	@Inject
	// scoped to single instance within viewer
	private ContentPartPool<VR> contentPartPool;

	@Override
	public void dispose() {
		// the content part pool is shared by all content behaviors of a viewer,
		// so the viewer disposes it.
		contentPartPool = null;
		contentObserver = null;
		contentModelObserver = null;
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
	protected void disposeIfObsolete(
			IContentPart<VR, ? extends VR> contentPart) {
		if (contentPart.getParent() == null
				&& contentPart.getAnchoredsUnmodifiable().isEmpty()) {
			// System.out.println("DISPOSE " + contentPart.getContent());
			contentPartPool.add(contentPart);
			contentPart.setContent(null);
		}
	}

	@Override
	protected void doActivate() {
		IVisualPart<VR, ? extends VR> host = getHost();
		if (host == host.getRoot()) {
			final ContentModel contentModel = getContentModel();
			contentModel.getContents().addListener(contentModelObserver);
			synchronizeContentChildren(contentModel.getContents());
		} else {
			synchronizeContentChildren(
					ImmutableList.copyOf(((IContentPart<VR, ? extends VR>) host)
							.getContentChildrenUnmodifiable()));
			synchronizeContentAnchorages(ImmutableSetMultimap
					.copyOf(((IContentPart<VR, ? extends VR>) host)
							.getContentAnchoragesUnmodifiable()));
			((IContentPart<VR, ? extends VR>) host).contentProperty()
					.addListener(contentObserver);
			((IContentPart<VR, ? extends VR>) host)
					.getContentChildrenUnmodifiable()
					.addListener(contentChildrenObserver);
			((IContentPart<VR, ? extends VR>) host)
					.getContentAnchoragesUnmodifiable()
					.addListener(contentAnchoragesObserver);
		}
	}

	@Override
	protected void doDeactivate() {
		IVisualPart<VR, ? extends VR> host = getHost();
		if (host == host.getRoot()) {
			getContentModel().getContents()
					.removeListener(contentModelObserver);
		} else {
			((IContentPart<VR, ? extends VR>) host).contentProperty()
					.removeListener(contentObserver);
			((IContentPart<VR, ? extends VR>) host)
					.getContentChildrenUnmodifiable()
					.removeListener(contentChildrenObserver);
			((IContentPart<VR, ? extends VR>) host)
					.getContentAnchoragesUnmodifiable()
					.removeListener(contentAnchoragesObserver);
		}
	}

	/**
	 * Finds/Revives/Creates an {@link IContentPart} for the given
	 * <i>content</i> {@link Object}. If an {@link IContentPart} for the given
	 * content {@link Object} can be found in the viewer's content-part-map,
	 * then this part is returned. If an {@link IContentPart} for the given
	 * content {@link Object} is stored in the injected {@link ContentPartPool},
	 * then this part is returned. Otherwise, the injected
	 * {@link IContentPartFactory} is used to create a new {@link IContentPart}
	 * for the given content {@link Object}.
	 *
	 * @param content
	 *            The content {@link Object} for which the corresponding
	 *            {@link IContentPart} is to be returned.
	 * @return The {@link IContentPart} corresponding to the given
	 *         <i>content</i> {@link Object}.
	 */
	protected IContentPart<VR, ? extends VR> findOrCreatePartFor(
			Object content) {
		Map<Object, IContentPart<VR, ? extends VR>> contentPartMap = getHost()
				.getRoot().getViewer().getContentPartMap();
		if (contentPartMap.containsKey(content)) {
			// System.out.println("FOUND " + content);
			return contentPartMap.get(content);
		} else {
			// 'Revive' a content part, if it was removed before
			IContentPart<VR, ? extends VR> contentPart = null;
			contentPart = contentPartPool.remove(content);

			// If the part could not be revived, a new one is created
			if (contentPart == null) {
				// create part using the factory
				// System.out.println("CREATE " + content);
				IContentPartFactory<VR> contentPartFactory = getContentPartFactory();
				contentPart = contentPartFactory.createContentPart(content,
						this, Collections.emptyMap());
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
	 * Returns the {@link ContentModel} in the context of the {@link #getHost()
	 * host}.
	 *
	 * @return The {@link ContentModel} in the context of the {@link #getHost()
	 *         host}.
	 */
	protected ContentModel getContentModel() {
		ContentModel contentModel = getHost().getRoot().getViewer()
				.getAdapter(ContentModel.class);
		return contentModel;
	}

	/**
	 * Returns the {@link IContentPartFactory} of the current viewer.
	 *
	 * @return the {@link IContentPartFactory} of the current viewer.
	 */
	protected IContentPartFactory<VR> getContentPartFactory() {
		IViewer<VR> viewer = getHost().getRoot().getViewer();
		@SuppressWarnings("serial")
		IContentPartFactory<VR> cpFactory = viewer
				.getAdapter(new TypeToken<IContentPartFactory<VR>>() {
				}.where(new TypeParameter<VR>() {
				}, Types.<VR> argumentOf(viewer.getClass())));
		return cpFactory;
	}

	@Override
	protected String getFeedbackPartFactoryRole() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected String getHandlePartFactoryRole() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Updates the host {@link IVisualPart}'s {@link IContentPart} anchorages
	 * (see {@link IVisualPart#getAnchoragesUnmodifiable()}) so that it is in
	 * sync with the set of content anchorages that is passed in.
	 *
	 * @param contentAnchorages
	 *            * The map of content anchorages with roles to be synchronized
	 *            with the list of {@link IContentPart} anchorages (
	 *            {@link IContentPart#getAnchoragesUnmodifiable()}).
	 *
	 * @see IContentPart#getContentAnchoragesUnmodifiable()
	 * @see IContentPart#getAnchoragesUnmodifiable()
	 */
	public void synchronizeContentAnchorages(
			SetMultimap<? extends Object, ? extends String> contentAnchorages) {
		if (contentAnchorages == null) {
			throw new IllegalArgumentException(
					"contentAnchorages may not be null");
		}
		SetMultimap<IVisualPart<VR, ? extends VR>, String> anchorages = getHost()
				.getAnchoragesUnmodifiable();

		// find anchorages whose content vanished
		List<Entry<IVisualPart<VR, ? extends VR>, String>> toRemove = new ArrayList<>();
		Set<Entry<IVisualPart<VR, ? extends VR>, String>> entries = anchorages
				.entries();
		for (Entry<IVisualPart<VR, ? extends VR>, String> e : entries) {
			if (!(e.getKey() instanceof IContentPart)) {
				continue;
			}
			Object content = ((IContentPart<VR, ? extends VR>) e.getKey())
					.getContent();
			if (!contentAnchorages.containsEntry(content, e.getValue())) {
				toRemove.add(e);
			}
		}

		// Correspondingly remove the anchorages. This is done in a separate
		// step to prevent ConcurrentModificationException.
		for (Entry<IVisualPart<VR, ? extends VR>, String> e : toRemove) {
			getHost().detachFromAnchorage(e.getKey(), e.getValue());
			disposeIfObsolete((IContentPart<VR, ? extends VR>) e.getKey());
		}

		// find content for which no anchorages exist
		List<Entry<IVisualPart<VR, ? extends VR>, String>> toAdd = new ArrayList<>();
		for (Entry<? extends Object, ? extends String> e : contentAnchorages
				.entries()) {
			IContentPart<VR, ? extends VR> anchorage = findOrCreatePartFor(
					e.getKey());
			if (!anchorages.containsEntry(anchorage, e.getValue())) {
				toAdd.add(
						Maps.<IVisualPart<VR, ? extends VR>, String> immutableEntry(
								anchorage, e.getValue()));
			}
		}

		// Correspondingly add the anchorages. This is done in a separate step
		// to prevent ConcurrentModificationException.
		for (Entry<IVisualPart<VR, ? extends VR>, String> e : toAdd) {
			getHost().attachToAnchorage(e.getKey(), e.getValue());
		}
	}

	/**
	 * Updates the host {@link IVisualPart}'s {@link IContentPart} children (see
	 * {@link IVisualPart#getChildrenUnmodifiable()}) so that it is in sync with
	 * the set of content children that is passed in.
	 *
	 * @param contentChildren
	 *            The list of content children to be synchronized with the list
	 *            of {@link IContentPart} children (
	 *            {@link IContentPart#getChildrenUnmodifiable()}).
	 *
	 * @see IContentPart#getContentChildrenUnmodifiable()
	 * @see IContentPart#getChildrenUnmodifiable()
	 */
	@SuppressWarnings("unchecked")
	public void synchronizeContentChildren(
			final List<? extends Object> contentChildren) {
		if (contentChildren == null) {
			throw new IllegalArgumentException(
					"contentChildren may not be null");
		}
		// only synchronize IContentPart children
		List<IContentPart<VR, ? extends VR>> childContentParts = PartUtils
				.filterParts(getHost().getChildrenUnmodifiable(),
						IContentPart.class);
		// store the existing content parts in a map using the contents as keys
		Map<Object, IContentPart<VR, ? extends VR>> contentPartMap = new HashMap<>();
		// find all content parts for which no content element exists in
		// contentChildren, and therefore have to be removed
		Set<? extends Object> newContents = new HashSet<>(contentChildren);
		List<IContentPart<VR, ? extends VR>> toRemove = new ArrayList<>();
		for (IContentPart<VR, ? extends VR> cp : childContentParts) {
			// store content part in map
			contentPartMap.put(cp.getContent(), cp);
			// mark for removal
			if (!newContents.contains(cp.getContent())) {
				toRemove.add(cp);
			}
		}
		// remove the parts
		childContentParts.removeAll(toRemove);
		for (IContentPart<VR, ? extends VR> cp : toRemove) {
			getHost().removeChild(cp);
			disposeIfObsolete(cp);
		}
		// walk over the new content children to reorder existing parts or
		// create missing parts
		Object content;
		int contentChildrenSize = contentChildren.size();
		int childContentPartsSize = childContentParts.size();
		for (int i = 0; i < contentChildrenSize; i++) {
			content = contentChildren.get(i);
			// Do a quick check to see if the existing content part is at the
			// correct location in the children list.
			if (i < childContentPartsSize
					&& childContentParts.get(i).getContent() == content) {
				continue;
			}
			// Look to see if the ContentPart is already around but in the
			// wrong location.
			IContentPart<VR, ? extends VR> contentPart = contentPartMap
					.get(content);
			if (contentPart != null) {
				// Re-order the existing content part to its designated
				// location in the children list.
				// TODO: this is wrong, it has to take into consideration the
				// visual parts in between
				getHost().reorderChild(contentPart, i);
			} else {
				// A ContentPart for this model does not exist yet. Create and
				// insert one.
				contentPart = findOrCreatePartFor(content);
				if (contentPart.getParent() != null) {
					// TODO: Up to now a model element may only be controlled by
					// a single content part; unless we differentiate content
					// elements by context (which is not covered by the current
					// content part map implementation) it is an illegal state
					// if we locate a content part, which is already bound to a
					// parent and whose content is equal to the one we are
					// processing here.
					throw new IllegalStateException(
							"Located a ContentPart which controls the same (or an equal) content element but is already bound to a parent. A content element may only be controlled by a single ContentPart.");
				}
				getHost().addChild(contentPart, i);
			}
		}
	}

}
