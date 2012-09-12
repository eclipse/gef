package org.eclipse.gef4.graphics.images;

import org.eclipse.gef4.graphics.Image;

public abstract class AbstractChannelFilterOperation extends
AbstractPixelFilterOperation {

	protected abstract int processChannel(int v, int x, int y, int i, Image input);

	@Override
	protected int processPixel(int argb, int x, int y, Image input) {
		int[] argbIn = Utils.getARGB(argb);
		int[] argbOut = new int[4];
		for (int i = 0; i < Image.NUM_CHANNELS; i++) {
			argbOut[i] = Utils.getClamped(processChannel(argbIn[i], x, y, i,
					input));
		}
		return Utils.getPixel(argbOut);
	}

}
