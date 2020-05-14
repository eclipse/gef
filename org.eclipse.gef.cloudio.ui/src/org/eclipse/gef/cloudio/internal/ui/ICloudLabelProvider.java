/******************************************************************************
 * Copyright (c) 2011, 2016 Stephan Schwiebert and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Schwiebert - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.cloudio.internal.ui;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;

/**
 * Defines the label of an element within the cloud. Besides of the
 * string-label, each element can be assigned a unique weight (used to calculate
 * the font size of the rendered element), color, font and angle.
 * 
 * @author sschwieb
 *
 */
public interface ICloudLabelProvider extends IBaseLabelProvider {

	/**
	 * The label of the given element, which must not be <code>null</code>.
	 * 
	 * @param element
	 * @return the label of the given element
	 */
	public String getLabel(Object element);

	/**
	 * The weight of the given element, which must be between 0 and 1
	 * (inclusive).
	 * 
	 * @param element
	 * @return the weight of the given element
	 */
	public double getWeight(Object element);

	/**
	 * The {@link Color} of the given element, which must not be
	 * <code>null</code>.
	 * 
	 * @param element
	 * @return the color of the given element
	 */
	public Color getColor(Object element);

	/**
	 * The {@link FontData}-array which defines the font of the given element.
	 * Each element must be provided with a unique array. Must not return
	 * <code>null</code>.
	 * 
	 * @param element
	 * @return the font data for the given element
	 */
	public FontData[] getFontData(Object element);

	/**
	 * The angle of the element, which must be between -90 and 90, inclusive.
	 * 
	 * @param element
	 * @return the angle of the given element
	 */
	public float getAngle(Object element);

	/**
	 * Return the tool tip of the element, or <code>null</code>, if none.
	 * 
	 * @param element
	 * @return the tooltip of the given element
	 */
	public String getToolTip(Object element);

}
