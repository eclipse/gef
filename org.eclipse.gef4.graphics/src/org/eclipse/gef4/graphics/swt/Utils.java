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

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.gef4.graphics.Color;
import org.eclipse.gef4.graphics.Font;
import org.eclipse.gef4.graphics.Image;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

class Utils {

	static org.eclipse.swt.graphics.Color createSWTColor(Color color) {
		return new org.eclipse.swt.graphics.Color(Display.getCurrent(),
				color.getRed(), color.getGreen(), color.getBlue());
	}

	static org.eclipse.swt.graphics.Font createSWTFont(Font font) {
		int swtStyle = (font.isBold() ? SWT.BOLD : 0)
				| (font.isItalic() ? SWT.ITALIC : 0);
		return new org.eclipse.swt.graphics.Font(Display.getCurrent(),
				font.getFamily(), (int) font.getSize(), swtStyle);
	}

	static org.eclipse.swt.graphics.Image createSWTImage(Image img) {
		InputStream is = null;
		try {
			is = img.getImageFile().openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new org.eclipse.swt.graphics.Image(Display.getCurrent(), is);
	}

	static void dispose(org.eclipse.swt.graphics.Resource res) {
		if (res != null && !res.isDisposed()) {
			res.dispose();
		}
	}

}
