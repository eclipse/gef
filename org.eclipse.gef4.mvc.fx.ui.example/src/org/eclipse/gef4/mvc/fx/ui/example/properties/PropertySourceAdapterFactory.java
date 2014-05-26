package org.eclipse.gef4.mvc.fx.ui.example.properties;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.ui.views.properties.IPropertySource;

public class PropertySourceAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IPropertySource.class.equals(adapterType)) {
			if (adaptableObject instanceof FXGeometricCurve) {
				return new FXCurvePropertySource(
						(FXGeometricCurve) adaptableObject);
			} else if (adaptableObject instanceof FXGeometricShape) {
				return new FXShapePropertySource(
						(FXGeometricShape) adaptableObject);
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] { IPropertySource.class };
	}

}
