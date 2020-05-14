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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPart.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.gef.common.beans.property.ReadOnlySetMultimapProperty;
import org.eclipse.gef.common.collections.ObservableSetMultimap;
import org.eclipse.gef.mvc.fx.models.FocusModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SelectionModel;

/**
 * An {@link IVisualPart} that visualizes an underlying content element.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 *              Instead, {@link AbstractContentPart} should be sub-classed.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual node used by this {@link IContentPart}.
 *
 */
// TODO: parameterize with content type
public interface IContentPart<V extends Node> extends IVisualPart<V> {

	/**
	 * Name of the {@link #contentProperty() content property}.
	 */
	public static final String CONTENT_PROPERTY = "content";

	/**
	 * Name of the {@link #contentChildrenUnmodifiableProperty()}.
	 */
	public static final String CONTENT_CHILDREN_PROPERTY = "contentChildren";

	/**
	 * Name of the {@link #contentAnchoragesUnmodifiableProperty()}.
	 */
	public static final String CONTENT_ANCHORAGES_PROPERTY = "contentAnchorages";

	/**
	 * Inserts the given <i>contentChild</i> as a child to this part's content,
	 * so that it will be returned by subsequent calls to
	 * {@link #getContentChildrenUnmodifiable()}.
	 *
	 * @param contentChild
	 *            An {@link Object} which should be added as a child to this
	 *            part's content.
	 * @param index
	 *            The index at which the <i>contentChild</i> should be added.
	 */
	public void addContentChild(Object contentChild, int index);

	/**
	 * Attaches this part's content to the given <i>contentAnchorage</i> under
	 * the specified <i>role</i>, so that it will be returned by subsequent
	 * calls to {@link #getContentAnchoragesUnmodifiable()}.
	 *
	 * @param contentAnchorage
	 *            An {@link Object} to which this part's content should be
	 *            attached to.
	 * @param role
	 *            The role under which the attachment is to be established.
	 */
	public void attachToContentAnchorage(Object contentAnchorage, String role);

	/**
	 * Returns an unmodifiable read-only set-multimap property containing the
	 * content anchorages.
	 *
	 * @see #getContentAnchoragesUnmodifiable()
	 * @see #attachToContentAnchorage(Object, String)
	 * @see #detachFromContentAnchorage(Object, String)
	 *
	 * @return An unmodifiable read-only set-multimap property.
	 */
	public ReadOnlySetMultimapProperty<Object, String> contentAnchoragesUnmodifiableProperty();

	/**
	 * Returns an unmodfiable read-only property containing the content
	 * children.
	 *
	 * @return An unmodifiable read-only property named
	 *         {@link #CONTENT_CHILDREN_PROPERTY}.
	 */
	public ReadOnlyListProperty<Object> contentChildrenUnmodifiableProperty();

	/**
	 * A writable property representing the {@link IContentPart}'s content.
	 *
	 * @return A writable property named {@link #CONTENT_PROPERTY}.
	 */
	public ObjectProperty<Object> contentProperty();

	/**
	 * Detaches this part's content from the given <i>contentAnchorage</i> under
	 * the specified <i>role</i>, so that it will no longer be returned by
	 * subsequent calls to {@link #getContentAnchoragesUnmodifiable()}.
	 *
	 * @param contentAnchorage
	 *            An {@link Object} from which this part's content should be
	 *            detached from.
	 * @param role
	 *            The role under which the attachment is established.
	 */
	public void detachFromContentAnchorage(Object contentAnchorage,
			String role);

	/**
	 * Returns this part's content.
	 *
	 * @return This part's content.
	 */
	public Object getContent();

	/**
	 * Returns an unmodifiable {@link ObservableSetMultimap} that contains the
	 * content objects that are to be regarded as anchorages of this
	 * {@link IContentPart}'s content ({@link #getContent()}) with an (optional)
	 * role qualifier for each anchorage-anchored link that has to be
	 * established.
	 * <p>
	 * In case of a connection, one anchorage could have the "START" role, and
	 * another the "END" role. Using the role mechanism, the same anchorage may
	 * also have both roles, which can, for instance, be used for self
	 * connections.
	 *
	 * @return An unmodifiable {@link ObservableSetMultimap} of the content
	 *         anchorages with a role to qualify each anchorage-anchored link.
	 *         If there is only a single anchorage-anchored link to a respective
	 *         anchorage, its role may be left undefined (i.e. the map will
	 *         contain an entry of the form (anchorage, <code>null</code>)).
	 */
	public ObservableSetMultimap<Object, String> getContentAnchoragesUnmodifiable();

	/**
	 * Returns an unmodifiable {@link ObservableList} that contains the content
	 * children.
	 *
	 * @return A {@link List} of all of this part's content children.
	 */
	public ObservableList<Object> getContentChildrenUnmodifiable();

	/**
	 * Returns <code>true</code> if policies and other parts of the application
	 * are allowed to assign focus to this part by changing the
	 * {@link FocusModel}. Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if policies and other parts of the application
	 *         are allowed to assign focus to this part by changing the
	 *         {@link FocusModel}, otherwise <code>false</code>.
	 */
	public boolean isFocusable();

	/**
	 * Returns <code>true</code> if policies and other parts of the application
	 * are allowed to add this part to the selection maintained by the
	 * {@link SelectionModel}. Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if policies and other parts of the application
	 *         are allowed to add this part to the selection maintained by the
	 *         {@link SelectionModel}, otherwise <code>false</code>.
	 */
	public boolean isSelectable();

	/**
	 * Triggers a re-computation of the content anchorages of this
	 * {@link IContentPart}.
	 */
	public void refreshContentAnchorages();

	/**
	 * Triggers a re-computation of the content children of this
	 * {@link IContentPart}.
	 */
	public void refreshContentChildren();

	/**
	 * Removes the given <i>contentChild</i> from this part's content children,
	 * so that it will no longer be returned by subsequent calls to
	 * {@link #getContentChildrenUnmodifiable()}.
	 *
	 * @param contentChild
	 *            An {@link Object} which should be removed from this part's
	 *            content children.
	 */
	public void removeContentChild(Object contentChild);

	/**
	 * Rearranges the given <i>contentChild</i> to the new index position. Fires
	 * property change events using {@link #CONTENT_CHILDREN_PROPERTY} as
	 * {@link PropertyChangeEvent#getPropertyName() property name}.
	 *
	 * @param contentChild
	 *            The {@link Object} which is to be reordered.
	 * @param newIndex
	 *            The index to which the content child is to be reordered.
	 */
	public void reorderContentChild(Object contentChild, int newIndex);

	/**
	 * Sets this part's content to the given {@link Object value}. Fires
	 * property change events using {@link #CONTENT_PROPERTY} as property name.
	 *
	 * @param content
	 *            The new content for this part.
	 */
	public void setContent(Object content);

}
