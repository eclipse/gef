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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.printing.Printer;

/**
 * The PrinterGraphics is used to paint on a {@link Printer}. To construct a
 * PrinterGraphics instance, you have to provide the {@link Printer} to paint on
 * ({@link #PrinterGraphics(Printer)}).
 * 
 * @author mwienand
 * 
 */
public class PrinterGraphics extends DisplayGraphics {

	public PrinterGraphics(Printer printer) {
		super(new GC(printer));
	}

	/*
	 * TODO: Compute startPage(), endPage(), etc. calls.
	 */

	@Override
	public void cleanUp() {
		super.cleanUp();
		getGC().dispose();
	}

}
