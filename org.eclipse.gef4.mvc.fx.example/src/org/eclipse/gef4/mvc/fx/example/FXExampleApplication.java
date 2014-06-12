package org.eclipse.gef4.mvc.fx.example;

import java.util.Collections;

import javafx.application.Application;
import javafx.stage.Stage;

import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXZoomBehavior;
import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.example.domain.FXExampleDomain;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleContentPartFactory;
import org.eclipse.gef4.mvc.fx.example.parts.FXExampleHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.fx.viewer.FXStageViewer;

public class FXExampleApplication extends Application {

	public static void main(String[] args) {
		FXExampleApplication.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXStageViewer viewer = new FXStageViewer(primaryStage);
		viewer.setRootPart(new FXRootPart());
		viewer.setHandlePartFactory(new FXExampleHandlePartFactory());
		viewer.setContentPartFactory(new FXExampleContentPartFactory());
		viewer.setFeedbackPartFactory(new FXDefaultFeedbackPartFactory());

		viewer.getRootPart().setAdapter(FXSelectionBehavior.class,
				new FXSelectionBehavior());
		viewer.getRootPart().setAdapter(FXZoomBehavior.class,
				new FXZoomBehavior());

		FXDomain domain = new FXExampleDomain();
		viewer.setDomain(domain);

		viewer.setContents(Collections
				.<Object> singletonList(new FXGeometricModel()));

		primaryStage.setTitle("GEF4 MVC.FX Example");
		primaryStage.sizeToScene();
		primaryStage.show();
	}
}
