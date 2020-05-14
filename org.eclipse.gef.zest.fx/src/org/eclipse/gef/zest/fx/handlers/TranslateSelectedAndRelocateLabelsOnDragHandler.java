/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - extract PreserveLabelOffsetsSupport
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.handlers;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.handlers.TranslateSelectedOnDragHandler;

import javafx.scene.input.MouseEvent;

/**
 * A specific {@link TranslateSelectedOnDragHandler} that includes dragging of
 * unselected label parts.
 *
 * @author anyssen
 *
 */
public class TranslateSelectedAndRelocateLabelsOnDragHandler extends TranslateSelectedOnDragHandler {

	private LabelOffsetSupport labelOffsetsSupport;

	@Override
	public void abortDrag() {
		if (labelOffsetsSupport != null) {
			labelOffsetsSupport.abort();
		}
		super.abortDrag();
		labelOffsetsSupport = null;
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		super.drag(e, delta);
		if (labelOffsetsSupport != null) {
			labelOffsetsSupport.preserveLabelOffsets();
		}
	}

	@Override
	public void endDrag(MouseEvent e, Dimension delta) {
		if (labelOffsetsSupport != null) {
			labelOffsetsSupport.commit();
		}
		super.endDrag(e, delta);
		labelOffsetsSupport = null;
	}

	@Override
	public void startDrag(MouseEvent e) {
		super.startDrag(e);
		labelOffsetsSupport = getHost().getViewer().getAdapter(LabelOffsetSupport.class);
		if (labelOffsetsSupport != null) {
			labelOffsetsSupport.init(getTargetParts());
		}
	}
}
