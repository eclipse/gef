/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.ui.properties;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricCurve;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricModel;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricShape;
import org.eclipse.ui.views.properties.IPropertySource;

public class PropertySourceAdapterFactory implements IAdapterFactory {

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IPropertySource.class.equals(adapterType)) {
			if (adaptableObject instanceof FXGeometricCurve) {
				return new FXCurvePropertySource(
						(FXGeometricCurve) adaptableObject);
			} else if (adaptableObject instanceof FXGeometricShape) {
				return new FXShapePropertySource(
						(FXGeometricShape) adaptableObject);
			} else if (adaptableObject instanceof FXGeometricModel) {
				return new FXModelPropertySource(
						(FXGeometricModel) adaptableObject);
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] { IPropertySource.class };
	}

}
