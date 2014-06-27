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

import java.util.List;

/**
 * An {@link IVisualPart} that visualizes an underlying content element.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 *              Instead, {@link AbstractContentPart} should be subclassed.
 * 
 * @author anyssen
 */
// TODO: parameterize with content type
public interface IContentPart<VR> extends IVisualPart<VR> {
	
	public static final String CONTENT_PROPERTY = "content";

	public void setContent(Object content);

	public Object getContent();

	public List<Object> getContentChildren();

	public List<Object> getContentAnchored();

}
