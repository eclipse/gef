package org.eclipse.gef4.mvc.javafx.example;

import java.util.Arrays;

import javafx.embed.swt.FXCanvas;
import javafx.geometry.Rectangle2D;

import org.eclipse.gef4.mvc.javafx.FXEditDomain;
import org.eclipse.gef4.mvc.javafx.FXViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class FXExampleViewPart extends ViewPart {

	private FXCanvas canvas;

	public FXExampleViewPart() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		canvas = new FXCanvas(parent,  SWT.NONE);
		
		FXEditDomain domain = new FXEditDomain();

		FXViewer viewer = new FXViewer(canvas);
		viewer.setEditPartFactory(new FXExampleEditPartFactory());
		viewer.setContents(Arrays.asList(new Rectangle2D[]{new Rectangle2D(50, 50,  50, 50), new Rectangle2D(150, 50, 50, 50)}));
		viewer.setEditDomain(domain);
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

}
