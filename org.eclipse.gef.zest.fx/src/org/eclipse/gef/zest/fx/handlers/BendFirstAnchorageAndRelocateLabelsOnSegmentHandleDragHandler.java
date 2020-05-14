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
 *     Matthias Wienand (itemis AG) - unify with TSARLODH API
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.handlers;

import java.util.Collections;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.mvc.fx.handlers.BendFirstAnchorageOnSegmentHandleDragHandler;
import org.eclipse.gef.zest.fx.parts.EdgeLabelPart;

import javafx.scene.input.MouseEvent;

/**
 * An {@link BendFirstAnchorageOnSegmentHandleDragHandler} that also takes care
 * of relocating related {@link EdgeLabelPart}s.
 *
 * @author anyssen
 *
 */
public class BendFirstAnchorageAndRelocateLabelsOnSegmentHandleDragHandler extends BendFirstAnchorageOnSegmentHandleDragHandler {

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
			labelOffsetsSupport.init(Collections.singletonList(getTargetPart()));
		}
	}
}
