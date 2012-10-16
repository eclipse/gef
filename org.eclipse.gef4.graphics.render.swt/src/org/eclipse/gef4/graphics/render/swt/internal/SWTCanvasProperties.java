/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.graphics.render.swt.internal;

import org.eclipse.gef4.geometry.convert.swt.Geometry2SWT;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Region;
import org.eclipse.gef4.geometry.planar.Ring;
import org.eclipse.gef4.graphics.render.AbstractCanvasProperties;
import org.eclipse.gef4.graphics.render.ICanvasProperties;
import org.eclipse.gef4.graphics.render.IGraphics;
import org.eclipse.gef4.graphics.render.swt.SWTGraphics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

public class SWTCanvasProperties extends AbstractCanvasProperties {

	private org.eclipse.swt.graphics.Region swtRegion = null;
	private org.eclipse.swt.graphics.Transform swtTransform = null;
	private boolean dirtyRegion = true;
	private boolean dirtyTransform = true;

	public SWTCanvasProperties() {
		super();
	}

	public void applyOn(IGraphics g) {
		// factory pattern contract
		GC gc = ((SWTGraphics) g).getGC();

		// apply clippingArea
		if (dirtyRegion) {
			if (clippingArea == null) {
				gc.setClipping((org.eclipse.swt.graphics.Region) null);
				SWTGraphicsUtils.dispose(swtRegion);
			} else {
				org.eclipse.swt.graphics.Region tmpRegion = Geometry2SWT
						.toSWTRegion(clippingArea);
				gc.setClipping(tmpRegion);
				SWTGraphicsUtils.dispose(swtRegion);
				swtRegion = tmpRegion;
			}
			dirtyRegion = false;
		} else {
			gc.setClipping(swtRegion);
		}

		// apply affineTransform
		if (dirtyTransform) {
			if (affineTransform == null) {
				gc.setTransform(null);
				SWTGraphicsUtils.dispose(swtTransform);
			} else {
				double[] matrix = affineTransform.getMatrix();
				float[] matrixAsFloats = new float[matrix.length];
				for (int i = 0; i < matrix.length; i++) {
					matrixAsFloats[i] = (float) matrix[i];
				}
				org.eclipse.swt.graphics.Transform tmpTransform = new Transform(
						Display.getCurrent(), matrixAsFloats);
				gc.setTransform(tmpTransform);
				SWTGraphicsUtils.dispose(swtTransform);
				swtTransform = tmpTransform;
			}
			dirtyTransform = false;
		} else {
			gc.setTransform(swtTransform);
		}
	}

	public void cleanUp(IGraphics g) {
		// abstract factory contract
		GC gc = ((SWTGraphics) g).getGC();

		// TODO: reset to the correct values

		// reset to defaults
		gc.setClipping((org.eclipse.swt.graphics.Region) null);
		gc.setTransform(null);
	}

	public SWTCanvasProperties getCopy() {
		SWTCanvasProperties copy = new SWTCanvasProperties();
		copy.affineTransform = getAffineTransform();
		copy.clippingArea = getClippingArea();
		return copy;
	}

	public void init(IGraphics g) {
		// TODO: read out the values
	}

	@Override
	public ICanvasProperties setAffineTransform(AffineTransform affineTransform) {
		super.setAffineTransform(affineTransform);
		dirtyTransform = true;
		return this;
	}

	@Override
	public ICanvasProperties setClippingArea(Region clippingArea) {
		super.setClippingArea(clippingArea);
		dirtyRegion = true;
		return this;
	}

	@Override
	public ICanvasProperties setClippingArea(Ring clippingArea) {
		super.setClippingArea(clippingArea);
		dirtyRegion = true;
		return this;
	}

}
