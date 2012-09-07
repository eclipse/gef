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
package org.eclipse.gef4.graphics;

import java.net.URL;

/**
 * An {@link Image} is a lightweight object that stores a {@link URL} to an
 * image file.
 * 
 * @author mwienand
 * 
 */
public class Image {

	private URL imageFile;

	/**
	 * Constructs a new {@link Image} and associates that {@link Image} with the
	 * given {@link URL} of an image file.
	 * 
	 * @param imageFile
	 */
	public Image(URL imageFile) {
		this.imageFile = imageFile;
	}

	/**
	 * Returns the {@link URL} of the image file associated with this
	 * {@link Image}.
	 * 
	 * @return the {@link URL} of the image file associated with this
	 *         {@link Image}
	 */
	public URL getImageFile() {
		return imageFile;
	}

	@Override
	public String toString() {
		return "Image(imageFile = " + imageFile + ")";
	}

}