package org.eclipse.gef.dot.internal.ui;

import org.eclipse.gef.zest.fx.parts.NodeLabelPart;

public class DotNodeLabelPart extends NodeLabelPart {

	@Override
	public void recomputeLabelPosition() {
		// only compute label positions in emulated mode
		// TODO: make native mode available within viewer
		if (!GraphvizPreferencePage.isGraphvizConfigured()) {
			super.recomputeLabelPosition();
		}
	}

}
