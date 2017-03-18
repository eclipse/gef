package org.eclipse.gef.dot.internal.ui;

import org.eclipse.gef.zest.fx.parts.EdgeLabelPart;

public class DotEdgeLabelPart extends EdgeLabelPart {

	@Override
	public void recomputeLabelPosition() {
		// only compute label positions in emulated mode
		// TODO: make native mode available within viewer
		if (!GraphvizPreferencePage.isGraphvizConfigured()) {
			super.recomputeLabelPosition();
			;
		}
	}

}
