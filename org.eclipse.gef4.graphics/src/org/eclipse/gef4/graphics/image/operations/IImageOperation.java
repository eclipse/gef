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
package org.eclipse.gef4.graphics.image.operations;

import org.eclipse.gef4.graphics.image.Image;

/**
 * An IImageOperation can be {@link #apply(Image) applied} to a an {@link Image}
 * and results in another {@link Image}. Therefore, you can easily chain
 * IImageOperations as follows: <blockquote>
 * 
 * <pre>
 * Image result = source.apply(op0).apply(op1).apply(op2);
 * </pre>
 * 
 * </blockquote>
 * 
 * @author mwienand
 * 
 */
public interface IImageOperation {

	/**
	 * Applies this {@link IImageOperation} to the given <i>source</i>
	 * {@link Image}. Returns a new {@link Image} representing the result of the
	 * operation.
	 * 
	 * @param source
	 *            the {@link Image} to process
	 * @return a new {@link Image} representing the result of this
	 *         {@link IImageOperation}
	 */
	Image apply(Image source);

}
