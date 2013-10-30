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
package org.eclipse.gef4.swtfx.examples;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.SwtControlAdapterNode;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class TestRotatedButton implements IExample {

	// TODO: DOES NOT WORK CURRENTLY! FIX IT!

	private static final int ROTATION_ANGLE_DEG = 15;

	public static void main(String[] args) {
		new Example(new TestRotatedButton());
	}

	private Angle rotationAngle = Angle.fromRad(0);

	@Override
	public void addUi(final IParent root) {
		final SwtControlAdapterNode<Button> pushCw = new SwtControlAdapterNode<Button>(new Button(
				root.getScene(), SWT.PUSH));
		pushCw.getControl().setText("Push! (CW)");
		pushCw.relocate(300, 300);

		Button control = new Button(root.getScene(), SWT.PUSH);
		final SwtControlAdapterNode<Button> pushCcw = new SwtControlAdapterNode<Button>(control);
		pushCcw.getControl().setText("Push! (CCW)");
		pushCcw.relocate(400, 200);

		ShapeFigure rect = new ShapeFigure(new Rectangle(0, 0, 100, 100));
		rect.relocate(100, 100);

		root.addChildren(pushCw, pushCcw, rect);

		pushCw.addEventHandler(ActionEvent.ACTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						rotationAngle = rotationAngle.getAdded(Angle
								.fromDeg(ROTATION_ANGLE_DEG));
						update(root, pushCw, pushCcw);
					}
				});

		pushCcw.getControl().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				rotationAngle = rotationAngle.getAdded(Angle
						.fromDeg(-ROTATION_ANGLE_DEG));
				update(root, pushCw, pushCcw);
			}
		});
	}

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "Rotated Button";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	private void update(final IParent root, final SwtControlAdapterNode n1,
			final SwtControlAdapterNode n2) {
		root.setRotationAngle(rotationAngle);
		root.getScene().refreshVisuals();
	}

}
