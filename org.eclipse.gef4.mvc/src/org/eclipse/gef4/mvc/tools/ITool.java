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
 * Note: Certain parts of this interface have been transferred from org.eclipse.gef.Tool.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.tools;

import org.eclipse.gef4.mvc.IActivatable;
import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.models.IZoomModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IEditPolicy;
import org.eclipse.gef4.mvc.viewer.IVisualPartViewer;

/**
 * An {@link ITool} handles a certain aspect of user interaction. It may react
 * to input mouse, keyboard, and gesture events, as well as to changes to the
 * {@link IVisualPartViewer}'s logical models like {@link ISelectionModel},
 * {@link IZoomModel}, or {@link IHoverModel}.
 * 
 * As an reaction to input events, an {@link ITool} may manipulate the
 * {@link IVisualPartViewer}'s logical models, or interact with the
 * {@link IVisualPartViewer}'s {@link IVisualPart} via a respective
 * {@link IEditPolicy}.
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public interface ITool<V> extends IActivatable {

	/**
	 * Called to set/change/unset the {@link IEditDomain} this {@link ITool} is
	 * bound to. To set or change the {@link IEditDomain}, pass in a valid
	 * {@link IEditDomain}, to unset it, pass in <code>null</code>.
	 * 
	 * An {@link ITool needs to obtain a reference to an {@link IEditDomain}
	 * before being activated via {@link #activate()}, and this
	 * {@link IEditDomain} may not be unset or changed while the tool is active.
	 * After deactivation via {@link #deactivate()}, the {@link IEditDomain} may
	 * be safely changed or unset.
	 * 
	 * @param domain
	 *            The {@link IEditDomain} to which this {@link ITool} belongs
	 */
	void setDomain(IEditDomain<V> domain);

	IEditDomain<V> getDomain();

	// TODO: tools/handles should change the cursor
}
