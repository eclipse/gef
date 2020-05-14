/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.beans.property.ReadOnlyListWrapperEx;
import org.eclipse.gef.common.beans.property.ReadOnlySetMultimapProperty;
import org.eclipse.gef.common.beans.property.ReadOnlySetMultimapWrapper;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * The {@link AbstractContentPart} is an {@link IContentPart} implementation
 * that binds the VR type parameter (visual root type) to {@link Node}.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual {@link Node} used by this {@link AbstractContentPart} .
 */
public abstract class AbstractContentPart<V extends Node>
		extends AbstractVisualPart<V> implements IContentPart<V> {

	private final ObjectProperty<Object> contentProperty = new SimpleObjectProperty<>(
			this, CONTENT_PROPERTY);

	private ObservableList<Object> contentChildren = CollectionUtils
			.observableArrayList();

	private ObservableList<Object> contentChildrenUnmodifiable;
	private ReadOnlyListWrapper<Object> contentChildrenUnmodifiableProperty;

	private ObservableSetMultimap<Object, String> contentAnchorages = CollectionUtils
			.observableHashMultimap();
	private ObservableSetMultimap<Object, String> contentAnchoragesUnmodifiable;
	private ReadOnlySetMultimapWrapper<Object, String> contentAnchoragesUnmodifiableProperty;

	/**
	 * Creates a new {@link AbstractContentPart}.
	 */
	public AbstractContentPart() {
		// XXX: Register the first listener on the content property, so
		// registration is performed before all listeners are notified.
		contentProperty.addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<? extends Object> observable,
					Object oldValue, Object newValue) {
				onContentChanged(oldValue, newValue);
			}
		});
	}

	// TODO: Implement refresh(ITransformable), refresh(IResizable), and
	// refresh(IBendable)
	// @Override
	// protected void doRefreshVisual(V visual) {
	// if (this instanceof IBendable) {
	// refresh((IBendable) this);
	// } else {
	// if (this instanceof ITransformable) {
	// refresh((ITransformable) this);
	// }
	// if (this instanceof IResizable) {
	// refresh((IResizable) this);
	// }
	// }
	// }

	/**
	 * {@inheritDoc}
	 * <p>
	 * Delegates to {@link #doAddContentChild(Object, int)}, which is to be
	 * overwritten by subclasses.
	 */
	@Override
	public final void addContentChild(Object contentChild, int index) {
		List<Object> oldContentChildren = new ArrayList<>(
				doGetContentChildren());
		if (oldContentChildren.contains(contentChild)) {
			int oldIndex = oldContentChildren.indexOf(contentChild);
			if (oldIndex == index) {
				throw new IllegalArgumentException("Cannot add " + contentChild
						+ " because its already contained at given index "
						+ index);
			} else {
				throw new IllegalArgumentException("Cannot add " + contentChild
						+ " because its already a content child at index "
						+ oldIndex);
			}
		}
		doAddContentChild(contentChild, index);
		// check doAddContentChild(Object, int) does not violate postconditions
		List<? extends Object> newContentChildren = doGetContentChildren();
		if (!newContentChildren.contains(contentChild)) {
			throw new IllegalStateException(
					"doAddContentChild(Object, int) did not add content child "
							+ contentChild + " .");
		}
		int newIndex = newContentChildren.indexOf(contentChild);
		if (newIndex != index) {
			throw new IllegalStateException(
					"doAddContentChild(Object, int) did not add content child "
							+ contentChild + " at index " + index
							+ ", but at index " + newIndex + ".");
		}
		contentChildren.setAll(newContentChildren);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Delegates to {@link #doAttachToContentAnchorage(Object, String)}, which
	 * is to be overwritten by subclasses.
	 */
	@Override
	public final void attachToContentAnchorage(Object contentAnchorage,
			String role) {
		SetMultimap<Object, String> oldContentAnchorages = HashMultimap
				.create(doGetContentAnchorages());
		if (oldContentAnchorages.containsEntry(contentAnchorage, role)) {
			throw new IllegalArgumentException("Already attached to anchorage "
					+ contentAnchorage + " in role '" + role + "'.");
		}
		doAttachToContentAnchorage(contentAnchorage, role);
		// check doAttachToContentAnchorage(Object, String) does not violate
		// postconditions
		SetMultimap<Object, String> newContentAnchorages = HashMultimap
				.create(doGetContentAnchorages());
		if (!newContentAnchorages.containsEntry(contentAnchorage, role)) {
			throw new IllegalArgumentException(
					"doAttachToContentAnchorage did not properly attach to "
							+ contentAnchorage + " with role '" + role + "'.");
		}

		// TODO: extract; is duplicate to code in detachFromContentAnchorages()
		// ensure we have an atomic change per key
		for (Object key : oldContentAnchorages.keySet()) {
			if (newContentAnchorages.containsKey(key)) {
				contentAnchorages.replaceValues(key,
						newContentAnchorages.get(key));
			} else {
				contentAnchorages.removeAll(key);
			}
		}
		for (Object key : newContentAnchorages.keySet()) {
			if (!oldContentAnchorages.containsKey(key)) {
				contentAnchorages.putAll(key, newContentAnchorages.get(key));
			}
		}
	}

	@Override
	public ReadOnlySetMultimapProperty<Object, String> contentAnchoragesUnmodifiableProperty() {
		if (contentAnchoragesUnmodifiableProperty == null) {
			contentAnchoragesUnmodifiableProperty = new ReadOnlySetMultimapWrapper<>(
					this, CONTENT_ANCHORAGES_PROPERTY,
					getContentAnchoragesUnmodifiable());
		}
		return contentAnchoragesUnmodifiableProperty.getReadOnlyProperty();
	}

	@Override
	public ReadOnlyListProperty<Object> contentChildrenUnmodifiableProperty() {
		if (contentChildrenUnmodifiableProperty == null) {
			contentChildrenUnmodifiableProperty = new ReadOnlyListWrapperEx<>(
					this, CONTENT_CHILDREN_PROPERTY,
					getContentChildrenUnmodifiable());
		}
		return contentChildrenUnmodifiableProperty.getReadOnlyProperty();
	}

	@Override
	public final ObjectProperty<Object> contentProperty() {
		return contentProperty;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Delegates to {@link #doDetachFromContentAnchorage(Object, String)}, which
	 * is to be overwritten by subclasses.
	 */
	@Override
	public final void detachFromContentAnchorage(Object contentAnchorage,
			String role) {
		SetMultimap<Object, String> oldContentAnchorages = HashMultimap
				.create(doGetContentAnchorages());
		if (!oldContentAnchorages.containsEntry(contentAnchorage, role)) {
			throw new IllegalArgumentException(
					"Not attached to content anchorage " + contentAnchorage
							+ " with role '" + role + "'.");
		}
		doDetachFromContentAnchorage(contentAnchorage, role);
		// check postconditions for doDetachFromContentAnchorage(Object, String)
		SetMultimap<Object, String> newContentAnchorages = HashMultimap
				.create(doGetContentAnchorages());
		if (newContentAnchorages.containsEntry(contentAnchorage, role)) {
			throw new IllegalArgumentException(
					"doDetachFromContentAnchorage did not properly detach from "
							+ contentAnchorage + " with role '" + role + "'.");
		}
		// ensure we have an atomic change per key
		for (Object key : oldContentAnchorages.keySet()) {
			if (newContentAnchorages.containsKey(key)) {
				contentAnchorages.replaceValues(key,
						newContentAnchorages.get(key));
			} else {
				contentAnchorages.removeAll(key);
			}
		}
		for (Object key : newContentAnchorages.keySet()) {
			if (!oldContentAnchorages.containsKey(key)) {
				contentAnchorages.putAll(key, newContentAnchorages.get(key));
			}
		}
	}

	/**
	 * Adds the given <i>contentChild</i> to this part's content children, so
	 * that it will no longer be returned by subsequent calls to
	 * {@link #doGetContentChildren()}.
	 *
	 * @param contentChild
	 *            An {@link Object} which should be removed from this part's
	 *            content children.
	 * @param index
	 *            The index of the <i>contentChild</i> that is removed.
	 */
	protected void doAddContentChild(Object contentChild, int index) {
		throw new UnsupportedOperationException(
				"Need to implement doAddContentChild(Object, int) for "
						+ this.getClass());
	}

	/**
	 * Attaches this part's content to the given <i>contentAnchorage</i> under
	 * the specified <i>role</i>, so that it will be returned by subsequent
	 * calls to {@link #doGetContentAnchorages()}.
	 *
	 * @param contentAnchorage
	 *            An {@link Object} to which this part's content should be
	 *            attached to.
	 * @param role
	 *            The role under which the attachment is to be established.
	 */
	protected void doAttachToContentAnchorage(Object contentAnchorage,
			String role) {
		throw new UnsupportedOperationException(
				"Need to implement doAttachContentChild(Object, String) for "
						+ this.getClass());
	}

	/**
	 * Detaches this part's content from the given <i>contentAnchorage</i> under
	 * the specified <i>role</i>, so that it will no longer be returned by
	 * subsequent calls to {@link #doGetContentAnchorages()}.
	 *
	 * @param contentAnchorage
	 *            An {@link Object} from which this part's content should be
	 *            detached from.
	 * @param role
	 *            The role under which the attachment is established.
	 */
	protected void doDetachFromContentAnchorage(Object contentAnchorage,
			String role) {
		throw new UnsupportedOperationException(
				"Need to implement doDetachContentChild(Object, String) for "
						+ this.getClass());
	}

	/**
	 * Hook method to return the current list of content anchorages. Has to be
	 * overwritten by clients.
	 *
	 * @return The current list of content anchorages.
	 */
	protected abstract SetMultimap<? extends Object, String> doGetContentAnchorages();

	/**
	 * Hook method to return the current list of content children. Has to be
	 * overwritten by clients.
	 *
	 * @return The current list of content children.
	 */
	protected abstract List<? extends Object> doGetContentChildren();

	/**
	 * Removes the given <i>contentChild</i> from this part's content children,
	 * so that it will no longer be returned by subsequent calls to
	 * {@link #doGetContentChildren()}.
	 *
	 * @param contentChild
	 *            An {@link Object} which should be removed from this part's
	 *            content children.
	 */
	protected void doRemoveContentChild(Object contentChild) {
		throw new UnsupportedOperationException(
				"Need to implement doRemoveContentChild(Object, int) for "
						+ this.getClass());
	}

	/**
	 * Rearranges the given <i>contentChild</i> to the new index position.
	 *
	 * @param contentChild
	 *            The {@link Object} which is to be reordered.
	 * @param newIndex
	 *            The index to which the content child is to be reordered.
	 */
	protected void doReorderContentChild(Object contentChild, int newIndex) {
		throw new UnsupportedOperationException(
				"Need to implement doReorderContentChild(Object, int) for "
						+ this.getClass());
	}

	/**
	 * @see IContentPart#getContent()
	 */
	@Override
	public Object getContent() {
		return contentProperty.get();
	}

	@Override
	public ObservableSetMultimap<Object, String> getContentAnchoragesUnmodifiable() {
		if (contentAnchoragesUnmodifiable == null) {
			contentAnchoragesUnmodifiable = CollectionUtils
					.unmodifiableObservableSetMultimap(contentAnchorages);
		}
		return contentAnchoragesUnmodifiable;
	}

	@Override
	public ObservableList<Object> getContentChildrenUnmodifiable() {
		if (contentChildrenUnmodifiable == null) {
			contentChildrenUnmodifiable = FXCollections
					.unmodifiableObservableList(contentChildren);
		}
		return contentChildrenUnmodifiable;
	}

	@Override
	public boolean isFocusable() {
		return true;
	}

	@Override
	public boolean isSelectable() {
		return true;
	}

	/**
	 * Called whenever the content of this {@link IContentPart} changed.
	 *
	 * @param oldContent
	 *            The old content.
	 * @param newContent
	 *            The new/current content.
	 */
	private void onContentChanged(Object oldContent, Object newContent) {
		if (oldContent != null && oldContent != newContent) {
			// unregister from content part map if we did not loose the
			// viewer reference (otherwise we should already have
			// removed ourselves)
			if (getViewer() != null) {
				unregisterFromContentPartMap(getViewer(), oldContent);
			}
			// clear content children and anchorages
			if (newContent == null) {
				contentChildren.clear();
				contentAnchorages.clear();
			}
		}
		if (newContent != null && newContent != oldContent) {
			// if we have a viewer reference, register at content part
			// map (otherwise do this as soon as we obtain the viewer
			// reference)
			if (getViewer() != null) {
				registerAtContentPartMap(getViewer(), newContent);
			}
			// lazily initialize content children and anchorages
			refreshContentChildren();
			refreshContentAnchorages();
		}
	}

	@Override
	public void refreshContentAnchorages() {
		// XXX: We use atomic operations here to replace the contents so we
		// have minimal resulting change notifications.
		contentAnchorages.replaceAll(doGetContentAnchorages());
	}

	@Override
	public void refreshContentChildren() {
		// XXX: We use atomic operations here to replace the contents so we
		// have minimal resulting change notifications.
		contentChildren.setAll(doGetContentChildren());
	}

	@Override
	protected void register(IViewer viewer) {
		super.register(viewer);
		if (contentProperty.get() != null) {
			registerAtContentPartMap(viewer, contentProperty.get());
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
	protected void registerAtContentPartMap(IViewer viewer, Object content) {
		viewer.getContentPartMap().put(content, this);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Delegates to {@link #doRemoveContentChild(Object)}, which is to be
	 * overwritten by subclasses.
	 */
	@Override
	public final void removeContentChild(Object contentChild) {
		List<Object> oldContentChildren = new ArrayList<>(
				doGetContentChildren());
		if (!oldContentChildren.contains(contentChild)) {
			throw new IllegalArgumentException("Cannot remove " + contentChild
					+ " because its not a content child.");
		}
		doRemoveContentChild(contentChild);
		// check doRemoveContentChild(Object, int) does not violate
		// postconditions
		List<? extends Object> newContentChildren = doGetContentChildren();
		if (newContentChildren.contains(contentChild)) {
			throw new IllegalStateException(
					"doRemoveContentChild(Object, int) did not remove content child "
							+ contentChild + " .");
		}
		contentChildren.setAll(newContentChildren);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Delegates to {@link #doReorderContentChild(Object, int)}, which is to be
	 * overwritten by subclasses.
	 */
	@Override
	public void reorderContentChild(Object contentChild, int newIndex) {
		List<Object> oldContentChildren = new ArrayList<>(
				doGetContentChildren());
		if (!oldContentChildren.contains(contentChild)) {
			throw new IllegalArgumentException("Cannot reorder " + contentChild
					+ " because it is not a content child.");
		}
		if (oldContentChildren.indexOf(contentChild) == newIndex) {
			throw new IllegalArgumentException(
					"Cannot reorder " + contentChild + " to given index + "
							+ newIndex + ", because it is already there.");
		}
		doReorderContentChild(contentChild, newIndex);
		// check doReorderContentChild(Object, int) does not violate
		// postconditions
		List<? extends Object> newContentChildren = doGetContentChildren();
		if (newContentChildren.indexOf(contentChild) != newIndex) {
			throw new IllegalStateException(
					"doReorderContentChild(Object, int) did not reorder content child "
							+ contentChild + " to index " + newIndex + ".");
		}
		contentChildren.setAll(newContentChildren);
	}

	/**
	 * Set the primary content object that this EditPart represents. This method
	 * is used by an {@link IContentPartFactory} when creating an
	 * {@link IContentPart}.
	 *
	 * @see IContentPart#setContent(Object)
	 */

	@Override
	public void setContent(Object content) {
		this.contentProperty.set(content);
	}

	@Override
	protected void unregister(IViewer viewer) {
		// remove content children and anchorages
		super.unregister(viewer);
		if (getContent() != null) {
			unregisterFromContentPartMap(viewer, getContent());
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
	protected void unregisterFromContentPartMap(IViewer viewer,
			Object content) {
		Map<Object, IContentPart<? extends Node>> registry = viewer
				.getContentPartMap();
		if (registry.get(content) != this) {
			throw new IllegalArgumentException("Not registered under content");
		}
		registry.remove(content);
	}

}
