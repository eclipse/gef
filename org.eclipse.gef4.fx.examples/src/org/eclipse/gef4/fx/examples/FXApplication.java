package org.eclipse.gef4.fx.examples;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class FXApplication extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(createScene());
		primaryStage.show();
	}

	public abstract Scene createScene();

}
