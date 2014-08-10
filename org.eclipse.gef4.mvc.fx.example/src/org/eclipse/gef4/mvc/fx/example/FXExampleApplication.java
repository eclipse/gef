package org.eclipse.gef4.mvc.fx.example;

import java.util.Collections;

import javafx.application.Application;
import javafx.stage.Stage;

import org.eclipse.gef4.mvc.fx.domain.FXDomain;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.viewer.FXStageSceneContainer;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class FXExampleApplication extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		// TODO: inject domain
		Injector injector = Guice.createInjector(new FXExampleModule());
		FXDomain domain = new FXDomain();
		injector.injectMembers(domain);
		FXViewer viewer = domain.getAdapter(IViewer.class);
		viewer.setSceneContainer(new FXStageSceneContainer(primaryStage));
		primaryStage.setResizable(true);
		primaryStage.setWidth(640);
		primaryStage.setHeight(480);

		// activate domain only after viewers have been hooked
		domain.activate();

		viewer.setContents(Collections
				.<Object> singletonList(new FXGeometricModel()));

		primaryStage.setTitle("GEF4 MVC.FX Example");
		primaryStage.sizeToScene();
		primaryStage.show();
	}
}
