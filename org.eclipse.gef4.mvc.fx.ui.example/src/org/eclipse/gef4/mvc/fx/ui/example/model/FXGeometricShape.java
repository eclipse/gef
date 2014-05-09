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
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.example.model;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.mvc.fx.ui.properties.FXFillPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class FXGeometricShape extends AbstractFXGeometricElement<IShape>
		implements IPropertySource {

	private static final IPropertyDescriptor FILL_PROPERTY_DESCRIPTOR = new FXFillPropertyDescriptor(
			"fillColor", "Fill");

	private Paint fill;

	public FXGeometricShape(IShape shape, AffineTransform transform,
			Paint fill, Effect effect) {
		this(shape, transform, new Color(0, 0, 0, 1), 1.0, fill, effect);
	}

	public FXGeometricShape(IShape shape, AffineTransform transform,
			Color stroke, double strokeWidth, Paint fill, Effect effect) {
		super(shape, transform, stroke, strokeWidth, effect);
		setFill(fill);
	}

	public void setFill(Paint fill) {
		Paint oldFill = this.fill;
		this.fill = fill;
		pcs.firePropertyChange((String) FILL_PROPERTY_DESCRIPTOR.getId(), oldFill, fill);
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
			return fill;
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (FILL_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return fill != null;
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (FILL_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			setFill(null);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (FILL_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			setFill((Paint) value);
		}
	}

	public Paint getFill() {
		return fill;
	}

}
