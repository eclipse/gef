/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx.examples.snippets;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
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
import org.eclipse.swt.widgets.Display;

public class SwtFXStyledTextSnippet extends SwtFXApplication {

	private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur\nadipisicing elit, sed do eiusmod tempor\nincididunt ut labore et dolore magna\naliqua. Ut enim ad minim veniam,\nquis nostrud exercitation ullamco laboris nisi\nut aliquip ex ea commodo consequat.\nDuis aute irure dolor in reprehenderit\nin voluptate velit esse cillum dolore\neu fugiat nulla pariatur. Excepteur sint\noccaecat cupidatat non proident, sunt\nin culpa qui officia deserunt mollit\nanim id est laborum.";

	public static void main(String[] args) {
		new SwtFXStyledTextSnippet();
	}

	private void colorAction(Node actionNode, final SwtFXStyledText stNode,
			final int fgSwtColorId, final int bgSwtColorId) {
		actionNode.addEventHandler(ActionEvent.ACTION,
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
						Display display = st.getDisplay();
						styleRange.foreground = display
								.getSystemColor(fgSwtColorId);
						styleRange.background = display
								.getSystemColor(bgSwtColorId);
						st.setStyleRange(styleRange);
					}
				});
	}

	@Override
	public SwtFXScene createScene() {
		// create layout panes
		VBox vbox = new VBox();

		AnchorPane toolBarPane = new AnchorPane();
		vbox.getChildren().add(toolBarPane);

		AnchorPane stPane = new AnchorPane();
		vbox.getChildren().add(stPane);
		VBox.setVgrow(stPane, Priority.ALWAYS);

		// create toolbar
		Button whiteBlackButton = new Button("white/black");
		Button yellowGrayButton = new Button("yellow/gray");
		Button boldButton = new Button("Bold");
		Button italicButton = new Button("Italic");
		Button underlineButton = new Button("Underline");
		Button clearStyleButton = new Button("Clear style");
		SwtFXButton loremIpsumButton = new SwtFXButton("Lorem Ipsum");
		SwtFXButton newButton = new SwtFXButton("New");
		ToolBar toolBar = new ToolBar(whiteBlackButton, yellowGrayButton,
				new Separator(), boldButton, italicButton, underlineButton,
				clearStyleButton, new Separator(), loremIpsumButton, newButton);

		// layout toolbar
		toolBarPane.getChildren().add(toolBar);
		AnchorPane.setTopAnchor(toolBar, 10d);
		AnchorPane.setLeftAnchor(toolBar, 10d);
		AnchorPane.setRightAnchor(toolBar, 10d);

		// create styled text
		final SwtFXStyledText stNode = new SwtFXStyledText();

		// layout styled text
		stPane.getChildren().add(stNode);
		AnchorPane.setTopAnchor(stNode, 10d);
		AnchorPane.setLeftAnchor(stNode, 10d);
		AnchorPane.setRightAnchor(stNode, 10d);
		AnchorPane.setBottomAnchor(stNode, 10d);

		// add behavior
		colorAction(whiteBlackButton, stNode, SWT.COLOR_WHITE, SWT.COLOR_BLACK);
		colorAction(yellowGrayButton, stNode, SWT.COLOR_DARK_YELLOW,
				SWT.COLOR_GRAY);
		styleAction(boldButton, stNode, SWT.BOLD, false);
		styleAction(italicButton, stNode, SWT.ITALIC, false);
		styleAction(underlineButton, stNode, SWT.NORMAL, true);
		styleAction(clearStyleButton, stNode, SWT.NORMAL, false);
		textAction(newButton, stNode, "");
		textAction(loremIpsumButton, stNode, LOREM_IPSUM);

		return new SwtFXScene(vbox, 800, 600);
	}

	private void styleAction(Node actionNode, final SwtFXStyledText stNode,
			final int fontStyle, final boolean underline) {
		actionNode.addEventHandler(ActionEvent.ACTION,
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
						styleRange.fontStyle = fontStyle;
						styleRange.underline = underline;
						st.setStyleRange(styleRange);
					}
				});
	}

	private void textAction(Node actionNode, final SwtFXStyledText stNode,
			final String text) {
		actionNode.addEventHandler(ActionEvent.ACTION,
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						stNode.getControl().setText(text);
					}
				});
	}
}
