/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.behaviors;

import javafx.scene.Node;
import javafx.scene.transform.Affine;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;

/**
 * Adds an {@link Affine} transformation matrix to the
 * {@link Node#getTransforms() transforms} of the {@link #getHost() host} visual
 * and makes it accessible.
 *
 * @author wienand
 *
 */
public class FXTransformationBehavior extends AbstractBehavior<Node> {

	private Affine transform;

	public FXTransformationBehavior() {
	}

	@Override
	public void activate() {
		super.activate();
		transform = new Affine();
		getHost().getVisual().getTransforms().add(transform);
	}

	@Override
	public void deactivate() {
		getHost().getVisual().getTransforms().remove(transform);
		super.deactivate();
	}

	public Affine getTransform() {
		return transform;
	}

}
