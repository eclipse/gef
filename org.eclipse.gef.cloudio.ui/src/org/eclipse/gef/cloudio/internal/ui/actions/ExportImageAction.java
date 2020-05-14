/******************************************************************************
 * Copyright (c) 2011, 2016 Stephan Schwiebert and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Schwiebert - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.cloudio.internal.ui.actions;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;

/**
 * 
 * @author sschwieb
 *
 */
public class ExportImageAction extends AbstractTagCloudAction {

	@Override
	public void run(IAction action) {
		FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
		dialog.setFileName("Cloud.png");
		dialog.setText("Export PNG image to...");
		String destFile = dialog.open();
		if (destFile == null)
			return;
		File f = new File(destFile);
		if (f.exists()) {
			boolean confirmed = MessageDialog.openConfirm(getShell(), "File already exists",
					"The file '" + f.getName() + "' does already exist. Do you want to override it?");
			if (!confirmed)
				return;
		}
		ImageLoader il = new ImageLoader();
		try {
			il.data = new ImageData[] { getViewer().getCloud().getImageData() };
			il.save(destFile, SWT.IMAGE_PNG);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
