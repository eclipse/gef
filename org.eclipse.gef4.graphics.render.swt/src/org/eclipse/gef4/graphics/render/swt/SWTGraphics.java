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
package org.eclipse.gef4.graphics.render.swt;

import org.eclipse.gef4.graphics.render.AbstractGraphics;
import org.eclipse.gef4.graphics.render.IFontUtils;
import org.eclipse.gef4.graphics.render.swt.internal.SWTBlitProperties;
import org.eclipse.gef4.graphics.render.swt.internal.SWTCanvasProperties;
import org.eclipse.gef4.graphics.render.swt.internal.SWTDrawProperties;
import org.eclipse.gef4.graphics.render.swt.internal.SWTFillProperties;
import org.eclipse.gef4.graphics.render.swt.internal.SWTFontUtils;
import org.eclipse.gef4.graphics.render.swt.internal.SWTWriteProperties;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.printing.Printer;

public class SWTGraphics extends AbstractGraphics {

	private GC gc;
	private boolean disposeGC = false;

	public SWTGraphics(GC gc) {
		this.gc = gc;

		SWTCanvasProperties cp = new SWTCanvasProperties();
		SWTDrawProperties dp = new SWTDrawProperties();
		SWTFillProperties fp = new SWTFillProperties();
		SWTBlitProperties bp = new SWTBlitProperties();
		SWTWriteProperties wp = new SWTWriteProperties();

		pushInitialState(cp, dp, fp, bp, wp);
	}

	public SWTGraphics(Image image) {
		this(new GC(image));
		disposeGC = true;
	}

	public SWTGraphics(Printer printer) {
		this(new GC(printer));
		disposeGC = true;
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
		if(disposeGC){
			gc.dispose();
		}
	}

	public IFontUtils fontUtils() {
		return new SWTFontUtils(this);
	}

	/**
	 * Returns the {@link GC} associated with this {@link SWTGraphics}.
	 * 
	 * @return the {@link GC} associated with this {@link SWTGraphics}
	 */
	public GC getGC() {
		return gc;
	}

}
