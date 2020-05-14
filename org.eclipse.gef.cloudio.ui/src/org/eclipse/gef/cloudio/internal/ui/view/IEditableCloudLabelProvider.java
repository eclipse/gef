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
package org.eclipse.gef.cloudio.internal.ui.view;

import java.util.List;

import org.eclipse.gef.cloudio.internal.ui.ICloudLabelProvider;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * An {@link IEditableCloudLabelProvider} is supported by the
 * {@link CloudOptionsComposite}, such that it can be used to modify colors,
 * fonts, and angles.
 * 
 * @author sschwieb
 *
 */
public interface IEditableCloudLabelProvider extends ICloudLabelProvider {

	public void setColors(List<RGB> colors);

	public void setFonts(List<FontData> fonts);

	public void setAngles(List<Float> list);

}
