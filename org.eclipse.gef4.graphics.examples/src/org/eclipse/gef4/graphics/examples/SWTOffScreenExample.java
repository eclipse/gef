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
package org.eclipse.gef4.graphics.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.gef4.graphics.render.swt.SWTGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

public class SWTOffScreenExample {

	public static void main(String[] args) throws IOException {
		new SWTOffScreenExample("GEF4 Graphics - SWT OffScreen");
	}

	public SWTOffScreenExample(String title) throws IOException {
		Display display = new Display();
		Image image = new Image(display, 640, 480);
		SWTGraphics g = new SWTGraphics(image);
		SimpleGraphicsUtil.renderScene(g);
		g.cleanUp();
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { image.getImageData() };

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Type image path, please.");
		String path = br.readLine();

		imageLoader.save(path, SWT.IMAGE_PNG);
	}

}
