package org.eclipse.gef4.zest.examples.swt;

import org.eclipse.gef4.layout.IEntityLayout;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.LayoutProperties;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;
import org.eclipse.gef4.zest.core.widgets.GraphWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This snippet shows how to create a custom layout. This layout simply lays the
 * nodes out vertically on the same Y-Axis as they currently have. All the work
 * is done in the applyLayoutInternal Method.
 * 
 * @author irbull
 * 
 */
public class CustomLayout {

	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		shell.setText("Custom Layout Example");
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		GraphWidget g = new GraphWidget(shell, SWT.NONE);

		GraphNode n = new GraphNode(g, SWT.NONE, "Paper");
		GraphNode n2 = new GraphNode(g, SWT.NONE, "Rock");
		GraphNode n3 = new GraphNode(g, SWT.NONE, "Scissors");
		new GraphConnection(g, SWT.NONE, n, n2);
		new GraphConnection(g, SWT.NONE, n2, n3);
		new GraphConnection(g, SWT.NONE, n3, n);

		ILayoutAlgorithm layoutAlgorithm = new ILayoutAlgorithm() {
			private ILayoutContext context;

			public void setLayoutContext(ILayoutContext context) {
				this.context = context;
			}

			public ILayoutContext getLayoutContext() {
				return context;
			}

			public void applyLayout(boolean clean) {
				IEntityLayout[] entitiesToLayout = context.getEntities();
				int totalSteps = entitiesToLayout.length;
				double distance = LayoutProperties.getBounds(context)
						.getWidth() / totalSteps;
				int xLocation = 0;

				for (int currentStep = 0; currentStep < entitiesToLayout.length; currentStep++) {
					IEntityLayout layoutEntity = entitiesToLayout[currentStep];
					LayoutProperties.setLocation(layoutEntity, xLocation,
							LayoutProperties.getLocation(layoutEntity).y);
					xLocation += distance;
				}
			}
		};
		g.setLayoutAlgorithm(layoutAlgorithm, true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
	}
}
