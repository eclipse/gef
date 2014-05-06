/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.properties;

import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

// TODO: maybe move the cell editor to swtfx (and introduce jface dependency there) or swtfx.ui
public class FXFillCellEditor extends DialogCellEditor {

	private Image image;

	public FXFillCellEditor(Composite parent) {
		this(parent, SWT.NONE);
	}

	public FXFillCellEditor(Composite parent, int style) {
		super(parent, style);
		doSetValue(Color.TRANSPARENT);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		FXFillSelectionDialog dialog = new FXFillSelectionDialog(
				cellEditorWindow.getShell(), "Select Fill");
		Object value = getValue();
		if (value != null) {
			dialog.setPaint((Paint) value);
		}
		int result = dialog.open();
		if (result == Dialog.CANCEL) {
			return value;
		} else {
			return dialog.getPaint();
		}
	}

	ImageData createPaintImage(Control w, Paint paint) {
		int width = 64;
		int height = 16;
		if (w instanceof Table) {
			height = ((Table) w).getItemHeight() - 1;
		} else if (w instanceof Tree) {
			height = ((Tree) w).getItemHeight() - 1;
		} else if (w instanceof TableTree) {
			height = ((TableTree) w).getItemHeight() - 1;
		}
		return FXColorPicker.createPaintImage(width, height, paint);
	}

	@Override
	protected void updateContents(Object value) {
		final Paint paint = value == null ? Color.TRANSPARENT : (Paint) value;

		if (image != null) {
			image.dispose();
		}

		ImageData id = createPaintImage(getDefaultLabel().getParent()
				.getParent(), paint);
		image = new Image(getDefaultLabel().getDisplay(), id,
				id.getTransparencyMask());

		getDefaultLabel().setImage(image);
	}

	@Override
	public void dispose() {
		if (image != null) {
			image.dispose();
			image = null;
		}
		super.dispose();
	}
}
