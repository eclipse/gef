/*******************************************************************************
 * Copyright (c) 2020 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Tamas Miklossy (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.language.codemining;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

public class ColoredRectangleLineContentCodeMining
		extends LineContentCodeMining {

	private final Color color;

	public ColoredRectangleLineContentCodeMining(Position position,
			ICodeMiningProvider provider, Color color) {
		super(position, provider);
		this.color = color;
	}

	@Override
	public Point draw(GC gc, StyledText textWidget, Color color, int x, int y) {
		int fontHeight = gc.getFontMetrics().getHeight();
		int rectangleHeight = (int) (fontHeight * 0.75);
		int offset = (fontHeight - rectangleHeight) / 2;

		gc.setAntialias(SWT.ON);

		gc.setForeground(this.color);
		gc.setBackground(this.color);
		gc.fillRectangle(x + offset, y + offset, rectangleHeight,
				rectangleHeight);

		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(x + offset, y + offset, rectangleHeight,
				rectangleHeight);

		Point codeMiningSize = new Point(rectangleHeight + 5, rectangleHeight);
		return codeMiningSize;
	}

	@Override
	public String getLabel() {
		return " "; //$NON-NLS-1$
	}

}
