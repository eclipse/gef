package org.eclipse.gef4.mvc.fx.viewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FXStageSceneContainer implements ISceneContainer {

	private final ChangeListener<? super Boolean> focusChangeListener = new ChangeListener<Boolean>() {

		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			// System.out.println("Focus on stage change " + oldValue + " -> "
			// + newValue);
			// TODO: nofiy viewer about focus change
		}
	};

	private final FXViewer viewer;
	private final Stage stage;

	public FXStageSceneContainer(FXViewer viewer, Stage stage) {
		this.viewer = viewer;
		this.stage = stage;
	}

	@Override
	public void registerFocusForwarding(FXViewer viewer) {
		stage.focusedProperty().addListener(focusChangeListener);
	}

	@Override
	public void setScene(Scene scene) {
		stage.setScene(scene);

	}

	@Override
	public void unregisterFocusForwarding(FXViewer viewer) {
		stage.focusedProperty().removeListener(focusChangeListener);
	}

}
