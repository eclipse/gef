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

public class CompositeChannelFilterOperation extends
AbstractChannelFilterOperation {

	private List<AbstractChannelFilterOperation> channelOps = null;

	/**
	 * <p>
	 * Constructs a new {@link CompositeChannelFilterOperation} from the given
	 * {@link AbstractChannelFilterOperation}s.
	 * </p>
	 * 
	 * <p>
	 * The provided {@link AbstractChannelFilterOperation}s are applied in the
	 * given order when this {@link CompositeChannelFilterOperation} is
	 * {@link #apply(Image) applied}.
	 * </p>
	 * 
	 * @param pixelOps
	 *            the {@link AbstractChannelFilterOperation}s to apply when this
	 *            {@link CompositeChannelFilterOperation} is applied
	 */
	public CompositeChannelFilterOperation(
			AbstractChannelFilterOperation... pixelOps) {
		setChannelFilterOperations(pixelOps);
	}

	/**
	 * <p>
	 * Returns the {@link AbstractChannelFilterOperation}s that are combined in
	 * this {@link CompositeChannelFilterOperation}.
	 * </p>
	 * 
	 * @return the {@link AbstractChannelFilterOperation}s that are combined in
	 *         this {@link CompositeChannelFilterOperation}
	 */
	public AbstractChannelFilterOperation[] getChannelFilterOperations() {
		return channelOps.toArray(new AbstractChannelFilterOperation[] {});
	}

	@Override
	protected int processChannel(int v, int x, int y, int i, Image input) {
		int res = v;
		for (AbstractChannelFilterOperation chOp : channelOps) {
			res = chOp.processChannel(res, x, y, i, input);
		}
		return res;
	}

	/**
	 * Sets the {@link AbstractChannelFilterOperation}s to apply when this
	 * {@link CompositeChannelFilterOperation} is applied.
	 * 
	 * @param channelOps
	 *            the new {@link AbstractChannelFilterOperation}s to apply when
	 *            this {@link CompositeChannelFilterOperation} is applied
	 */
	public void setChannelFilterOperations(
			AbstractChannelFilterOperation... channelOps) {
		this.channelOps = channelOps == null ? new ArrayList<AbstractChannelFilterOperation>()
				: new ArrayList<AbstractChannelFilterOperation>(
						Arrays.asList(channelOps));
	}

}
