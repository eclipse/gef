package org.eclipse.gef4.mvc.fx.example;

import java.util.Arrays;

import javafx.embed.swt.FXCanvas;

import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.fx.FXEditDomain;
import org.eclipse.gef4.mvc.fx.FXViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class FXExampleViewPart extends ViewPart {
	
	private FXCanvas canvas;

	public FXExampleViewPart() {
	}

	@Override
	public void createPartControl(Composite parent) {
		canvas = new FXCanvas(parent, SWT.NONE);
		FXEditDomain domain = new FXEditDomain();
		FXViewer viewer = new FXViewer(canvas);
		viewer.setContentPartFactory(new FXExampleContentPartFactory());
		viewer.setHandlePartFactory(new FXExampleHandlePartFactory());
		viewer.setEditDomain(domain);
		viewer.setContents(Arrays.asList(new IGeometry[] {
				new Rectangle(50, 50, 50, 50),
				new Rectangle(150, 50, 50, 50),
				new Rectangle(400, 400, 50, 50),
				new Rectangle(-5, -5, 10, 10) }));
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

}
