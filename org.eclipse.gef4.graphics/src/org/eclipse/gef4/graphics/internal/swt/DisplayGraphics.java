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
package org.eclipse.gef4.graphics.internal.swt;

import org.eclipse.gef4.graphics.AbstractGraphics;
import org.eclipse.gef4.graphics.IFontUtils;
import org.eclipse.swt.graphics.GC;

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

	public IFontUtils fontUtils() {
		return new FontUtils(this);
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
