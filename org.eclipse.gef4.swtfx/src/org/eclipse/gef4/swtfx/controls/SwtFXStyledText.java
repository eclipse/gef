package org.eclipse.gef4.swtfx.controls;

import org.eclipse.gef4.swtfx.AbstractSwtFXControl;
import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;

public class SwtFXStyledText extends AbstractSwtFXControl<StyledText> {

	@Override
	protected StyledText createControl(SwtFXCanvas fxCanvas) {
		return new StyledText(fxCanvas, SWT.BORDER);
	}

	@Override
	protected void hookControl() {
		super.hookControl();
		setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

}
