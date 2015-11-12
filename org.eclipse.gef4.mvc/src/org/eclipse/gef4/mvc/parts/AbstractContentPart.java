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
 * Note: Parts of this class have been transferred from org.eclipse.gef.editparts.AbstractEditPart and org.eclipse.gef.editparts.AbstractGraphicalEditPart.
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.behaviors.ContentBehavior;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * The abstract base implementation of {@link IContentPart}, intended to be
 * sub-classed by clients to create their own custom {@link IContentPart}.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this
 *            {@link AbstractContentPart} is used in, e.g. javafx.scene.Node in
 *            case of JavaFX.
 * @param <V>
 *            The visual node used by this {@link AbstractContentPart}.
 */
public abstract class AbstractContentPart<VR, V extends VR>
		extends AbstractVisualPart<VR, V>implements IContentPart<VR, V> {

	private Object content;

	@Override
	public final void addContentChild(Object contentChild, int index) {
		List<Object> oldContentChildren = new ArrayList<Object>(
				getContentChildren());
		doAddContentChild(contentChild, index);
		List<Object> newContentChildren = new ArrayList<Object>(
				getContentChildren());
		pcs.firePropertyChange(CONTENT_CHILDREN_PROPERTY, oldContentChildren,
				newContentChildren);
	}

	@Override
	public final void attachToContentAnchorage(Object contentAnchorage,
			String role) {
		SetMultimap<Object, String> oldContentAnchorages = HashMultimap
				.create(getContentAnchorages());
		doAttachToContentAnchorage(contentAnchorage, role);
		SetMultimap<Object, String> newContentAnchorages = HashMultimap
				.create(getContentAnchorages());
		pcs.firePropertyChange(CONTENT_ANCHORAGES_PROPERTY,
				oldContentAnchorages, newContentAnchorages);
	}

	@Override
	public final void detachFromContentAnchorage(Object contentAnchorage,
			String role) {
		SetMultimap<Object, String> oldContentAnchorages = HashMultimap
				.create(getContentAnchorages());
		doDetachFromContentAnchorage(contentAnchorage, role);
		SetMultimap<Object, String> newContentAnchorages = HashMultimap
				.create(getContentAnchorages());
		pcs.firePropertyChange(CONTENT_ANCHORAGES_PROPERTY,
				oldContentAnchorages, newContentAnchorages);
	}

	/**
	 * Adds the given <i>contentChild</i> to this part's content children, so
	 * that it will no longer be returned by subsequent calls to
	 * {@link #getContentChildren()}.
	 *
	 * @param contentChild
	 *            An {@link Object} which should be removed from this part's
	 *            content children.
	 * @param index
	 *            The index of the <i>contentChild</i> that is removed.
	 */
	protected abstract void doAddContentChild(Object contentChild, int index);

	/**
	 * Attaches this part's content to the given <i>contentAnchorage</i> under
	 * the specified <i>role</i>, so that it will be returned by subsequent
	 * calls to {@link #getContentAnchorages()}.
	 *
	 * @param contentAnchorage
	 *            An {@link Object} to which this part's content should be
	 *            attached to.
	 * @param role
	 *            The role under which the attachment is to be established.
	 */
	protected abstract void doAttachToContentAnchorage(Object contentAnchorage,
			String role);

	/**
	 * Detaches this part's content from the given <i>contentAnchorage</i> under
	 * the specified <i>role</i>, so that it will no longer be returned by
	 * subsequent calls to {@link #getContentAnchorages()}.
	 *
	 * @param contentAnchorage
	 *            An {@link Object} from which this part's content should be
	 *            detached from.
	 * @param role
	 *            The role under which the attachment is established.
	 */
	protected abstract void doDetachFromContentAnchorage(
			Object contentAnchorage, String role);

	/**
	 * Removes the given <i>contentChild</i> from this part's content children,
	 * so that it will no longer be returned by subsequent calls to
	 * {@link #getContentChildren()}.
	 *
	 * @param contentChild
	 *            An {@link Object} which should be removed from this part's
	 *            content children.
	 * @param index
	 *            The index of the <i>contentChild</i> that is removed.
	 */
	protected abstract void doRemoveContentChild(Object contentChild,
			int index);

	/**
	 * @see IContentPart#getContent()
	 */
	@Override
	public Object getContent() {
		return content;
	}

	@Override
	protected void register(IViewer<VR> viewer) {
		super.register(viewer);
		if (content != null) {
			registerAtContentPartMap(viewer, content);
		}
	}

	/**
	 * Registers the <i>model</i> in the {@link IViewer#getContentPartMap()}.
	 * Subclasses should only extend this method if they need to register this
	 * EditPart in additional ways.
	 *
	 * @param viewer
	 *            The viewer to register at.
	 *
	 * @param content
	 *            The content to register.
	 */
	protected void registerAtContentPartMap(IViewer<VR> viewer,
			Object content) {
		viewer.getContentPartMap().put(content, this);
	}

	@Override
	public final void removeContentChild(Object contentChild, int index) {
		List<Object> oldContentChildren = new ArrayList<Object>(
				getContentChildren());
		doRemoveContentChild(contentChild, index);
		List<Object> newContentChildren = new ArrayList<Object>(
				getContentChildren());
		pcs.firePropertyChange(CONTENT_CHILDREN_PROPERTY, oldContentChildren,
				newContentChildren);
	}

	/**
	 * Set the primary model object that this EditPart represents. This method
	 * is used by an <code>EditPartFactory</code> when creating an EditPart.
	 *
	 * @see IContentPart#setContent(Object)
	 */
	@Override
	public void setContent(Object content) {
		if (this.content == content) {
			return;
		}

		Object oldContent = this.content;
		if (oldContent != null && oldContent != content
				&& getViewer() != null) {
			unregisterFromContentPartMap(getViewer(), oldContent);
		}
		this.content = content;
		if (content != null && content != oldContent && getViewer() != null) {
			registerAtContentPartMap(getViewer(), content);
		}

		pcs.firePropertyChange(CONTENT_PROPERTY, oldContent, content);
	}

	@Override
	protected void unregister(IViewer<VR> viewer) {
		// remove content children and anchorages
		ContentBehavior<VR> contentBehavior = this
				.<ContentBehavior<VR>> getAdapter(ContentBehavior.class);
		if (contentBehavior != null) {
			contentBehavior.synchronizeContentChildren(Collections.emptyList());
			contentBehavior.synchronizeContentAnchorages(
					HashMultimap.<Object, String> create());
		}
		super.unregister(viewer);
		if (content != null) {
			unregisterFromContentPartMap(viewer, content);
		}
	}

	/**
	 * Unregisters the <i>model</i> in the {@link IViewer#getContentPartMap()}.
	 * Subclasses should only extend this method if they need to unregister this
	 * EditPart in additional ways.
	 *
	 * @param viewer
	 *            The viewer to unregister from.
	 *
	 * @param content
	 *            The content to unregister.
	 */
	protected void unregisterFromContentPartMap(IViewer<VR> viewer,
			Object content) {
		Map<Object, IContentPart<VR, ? extends VR>> registry = viewer
				.getContentPartMap();
		if (registry.get(content) != this) {
			throw new IllegalArgumentException("Not registered under content");
		}
		registry.remove(content);
	}

}
