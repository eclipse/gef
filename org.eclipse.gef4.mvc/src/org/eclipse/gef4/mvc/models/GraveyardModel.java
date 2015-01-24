/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.models;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.mvc.parts.IContentPart;

// TODO: rename in to "content part pool" and inject into behavior (as singleton within viewer scope)
// -> domain and viewer scope will be needed
public class GraveyardModel<VR> {

	private Map<Object, IContentPart<VR, ? extends VR>> graveyard = new HashMap<Object, IContentPart<VR, ? extends VR>>();

	public Map<Object, IContentPart<VR, ? extends VR>> getContentPartPool() {
		return graveyard;
	}

}
