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
package org.eclipse.gef4.swtfx.examples.nestedgraph;

import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.TextFigure;
import org.eclipse.gef4.swtfx.controls.SwtText;
import org.eclipse.gef4.swtfx.controls.SwtText.Type;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.KeyEvent;
import org.eclipse.gef4.swtfx.event.MouseEvent;

public class EditLabPane extends LabPane {

	public EditLabPane(String label) {
		super(label);

		// replace title-figure with swt-text to change the title
		final TextFigure titleFigure = getTitleFigure();
		titleFigure.addEventHandler(MouseEvent.MOUSE_DOUBLE_CLICKED,
				new IEventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						setDragging(false);

						final SwtText swtText = new SwtText(Type.SINGLE);
						String text = titleFigure.getText().trim();
						swtText.setText(text);

						final IParent parent = titleFigure.getParentNode();
						parent.replace(titleFigure, swtText);
						swtText.requestFocus();
						swtText.setSelection(0, text.length());

						swtText.addEventHandler(KeyEvent.KEY_PRESSED,
								new IEventHandler<KeyEvent>() {
									@Override
									public void handle(KeyEvent event) {
										// commit change via <RETURN>
										if (event.getCode() == 13) {
											event.consume();
											titleFigure.setText(" "
													+ swtText.getText());
											parent.replace(swtText, titleFigure);
										}
									}
								});
					}
				});
	}
}
