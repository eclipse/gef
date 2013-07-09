/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.swtfx.gc;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.graphics.Transform;

public class GcState {

	private boolean advanced;
	private int alpha;
	private int antialias;
	private org.eclipse.swt.graphics.Color background;
	private org.eclipse.swt.graphics.Pattern backgroundPattern;
	private Region region;
	private boolean isDisposed;
	private int fillRule;
	private org.eclipse.swt.graphics.Font font;
	private org.eclipse.swt.graphics.Color foreground;
	private org.eclipse.swt.graphics.Pattern foregroundPattern;
	private int interpolation;
	private LineAttributes lineAttributes;
	private int textAntialias;
	private Transform transform;
	private boolean xorMode;

	public GcState(GC gc) {
		advanced = gc.getAdvanced();
		alpha = gc.getAlpha();
		antialias = gc.getAntialias();
		background = gc.getBackground();
		backgroundPattern = gc.getBackgroundPattern();
		region = new Region(gc.getDevice());
		gc.getClipping(region);
		fillRule = gc.getFillRule();
		font = gc.getFont();
		foreground = gc.getForeground();
		foregroundPattern = gc.getForegroundPattern();
		interpolation = gc.getInterpolation();
		lineAttributes = gc.getLineAttributes();
		textAntialias = gc.getTextAntialias();
		transform = new Transform(gc.getDevice());
		gc.getTransform(transform);
		xorMode = gc.getXORMode();
	}

	@SuppressWarnings("deprecation")
	public void apply(GC gc) {
		if (isDisposed) {
			throw new IllegalStateException("Cannot apply GcState twice!");
		}
		isDisposed = true;

		gc.setAdvanced(advanced);
		gc.setAlpha(alpha);
		gc.setAntialias(antialias);
		gc.setBackground(background);
		gc.setBackgroundPattern(backgroundPattern);
		gc.setClipping(region);
		region.dispose();
		gc.setFillRule(fillRule);
		gc.setFont(font);
		gc.setForeground(foreground);
		gc.setForegroundPattern(foregroundPattern);
		gc.setInterpolation(interpolation);
		gc.setLineAttributes(lineAttributes);
		gc.setTextAntialias(textAntialias);
		gc.setTransform(transform);
		transform.dispose();
		gc.setXORMode(xorMode);
	}

}