package org.eclipse.gef4.mvc.fx.viewer;

import javafx.scene.Scene;

public interface ISceneContainer {

	public void registerFocusForwarding(FXViewer viewer);

	public void setScene(Scene scene);

	public void unregisterFocusForwarding(FXViewer viewer);
}