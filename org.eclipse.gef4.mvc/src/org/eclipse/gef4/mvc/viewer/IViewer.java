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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPartViewer.
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.viewer;

import java.util.Map;

import org.eclipse.gef4.common.activate.IActivatable;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.common.dispose.IDisposable;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
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
	 *
	 * @return The visual part map
	 */
	public Map<VR, IVisualPart<VR, ? extends VR>> getVisualPartMap();

	/**
	 * Ensure that the visual of the given {@link IVisualPart} is visible in
	 * this viewer.
	 *
	 * @param visualPart
	 *            The {@link IVisualPart} that is to be revealed.
	 */
	public void reveal(IVisualPart<VR, ? extends VR> visualPart);

}
