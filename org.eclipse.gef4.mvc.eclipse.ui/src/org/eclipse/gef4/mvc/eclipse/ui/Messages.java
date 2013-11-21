package org.eclipse.gef4.mvc.eclipse.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "org.eclipse.gef4.mvc.eclipse.ui.messages"; //$NON-NLS-1$
	
	public static String SetPropertyValueCommand_Label;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
