package org.eclipse.gef4.mvc.fx.example;

import javafx.embed.swt.FXCanvas;

import org.eclipse.gef4.mvc.fx.domain.FXEditDomain;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
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
		FXEditDomain domain = new FXExampleDomain();
		FXViewer viewer = new FXViewer(canvas);
		viewer.setContentPartFactory(new FXExampleContentPartFactory());
		viewer.setHandlePartFactory(new FXExampleHandlePartFactory());
		viewer.setEditDomain(domain);
		viewer.setContents(new FXGeometricModel());
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

}
