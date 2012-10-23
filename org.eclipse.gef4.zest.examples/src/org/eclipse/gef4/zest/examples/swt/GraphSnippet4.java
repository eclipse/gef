/*******************************************************************************
 * Copyright 2005-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     The Chisel Group, University of Victoria
 *******************************************************************************/
package org.eclipse.gef4.zest.examples.swt;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.ZestStyles;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet shows how a custom figure can be used as a ToolTip for
 * connections. Let your mouse hover over an edge to see the custom tooltip.
 * 
 * @author Ian Bull
 * 
 */
public class GraphSnippet4 {

	/**
	 * Merges 2 images so they appear beside each other
	 * 
	 * You must dispose this image!
	 * 
	 * @param image1
	 * @param image2
	 * @param result
	 * @return
	 */
	public static Image mergeImages(Image image1, Image image2) {
		Image mergedImage = new Image(Display.getDefault(),
				image1.getBounds().width + image2.getBounds().width,
				image1.getBounds().height);
		GC gc = new GC(mergedImage);
		gc.drawImage(image1, 0, 0);
		gc.drawImage(image2, image1.getBounds().width, 0);
		gc.dispose();
		return mergedImage;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("Graph Snippet 4");
		Image image1 = Display.getDefault()
				.getSystemImage(SWT.ICON_INFORMATION);
		Image image2 = Display.getDefault().getSystemImage(SWT.ICON_WARNING);
		Image image3 = Display.getDefault().getSystemImage(SWT.ICON_ERROR);
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		Graph g = new Graph(shell, SWT.NONE);
		g.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		GraphNode n1 = new GraphNode(g, SWT.NONE);
		n1.setText("Information");
		n1.setImage(image1);
		GraphNode n2 = new GraphNode(g, SWT.NONE);
		n2.setText("Warning");
		n2.setImage(image2);
		GraphNode n3 = new GraphNode(g, SWT.NONE);
		n3.setText("Error");
		n3.setImage(image3);

		GraphConnection connection1 = new GraphConnection(g, SWT.NONE, n1, n2);
		GraphConnection connection2 = new GraphConnection(g, SWT.NONE, n2, n3);

		Image information2warningImage = mergeImages(image1, image2);
		Image warning2error = mergeImages(image2, image3);
		IFigure tooltip1 = new Label("Information to Warning",
				information2warningImage);
		IFigure tooltip2 = new Label("Warning to Error", warning2error);
		connection1.setTooltip(tooltip1);
		connection2.setTooltip(tooltip2);

		n1.setLocation(10, 10);
		n2.setLocation(200, 10);
		n3.setLocation(200, 200);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}

		image1.dispose();
		image2.dispose();
		image3.dispose();

		information2warningImage.dispose();
		warning2error.dispose();

	}
}
