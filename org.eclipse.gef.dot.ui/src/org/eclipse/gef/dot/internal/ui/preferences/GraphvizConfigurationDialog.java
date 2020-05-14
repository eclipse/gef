/*******************************************************************************
 * Copyright (c) 2016, 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.preferences;

import org.eclipse.gef.dot.internal.ui.DotUiMessages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class GraphvizConfigurationDialog extends MessageDialog {

	private static final String INVALID_GRAPHVIZ_CONF = DotUiMessages.GraphvizPreference_3;
	private static final String GRAPHVIZ_CONF_HINT = DotUiMessages.GraphvizPreference_4;

	public GraphvizConfigurationDialog(Shell parentShell) {
		super(parentShell, INVALID_GRAPHVIZ_CONF, null, GRAPHVIZ_CONF_HINT,
				WARNING, new String[] { IDialogConstants.CANCEL_LABEL }, 0);
	}

	@Override
	protected Control createMessageArea(Composite composite) {
		// prevent creation of messageLabel by super implementation
		String linkText = message;
		message = null;
		super.createMessageArea(composite);
		message = linkText;

		Link messageLink = new Link(composite, SWT.WRAP);
		messageLink.setText(message);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(messageLink);
		messageLink.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Shell shell = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell();
				PreferenceDialog pref = PreferencesUtil
						.createPreferenceDialogOn(shell,
								"org.eclipse.gef.dot.internal.ui.GraphvizPreferencePage", //$NON-NLS-1$
								null, null);
				if (pref != null) {
					close();
					pref.open();
				}
			}
		});
		return composite;
	}

}