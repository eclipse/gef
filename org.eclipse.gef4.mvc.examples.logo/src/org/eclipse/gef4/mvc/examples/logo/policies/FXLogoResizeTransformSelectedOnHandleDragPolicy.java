package org.eclipse.gef4.mvc.examples.logo.policies;

import org.eclipse.gef4.mvc.fx.policies.FXResizeTransformSelectedOnHandleDragPolicy;

import javafx.scene.input.MouseEvent;

public class FXLogoResizeTransformSelectedOnHandleDragPolicy
		extends FXResizeTransformSelectedOnHandleDragPolicy {

	@Override
	public void press(MouseEvent e) {
		super.press(e);
		// change cursor to rotate cursor
	}

}
