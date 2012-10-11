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
package org.eclipse.gef4.graphics.images;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef4.graphics.Image;

/**
 * Instances of the CompositeImageOperation class combine several
 * {@link IImageOperation}s to a single {@link IImageOperation} so that they can
 * be applied at one go.
 * 
 * @author mwienand
 * 
 */
public class CompositeImageOperation implements IImageOperation {

	private List<IImageOperation> imageOps = null;

	/**
	 * <p>
	 * Constructs a new {@link CompositeImageOperation} from the given
	 * {@link IImageOperation}s.
	 * </p>
	 * 
	 * <p>
	 * The provided {@link IImageOperation}s are applied in the given order when
	 * this {@link CompositeImageOperation} is {@link #apply(Image) applied}.
	 * </p>
	 * 
	 * @param imageOps
	 */
	public CompositeImageOperation(IImageOperation... imageOps) {
		setImageOperations(imageOps);
	}

	public Image apply(Image source) {
		for (IImageOperation imageOp : imageOps) {
			source = imageOp.apply(source);
		}
		return source;
	}

	/**
	 * <p>
	 * Returns the {@link IImageOperation}s that are combined in this
	 * {@link CompositeImageOperation}.
	 * </p>
	 * 
	 * @return the {@link IImageOperation}s that are combined in this
	 *         {@link CompositeImageOperation}
	 */
	public IImageOperation[] getImageOperations() {
		return imageOps.toArray(new IImageOperation[] {});
	}

	/**
	 * Sets the {@link IImageOperation}s to apply when this
	 * {@link CompositeImageOperation} is applied.
	 * 
	 * @param imageOps
	 *            the new {@link IImageOperation}s to apply when this
	 *            {@link CompositeImageOperation} is applied
	 */
	public void setImageOperations(IImageOperation... imageOps) {
		this.imageOps = imageOps == null ? new ArrayList<IImageOperation>()
				: new ArrayList<IImageOperation>(Arrays.asList(imageOps));
	}

}
