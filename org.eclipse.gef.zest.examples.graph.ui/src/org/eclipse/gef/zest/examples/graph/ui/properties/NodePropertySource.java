/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.zest.examples.graph.ui.properties;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.zest.fx.ZestProperties;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 *
 * @author mwienand
 *
 */
public class NodePropertySource implements IPropertySource {

	private static final String POSITION_DELIMITER = ", ";
	private static final String SIZE_DELIMITER = " x ";
	private static final IPropertyDescriptor POSITION_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(
			ZestProperties.POSITION__N, ZestProperties.POSITION__N);
	private static final IPropertyDescriptor SIZE_PROPERTY_DESCRIPTOR = new TextPropertyDescriptor(
			ZestProperties.SIZE__N, ZestProperties.SIZE__N);
	private Node node;
	private Dimension initialSize;
	private Point initialPosition;

	/**
	 *
	 * @param node
	 *            {@link Node}
	 */
	public NodePropertySource(Node node) {
		this.node = node;
		initialPosition = ZestProperties.getPosition(node);
		initialSize = ZestProperties.getSize(node);
	}

	@Override
	public Object getEditableValue() {
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { POSITION_PROPERTY_DESCRIPTOR, SIZE_PROPERTY_DESCRIPTOR };
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (POSITION_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			Point position = ZestProperties.getPosition(node);
			return position == null ? "null" : position.x + POSITION_DELIMITER + position.y;
		} else if (SIZE_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			Dimension size = ZestProperties.getSize(node);
			return size == null ? "null" : size.width + SIZE_DELIMITER + size.height;
		}
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (POSITION_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			return ZestProperties.getPosition(node) != null;
		} else if (SIZE_PROPERTY_DESCRIPTOR.equals(id)) {
			return ZestProperties.getSize(node) != null;
		}
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (POSITION_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			ZestProperties.setPosition(node, initialPosition);
		} else if (SIZE_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			ZestProperties.setSize(node, initialSize);
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (POSITION_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			String[] xy = ((String) value).split(POSITION_DELIMITER);
			ZestProperties.setPosition(node, new Point(Double.parseDouble(xy[0]), Double.parseDouble(xy[1])));
		} else if (SIZE_PROPERTY_DESCRIPTOR.getId().equals(id)) {
			String[] wh = ((String) value).split(SIZE_DELIMITER);
			ZestProperties.setSize(node, new Dimension(Double.parseDouble(wh[0]), Double.parseDouble(wh[1])));
		}
	}

}
