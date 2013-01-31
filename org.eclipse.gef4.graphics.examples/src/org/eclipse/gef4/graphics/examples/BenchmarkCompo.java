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

import java.io.IOException;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.eclipse.gef4.graphics.image.AbstractPixelFilterOperation;
import org.eclipse.gef4.graphics.image.ArithmeticOperations;
import org.eclipse.gef4.graphics.image.CompositeImageOperation;
import org.eclipse.gef4.graphics.image.CompositePixelFilterOperation;
import org.eclipse.gef4.graphics.image.Image;
import org.eclipse.gef4.graphics.image.PixelOperations;

public class BenchmarkCompo {

	public static void main(String[] args) throws IOException,
			URISyntaxException {
		Image img = new Image(ImageIO.read(BenchmarkCompo.class
				.getResource("test.png")));

		AbstractPixelFilterOperation invOp = ArithmeticOperations
				.getInvertOperation();
		AbstractPixelFilterOperation andOp = ArithmeticOperations
				.getAndOperation(0xffe0e0e0);
		AbstractPixelFilterOperation mulOp = ArithmeticOperations
				.getMultiplyOperation(2);
		AbstractPixelFilterOperation thrOp = PixelOperations
				.getThresholdOperation(128);

		CompositeImageOperation compo0 = new CompositeImageOperation(invOp,
				andOp, mulOp, thrOp);

		CompositePixelFilterOperation compo1 = new CompositePixelFilterOperation(
				invOp, andOp, mulOp, thrOp);

		int n = 1000;
		int p = n / 100;

		double time = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
			if (i % p == 0) {
				System.out.println(100 * i / n + "%");
			}
			img.apply(compo0);
		}
		time = System.currentTimeMillis() - time;
		System.out.println("general CompositeImageOperation: " + time + " ("
				+ (time / n) + " avg)");

		time = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
			if (i % p == 0) {
				System.out.println(100 * i / n + "%");
			}
			img.apply(compo1);
		}
		time = System.currentTimeMillis() - time;
		System.out.println("specific CompositePixelFilterOperation: " + time
				+ " (" + (time / n) + " avg)");
	}
}
