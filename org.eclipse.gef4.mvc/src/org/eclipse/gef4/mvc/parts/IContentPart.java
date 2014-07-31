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
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPart.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import com.google.common.collect.SetMultimap;

/**
 * An {@link IVisualPart} that visualizes an underlying content element.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *              Instead, {@link AbstractContentPart} should be sub-classed.
 * 
 * @author anyssen
 * 
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 * 
 * 
 */
// TODO: parameterize with content type
public interface IContentPart<VR> extends IVisualPart<VR> {

	/**
	 * Property name used within {@link PropertyChangeEvent}s, which are fired
	 * whenever the content changes ({@link #setContent(Object)}).
	 */
	public static final String CONTENT_PROPERTY = "content";

	public Object getContent();

	/**
	 * Returns the content objects that are to be regarded as anchorages of this
	 * {@link IContentPart}'s content ({@link #getContent()}) with an (optional)
	 * role qualifier for each anchorage-anchored link that has to be
	 * established.
	 * <p>
	 * In case of a connection, one anchorage could have the "START" role, and
	 * another the "END" role. Using the role mechanism, the same anchorage may
	 * also have both roles, which can, for instance, be used for self
	 * connections.
	 * 
	 * @return A {@link SetMultimap} of the content anchorages with a role to
	 *         qualify each anchorage-anchored link. If there is only a single
	 *         anchorage-anchored link to a respective anchorage, its role may
	 *         be left undefined (i.e. the map will contain an entry of the form
	 *         (anchorage, <code>null</code>)).
	 */
	public SetMultimap<Object, String> getContentAnchorages();

	public List<? extends Object> getContentChildren();

	public void setContent(Object content);

}
