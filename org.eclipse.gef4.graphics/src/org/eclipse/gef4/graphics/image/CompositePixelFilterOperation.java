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
package org.eclipse.gef4.graphics.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A CompositePixelFilterOperation combines a set of
 * {@link AbstractPixelFilterOperation}s to apply them at once to an
 * {@link Image}. The combination of multiple
 * {@link AbstractPixelFilterOperation}s does not only ease the appliance by
 * grouping the operations, it does also increase the execution speed for the
 * grouped operations. In fact, if when {@link IImageOperation#apply(Image)
 * applying} the operations sequentially, the {@link Image}'s pixels have to be
 * iterated for each of the operations. But if they are
 * {@link IImageOperation#apply(Image) applied} as a group, the {@link Image}'s
 * pixels do only have to be iterated once.
 * 
 * @author mwienand
 * 
 */
public class CompositePixelFilterOperation extends AbstractPixelFilterOperation {

	private List<AbstractPixelFilterOperation> pixelOps = null;

	/**
	 * <p>
	 * Constructs a new {@link CompositePixelFilterOperation} from the given
	 * {@link AbstractPixelFilterOperation}s.
	 * </p>
	 * 
	 * <p>
	 * The provided {@link AbstractPixelFilterOperation}s are applied in the
	 * given order when this {@link CompositePixelFilterOperation} is
	 * {@link #apply(Image) applied}.
	 * </p>
	 * 
	 * @param pixelOps
	 *            the {@link AbstractPixelFilterOperation}s to apply when this
	 *            {@link CompositePixelFilterOperation} is applied
	 */
	public CompositePixelFilterOperation(
			AbstractPixelFilterOperation... pixelOps) {
		setPixelFilterOperations(pixelOps);
	}

	/**
	 * <p>
	 * Returns the {@link AbstractPixelFilterOperation}s that are combined in
	 * this {@link CompositePixelFilterOperation}.
	 * </p>
	 * 
	 * @return the {@link AbstractPixelFilterOperation}s that are combined in
	 *         this {@link CompositePixelFilterOperation}
	 */
	public AbstractPixelFilterOperation[] getPixelFilterOperations() {
		return pixelOps.toArray(new AbstractPixelFilterOperation[] {});
	}

	@Override
	protected int processPixel(int argb, int x, int y, Image input) {
		for (AbstractPixelFilterOperation pixelOp : pixelOps) {
			argb = pixelOp.processPixel(argb, x, y, input);
		}
		return argb;
	}

	/**
	 * Sets the {@link AbstractPixelFilterOperation}s to apply when this
	 * {@link CompositePixelFilterOperation} is applied.
	 * 
	 * @param pixelOps
	 *            the new {@link AbstractPixelFilterOperation}s to apply when
	 *            this {@link CompositePixelFilterOperation} is applied
	 */
	public void setPixelFilterOperations(
			AbstractPixelFilterOperation... pixelOps) {
		this.pixelOps = pixelOps == null ? new ArrayList<AbstractPixelFilterOperation>()
				: new ArrayList<AbstractPixelFilterOperation>(
						Arrays.asList(pixelOps));
	}

}
