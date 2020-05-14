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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPartViewer.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.viewer;

import java.util.Map;

import org.eclipse.gef.common.activate.IActivatable;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.dispose.IDisposable;
import org.eclipse.gef.mvc.fx.domain.IDomain;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IFeedbackPart;
import org.eclipse.gef.mvc.fx.parts.IHandlePart;
import org.eclipse.gef.mvc.fx.parts.IRootPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * The {@link IViewer} interface specifies all services that a viewer needs to
 * provide. One application (within one {@link IDomain}) can be consisting of
 * multiple viewers. Each viewer maintains its own {@link #getContentPartMap()}
 * and {@link #getVisualPartMap()} that can be used to navigate from/to content,
 * controller, and visual objects.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 *              Instead, {@link InfiniteCanvasViewer} should be subclassed.
 *
 * @author anyssen
 *
 */
public interface IViewer extends IAdaptable, IActivatable, IDisposable,
		IAdaptable.Bound<IDomain> {

	/**
	 * Name of the {@link #contentsProperty()}.
	 */
	public static final String CONTENTS_PROPERTY = "contents";

	/**
	 * Name of the {@link #contentPartMapProperty()}.
	 */
	public static final String CONTENT_PART_MAP_PROPERTY = "contentPartMap";

	/**
	 * Name of the {@link #visualPartMapProperty()}.
	 */
	public static final String VISUAL_PART_MAP_PROPERTY = "visualPartMap";

	/**
	 * Returns an unmodifiable read-only map property that contains the
	 * registered {@link IContentPart}s mapped to their respective content.
	 *
	 * @return An unmodifiable read-only map property.
	 */
	public ReadOnlyMapProperty<Object, IContentPart<? extends Node>> contentPartMapProperty();

	/**
	 * A read-only property containing the current content objects.
	 *
	 * @return A read-only list property named {@link #CONTENTS_PROPERTY}.
	 */
	public ReadOnlyListProperty<Object> contentsProperty();

	/**
	 * Returns the {@link Parent} that displays the visuals of this
	 * {@link IViewer viewer's} {@link IVisualPart parts}
	 *
	 * @return The {@link Parent} that displays the visuals of the
	 *         {@link IViewer viewer's} {@link IVisualPart parts}.
	 */
	public Parent getCanvas();

	/**
	 * Returns the {@link Map} for registering {@link IContentPart}s by their
	 * <i>content</i>.
	 *
	 * @return The content part map
	 */
	public Map<Object, IContentPart<? extends Node>> getContentPartMap();

	/**
	 * Returns an {@link ObservableList} containing the content objects.
	 *
	 * @return An {@link ObservableList}.
	 */
	public ObservableList<Object> getContents();

	/**
	 * Returns the {@link IDomain} this {@link IViewer} is bound to.
	 *
	 * @return The {@link IDomain} this {@link IViewer} is bound to, or
	 *         <code>null</code> if this {@link IViewer} is not (yet) bound to
	 *         an {@link IDomain}.
	 */
	public IDomain getDomain();

	/**
	 * Returns the {@link IRootPart} of this viewer. The {@link IRootPart} is a
	 * special {@link IVisualPart} that serves as the parent to all contained
	 * {@link IContentPart}s, {@link IHandlePart}s, and {@link IFeedbackPart}s.
	 *
	 * @return The {@link IRootPart} of this viewer.
	 */
	public IRootPart<? extends Node> getRootPart();

	/**
	 * Returns the {@link Map} for registering {@link IVisualPart}s by their
	 * <i>visual</i>. This map is used for hit-testing. Hit testing is performed
	 * by first determining which visual is hit, and then mapping that to an
	 * {@link IVisualPart}.
	 * <p>
	 * Note, that when looking up an {@link IVisualPart} for a given visual in
	 * the map, it is required to walk up the visual hierarchy until a
	 * registered visual is found, because an {@link IVisualPart} only has to
	 * register its "main" visual (i.e. the one returned by
	 * {@link IVisualPart#getVisual()}) at the visual-part-map, but potential
	 * children visuals do not have to be registered.
	 *
	 * @return The visual-to-visual-part map.
	 */
	public Map<Node, IVisualPart<? extends Node>> getVisualPartMap();

	/**
	 * Returns the value of the property {@link #viewerFocusedProperty()}.
	 *
	 * @return The value of the property {@link #viewerFocusedProperty()}.
	 */
	public boolean isViewerFocused();

	/**
	 * Ensure that the visual of the given {@link IVisualPart} is visible in
	 * this viewer.
	 *
	 * @param visualPart
	 *            The {@link IVisualPart} that is to be revealed.
	 */
	public void reveal(IVisualPart<? extends Node> visualPart);

	/**
	 * Returns a {@link ReadOnlyBooleanProperty} that represents the "focused"
	 * state of this {@link IViewer}. An {@link IViewer} is focused when its
	 * visualization has keyboard focus and its window is active, i.e. it is
	 * focused if it will receive keyboard events.
	 *
	 * @return A {@link ReadOnlyBooleanProperty} that represents the "focused"
	 *         state of this {@link IViewer}.
	 */
	public ReadOnlyBooleanProperty viewerFocusedProperty();

	/**
	 * Returns an unmodifiable read-only map property that contains the
	 * registered {@link IVisualPart}s mapped to their respective visual.
	 *
	 * @return An unmodifiable read-only map property.
	 */
	public ReadOnlyMapProperty<Node, IVisualPart<? extends Node>> visualPartMapProperty();

}
