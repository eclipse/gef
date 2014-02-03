package org.eclipse.gef4.mvc.fx.example;

import java.util.Collections;

import javafx.embed.swt.FXCanvas;

import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.policies.FXSelectionFeedbackByEffectPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.policies.AbstractSelectionFeedbackPolicy;
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
		FXViewer viewer = new FXViewer(canvas);
		FXDomain domain = new FXExampleDomain();
		viewer.setDomain(domain);
		viewer.setContentPartFactory(new FXExampleContentPartFactory());
		viewer.setHandlePartFactory(new FXExampleHandlePartFactory());
		viewer.setContents(Collections.<Object>singletonList(new FXGeometricModel()));

		// install selection feedback policy
		viewer.getRootPart().installPolicy(
				AbstractSelectionFeedbackPolicy.class,
				new FXSelectionFeedbackByEffectPolicy());
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

}
