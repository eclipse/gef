/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

/**
 * The abstract base implementation of {@link IFeedbackPart}, intended to be
 * sub-classed by clients to create their own custom {@link IFeedbackPart}.
 * 
 * @author anyssen
 * 
 * @param <VR> The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractFeedbackPart<VR> extends AbstractVisualPart<VR>
		implements IFeedbackPart<VR> {

	@Override
	protected void addChildVisual(IVisualPart<VR> child, int index) {
		throw new UnsupportedOperationException(
				"IFeedbackParts do not support children");
	}

	@Override
	protected void removeChildVisual(IVisualPart<VR> child, int index) {
		throw new UnsupportedOperationException(
				"IFeedbackParts do not support this");
	}

}
