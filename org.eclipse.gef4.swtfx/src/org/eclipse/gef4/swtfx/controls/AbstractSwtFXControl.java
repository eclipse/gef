package org.eclipse.gef4.swtfx.controls;

import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.gef4.swtfx.SwtFXControlAdapter;
import org.eclipse.swt.widgets.Control;

abstract public class AbstractSwtFXControl<T extends Control> extends
		SwtFXControlAdapter<T> {

	public AbstractSwtFXControl() {
		super((T) null);
	}

	abstract protected T createControl(SwtFXCanvas fxCanvas);

	@Override
	protected void hookControl(final SwtFXCanvas newCanvas) {
		setControl(createControl(newCanvas));
		super.hookControl(newCanvas);
	}

	@Override
	protected void unhookControl(final SwtFXCanvas oldCanvas) {
		super.unhookControl(oldCanvas);
		getControl().dispose();
	}

}
