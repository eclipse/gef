package org.eclipse.gef4.mvc.fx.example;

import java.util.Collections;

import javafx.application.Application;
import javafx.stage.Stage;

import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.viewer.FXStageViewer;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class FXExampleApplication extends Application {

	public static void main(String[] args) {
		FXExampleApplication.launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		Injector injector = Guice.createInjector(new FXExampleModule());
		FXDomain domain = new FXDomain();
		injector.injectMembers(domain);

		FXStageViewer viewer = new FXStageViewer(primaryStage);
		injector.injectMembers(viewer);

		// hook viewer to domain
		domain.addViewer(viewer);
		// identical to: viewer.setDomain(domain);

		viewer.setContents(Collections
				.<Object> singletonList(new FXGeometricModel()));

		primaryStage.setTitle("GEF4 MVC.FX Example");
		primaryStage.sizeToScene();
		primaryStage.show();
	}
}
