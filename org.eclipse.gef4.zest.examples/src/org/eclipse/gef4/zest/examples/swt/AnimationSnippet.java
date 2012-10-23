package org.eclipse.gef4.zest.examples.swt;

import org.eclipse.draw2d.Animation;
import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * The following snippet shows how to setup animation in Zest. By using the
 * Draw2D animation, you can simply start the animation, set the node locations,
 * and then run the animation (with a set time).
 * 
 * @author irbull
 * 
 */
public class AnimationSnippet {

	public static void main(String[] args) {

		Display d = new Display();
		final Shell shell = new Shell(d);
		shell.setText("Animation Example");
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		shell.setSize(400, 400);

		Button b = new Button(shell, SWT.PUSH);
		b.setText("Animate");

		final Graph g = new Graph(shell, SWT.NONE);

		final GraphNode n = new GraphNode(g, SWT.NONE, "Paper");
		final GraphNode n2 = new GraphNode(g, SWT.NONE, "Rock");

		b.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				Animation.markBegin();
				n2.setLocation(0, 0);
				n.setLocation(g.getSize().x - n2.getSize().width - 5, 0);
				Animation.run(1000);
			}
		});

		new GraphConnection(g, SWT.NONE, n, n2);

		int centerX = shell.getSize().x / 2;
		int centerY = shell.getSize().y / 4;

		n.setLocation(centerX, centerY);
		n2.setLocation(centerX, centerY);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
