package org.eclipse.gef4.swtfx.controls;

import org.eclipse.gef4.swtfx.AbstractSwtFXControl;
import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;

public class SwtFXStyledText extends AbstractSwtFXControl<StyledText> {

	private int swtStyleFlags;

	public SwtFXStyledText() {
		this(SWT.BORDER);
	}

	public SwtFXStyledText(int swtStyleFlags) {
		this.swtStyleFlags = swtStyleFlags;
	}

	@Override
	protected StyledText createControl(SwtFXCanvas fxCanvas) {
		return new StyledText(fxCanvas, swtStyleFlags);
	}

	@Override
	protected void hookControl() {
		super.hookControl();
		setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

}
