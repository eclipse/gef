package org.eclipse.gef4.mvc.fx.ui.example.properties;

import org.eclipse.gef4.mvc.fx.ui.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.ui.example.model.FXGeometricCurve.Decoration;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class FXCurvePropertySource implements IPropertySource {

	private static final IPropertyDescriptor SOURCE_DECORATION_PROPERTY = new ComboBoxPropertyDescriptor(
			FXGeometricCurve.SOURCE_DECORATION_PROPERTY, "Source Decoration", new String[] {
					Decoration.NONE.name(), Decoration.ARROW.name(),
					Decoration.CIRCLE.name() });
	private static final IPropertyDescriptor TARGET_DECORATION_PROPERTY = new ComboBoxPropertyDescriptor(
			FXGeometricCurve.TARGET_DECORATION_PROPERTY, "Target Decoration", new String[] {
					Decoration.NONE.name(), Decoration.ARROW.name(),
					Decoration.CIRCLE.name() });
	
	private FXGeometricCurve curve;

	public FXCurvePropertySource(FXGeometricCurve curve){
		this.curve = curve;
	}
	
	@Override
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { SOURCE_DECORATION_PROPERTY,
				TARGET_DECORATION_PROPERTY };
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			return curve.getSourceDecoration().ordinal();
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			return curve.getTargetDecoration().ordinal();
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			return !curve.getSourceDecoration().equals(Decoration.NONE);
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			return !curve.getTargetDecoration().equals(Decoration.NONE);
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			curve.setSourceDecoration(Decoration.NONE);
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			curve.setTargetDecoration(Decoration.NONE);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (SOURCE_DECORATION_PROPERTY.getId().equals(id)) {
			curve.setSourceDecoration(Decoration.values()[(int) value]);
		} else if (TARGET_DECORATION_PROPERTY.getId().equals(id)) {
			curve.setTargetDecoration(Decoration.values()[(int) value]);
		}
	}

}
