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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPartViewer.
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.viewer;

import java.util.Map;

import org.eclipse.gef.common.activate.IActivatable;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.common.dispose.IDisposable;
import org.eclipse.gef.mvc.domain.IDomain;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IFeedbackPart;
import org.eclipse.gef.mvc.parts.IHandlePart;
import org.eclipse.gef.mvc.parts.IRootPart;
import org.eclipse.gef.mvc.parts.IVisualPart;

import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * The {@link IViewer} interface specifies all services that a viewer needs to
 * provide. One application (within one {@link IDomain}) can be consisting of
 * multiple viewers. Each viewer maintains its own {@link #getContentPartMap()}
 * and {@link #getVisualPartMap()} that can be used to navigate from/to content,
 * controller, and visual objects.
 *
 * @noimplement This interface is not intended to be implemented by clients.
 *              Instead, {@link AbstractViewer} should be subclassed.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit used, e.g.
 *            javafx.scene.Node in case of JavaFX.
 */
public interface IViewer<VR> extends IAdaptable, IActivatable, IDisposable,
		IAdaptable.Bound<IDomain<VR>> {

	/**
	 * Returns the {@link Map} for registering {@link IContentPart}s by their
	 * <i>content</i>.
	 *
	 * @return The content part map
	 */
	// TODO: move into content model or content behavior
	public Map<Object, IContentPart<VR, ? extends VR>> getContentPartMap();

	/**
	 * Returns the {@link IDomain} this {@link IViewer} is bound to.
	 *
	 * @return The {@link IDomain} this {@link IViewer} is bound to, or
	 *         <code>null</code> if this {@link IViewer} is not (yet) bound to
	 *         an {@link IDomain}.
	 */
	public IDomain<VR> getDomain();

	/**
	 * Returns the {@link IRootPart} of this viewer. The {@link IRootPart} is a
	 * special {@link IVisualPart} that serves as the parent to all contained
	 * {@link IContentPart}s, {@link IHandlePart}s, and {@link IFeedbackPart}s.
	 *
	 * @return The {@link IRootPart} of this viewer.
	 */
	public IRootPart<VR, ? extends VR> getRootPart();

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
	public Map<VR, IVisualPart<VR, ? extends VR>> getVisualPartMap();

	/**
	 * Returns the value of the property {@link #viewerFocusedProperty()}.
	 *
	 * @return The value of the property {@link #viewerFocusedProperty()}.
	 */
	public boolean isViewerFocused();

	/**
	 * Returns <code>true</code> if the given visual is contained within this
	 * {@link IViewer}. Otherwise returns <code>false</code>.
	 *
	 * @param node
	 *            The visual for which is determined if it is contained within
	 *            this {@link IViewer}.
	 * @return <code>true</code> if the given visual is contained within this
	 *         {@link IViewer}, otherwise <code>false</code>.
	 */
	public boolean isViewerVisual(VR node);

	/**
	 * Ensure that the visual of the given {@link IVisualPart} is visible in
	 * this viewer.
	 *
	 * @param visualPart
	 *            The {@link IVisualPart} that is to be revealed.
	 */
	public void reveal(IVisualPart<VR, ? extends VR> visualPart);

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

}
