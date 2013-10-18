/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.swtfx.animation;

import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.swtfx.INode;
import org.eclipse.gef4.swtfx.Scene;

public class PathTransition extends AbstractTransition {

	private PathEvaluator evaluator;
	private INode node;

	public PathTransition(Scene scene, long durationMillis, double cycleCount,
			boolean autoReverse, INode node, Path path) {
		super(scene, durationMillis, cycleCount, autoReverse);
		evaluator = new PathEvaluator(path);
		this.node = node;
	}

	@Override
	public void step(double t) {
		Point p = evaluator.get(t);
		node.setTranslateX(p.x);
		node.setTranslateY(p.y);
	}

}
