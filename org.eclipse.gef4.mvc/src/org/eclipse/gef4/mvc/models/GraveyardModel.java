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

// TODO: we could turn this into a part pool, and let's add the part factories, or merge it with the content model (and add the content part map there as well)
public class GraveyardModel<VR> {

	private Map<Object, IContentPart<VR, ? extends VR>> graveyard = new HashMap<Object, IContentPart<VR, ? extends VR>>();

	public Map<Object, IContentPart<VR, ? extends VR>> getContentPartPool() {
		return graveyard;
	}

}
