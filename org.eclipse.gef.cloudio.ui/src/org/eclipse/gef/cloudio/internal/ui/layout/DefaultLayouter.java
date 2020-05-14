/******************************************************************************
 * Copyright (c) 2011, 2016 Stephan Schwiebert and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Schwiebert - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.cloudio.internal.ui.layout;

import java.util.Random;

import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.cloudio.internal.ui.Word;
import org.eclipse.gef.cloudio.internal.ui.util.CloudMatrix;
import org.eclipse.gef.cloudio.internal.ui.util.RectTree;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

/**
 * 
 * @author sschwieb
 *
 */
public class DefaultLayouter implements ILayouter {

	public static final String X_AXIS_VARIATION = "xaxis";

	public static final String Y_AXIS_VARIATION = "yaxis";

	private Random random = new Random();

	/**
	 * Percentage of the x axis variation. By default, searching for free space is
	 * started in the center of the available area. By increasing this value,
	 * the start point is moved on the x axis.
	 * 
	 */
	private int xAxisVariation;

	/**
	 * Percentage of the y axis variation. By default, searching for free space is
	 * started in the center of the available area. By increasing this value,
	 * the start point is moved on the y axis.
	 * 
	 */
	private int yAxisVariation;

	public DefaultLayouter(int i, int j) {
		this.xAxisVariation = i;
		this.yAxisVariation = j;
	}

	public Point getInitialOffset(Word word, Rectangle cloudArea) {
		int xOff = 0;
		if (xAxisVariation > 0) {
			int range = (cloudArea.width - word.width) / 200 * xAxisVariation;
			if (range > 0) {
				xOff = random.nextInt(range);
				if (random.nextBoolean()) {
					xOff = -xOff;
				}
			}
		}
		int yOff = 0;
		if (yAxisVariation > 0) {
			int range = cloudArea.height / 200 * yAxisVariation;
			if (range > 0) {
				yOff = random.nextInt(range);
				if (random.nextBoolean())
					yOff = -yOff;
			}
		}
		return new Point(xOff, yOff);
	}

	/**
	 * Tries to position the given word in the given area. First a start point
	 * is chosen, then the {@link RectTree} of the word and the main area is
	 * used to detect whether the word can be placed at the given position, or
	 * not. If not, the current point is moved slightly in a spiral manner,
	 * similar to the approach of Wordle.
	 * 
	 * @param word
	 * @param cloudArea
	 * @return whether the given word could be placed in the respective cloud
	 *         area
	 */
	public boolean layout(Point offset, final Word word, final Rectangle cloudArea, CloudMatrix mainTree) {
		Assert.isLegal(word != null, "Word cannot be null!");
		Point next = new Point(-word.width / 2, -word.height / 2);
		next.x += random.nextInt(25);
		next.y += random.nextInt(25);
		double growFactor = 1.6;
		offset.x += cloudArea.width / 2;
		offset.y += cloudArea.height / 2;
		final int accuracy = mainTree.getMinResolution();
		for (int i = 0; i < 5000; i++) {
			final double radius = Math.sqrt((double) (next.x * next.x + next.y * next.y)) + growFactor;
			double atan = Math.atan2(next.y, next.x);
			if (growFactor > 1.1) {
				growFactor -= 0.0007;
			}
			if (radius < 80) {
				atan += 0.7;
			} else {
				atan += 20 / radius;
			}
			if (growFactor < 0.0005) {
				growFactor = 0.0005;
			}
			next.x = (int) (radius * Math.cos(atan));
			next.y = (int) (radius * Math.sin(atan));
			word.x = ((next.x + offset.x) / accuracy) * accuracy;
			word.y = ((next.y + offset.y) / accuracy) * accuracy;
			RectTree rt = word.tree;
			if (rt == null)
				break;
			rt.move(word.x, word.y);
			if (cloudArea.x <= word.x && cloudArea.y <= word.y && cloudArea.x + cloudArea.width >= word.x + word.width
					&& cloudArea.y + cloudArea.height >= word.y + word.height) {
				if (rt.fits(mainTree)) {
					rt.place(mainTree, word.id);
					return true;
				}
			}
		}
		return false;
	}

	public void setOption(String optionName, Object object) {
		if (X_AXIS_VARIATION.equals(optionName)) {
			Integer value = (Integer) object;
			Assert.isLegal(value >= 0, "Parameter must be between 0 and 100 (inclusive): " + value);
			Assert.isLegal(value <= 100, "Parameter must be between 0 and 100 (inclusive): " + value);
			this.xAxisVariation = value;
			return;
		}
		if (Y_AXIS_VARIATION.equals(optionName)) {
			Integer value = (Integer) object;
			Assert.isLegal(value >= 0, "Parameter must be between 0 and 100 (inclusive): " + value);
			Assert.isLegal(value <= 100, "Parameter must be between 0 and 100 (inclusive): " + value);
			this.yAxisVariation = value;
			return;
		}
		System.err.println("Unrecognized option: " + optionName);
	}

}
