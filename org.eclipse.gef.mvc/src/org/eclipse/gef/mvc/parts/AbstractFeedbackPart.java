/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.parts;

/**
 * The abstract base implementation of {@link IFeedbackPart}, intended to be
 * sub-classed by clients to create their own custom {@link IFeedbackPart}.
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this
 *            {@link AbstractFeedbackPart} is used in, e.g. javafx.scene.Node in
 *            case of JavaFX.
 *
 * @param <V>
 *            The visual node used by this {@link AbstractFeedbackPart}.
 */
public abstract class AbstractFeedbackPart<VR, V extends VR>
		extends AbstractVisualPart<VR, V>implements IFeedbackPart<VR, V> {

	@Override
	protected void doAddChildVisual(IVisualPart<VR, ? extends VR> child,
			int index) {
		throw new UnsupportedOperationException(
				"IFeedbackParts do not support children");
	}

	@Override
	protected void doRemoveChildVisual(IVisualPart<VR, ? extends VR> child,
			int index) {
		throw new UnsupportedOperationException(
				"IFeedbackParts do not support this");
	}

}
