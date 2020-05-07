package org.eclipse.gef.dot.internal.ui.language.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.gef.dot.internal.ui.language.wizard.messages"; //$NON-NLS-1$
	
	public static String HelloWorldFile_Label;
	public static String HelloWorldFile_Description;
	public static String DirectedGraph_Label;
	public static String DirectedGraph_Description;
	public static String EmptyProject_Label;
	public static String EmptyProject_Description;
	public static String ParameterisedDotFile_Label;
	public static String ParameterisedDotFile_Description;
	
	static {
	// initialize resource bundle
	NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
