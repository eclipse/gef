/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gef4.mvc.ui.properties;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.ui.views.properties.IPropertySource;

public class PropertySourceAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		IContentPart part = (IContentPart) adaptableObject;
		if(adapterType.isInstance(part)){
			return part;
		}
		Object adapter = part.getAdapter(adapterType);
		if(adapter != null){
			return adapter;
		}
		
		Object model = part.getContent();
		// check if model is already of the desired adapter type
		if (adapterType.isInstance(model)) {
			return model;
		}
		// check if model is adaptable and does provide an adapter of the
		// desired type
		if (model instanceof IAdaptable) {
			adapter = ((IAdaptable) model).getAdapter(adapterType);
			if (adapter != null) {
				return adapter;
			}
		}
		// fall back to platform's adapter manager
		return Platform.getAdapterManager().getAdapter(model, adapterType);
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] { IPropertySource.class };
	}

}
