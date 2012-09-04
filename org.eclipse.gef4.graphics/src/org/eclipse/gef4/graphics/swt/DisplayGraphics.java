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
package org.eclipse.gef4.graphics.swt;

import org.eclipse.gef4.geometry.convert.Geometry2SWT;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IMultiShape;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.graphics.AbstractGraphics;
import org.eclipse.gef4.graphics.Image;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

public class DisplayGraphics extends AbstractGraphics {

	protected GC gc;

	public DisplayGraphics(GC gc) {
		this.gc = gc;

		CanvasProperties cp = new CanvasProperties();
		DrawProperties dp = new DrawProperties();
		FillProperties fp = new FillProperties();
		BlitProperties bp = new BlitProperties();
		WriteProperties wp = new WriteProperties();

		pushInitialState(cp, dp, fp, bp, wp);
	}

	@Override
	protected void doBlit(Image image) {
		org.eclipse.swt.graphics.Image swtImage = Utils.createSWTImage(image);
		gc.drawImage(swtImage, 0, 0);
		swtImage.dispose();
	}

	@Override
	protected void doDraw(ICurve curve) {
		doDraw(curve.toPath());
	}

	@Override
	protected void doDraw(Path path) {
		org.eclipse.swt.graphics.Path swtPath = new org.eclipse.swt.graphics.Path(
				Display.getCurrent(), Geometry2SWT.toSWTPathData(path));
		gc.drawPath(swtPath);
		swtPath.dispose();
	}

	@Override
	protected void doFill(IMultiShape multishape) {
		doFill(multishape.toPath());
	}

	@Override
	protected void doFill(IShape shape) {
		doFill(shape.toPath());
	}

	@Override
	protected void doFill(Path path) {
		org.eclipse.swt.graphics.Path swtPath = new org.eclipse.swt.graphics.Path(
				Display.getCurrent(), Geometry2SWT.toSWTPathData(path));
		gc.setFillRule(path.getWindingRule() == Path.WIND_EVEN_ODD ? SWT.FILL_EVEN_ODD
				: SWT.FILL_WINDING);
		gc.fillPath(swtPath);
		swtPath.dispose();
	}

	@Override
	protected void doWrite(String text) {
		boolean transparentBackground = writeProperties()
				.getBackgroundColor().getAlpha() < 128;
		gc.drawText(text, 0, 0, transparentBackground);
	}

	/**
	 * Returns the {@link GC} associated with this {@link DisplayGraphics}.
	 * 
	 * @return the {@link GC} associated with this {@link DisplayGraphics}
	 */
	public GC getGC() {
		return gc;
	}

}
