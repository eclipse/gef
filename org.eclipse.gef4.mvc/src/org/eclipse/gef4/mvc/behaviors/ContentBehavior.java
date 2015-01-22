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
 * Note: Parts of this class have been transferred from org.eclipse.gef.editparts.AbstractEditPart.
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.gef4.mvc.models.ContentModel;
import org.eclipse.gef4.mvc.models.GraveyardModel;
import org.eclipse.gef4.mvc.models.HoverModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.parts.PartUtils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;

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
public class ContentBehavior<VR> extends AbstractBehavior<VR> implements
		PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		if (getHost() == getHost().getRoot()) {
			ContentModel contentModel = getHost().getRoot().getViewer()
					.getAdapter(ContentModel.class);
			synchronizeContentChildren(contentModel.getContents());
			contentModel.addPropertyChangeListener(this);
		} else {
			synchronizeContentChildren(((IContentPart<VR, ? extends VR>) getHost())
					.getContentChildren());
			synchronizeContentAnchorages(((IContentPart<VR, ? extends VR>) getHost())
					.getContentAnchorages());
			getHost().addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate() {
		if (getHost() == getHost().getRoot()) {
			getHost().getRoot().getViewer().getAdapter(ContentModel.class)
					.removePropertyChangeListener(this);
			synchronizeContentChildren(Collections.emptyList());
		} else {
			getHost().removePropertyChangeListener(this);
			synchronizeContentAnchorages(HashMultimap.<Object, String> create());
			synchronizeContentChildren(Collections.emptyList());
		}
		super.deactivate();
	}

	protected void disposeIfObsolete(IContentPart<VR, ? extends VR> contentPart) {
		if (contentPart.getParent() == null
				&& contentPart.getAnchorages().isEmpty()) {
			// keep track of the removed content part, so we may relocate it
			// within findOrCreate() later
			Map<Object, IContentPart<VR, ? extends VR>> contentPartPool = getHost()
					.getRoot().getViewer()
					.<GraveyardModel<VR>> getAdapter(GraveyardModel.class)
					.getContentPartPool();
			contentPartPool.put(contentPart.getContent(), contentPart);
			contentPart.setContent(null);
		}
	}

	protected IContentPart<VR, ? extends VR> findOrCreatePartFor(Object content) {
		Map<Object, IContentPart<VR, ? extends VR>> contentPartMap = getHost()
				.getRoot().getViewer().getContentPartMap();
		if (contentPartMap.containsKey(content)) {
			return contentPartMap.get(content);
		} else {
			// 'Revive' a content part, if it was removed before
			Map<Object, IContentPart<VR, ? extends VR>> contentPartPool = getHost()
					.getRoot().getViewer()
					.<GraveyardModel<VR>> getAdapter(GraveyardModel.class)
					.getContentPartPool();
			IContentPart<VR, ? extends VR> contentPart = null;
			contentPart = contentPartPool.remove(content);

			// If the part could not be revived, a new one is created
			if (contentPart == null) {
				IContentPartFactory<VR> contentPartFactory = getHost()
						.getRoot().getViewer().getContentPartFactory();
				contentPart = contentPartFactory.createContentPart(content,
						this, Collections.emptyMap());
				if (contentPart == null) {
					throw new IllegalStateException("IContentPartFactory '"
							+ contentPartFactory.getClass().getSimpleName()
							+ "' did not create part for " + content + ".");
				}
			}

			// initialize part
			contentPart.setContent(content);
			return contentPart;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (ContentModel.CONTENTS_PROPERTY.equals(event.getPropertyName())) {
			synchronizeContentChildren((List<Object>) event.getNewValue());
			// TODO: flushing of models should be done somewhere more
			// appropriate
			getHost().getRoot().getViewer().getAdapter(SelectionModel.class)
					.deselectAll();
			getHost().getRoot().getViewer().getAdapter(HoverModel.class)
					.clearHover();
		} else if (IContentPart.CONTENT_PROPERTY
				.equals(event.getPropertyName())) {
			synchronizeContentChildren(((IContentPart<VR, ? extends VR>) getHost())
					.getContentChildren());
			synchronizeContentAnchorages(((IContentPart<VR, ? extends VR>) getHost())
					.getContentAnchorages());
		}
	}

	/**
	 * Updates the host {@link IVisualPart}'s {@link IContentPart} anchorages
	 * (see {@link IVisualPart#getAnchorages()}) so that it is in sync with the
	 * set of content anchorages that is passed in.
	 *
	 * @param contentAnchorages
	 *            * The map of content anchorages with roles to be synchronized
	 *            with the list of {@link IContentPart} anchorages (
	 *            {@link IContentPart#getAnchorages()}).
	 *
	 * @see IContentPart#getContentAnchorages()
	 * @see IContentPart#getAnchorages()
	 */
	public void synchronizeContentAnchorages(
			SetMultimap<? extends Object, String> contentAnchorages) {
		SetMultimap<IVisualPart<VR, ? extends VR>, String> anchorages = getHost()
				.getAnchorages();

		// find anchorages whose content vanished
		List<Entry<IVisualPart<VR, ? extends VR>, String>> toRemove = new ArrayList<Map.Entry<IVisualPart<VR, ? extends VR>, String>>();
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
			disposeIfObsolete((IContentPart<VR, ? extends VR>) e.getKey());
		}

		// Correspondingly remove the anchorages. This is done in a separate
		// step to prevent ConcurrentModificationException.
		for (Entry<IVisualPart<VR, ? extends VR>, String> e : toRemove) {
			getHost().removeAnchorage(e.getKey(), e.getValue());
		}

		// find content for which no anchorages exist
		List<Entry<IVisualPart<VR, ? extends VR>, String>> toAdd = new ArrayList<Map.Entry<IVisualPart<VR, ? extends VR>, String>>();
		for (Entry<? extends Object, String> e : contentAnchorages.entries()) {
			IContentPart<VR, ? extends VR> anchorage = findOrCreatePartFor(e
					.getKey());
			if (!anchorages.containsEntry(anchorage, e.getValue())) {
				toAdd.add(Maps
						.<IVisualPart<VR, ? extends VR>, String> immutableEntry(
								anchorage, e.getValue()));
			}
		}

		// Correspondingly add the anchorages. This is done in a separate step
		// to prevent ConcurrentModificationException.
		for (Entry<IVisualPart<VR, ? extends VR>, String> e : toAdd) {
			getHost().addAnchorage(e.getKey(), e.getValue());
		}
	}

	/**
	 * Updates the host {@link IVisualPart}'s {@link IContentPart} children (see
	 * {@link IVisualPart#getChildren()}) so that it is in sync with the set of
	 * content children that is passed in.
	 *
	 * @param contentChildren
	 *            The list of content children to be synchronized with the list
	 *            of {@link IContentPart} children (
	 *            {@link IContentPart#getChildren()}).
	 *
	 * @see IContentPart#getContentChildren()
	 * @see IContentPart#getChildren()
	 */
	@SuppressWarnings("unchecked")
	public void synchronizeContentChildren(
			final List<? extends Object> contentChildren) {
		int i;

		// only synchronize IContentPart children
		List<IContentPart<VR, ? extends VR>> childContentParts = PartUtils
				.filterParts(getHost().getChildren(), IContentPart.class);
		int contentChildrenSize = contentChildren.size();
		int childContentPartsSize = childContentParts.size();

		Map<Object, IContentPart<VR, ? extends VR>> contentToContentPartMap = Collections
				.emptyMap();
		IContentPart<VR, ? extends VR> contentPart;
		if (childContentPartsSize > 0) {
			contentToContentPartMap = new HashMap<Object, IContentPart<VR, ? extends VR>>(
					childContentPartsSize);
			for (i = 0; i < childContentPartsSize; i++) {
				contentPart = childContentParts.get(i);
				contentToContentPartMap.put(contentPart.getContent(),
						contentPart);
			}
		}

		Object content;
		for (i = 0; i < contentChildrenSize; i++) {
			content = contentChildren.get(i);

			// Do a quick check to see if editPart[i] == model[i]
			if (i < childContentPartsSize
					&& childContentParts.get(i).getContent() == content) {
				continue;
			}

			// Look to see if the EditPart is already around but in the
			// wrong location
			contentPart = contentToContentPartMap.get(content);

			if (contentPart != null) {
				// TODO: this is wrong, it has to take into consideration the
				// visual parts in between
				getHost().reorderChild(contentPart, i);
			} else {
				// An EditPart for this model doesn't exist yet. Create and
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

		// remove the remaining EditParts
		childContentParts = PartUtils.filterParts(getHost().getChildren(),
				IContentPart.class);
		childContentPartsSize = childContentParts.size();

		if (i < childContentPartsSize) {
			List<IContentPart<VR, ? extends VR>> trash = new ArrayList<IContentPart<VR, ? extends VR>>(
					childContentPartsSize - i);
			for (; i < childContentPartsSize; i++) {
				trash.add(childContentParts.get(i));
			}
			for (i = 0; i < trash.size(); i++) {
				IContentPart<VR, ? extends VR> ep = trash.get(i);
				getHost().removeChild(ep);
				disposeIfObsolete(ep);
			}
		}
	}

}
