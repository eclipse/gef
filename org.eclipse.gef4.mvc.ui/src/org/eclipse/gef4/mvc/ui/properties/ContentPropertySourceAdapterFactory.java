/*******************************************************************************
 * Copyright (c) 2006, 2014 IBM Corporation and others.
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

/**
 * An {@link IAdapterFactory} that adapts the {@link IContentPart#getContent()}
 * to {@link IPropertySource}.
 *
 * @author anyssen
 *
 */
public class ContentPropertySourceAdapterFactory implements IAdapterFactory {

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		// Check model of content part for adaptability
		// IMPORTANT: as all IVisualParts are adaptable, this
		// here is only called in case IPropertySource is not handled inside
		// getAdapter() of the content part)
		IContentPart<?, ?> part = (IContentPart<?, ?>) adaptableObject;
		Object model = part.getContent();
		// check if model is already of the desired adapter type
		if (adapterType.isInstance(model)) {
			return model;
		}
		// check if model is adaptable and does provide an adapter of the
		// desired type
		if (model instanceof IAdaptable) {
			Object adapter = ((IAdaptable) model).getAdapter(adapterType);
			if (adapter != null) {
				return adapter;
			}
		}
		/*
		 * TODO: Verify if this is the correct approach.
		 * 
		 * Return null when the model is null (no adapter available).
		 */
		if (model == null) {
			return null;
		}
		// fall back to adapter manager
		return Platform.getAdapterManager().getAdapter(model, adapterType);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] { IPropertySource.class };
	}

}
