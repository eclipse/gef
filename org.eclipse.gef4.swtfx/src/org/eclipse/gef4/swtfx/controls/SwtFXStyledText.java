/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
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
	protected void hookControl(StyledText control) {
		super.hookControl(control);
		setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
	}

}
