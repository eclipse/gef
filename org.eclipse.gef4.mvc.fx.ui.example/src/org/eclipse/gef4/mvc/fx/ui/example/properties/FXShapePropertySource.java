package org.eclipse.gef4.mvc.fx.ui.example.properties;

import javafx.scene.paint.Paint;

import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.ui.properties.FXFillPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class FXShapePropertySource implements IPropertySource {

	private static final IPropertyDescriptor FILL_PROPERTY_DESCRIPTOR = new FXFillPropertyDescriptor(
			FXGeometricShape.FILL_PROPERTY, "Fill");
	
	private FXGeometricShape shape;
	
	public FXShapePropertySource(FXGeometricShape shape){
		this.shape = shape;
	}
	
	@Override
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { FILL_PROPERTY_DESCRIPTOR };
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (FILL_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return shape.getFill();
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (FILL_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return shape.getFill() != null;
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (FILL_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			shape.setFill(null);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (FILL_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			shape.setFill((Paint) value);
		}
	}

}
