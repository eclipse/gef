package org.eclipse.gef4.swtfx.examples.snippets;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.eclipse.gef4.swtfx.SwtFXScene;
import org.eclipse.gef4.swtfx.controls.SwtFXButton;
import org.eclipse.gef4.swtfx.controls.SwtFXStyledText;
import org.eclipse.gef4.swtfx.examples.SwtFXApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;

public class StyledTextExample extends SwtFXApplication {

	public static void main(String[] args) {
		new StyledTextExample();
	}

	@Override
	public SwtFXScene createScene() {
		VBox vbox = new VBox();

		AnchorPane toolBarPane = new AnchorPane();
		vbox.getChildren().add(toolBarPane);

		SwtFXButton boldButton = new SwtFXButton("Bold");
		Button italicButton = new Button("Italic");
		ToolBar toolBar = new ToolBar(new Button("New"),
				new SwtFXButton("Open"), new Button("Save"), new Separator(),
				boldButton, italicButton);
		toolBarPane.getChildren().add(toolBar);
		AnchorPane.setTopAnchor(toolBar, 10d);
		AnchorPane.setLeftAnchor(toolBar, 10d);
		AnchorPane.setRightAnchor(toolBar, 10d);

		AnchorPane stPane = new AnchorPane();
		vbox.getChildren().add(stPane);
		VBox.setVgrow(stPane, Priority.ALWAYS);

		final SwtFXStyledText stNode = new SwtFXStyledText();
		stPane.getChildren().add(stNode);
		AnchorPane.setTopAnchor(stNode, 10d);
		AnchorPane.setLeftAnchor(stNode, 10d);
		AnchorPane.setRightAnchor(stNode, 10d);
		AnchorPane.setBottomAnchor(stNode, 10d);

		// behavior
		boldButton.addEventHandler(ActionEvent.ACTION,
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						StyledText st = stNode.getControl();
						if (st.getSelectionText().equals("")) {
							return;
						}
						Point selRange = st.getSelectionRange();
						StyleRange styleRange = new StyleRange();
						styleRange.start = selRange.x;
						styleRange.length = selRange.y;
						styleRange.fontStyle = SWT.BOLD;
						st.setStyleRange(styleRange);
					}
				});

		return new SwtFXScene(vbox, 800, 600);
	}
}
